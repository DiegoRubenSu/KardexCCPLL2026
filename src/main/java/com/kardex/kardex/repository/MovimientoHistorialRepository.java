package com.kardex.kardex.repository;

import com.kardex.kardex.model.MovimientoHistorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoHistorialRepository extends JpaRepository<MovimientoHistorial, Integer> {

    List<MovimientoHistorial> findByArticuloIdOrderByFechaMovimientoDesc(Integer articuloId);

    Page<MovimientoHistorial> findByArticuloId(Integer articuloId, Pageable pageable);

    List<MovimientoHistorial> findAllByOrderByFechaMovimientoDesc();

    @Query("SELECT m FROM MovimientoHistorial m WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaMovimiento DESC")
    List<MovimientoHistorial> findByFechaMovimientoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                           @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT m FROM MovimientoHistorial m WHERE m.articulo.codigo = :codigo ORDER BY m.fechaMovimiento DESC")
    List<MovimientoHistorial> findByArticuloCodigo(@Param("codigo") String codigo);

    @Query("SELECT m FROM MovimientoHistorial m WHERE " +
            "LOWER(m.articulo.descripcion) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(m.articulo.codigo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(m.tipoMovimiento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(m.motivo) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    Page<MovimientoHistorial> buscarPorDescripcionArticulo(@Param("filtro") String filtro, Pageable pageable);
}