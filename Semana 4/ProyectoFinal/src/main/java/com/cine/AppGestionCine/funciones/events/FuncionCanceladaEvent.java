package com.cine.AppGestionCine.funciones.events;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Evento que se dispara cuando una función se cancela
 * Los asientos deben reaccionar marcándose como cancelados
 */
@Data
@AllArgsConstructor
public class FuncionCanceladaEvent {
    private final Long funcionId;
    private final String pelicula;
    private final String sala;
}