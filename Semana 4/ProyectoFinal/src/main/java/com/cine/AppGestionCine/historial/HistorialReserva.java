package com.cine.AppGestionCine.historial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "historial_reservas")
//        ↑
//   Nombre de la "colección" (equivalente a tabla en MySQL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialReserva {

    // MongoDB genera IDs automáticamente como strings
    @Id
    private String id;  // Ej: "507f1f77bcf86cd799439011"

    // Tipo de operación realizada
    private TipoOperacion tipoOperacion;

    // Timestamp de cuando ocurrió
    private LocalDateTime timestamp;

    // ID de la función (si aplica)
    private Long funcionId;

    // ID del asiento (si aplica)
    private Long asientoId;

    // Email del cliente (si aplica)
    private String clienteEmail;

    // Datos adicionales flexibles
    // Puedes guardar cualquier información sin definir campos específicos
    private Map<String, Object> datosAdicionales;

    // Descripción legible de la operación
    private String descripcion;

    /**
     * Constructor para crear registro de reserva
     */
    public static HistorialReserva deReserva(
            Long funcionId,
            Long asientoId,
            String numeroAsiento,
            String clienteEmail) {

        HistorialReserva historial = new HistorialReserva();
        historial.setTipoOperacion(TipoOperacion.ASIENTO_RESERVADO);
        historial.setTimestamp(LocalDateTime.now());
        historial.setFuncionId(funcionId);
        historial.setAsientoId(asientoId);
        historial.setClienteEmail(clienteEmail);
        historial.setDescripcion(
                String.format("Cliente %s reservó asiento %s en función %d",
                        clienteEmail, numeroAsiento, funcionId)
        );

        // Datos adicionales
        historial.setDatosAdicionales(Map.of(
                "numeroAsiento", numeroAsiento,
                "timestampLegible", LocalDateTime.now().toString()
        ));

        return historial;
    }

    /**
     * Constructor para cancelación de reserva
     */
    public static HistorialReserva deCancelacion(
            Long funcionId,
            Long asientoId,
            String numeroAsiento,
            String clienteEmail) {

        HistorialReserva historial = new HistorialReserva();
        historial.setTipoOperacion(TipoOperacion.ASIENTO_CANCELADO);
        historial.setTimestamp(LocalDateTime.now());
        historial.setFuncionId(funcionId);
        historial.setAsientoId(asientoId);
        historial.setClienteEmail(clienteEmail);
        historial.setDescripcion(
                String.format("Se canceló reserva del asiento %s (función %d)",
                        numeroAsiento, funcionId)
        );

        historial.setDatosAdicionales(Map.of(
                "numeroAsiento", numeroAsiento,
                "motivoCancelacion", "Cancelación manual",
                "timestampLegible", LocalDateTime.now().toString()
        ));

        return historial;
    }

    /**
     * Constructor para función cancelada
     */
    public static HistorialReserva deFuncionCancelada(
            Long funcionId,
            String pelicula,
            int asientosCancelados) {

        HistorialReserva historial = new HistorialReserva();
        historial.setTipoOperacion(TipoOperacion.FUNCION_CANCELADA);
        historial.setTimestamp(LocalDateTime.now());
        historial.setFuncionId(funcionId);
        historial.setDescripcion(
                String.format("Función '%s' (ID: %d) cancelada. %d asientos afectados",
                        pelicula, funcionId, asientosCancelados)
        );

        historial.setDatosAdicionales(Map.of(
                "pelicula", pelicula,
                "asientosCancelados", asientosCancelados,
                "timestampLegible", LocalDateTime.now().toString()
        ));

        return historial;
    }

    /**
     * Constructor para función creada
     */
    public static HistorialReserva deFuncionCreada(
            Long funcionId,
            String pelicula,
            String sala,
            int totalAsientos) {

        HistorialReserva historial = new HistorialReserva();
        historial.setTipoOperacion(TipoOperacion.FUNCION_CREADA);
        historial.setTimestamp(LocalDateTime.now());
        historial.setFuncionId(funcionId);
        historial.setDescripcion(
                String.format("Nueva función creada: '%s' en %s con %d asientos",
                        pelicula, sala, totalAsientos)
        );

        historial.setDatosAdicionales(Map.of(
                "pelicula", pelicula,
                "sala", sala,
                "totalAsientos", totalAsientos,
                "timestampLegible", LocalDateTime.now().toString()
        ));

        return historial;
    }
}
