package com.cine.AppGestionCine.clientes.dto;

import com.cine.AppGestionCine.clientes.EstadoCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private EstadoCliente estado;
    private LocalDateTime fechaRegistro;

    // Constructor desde entidad
    public static ClienteResponse fromEntity(com.cine.AppGestionCine.clientes.Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getEstado(),
                cliente.getFechaRegistro()
        );
    }
}