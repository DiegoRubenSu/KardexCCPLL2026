package com.kardex.kardex.api;

import com.kardex.kardex.dto.ArticuloDTO;
import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ArticuloRestController {

    @Autowired
    private ArticuloRepository articuloRepository;

    // GET: Listar todos los artículos activos
    @GetMapping("/articulos")
    public ResponseEntity<List<ArticuloDTO>> listarArticulos() {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();
            List<ArticuloDTO> articulosDTO = articulos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(articulosDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: Obtener artículo por ID
    @GetMapping("/articulos/{id}")
    public ResponseEntity<ArticuloDTO> obtenerArticulo(@PathVariable Integer id) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);

            if (articuloOpt.isPresent() && articuloOpt.get().getActivo()) {
                ArticuloDTO dto = convertToDTO(articuloOpt.get());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST: Crear nuevo artículo
    @PostMapping("/articulos")
    public ResponseEntity<ArticuloDTO> crearArticulo(@RequestBody ArticuloDTO articuloDTO) {
        try {
            if (articuloDTO.getDescripcion() == null || articuloDTO.getDescripcion().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Articulo articulo = new Articulo();
            articulo.setCodigo("ART" + System.currentTimeMillis() % 10000);
            articulo.setDescripcion(articuloDTO.getDescripcion());
            articulo.setUnidad(articuloDTO.getUnidad());
            articulo.setPrecio(articuloDTO.getPrecio());
            articulo.setInventarioInicial(articuloDTO.getInventarioInicial() != null ? articuloDTO.getInventarioInicial() : 0);
            articulo.setEntradas(articuloDTO.getEntradas() != null ? articuloDTO.getEntradas() : 0);
            articulo.setSalidas(articuloDTO.getSalidas() != null ? articuloDTO.getSalidas() : 0);
            articulo.calcularInventarioFinal();
            articulo.setActivo(true);

            Articulo articuloGuardado = articuloRepository.save(articulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(articuloGuardado));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT: Actualizar artículo
    @PutMapping("/articulos/{id}")
    public ResponseEntity<ArticuloDTO> actualizarArticulo(@PathVariable Integer id,
                                                          @RequestBody ArticuloDTO articuloDTO) {
        try {
            Optional<Articulo> articuloExistente = articuloRepository.findById(id);

            if (!articuloExistente.isPresent() || !articuloExistente.get().getActivo()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Articulo articulo = articuloExistente.get();
            articulo.setDescripcion(articuloDTO.getDescripcion());
            articulo.setUnidad(articuloDTO.getUnidad());
            articulo.setPrecio(articuloDTO.getPrecio());
            articulo.setInventarioInicial(articuloDTO.getInventarioInicial() != null ? articuloDTO.getInventarioInicial() : 0);
            articulo.setEntradas(articuloDTO.getEntradas() != null ? articuloDTO.getEntradas() : 0);
            articulo.setSalidas(articuloDTO.getSalidas() != null ? articuloDTO.getSalidas() : 0);
            articulo.calcularInventarioFinal();

            Articulo articuloActualizado = articuloRepository.save(articulo);
            return ResponseEntity.ok(convertToDTO(articuloActualizado));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE: Eliminación lógica
    @DeleteMapping("/articulos/{id}")
    public ResponseEntity<Void> eliminarArticulo(@PathVariable Integer id) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);

            if (articuloOpt.isPresent()) {
                Articulo articulo = articuloOpt.get();
                articulo.setActivo(false);
                articuloRepository.save(articulo);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ArticuloDTO convertToDTO(Articulo articulo) {
        ArticuloDTO dto = new ArticuloDTO();
        dto.setId(articulo.getId());
        dto.setCodigo(articulo.getCodigo());
        dto.setDescripcion(articulo.getDescripcion());
        dto.setUnidad(articulo.getUnidad());
        dto.setPrecio(articulo.getPrecio());
        dto.setInventarioInicial(articulo.getInventarioInicial());
        dto.setEntradas(articulo.getEntradas());
        dto.setSalidas(articulo.getSalidas());
        dto.setAjuste(articulo.getAjuste());
        dto.setInventarioFinal(articulo.getInventarioFinal());
        dto.setCreatedAt(articulo.getCreatedAt());
        return dto;
    }
}