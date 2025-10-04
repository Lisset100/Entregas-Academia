package com.cine.AppGestionCine.funciones.events.handlers;

import com.cine.AppGestionCine.funciones.FuncionService;
import com.cine.AppGestionCine.asientos.events.AsientoReservadoEvent;
import com.cine.AppGestionCine.asientos.events.AsientoCanceladoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler que escucha eventos del módulo de asientos
 * Este handler vive en el módulo de funciones pero reacciona a eventos de asientos
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AsientoEventHandler {

    private final FuncionService funcionService;

    /**
     * Cuando se reserva un asiento, decrementar asientos disponibles
     */
    @EventListener
    @Transactional
    public void manejarAsientoReservado(AsientoReservadoEvent evento) {
        log.info("🎫 EVENTO RECIBIDO: AsientoReservadoEvent - Asiento {} reservado por {}",
                evento.getNumeroAsiento(), evento.getClienteEmail());

        try {
            // Decrementar asientos disponibles (-1)
            funcionService.actualizarAsientosDisponibles(evento.getFuncionId(), -1);

            log.info("✅ Asientos disponibles decrementados para función {} debido a reserva de {}",
                    evento.getFuncionId(), evento.getNumeroAsiento());

        } catch (Exception e) {
            log.error("❌ Error al actualizar asientos disponibles para función {}: {}",
                    evento.getFuncionId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cuando se cancela una reserva de asiento, incrementar asientos disponibles
     */
    @EventListener
    @Transactional
    public void manejarAsientoCancelado(AsientoCanceladoEvent evento) {
        log.info("🎫 EVENTO RECIBIDO: AsientoCanceladoEvent - Asiento {} cancelado por {}",
                evento.getNumeroAsiento(), evento.getClienteEmail());

        try {
            // Incrementar asientos disponibles (+1)
            funcionService.actualizarAsientosDisponibles(evento.getFuncionId(), 1);

            log.info("✅ Asientos disponibles incrementados para función {} debido a cancelación de {}",
                    evento.getFuncionId(), evento.getNumeroAsiento());

        } catch (Exception e) {
            log.error("❌ Error al actualizar asientos disponibles para función {}: {}",
                    evento.getFuncionId(), e.getMessage(), e);
            throw e;
        }
    }
}