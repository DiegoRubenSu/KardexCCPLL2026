package com.kardex.kardex.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    @Scheduled(cron = "0 * * * * *")
    public void verificarSistema() {
        logger.info("Sistema verificado: {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void tareaMantenimientoDiario() {
        logger.info("Tarea de mantenimiento diario ejecutada");
    }
}