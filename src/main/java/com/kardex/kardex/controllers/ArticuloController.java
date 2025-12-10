package com.kardex.kardex.controllers;

import com.kardex.kardex.dto.ArticuloDTO;
import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.repository.ArticuloRepository;
import com.kardex.kardex.service.CodigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private CodigoService codigoService;

    @GetMapping
    public String listaArticulos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filtro,
            Model model) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("descripcion").ascending());
            Page<Articulo> paginaArticulos;

            if (filtro != null && !filtro.trim().isEmpty()) {
                paginaArticulos = articuloRepository.buscarPorDescripcionPaginado(filtro, pageable);
            } else {
                paginaArticulos = articuloRepository.findByActivoTrue(pageable);
            }

            List<ArticuloDTO> articulos = paginaArticulos.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            model.addAttribute("articulos", articulos);
            model.addAttribute("paginaActual", paginaArticulos.getNumber());
            model.addAttribute("totalPaginas", paginaArticulos.getTotalPages());
            model.addAttribute("totalElementos", paginaArticulos.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("filtro", filtro);

            return "list";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los artículos: " + e.getMessage());
            return "list";
        }
    }

    @GetMapping("/registrarArticulo")
    public String mostrarFormularioRegistro(Model model) {
        try {
            String codigoParaMostrar = codigoService.obtenerProximoCodigoParaMostrar();
            ArticuloDTO articuloDTO = new ArticuloDTO();
            articuloDTO.setCodigo(codigoParaMostrar);
            model.addAttribute("articulo", articuloDTO);
            return "registrarArticulo";
        } catch (Exception e) {
            model.addAttribute("error", "Error al generar código: " + e.getMessage());
            return "registrarArticulo";
        }
    }

    @PostMapping("/registrarArticulo")
    public String crearArticulo(@ModelAttribute ArticuloDTO articuloDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            // Validaciones
            if (articuloDTO.getDescripcion() == null || articuloDTO.getDescripcion().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La descripción es obligatoria");
                return "redirect:/articulos/registrarArticulo";
            }

            if (articuloDTO.getUnidad() == null || articuloDTO.getUnidad().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La unidad es obligatoria");
                return "redirect:/articulos/registrarArticulo";
            }

            // Generar código definitivo
            String codigoDefinitivo = codigoService.generarYReservarCodigo();
            articuloDTO.setCodigo(codigoDefinitivo);

            Articulo articulo = convertToEntity(articuloDTO);
            articulo.calcularInventarioFinal();

            Articulo articuloGuardado = articuloRepository.save(articulo);

            redirectAttributes.addFlashAttribute("success",
                    "✅ Artículo " + articuloGuardado.getCodigo() + " registrado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "❌ Error al registrar el artículo: " + e.getMessage());
        }

        return "redirect:/articulos";
    }

    @GetMapping("/gestionar")
    public String gestionarArticulos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filtro,
            Model model) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("descripcion").ascending());
            Page<Articulo> paginaArticulos;

            if (filtro != null && !filtro.trim().isEmpty()) {
                paginaArticulos = articuloRepository.buscarPorDescripcionPaginado(filtro, pageable);
            } else {
                paginaArticulos = articuloRepository.findByActivoTrue(pageable);
            }

            List<ArticuloDTO> articulos = paginaArticulos.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            model.addAttribute("articulos", articulos);
            model.addAttribute("paginaActual", paginaArticulos.getNumber());
            model.addAttribute("totalPaginas", paginaArticulos.getTotalPages());
            model.addAttribute("totalElementos", paginaArticulos.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("filtro", filtro);

            return "gestionarArticulos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los artículos: " + e.getMessage());
            return "gestionarArticulos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);
            if (articuloOpt.isPresent() && articuloOpt.get().getActivo()) {
                ArticuloDTO articuloDTO = convertToDTO(articuloOpt.get());
                model.addAttribute("articulo", articuloDTO);
                return "editarArticulo";
            } else {
                model.addAttribute("error", "Artículo no encontrado");
                return "redirect:/articulos/gestionar";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el artículo: " + e.getMessage());
            return "redirect:/articulos/gestionar";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarArticulo(@PathVariable Integer id,
                                     @ModelAttribute ArticuloDTO articuloDTO,
                                     RedirectAttributes redirectAttributes) {
        try {
            Optional<Articulo> articuloExistente = articuloRepository.findById(id);
            if (!articuloExistente.isPresent() || !articuloExistente.get().getActivo()) {
                redirectAttributes.addFlashAttribute("error", "Artículo no encontrado");
                return "redirect:/articulos/gestionar";
            }

            // Mantener el código original
            articuloDTO.setCodigo(articuloExistente.get().getCodigo());

            Articulo articulo = convertToEntity(articuloDTO);
            articulo.setId(id);
            articulo.setActivo(true);
            articulo.calcularInventarioFinal();

            articuloRepository.save(articulo);

            redirectAttributes.addFlashAttribute("success",
                    "✅ Artículo " + articulo.getCodigo() + " actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el artículo: " + e.getMessage());
        }

        return "redirect:/articulos/gestionar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarArticulo(@PathVariable Integer id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String filtro,
                                   RedirectAttributes redirectAttributes) {
        try {
            Optional<Articulo> articuloOpt = articuloRepository.findById(id);
            if (articuloOpt.isPresent()) {
                Articulo articulo = articuloOpt.get();
                articulo.setActivo(false); // Eliminación lógica
                articuloRepository.save(articulo);

                redirectAttributes.addFlashAttribute("success",
                        "✅ Artículo " + articulo.getCodigo() + " eliminado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Artículo no encontrado");
            }

            // Mantener los parámetros de paginación
            if (filtro != null) {
                redirectAttributes.addAttribute("filtro", filtro);
            }
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("size", size);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar el artículo: " + e.getMessage());
        }
        return "redirect:/articulos/gestionar";
    }

    // Métodos auxiliares de conversión
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

    private Articulo convertToEntity(ArticuloDTO dto) {
        Articulo articulo = new Articulo();
        articulo.setId(dto.getId());
        articulo.setCodigo(dto.getCodigo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setUnidad(dto.getUnidad());
        articulo.setPrecio(dto.getPrecio());
        articulo.setInventarioInicial(dto.getInventarioInicial() != null ? dto.getInventarioInicial() : 0);
        articulo.setEntradas(dto.getEntradas() != null ? dto.getEntradas() : 0);
        articulo.setSalidas(dto.getSalidas() != null ? dto.getSalidas() : 0);
        articulo.setAjuste(dto.getAjuste() != null ? dto.getAjuste() : 0);
        articulo.setActivo(true);
        return articulo;
    }
}