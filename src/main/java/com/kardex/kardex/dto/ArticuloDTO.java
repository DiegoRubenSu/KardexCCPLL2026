package com.kardex.kardex.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ArticuloDTO {
    private Integer id;
    private String codigo;
    private String descripcion;
    private String unidad;
    private BigDecimal precio;
    private Integer inventarioInicial;
    private Integer entradas;
    private Integer salidas;
    private Integer ajuste;
    private Integer inventarioFinal;
    private LocalDateTime createdAt;

    // Constructor vacío
    public ArticuloDTO() {}

    // Constructor completo
    public ArticuloDTO(Integer id, String codigo, String descripcion, String unidad,
                       BigDecimal precio, Integer inventarioInicial, Integer entradas,
                       Integer salidas, Integer ajuste, Integer inventarioFinal,
                       LocalDateTime createdAt) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.precio = precio;
        this.inventarioInicial = inventarioInicial;
        this.entradas = entradas;
        this.salidas = salidas;
        this.ajuste = ajuste;
        this.inventarioFinal = inventarioFinal;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getInventarioInicial() { return inventarioInicial; }
    public void setInventarioInicial(Integer inventarioInicial) { this.inventarioInicial = inventarioInicial; }

    public Integer getEntradas() { return entradas; }
    public void setEntradas(Integer entradas) { this.entradas = entradas; }

    public Integer getSalidas() { return salidas; }
    public void setSalidas(Integer salidas) { this.salidas = salidas; }

    public Integer getAjuste() { return ajuste; }
    public void setAjuste(Integer ajuste) { this.ajuste = ajuste; }

    public Integer getInventarioFinal() { return inventarioFinal; }
    public void setInventarioFinal(Integer inventarioFinal) { this.inventarioFinal = inventarioFinal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}