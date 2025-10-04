package com.cine.AppGestionCine.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class CrearClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    //  ↑
    // Validación: No puede ser null, vacío o solo espacios
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    //  ↑
    // Validación: Debe ser email válido (ejemplo@dominio.com)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    //  ↑
    // Validación: Solo 10 dígitos numéricos
    private String telefono;
}
