package com.cine.AppGestionCine.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduler: Ejecuta jobs automáticamente
 *
 * Por defecto: Cada día a las 2:00 AM
 */
@Configuration
@EnableScheduling  // ← Habilita scheduling
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job limpiezaReservasJob;

    @Scheduled(cron = "0 0 2 * * ?")//Significa que se ejecutará todos los días a las 2am
    public void ejecutarLimpiezaDiaria() {
        try {
            log.info("🚀 ═══════════════════════════════════════════════");
            log.info("🚀 Iniciando Job Programado: Limpieza de Reservas");
            log.info("🚀 ═══════════════════════════════════════════════");

            // Crear parámetros únicos para cada ejecución
            // (Spring Batch requiere parámetros únicos)
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("trigger", "scheduled")
                    .toJobParameters();

            // Lanzar el job
            jobLauncher.run(limpiezaReservasJob, params);

            log.info("✅ ═══════════════════════════════════════════════");
            log.info("✅ Job Completado Exitosamente");
            log.info("✅ ═══════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ ═══════════════════════════════════════════════");
            log.error("❌ Error al ejecutar job: {}", e.getMessage(), e);
            log.error("❌ ═══════════════════════════════════════════════");
        }
    }

    /**
     * TESTING: Ejecutar cada 5 minutos
     *
     * Descomentar este metodo para probar más rápido
     * comentarlo en producción
     */
    /*
    @Scheduled(fixedRate = 300000) // 5 minutos = 300,000 ms
    public void ejecutarCada5Minutos() {
        try {
            log.info("🧪 TEST: Ejecutando job cada 5 minutos");

            JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("trigger", "test-5min")
                .toJobParameters();

            jobLauncher.run(limpiezaReservasJob, params);

        } catch (Exception e) {
            log.error("❌ Error en ejecución de prueba: {}", e.getMessage());
        }
    }
    */
}
