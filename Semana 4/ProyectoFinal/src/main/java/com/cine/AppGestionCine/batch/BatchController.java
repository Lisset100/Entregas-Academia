package com.cine.AppGestionCine.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller: Ejecutar jobs manualmente desde API
 *
 * Endpoints:
 * - POST /api/batch/limpiar-reservas  (ejecutar job)
 */
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job limpiezaReservasJob;

    /**
     * Ejecutar job manualmente
     *
     * POST /api/batch/limpiar-reservas
     *
     * Útil para:
     * - Testing
     * - Debugging
     * - Ejecutar bajo demanda
     */
    @PostMapping("/limpiar-reservas")
    public ResponseEntity<?> ejecutarLimpiezaManual() {
        try {
            log.info("🔧 ═══════════════════════════════════════════════");
            log.info("🔧 Ejecutando job MANUALMENTE desde API");
            log.info("🔧 ═══════════════════════════════════════════════");

            // Crear parámetros únicos
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("trigger", "manual-api")
                    .toJobParameters();

            // Ejecutar job
            JobExecution execution = jobLauncher.run(limpiezaReservasJob, params);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Job ejecutado exitosamente");
            response.put("jobName", execution.getJobInstance().getJobName());
            response.put("status", execution.getStatus().toString());
            response.put("exitStatus", execution.getExitStatus().toString());
            response.put("startTime", execution.getStartTime());
            response.put("endTime", execution.getEndTime());

            log.info("✅ Job completado: {}", execution.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al ejecutar job: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al ejecutar job");
            error.put("detalle", e.getMessage());

            return ResponseEntity.badRequest().body(error);
        }
    }
}