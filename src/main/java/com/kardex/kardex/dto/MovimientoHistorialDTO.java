package com.kardex.kardex.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovimientoHistorialDTO {
    private Integer id;
    private String codigoArticulo;
    private String descripcionArticulo;
    private String tipoMovimiento;
    private Integer cantidad;
    private String motivo;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private LocalDateTime fechaMovimiento;
    private Integer articuloId;

    public MovimientoHistorialDTO() {}

    public MovimientoHistorialDTO(Integer id, String codigoArticulo, String descripcionArticulo,
                                  String tipoMovimiento, Integer cantidad, String motivo,
                                  Integer stockAnterior, Integer stockNuevo, LocalDateTime fechaMovimiento,
                                  Integer articuloId) {
        this.id = id;
        this.codigoArticulo = codigoArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.fechaMovimiento = fechaMovimiento;
        this.articuloId = articuloId;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoArticulo() { return codigoArticulo; }
    public void setCodigoArticulo(String codigoArticulo) { this.codigoArticulo = codigoArticulo; }

    public String getDescripcionArticulo() { return descripcionArticulo; }
    public void setDescripcionArticulo(String descripcionArticulo) { this.descripcionArticulo = descripcionArticulo; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }

    public Integer getStockNuevo() { return stockNuevo; }
    public void setStockNuevo(Integer stockNuevo) { this.stockNuevo = stockNuevo; }

    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public Integer getArticuloId() { return articuloId; }
    public void setArticuloId(Integer articuloId) { this.articuloId = articuloId; }

    // Métodos útiles para Thymeleaf
    public String getFechaFormateada() {
        if (fechaMovimiento != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return fechaMovimiento.format(formatter);
        }
        return "";
    }

    public String getClaseMovimiento() {
        return "ENTRADA".equals(tipoMovimiento) ? "movimiento-entrada" : "movimiento-salida";
    }

    public String getIconoMovimiento() {
        return "ENTRADA".equals(tipoMovimiento) ? "arrow-down-circle" : "arrow-up-circle";
    }
}