package com.kardex.kardex.controllers;

import com.kardex.kardex.dto.MovimientoHistorialDTO;
import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.model.MovimientoHistorial;
import com.kardex.kardex.repository.ArticuloRepository;
import com.kardex.kardex.repository.MovimientoHistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private MovimientoHistorialRepository movimientoHistorialRepository;

    @GetMapping
    public String mostrarMovimientos(Model model) {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();
            model.addAttribute("articulos", articulos);
            model.addAttribute("movimiento", new MovimientoDTO());

            List<MovimientoHistorialDTO> historialReciente = movimientoHistorialRepository
                    .findAllByOrderByFechaMovimientoDesc()
                    .stream()
                    .limit(10)
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            model.addAttribute("historialReciente", historialReciente);
            return "movimientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los datos: " + e.getMessage());
            return "movimientos";
        }
    }

    @PostMapping("/registrar")
    @Transactional
    public String registrarMovimiento(@ModelAttribute MovimientoDTO movimientoDTO,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (movimientoDTO.getArticuloId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un artículo");
                return "redirect:/movimientos";
            }

            if (!"ENTRADA".equals(movimientoDTO.getTipoMovimiento()) &&
                    !"SALIDA".equals(movimientoDTO.getTipoMovimiento())) {
                redirectAttributes.addFlashAttribute("error", "Tipo de movimiento inválido");
                return "redirect:/movimientos";
            }

            if (movimientoDTO.getCantidad() == null || movimientoDTO.getCantidad() <= 0) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0");
                return "redirect:/movimientos";
            }

            Optional<Articulo> articuloOpt = articuloRepository.findById(movimientoDTO.getArticuloId());

            if (articuloOpt.isPresent()) {
                Articulo articulo = articuloOpt.get();
                int stockAnterior = articulo.getInventarioFinal();

                if ("SALIDA".equals(movimientoDTO.getTipoMovimiento())) {
                    if (articulo.getInventarioFinal() < movimientoDTO.getCantidad()) {
                        redirectAttributes.addFlashAttribute("error",
                                "Stock insuficiente. Stock actual: " + articulo.getInventarioFinal());
                        return "redirect:/movimientos";
                    }
                }

                if ("ENTRADA".equals(movimientoDTO.getTipoMovimiento())) {
                    articulo.setEntradas(articulo.getEntradas() + movimientoDTO.getCantidad());
                } else {
                    articulo.setSalidas(articulo.getSalidas() + movimientoDTO.getCantidad());
                }

                articulo.calcularInventarioFinal();
                Articulo articuloActualizado = articuloRepository.save(articulo);

                MovimientoHistorial historial = new MovimientoHistorial(
                        articuloActualizado,
                        movimientoDTO.getTipoMovimiento(),
                        movimientoDTO.getCantidad(),
                        movimientoDTO.getMotivo() != null ? movimientoDTO.getMotivo() : "Movimiento registrado",
                        stockAnterior,
                        articuloActualizado.getInventarioFinal()
                );
                movimientoHistorialRepository.save(historial);

                redirectAttributes.addFlashAttribute("success",
                        "✅ Movimiento registrado correctamente. Stock actual: " +
                                articuloActualizado.getInventarioFinal());

            } else {
                redirectAttributes.addFlashAttribute("error", "Artículo no encontrado");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar movimiento: " + e.getMessage());
        }

        return "redirect:/movimientos";
    }

    @GetMapping("/historial")
    public String mostrarHistorialCompleto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filtro,
            Model model) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
            Page<MovimientoHistorial> paginaHistorial;

            if (filtro != null && !filtro.trim().isEmpty()) {
                paginaHistorial = movimientoHistorialRepository.buscarPorDescripcionArticulo(filtro, pageable);
            } else {
                paginaHistorial = movimientoHistorialRepository.findAll(pageable);
            }

            List<MovimientoHistorialDTO> historial = paginaHistorial.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            model.addAttribute("historial", historial);
            model.addAttribute("paginaActual", paginaHistorial.getNumber());
            model.addAttribute("totalPaginas", paginaHistorial.getTotalPages());
            model.addAttribute("totalElementos", paginaHistorial.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("filtro", filtro);

            return "historialMovimientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            return "historialMovimientos";
        }
    }

    @GetMapping("/historial/articulo/{id}")
    public String mostrarHistorialArticulo(@PathVariable Integer id,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           Model model) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);
            if (articuloOpt.isPresent()) {
                Articulo articulo = articuloOpt.get();

                Pageable pageable = PageRequest.of(page, size, Sort.by("fechaMovimiento").descending());
                Page<MovimientoHistorial> paginaHistorial =
                        movimientoHistorialRepository.findByArticuloId(id, pageable);

                List<MovimientoHistorialDTO> historial = paginaHistorial.getContent().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

                model.addAttribute("articulo", articulo);
                model.addAttribute("historial", historial);
                model.addAttribute("paginaActual", paginaHistorial.getNumber());
                model.addAttribute("totalPaginas", paginaHistorial.getTotalPages());
                model.addAttribute("totalElementos", paginaHistorial.getTotalElements());
                model.addAttribute("size", size);

                return "historialArticulo";
            } else {
                model.addAttribute("error", "Artículo no encontrado");
                return "redirect:/movimientos";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            return "redirect:/movimientos";
        }
    }

    private MovimientoHistorialDTO convertToDTO(MovimientoHistorial historial) {
        MovimientoHistorialDTO dto = new MovimientoHistorialDTO();
        dto.setId(historial.getId());
        dto.setCodigoArticulo(historial.getArticulo().getCodigo());
        dto.setDescripcionArticulo(historial.getArticulo().getDescripcion());
        dto.setTipoMovimiento(historial.getTipoMovimiento());
        dto.setCantidad(historial.getCantidad());
        dto.setMotivo(historial.getMotivo() != null ? historial.getMotivo() : "Sin motivo especificado");
        dto.setStockAnterior(historial.getStockAnterior());
        dto.setStockNuevo(historial.getStockNuevo());
        dto.setFechaMovimiento(historial.getFechaMovimiento());
        dto.setArticuloId(historial.getArticulo().getId());
        return dto;
    }

    // Clase interna DTO para movimientos
    public static class MovimientoDTO {
        private Integer articuloId;
        private String tipoMovimiento;
        private Integer cantidad;
        private String motivo;

        // Getters y Setters
        public Integer getArticuloId() {
            return articuloId;
        }

        public void setArticuloId(Integer articuloId) {
            this.articuloId = articuloId;
        }

        public String getTipoMovimiento() {
            return tipoMovimiento;
        }

        public void setTipoMovimiento(String tipoMovimiento) {
            this.tipoMovimiento = tipoMovimiento;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }
}