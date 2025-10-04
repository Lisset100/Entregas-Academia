package com.cine.AppGestionCine.asientos.events.handlers;

import com.cine.AppGestionCine.asientos.AsientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler adicional para cuando se crea una función
 * Automáticamente genera los asientos para esa función
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FuncionCreatedEventHandler {

    private final AsientoService asientoService;

    /**
     * Evento personalizado para creación de función (opcional)
     * Por simplicidad, en este ejemplo no lo usaremos,
     * pero sería útil para generar asientos automáticamente
     */
}