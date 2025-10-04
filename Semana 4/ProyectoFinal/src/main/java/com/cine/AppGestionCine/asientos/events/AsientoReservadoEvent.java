package com.cine.AppGestionCine.asientos.events;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Evento que se dispara cuando se reserva un asiento
 * Las funciones deben actualizar el contador de asientos disponibles
 */
@Data
@AllArgsConstructor
public class AsientoReservadoEvent {
    private final Long asientoId;
    private final Long funcionId;
    private final String numeroAsiento;
    private final String clienteEmail;
}
