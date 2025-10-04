package com.cine.AppGestionCine.clientes.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO para actualizar datos de un cliente
 *
 * Nota: Todos los campos son opcionales
 * Solo se actualiza lo que se envíe
 */
@Data
public class ActualizarClienteRequest {

    private String nombre;

    private String apellido;

    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;
}
