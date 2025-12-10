package com.kardex.kardex.controllers;

import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.model.MovimientoHistorial;
import com.kardex.kardex.repository.ArticuloRepository;
import com.kardex.kardex.repository.MovimientoHistorialRepository;
import com.kardex.kardex.service.ReporteExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReporteController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private MovimientoHistorialRepository movimientoHistorialRepository;

    @Autowired
    private ReporteExcelService reporteExcelService;

    @GetMapping("/reportes")
    public String mostrarReportes() {
        return "reportes";
    }

    @GetMapping("/reportes/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();
            byte[] excelBytes = reporteExcelService.generarReporteArticulos(articulos);

            String filename = "reporte_inventario_" + new Date().getTime() + ".xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // NUEVO: Reporte de artículos con stock bajo
    @GetMapping("/reportes/exportar-excel-stock-bajo")
    public ResponseEntity<byte[]> exportarExcelStockBajo() {
        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();
            // Filtrar artículos con stock bajo (<= 10 unidades)
            List<Articulo> articulosStockBajo = articulos.stream()
                    .filter(a -> a.getInventarioFinal() <= 10)
                    .toList();

            byte[] excelBytes = reporteExcelService.generarReporteArticulos(articulosStockBajo);

            String filename = "reporte_stock_bajo_" + new Date().getTime() + ".xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // NUEVO: Reporte de movimientos
    @GetMapping("/reportes/exportar-excel-movimientos")
    public ResponseEntity<byte[]> exportarExcelMovimientos() {
        try {
            List<MovimientoHistorial> movimientos = movimientoHistorialRepository.findAllByOrderByFechaMovimientoDesc();
            byte[] excelBytes = reporteExcelService.generarReporteMovimientos(movimientos);

            String filename = "reporte_movimientos_" + new Date().getTime() + ".xlsx";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(excelBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Agregar este método al ReporteController.java
    @GetMapping("/reportes/estadisticas")
    @ResponseBody
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();

        try {
            List<Articulo> articulos = articuloRepository.findByActivoTrue();

            // Calcular estadísticas
            long totalArticulos = articulos.size();
            long enStock = articulos.stream().filter(a -> a.getInventarioFinal() > 10).count();
            long stockBajo = articulos.stream().filter(a -> a.getInventarioFinal() > 0 && a.getInventarioFinal() <= 10).count();
            long sinStock = articulos.stream().filter(a -> a.getInventarioFinal() == 0).count();

            estadisticas.put("totalArticulos", totalArticulos);
            estadisticas.put("enStock", enStock);
            estadisticas.put("stockBajo", stockBajo);
            estadisticas.put("sinStock", sinStock);
            estadisticas.put("fechaActualizacion", new Date());
            estadisticas.put("success", true);

        } catch (Exception e) {
            estadisticas.put("success", false);
            estadisticas.put("error", e.getMessage());
        }

        return estadisticas;
    }

}
