package com.kardex.kardex.service;

import com.kardex.kardex.model.Articulo;
import com.kardex.kardex.model.MovimientoHistorial;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteExcelService {

    public byte[] generarReporteArticulos(List<Articulo> articulos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventario Artículos");

            // Crear headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Descripción", "Unidad", "Precio", "Inv. Final", "Estado"};

            CellStyle headerStyle = crearEstiloHeader(workbook);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (Articulo articulo : articulos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(articulo.getCodigo() != null ? articulo.getCodigo() : "N/A");
                row.createCell(1).setCellValue(articulo.getDescripcion() != null ? articulo.getDescripcion() : "Sin descripción");
                row.createCell(2).setCellValue(articulo.getUnidad() != null ? articulo.getUnidad() : "N/A");

                if (articulo.getPrecio() != null) {
                    row.createCell(3).setCellValue(articulo.getPrecio().doubleValue());
                } else {
                    row.createCell(3).setCellValue(0.0);
                }

                row.createCell(4).setCellValue(articulo.getInventarioFinal());

                String estado = articulo.getInventarioFinal() == 0 ? "SIN STOCK" :
                        articulo.getInventarioFinal() <= 10 ? "STOCK BAJO" : "EN STOCK";
                row.createCell(5).setCellValue(estado);
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // NUEVO: Método para generar reporte de movimientos
    public byte[] generarReporteMovimientos(List<MovimientoHistorial> movimientos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Movimientos");

            // Crear headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Fecha", "Código Artículo", "Descripción", "Tipo", "Cantidad",
                    "Motivo", "Stock Anterior", "Stock Nuevo", "Diferencia"};

            CellStyle headerStyle = crearEstiloHeader(workbook);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (MovimientoHistorial movimiento : movimientos) {
                Row row = sheet.createRow(rowNum++);

                // Fecha
                row.createCell(0).setCellValue(
                        movimiento.getFechaMovimiento().format(formatter)
                );

                // Artículo
                row.createCell(1).setCellValue(movimiento.getArticulo().getCodigo());
                row.createCell(2).setCellValue(movimiento.getArticulo().getDescripcion());

                // Tipo de movimiento
                row.createCell(3).setCellValue(movimiento.getTipoMovimiento());

                // Cantidad
                row.createCell(4).setCellValue(movimiento.getCantidad());

                // Motivo
                row.createCell(5).setCellValue(
                        movimiento.getMotivo() != null ? movimiento.getMotivo() : "Sin motivo"
                );

                // Stock anterior y nuevo
                row.createCell(6).setCellValue(movimiento.getStockAnterior());
                row.createCell(7).setCellValue(movimiento.getStockNuevo());

                // Diferencia
                row.createCell(8).setCellValue(
                        movimiento.getStockNuevo() - movimiento.getStockAnterior()
                );
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }
}