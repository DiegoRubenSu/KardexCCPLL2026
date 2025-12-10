package com.kardex.kardex.api;

import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.model.MovimientoHistorial;
import com.kardex.kardex.repository.ArticuloRepository;
import com.kardex.kardex.repository.MovimientoHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ReporteRestController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private MovimientoHistorialRepository movimientoHistorialRepository;

    // GET: Estadísticas del inventario
    @GetMapping("/reportes/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();

            Map<String, Object> estadisticas = new HashMap<>();

            long totalArticulos = articulos.size();
            long enStock = articulos.stream().filter(a -> a.getInventarioFinal() > 10).count();
            long stockBajo = articulos.stream().filter(a -> a.getInventarioFinal() > 0 && a.getInventarioFinal() <= 10).count();
            long sinStock = articulos.stream().filter(a -> a.getInventarioFinal() == 0).count();

            // Valor total del inventario
            double valorTotal = articulos.stream()
                    .mapToDouble(a -> a.getInventarioFinal() * (a.getPrecio() != null ? a.getPrecio().doubleValue() : 0))
                    .sum();

            estadisticas.put("totalArticulos", totalArticulos);
            estadisticas.put("enStock", enStock);
            estadisticas.put("stockBajo", stockBajo);
            estadisticas.put("sinStock", sinStock);
            estadisticas.put("valorTotalInventario", valorTotal);
            estadisticas.put("fechaConsulta", LocalDateTime.now());
            estadisticas.put("status", "success");

            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET: Movimientos por rango de fechas
    @GetMapping("/reportes/movimientos")
    public ResponseEntity<Map<String, Object>> obtenerMovimientosPorFecha(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {

        try {
            List<MovimientoHistorial> movimientos;

            if (fechaInicio != null && fechaFin != null) {
                // Convertir fechas (en un caso real usarías LocalDateTime.parse con formato)
                LocalDateTime inicio = LocalDateTime.parse(fechaInicio + "T00:00:00");
                LocalDateTime fin = LocalDateTime.parse(fechaFin + "T23:59:59");

                movimientos = movimientoHistorialRepository.findByFechaMovimientoBetween(inicio, fin);
            } else {
                movimientos = movimientoHistorialRepository.findAllByOrderByFechaMovimientoDesc();
            }

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("totalMovimientos", movimientos.size());
            resultado.put("movimientos", movimientos);
            resultado.put("status", "success");

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET: Artículos con stock bajo
    @GetMapping("/reportes/stock-bajo")
    public ResponseEntity<Map<String, Object>> obtenerArticulosStockBajo() {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();

            List<Articulo> stockBajo = articulos.stream()
                    .filter(a -> a.getInventarioFinal() <= 10 && a.getInventarioFinal() > 0)
                    .toList();

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("totalArticulosStockBajo", stockBajo.size());
            resultado.put("articulos", stockBajo);
            resultado.put("umbralStockBajo", 10);
            resultado.put("status", "success");

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // GET: Información de un artículo específico
    @GetMapping("/articulos/{id}/informacion")
    public ResponseEntity<Map<String, Object>> obtenerInformacionArticulo(@PathVariable Integer id) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);

            if (!articuloOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Articulo articulo = articuloOpt.get();
            List<MovimientoHistorial> movimientos = movimientoHistorialRepository
                    .findByArticuloIdOrderByFechaMovimientoDesc(id);

            Map<String, Object> informacion = new HashMap<>();
            informacion.put("articulo", articulo);
            informacion.put("totalMovimientos", movimientos.size());
            informacion.put("ultimosMovimientos", movimientos.stream().limit(5).toList());
            informacion.put("valorTotal", articulo.getInventarioFinal() *
                    (articulo.getPrecio() != null ? articulo.getPrecio().doubleValue() : 0));
            informacion.put("status", "success");

            return ResponseEntity.ok(informacion);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}