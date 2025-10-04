package com.cine.AppGestionCine.clientes.events;


import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Evento que se publica cuando se registra un nuevo cliente
 */
@Data
@AllArgsConstructor
public class ClienteRegistradoEvent {
    private final Long clienteId;
    private final String nombre;
    private final String email;
}
