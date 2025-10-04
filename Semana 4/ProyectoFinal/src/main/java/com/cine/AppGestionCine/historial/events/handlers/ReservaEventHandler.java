package com.cine.AppGestionCine.historial.events.handlers;

import com.cine.AppGestionCine.asientos.events.AsientoReservadoEvent;
import com.cine.AppGestionCine.asientos.events.AsientoCanceladoEvent;
import com.cine.AppGestionCine.funciones.events.FuncionCanceladaEvent;
import com.cine.AppGestionCine.historial.HistorialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservaEventHandler {

    private final HistorialService historialService;

    // ════════════════════════════════════════════════════════
    // ESCUCHAR EVENTOS DE ASIENTOS
    // ════════════════════════════════════════════════════════

    /**
     * Cuando se reserva un asiento, registrarlo en historial
     */
    @EventListener
    public void manejarAsientoReservado(AsientoReservadoEvent evento) {
        log.info("EVENTO RECIBIDO en Historial: AsientoReservadoEvent - Asiento {} por {}",
                evento.getNumeroAsiento(), evento.getClienteEmail());

        try {
            // Registrar en MongoDB
            historialService.registrarReserva(
                    evento.getFuncionId(),
                    evento.getAsientoId(),
                    evento.getNumeroAsiento(),
                    evento.getClienteEmail()
            );

            log.info("Reserva registrada en historial MongoDB");

        } catch (Exception e) {
            // Si falla el historial, NO debe afectar la funcionalidad principal
            log.error("Error al registrar en historial (no crítico): {}", e.getMessage());
            // NO lanzamos la excepción para no romper el flujo principal
        }
    }

    /**
     * Cuando se cancela una reserva, registrarlo en historial
     */
    @EventListener
    public void manejarAsientoCancelado(AsientoCanceladoEvent evento) {
        log.info("EVENTO RECIBIDO en Historial: AsientoCanceladoEvent - Asiento {}",
                evento.getNumeroAsiento());

        try {
            // Registrar en MongoDB
            historialService.registrarCancelacion(
                    evento.getFuncionId(),
                    evento.getAsientoId(),
                    evento.getNumeroAsiento(),
                    evento.getClienteEmail()
            );

            log.info("Cancelación registrada en historial MongoDB");

        } catch (Exception e) {
            log.error("Error al registrar cancelación en historial (no crítico): {}", e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════
    // ESCUCHAR EVENTOS DE FUNCIONES
    // ════════════════════════════════════════════════════════

    /**
     * Cuando se cancela una función, registrarlo en historial
     */
    @EventListener
    public void manejarFuncionCancelada(FuncionCanceladaEvent evento) {
        log.info("📢 EVENTO RECIBIDO en Historial: FuncionCanceladaEvent - Función {} ({})",
                evento.getFuncionId(), evento.getPelicula());

        try {
            // Registrar en MongoDB
            // Nota: No sabemos cuántos asientos se cancelaron aquí
            // Podrías consultar el repository si necesitas ese dato
            historialService.registrarFuncionCancelada(
                    evento.getFuncionId(),
                    evento.getPelicula(),
                    0  // Puedes mejorar esto consultando cuántos asientos tenía
            );

            log.info("Cancelación de función registrada en historial MongoDB");

        } catch (Exception e) {
            log.error("Error al registrar función cancelada en historial (no crítico): {}", e.getMessage());
        }
    }
}