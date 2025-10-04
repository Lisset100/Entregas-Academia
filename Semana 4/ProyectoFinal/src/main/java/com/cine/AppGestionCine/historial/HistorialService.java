package com.cine.AppGestionCine.historial;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistorialService {

    private final HistorialRepository historialRepository;

    // ════════════════════════════════════════════════════════
    // MÉTODOS PARA REGISTRAR OPERACIONES
    // ════════════════════════════════════════════════════════

    /**
     * Registrar reserva de asiento
     */
    public HistorialReserva registrarReserva(
            Long funcionId,
            Long asientoId,
            String numeroAsiento,
            String clienteEmail) {

        log.info("Registrando reserva: asiento {} por {}", numeroAsiento, clienteEmail);

        HistorialReserva historial = HistorialReserva.deReserva(
                funcionId,
                asientoId,
                numeroAsiento,
                clienteEmail
        );

        HistorialReserva guardado = historialRepository.save(historial);

        log.info("Historial registrado con ID: {}", guardado.getId());

        return guardado;
    }

    /**
     * Registrar cancelación de reserva
     */
    public HistorialReserva registrarCancelacion(
            Long funcionId,
            Long asientoId,
            String numeroAsiento,
            String clienteEmail) {

        log.info("Registrando cancelación: asiento {} de {}", numeroAsiento, clienteEmail);

        HistorialReserva historial = HistorialReserva.deCancelacion(
                funcionId,
                asientoId,
                numeroAsiento,
                clienteEmail
        );

        return historialRepository.save(historial);
    }

    /**
     * Registrar función cancelada
     */
    public HistorialReserva registrarFuncionCancelada(
            Long funcionId,
            String pelicula,
            int asientosCancelados) {

        log.info("Registrando cancelación de función: {} (ID: {})", pelicula, funcionId);

        HistorialReserva historial = HistorialReserva.deFuncionCancelada(
                funcionId,
                pelicula,
                asientosCancelados
        );

        return historialRepository.save(historial);
    }

    /**
     * Registrar creación de función
     */
    public HistorialReserva registrarFuncionCreada(
            Long funcionId,
            String pelicula,
            String sala,
            int totalAsientos) {

        log.info("Registrando creación de función: {} en {}", pelicula, sala);

        HistorialReserva historial = HistorialReserva.deFuncionCreada(
                funcionId,
                pelicula,
                sala,
                totalAsientos
        );

        return historialRepository.save(historial);
    }

    // ════════════════════════════════════════════════════════
    // MÉTODOS PARA CONSULTAR HISTORIAL
    // ════════════════════════════════════════════════════════

    /**
     * Obtener historial completo de una función
     */
    public List<HistorialReserva> getHistorialPorFuncion(Long funcionId) {
        log.info("Consultando historial de función {}", funcionId);
        return historialRepository.findByFuncionId(funcionId);
    }

    /**
     * Obtener historial de un cliente específico
     */
    public List<HistorialReserva> getHistorialPorCliente(String clienteEmail) {
        log.info("Consultando historial de cliente {}", clienteEmail);
        return historialRepository.findByClienteEmail(clienteEmail);
    }

    /**
     * Obtener operaciones por tipo
     */
    public List<HistorialReserva> getHistorialPorTipo(TipoOperacion tipo) {
        log.info("Consultando historial por tipo: {}", tipo);
        return historialRepository.findByTipoOperacion(tipo);
    }

    /**
     * Obtener historial en un rango de fechas
     */
    public List<HistorialReserva> getHistorialPorFechas(
            LocalDateTime inicio,
            LocalDateTime fin) {

        log.info("Consultando historial entre {} y {}", inicio, fin);
        return historialRepository.findByTimestampBetween(inicio, fin);
    }

    /**
     * Obtener últimas operaciones (para dashboard)
     */
    public List<HistorialReserva> getUltimasOperaciones() {
        log.info("Consultando últimas 10 operaciones");
        return historialRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * Contar operaciones por tipo (para estadísticas)
     */
    public long contarPorTipo(TipoOperacion tipo) {
        long count = historialRepository.countByTipoOperacion(tipo);
        log.info("Total de operaciones {}: {}", tipo, count);
        return count;
    }

    /**
     * Obtener estadísticas generales
     */
    public EstadisticasHistorial getEstadisticas() {
        log.info("Generando estadísticas de historial");

        long totalReservas = contarPorTipo(TipoOperacion.ASIENTO_RESERVADO);
        long totalCancelaciones = contarPorTipo(TipoOperacion.ASIENTO_CANCELADO);
        long totalFuncionesCreadas = contarPorTipo(TipoOperacion.FUNCION_CREADA);
        long totalFuncionesCanceladas = contarPorTipo(TipoOperacion.FUNCION_CANCELADA);

        return new EstadisticasHistorial(
                totalReservas,
                totalCancelaciones,
                totalFuncionesCreadas,
                totalFuncionesCanceladas,
                historialRepository.count()
        );
    }

    // ════════════════════════════════════════════════════════
    // DTO para estadísticas
    // ════════════════════════════════════════════════════════

    public static class EstadisticasHistorial {
        private final long totalReservas;
        private final long totalCancelaciones;
        private final long totalFuncionesCreadas;
        private final long totalFuncionesCanceladas;
        private final long totalOperaciones;

        public EstadisticasHistorial(
                long totalReservas,
                long totalCancelaciones,
                long totalFuncionesCreadas,
                long totalFuncionesCanceladas,
                long totalOperaciones) {
            this.totalReservas = totalReservas;
            this.totalCancelaciones = totalCancelaciones;
            this.totalFuncionesCreadas = totalFuncionesCreadas;
            this.totalFuncionesCanceladas = totalFuncionesCanceladas;
            this.totalOperaciones = totalOperaciones;
        }

        // Getters
        public long getTotalReservas() { return totalReservas; }
        public long getTotalCancelaciones() { return totalCancelaciones; }
        public long getTotalFuncionesCreadas() { return totalFuncionesCreadas; }
        public long getTotalFuncionesCanceladas() { return totalFuncionesCanceladas; }
        public long getTotalOperaciones() { return totalOperaciones; }
    }
}