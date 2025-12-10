package com.kardex.kardex.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "articulo")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo", unique = true, nullable = false)
    private String codigo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "unidad", nullable = false)
    private String unidad;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "inventario_inicial")
    private Integer inventarioInicial = 0;

    @Column(name = "entradas")
    private Integer entradas = 0;

    @Column(name = "salidas")
    private Integer salidas = 0;

    @Column(name = "ajuste")
    private Integer ajuste = 0;

    @Column(name = "inventario_final")
    private Integer inventarioFinal = 0;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    public Articulo() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.activo = true;
        this.inventarioInicial = 0;
        this.entradas = 0;
        this.salidas = 0;
        this.inventarioFinal = 0;
        this.usuarioCreacion = "Sistema";
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
    public void setInventarioInicial(Integer inventarioInicial) {
        this.inventarioInicial = inventarioInicial;
    }

    public Integer getEntradas() { return entradas; }
    public void setEntradas(Integer entradas) { this.entradas = entradas; }

    public Integer getSalidas() { return salidas; }
    public void setSalidas(Integer salidas) { this.salidas = salidas; }

    public Integer getAjuste() { return ajuste; }
    public void setAjuste(Integer ajuste) { this.ajuste = ajuste; }

    public Integer getInventarioFinal() { return inventarioFinal; }
    public void setInventarioFinal(Integer inventarioFinal) {
        this.inventarioFinal = inventarioFinal;
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

    public void calcularInventarioFinal() {
        this.inventarioFinal = this.inventarioInicial + this.entradas - this.salidas;
    }
}