package com.kardex.kardex.api;

import com.kardex.kardex.dto.MovimientoHistorialDTO;
import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.model.MovimientoHistorial;
import com.kardex.kardex.repository.ArticuloRepository;
import com.kardex.kardex.repository.MovimientoHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class MovimientoRestController {

    @Autowired
    private MovimientoHistorialRepository movimientoHistorialRepository;

    @Autowired
    private ArticuloRepository articuloRepository;

    // GET: Listar todos los movimientos
    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoHistorialDTO>> listarMovimientos() {
        try {
            List<MovimientoHistorial> movimientos = movimientoHistorialRepository.findAllByOrderByFechaMovimientoDesc();
            List<MovimientoHistorialDTO> movimientosDTO = movimientos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(movimientosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: Movimientos por artículo
    @GetMapping("/articulos/{id}/movimientos")
    public ResponseEntity<List<MovimientoHistorialDTO>> obtenerMovimientosPorArticulo(@PathVariable Integer id) {
        try {
            List<MovimientoHistorial> movimientos = movimientoHistorialRepository
                    .findByArticuloIdOrderByFechaMovimientoDesc(id);

            List<MovimientoHistorialDTO> movimientosDTO = movimientos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(movimientosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST: Registrar nuevo movimiento
    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoHistorialDTO> registrarMovimiento(@RequestBody MovimientoDTO movimientoDTO) {
        try {
            // Validaciones
            if (movimientoDTO.getArticuloId() == null ||
                    movimientoDTO.getTipoMovimiento() == null ||
                    movimientoDTO.getCantidad() == null || movimientoDTO.getCantidad() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Articulo> articuloOpt = articuloRepository.findById(movimientoDTO.getArticuloId());

            if (!articuloOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Articulo articulo = articuloOpt.get();
            int stockAnterior = articulo.getInventarioFinal();

            // Validar stock para salidas
            if ("SALIDA".equals(movimientoDTO.getTipoMovimiento()) &&
                    articulo.getInventarioFinal() < movimientoDTO.getCantidad()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Actualizar stock
            if ("ENTRADA".equals(movimientoDTO.getTipoMovimiento())) {
                articulo.setEntradas(articulo.getEntradas() + movimientoDTO.getCantidad());
            } else {
                articulo.setSalidas(articulo.getSalidas() + movimientoDTO.getCantidad());
            }

            articulo.calcularInventarioFinal();
            articuloRepository.save(articulo);

            // Crear registro de historial
            MovimientoHistorial historial = new MovimientoHistorial(
                    articulo,
                    movimientoDTO.getTipoMovimiento(),
                    movimientoDTO.getCantidad(),
                    movimientoDTO.getMotivo() != null ? movimientoDTO.getMotivo() : "Movimiento registrado vía API",
                    stockAnterior,
                    articulo.getInventarioFinal()
            );
            historial.setFechaMovimiento(LocalDateTime.now());

            MovimientoHistorial historialGuardado = movimientoHistorialRepository.save(historial);
            MovimientoHistorialDTO respuestaDTO = convertToDTO(historialGuardado);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO para recibir datos del movimiento
    public static class MovimientoDTO {
        private Integer articuloId;
        private String tipoMovimiento;
        private Integer cantidad;
        private String motivo;

        // Getters y Setters
        public Integer getArticuloId() { return articuloId; }
        public void setArticuloId(Integer articuloId) { this.articuloId = articuloId; }

        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    // Método auxiliar de conversión
    private MovimientoHistorialDTO convertToDTO(MovimientoHistorial historial) {
        MovimientoHistorialDTO dto = new MovimientoHistorialDTO();
        dto.setId(historial.getId());
        dto.setCodigoArticulo(historial.getArticulo().getCodigo());
        dto.setDescripcionArticulo(historial.getArticulo().getDescripcion());
        dto.setTipoMovimiento(historial.getTipoMovimiento());
        dto.setCantidad(historial.getCantidad());
        dto.setMotivo(historial.getMotivo());
        dto.setStockAnterior(historial.getStockAnterior());
        dto.setStockNuevo(historial.getStockNuevo());
        dto.setFechaMovimiento(historial.getFechaMovimiento());
        dto.setArticuloId(historial.getArticulo().getId());
        return dto;
    }
}