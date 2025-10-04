package com.cine.AppGestionCine.historial;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para consultar historial de operaciones
 *
 * Nota: NO hay endpoints POST/PUT/DELETE
 * El historial se registra AUTOMÁTICAMENTE por eventos
 */
@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HistorialController {

    private final HistorialService historialService;

    // ════════════════════════════════════════════════════════
    // ENDPOINTS DE CONSULTA
    // ════════════════════════════════════════════════════════

    /**
     * Obtener historial completo de una función
     * GET /api/historial/funcion/{funcionId}
     */
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<HistorialReserva>> getHistorialPorFuncion(
            @PathVariable Long funcionId) {

        log.info("📊 Consultando historial de función {}", funcionId);

        List<HistorialReserva> historial = historialService.getHistorialPorFuncion(funcionId);

        return ResponseEntity.ok(historial);
    }

    /**
     * Obtener historial de un cliente específico
     * GET /api/historial/cliente/{email}
     */
    @GetMapping("/cliente/{email}")
    public ResponseEntity<List<HistorialReserva>> getHistorialPorCliente(
            @PathVariable String email) {

        log.info("📊 Consultando historial de cliente {}", email);

        List<HistorialReserva> historial = historialService.getHistorialPorCliente(email);

        return ResponseEntity.ok(historial);
    }

    /**
     * Obtener operaciones por tipo
     * GET /api/historial/tipo/{tipo}
     *
     * Valores válidos: FUNCION_CREADA, FUNCION_CANCELADA,
     *                  ASIENTO_RESERVADO, ASIENTO_CANCELADO
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<HistorialReserva>> getHistorialPorTipo(
            @PathVariable TipoOperacion tipo) {

        log.info("📊 Consultando historial por tipo: {}", tipo);

        List<HistorialReserva> historial = historialService.getHistorialPorTipo(tipo);

        return ResponseEntity.ok(historial);
    }

    /**
     * Obtener historial en un rango de fechas
     * GET /api/historial/fechas?inicio=2025-09-27T00:00:00&fin=2025-09-27T23:59:59
     */
    @GetMapping("/fechas")
    public ResponseEntity<List<HistorialReserva>> getHistorialPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        log.info("📊 Consultando historial entre {} y {}", inicio, fin);

        List<HistorialReserva> historial = historialService.getHistorialPorFechas(inicio, fin);

        return ResponseEntity.ok(historial);
    }

    /**
     * Obtener últimas 10 operaciones (para dashboard)
     * GET /api/historial/recientes
     */
    @GetMapping("/recientes")
    public ResponseEntity<List<HistorialReserva>> getOperacionesRecientes() {
        log.info("📊 Consultando operaciones recientes");

        List<HistorialReserva> recientes = historialService.getUltimasOperaciones();

        return ResponseEntity.ok(recientes);
    }

    /**
     * Obtener estadísticas generales
     * GET /api/historial/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<HistorialService.EstadisticasHistorial> getEstadisticas() {
        log.info("📊 Consultando estadísticas de historial");

        HistorialService.EstadisticasHistorial stats = historialService.getEstadisticas();

        return ResponseEntity.ok(stats);
    }

    /**
     * Contar operaciones de un tipo específico
     * GET /api/historial/contar/{tipo}
     */
    @GetMapping("/contar/{tipo}")
    public ResponseEntity<ContadorResponse> contarPorTipo(@PathVariable TipoOperacion tipo) {
        log.info("📊 Contando operaciones de tipo: {}", tipo);

        long count = historialService.contarPorTipo(tipo);

        return ResponseEntity.ok(new ContadorResponse(tipo, count));
    }

    // ════════════════════════════════════════════════════════
    // DTO para respuesta de contador
    // ════════════════════════════════════════════════════════

    public static class ContadorResponse {
        private final TipoOperacion tipo;
        private final long total;

        public ContadorResponse(TipoOperacion tipo, long total) {
            this.tipo = tipo;
            this.total = total;
        }

        public TipoOperacion getTipo() { return tipo; }
        public long getTotal() { return total; }
    }
}