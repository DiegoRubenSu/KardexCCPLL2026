package com.kardex.kardex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CodigoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String obtenerProximoCodigoParaMostrar() {
        try {
            String sql = "SELECT siguiente_numero FROM secuencia_codigos WHERE prefijo = 'ART'";
            Integer siguienteNumero = jdbcTemplate.queryForObject(sql, Integer.class);

            if (siguienteNumero == null) {
                siguienteNumero = 1;
            }

            return String.format("ART%04d", siguienteNumero);
        } catch (Exception e) {
            // Si hay error, devolver código por defecto
            return "ART0001";
        }
    }

    @Transactional
    public String generarYReservarCodigo() {
        try {
            // Primero verificar si la tabla existe, si no, crearla
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'secuencia_codigos'";
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);

            if (tableExists == null || tableExists == 0) {
                // Crear tabla si no existe
                jdbcTemplate.execute(
                        "CREATE TABLE secuencia_codigos (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "prefijo VARCHAR(10) NOT NULL," +
                                "siguiente_numero INT NOT NULL DEFAULT 1," +
                                "descripcion VARCHAR(100)," +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                ")"
                );

                // Insertar registro inicial
                jdbcTemplate.update(
                        "INSERT INTO secuencia_codigos (prefijo, siguiente_numero, descripcion) VALUES (?, ?, ?)",
                        "ART", 2, "Secuencia para códigos de artículos"
                );

                return "ART0001";
            }

            // Obtener y bloquear la fila para evitar condiciones de carrera
            String lockSql = "SELECT siguiente_numero FROM secuencia_codigos WHERE prefijo = 'ART' FOR UPDATE";
            Integer siguienteNumero = jdbcTemplate.queryForObject(lockSql, Integer.class);

            if (siguienteNumero == null) {
                siguienteNumero = 1;
                jdbcTemplate.update(
                        "INSERT INTO secuencia_codigos (prefijo, siguiente_numero, descripcion) VALUES (?, ?, ?)",
                        "ART", 2, "Secuencia para códigos de artículos"
                );
            }

            String codigo = String.format("ART%04d", siguienteNumero);

            jdbcTemplate.update(
                    "UPDATE secuencia_codigos SET siguiente_numero = siguiente_numero + 1 WHERE prefijo = 'ART'"
            );

            return codigo;
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, generar código basado en la fecha
            return "ART" + System.currentTimeMillis() % 10000;
        }
    }
}