package com.kardex.kardex.repository;

import com.kardex.kardex.model.Articulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {

    List<Articulo> findByActivoTrue();

    Page<Articulo> findByActivoTrue(Pageable pageable);

    @Query("SELECT a FROM Articulo a WHERE a.activo = true AND " +
            "(LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(a.codigo) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    Page<Articulo> buscarPorDescripcionPaginado(@Param("filtro") String filtro, Pageable pageable);

    Optional<Articulo> findByCodigoAndActivoTrue(String codigo);

    boolean existsByCodigo(String codigo);
}