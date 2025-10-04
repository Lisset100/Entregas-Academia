package com.cine.AppGestionCine.asientos.events.handlers;


import com.cine.AppGestionCine.asientos.AsientoService;
import com.cine.AppGestionCine.funciones.events.FuncionCanceladaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler que escucha eventos del módulo de funciones
 * Este handler vive en el módulo de asientos reacciona a eventos de funciones
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FuncionEventHandler {

    private final AsientoService asientoService;

    /**
     * Cuando una función se cancela, todos sus asientos deben marcarse como cancelados
     */
    @EventListener
    @Transactional
    public void manejarFuncionCancelada(FuncionCanceladaEvent evento) {
        log.info("🎭 EVENTO RECIBIDO: FuncionCanceladaEvent para función {} - {}",
                evento.getFuncionId(), evento.getPelicula());

        try {
            // Delegar al servicio la cancelación de asientos
            asientoService.cancelarAsientosPorFuncion(evento.getFuncionId());

            log.info("✅ Asientos cancelados exitosamente para función {} - {}",
                    evento.getFuncionId(), evento.getPelicula());

        } catch (Exception e) {
            log.error("❌ Error al cancelar asientos para función {}: {}",
                    evento.getFuncionId(), e.getMessage(), e);
            throw e; // Relanzar para que Spring maneje la transacción
        }
    }
}
