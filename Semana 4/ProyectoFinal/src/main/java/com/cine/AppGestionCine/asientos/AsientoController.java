package com.cine.AppGestionCine.asientos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AsientoController {

    private final AsientoService asientoService;

    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Asiento>> getAsientosPorFuncion(@PathVariable Long funcionId) {
        List<Asiento> asientos = asientoService.getAsientosPorFuncion(funcionId);
        return ResponseEntity.ok(asientos);
    }

    @GetMapping("/funcion/{funcionId}/disponibles")
    public ResponseEntity<List<Asiento>> getAsientosDisponibles(@PathVariable Long funcionId) {
        List<Asiento> asientos = asientoService.getAsientosDisponibles(funcionId);
        return ResponseEntity.ok(asientos);
    }

    @PostMapping("/reservar")
    public ResponseEntity<?> reservarAsiento(@RequestBody ReservarAsientoRequest request) {
        try {
            log.info("Reservando asiento: función {}, asiento {}, cliente {}",
                    request.getFuncionId(), request.getNumeroAsiento(), request.getClienteEmail());

            Asiento asientoReservado = asientoService.reservarAsiento(
                    request.getFuncionId(),
                    request.getNumeroAsiento(),
                    request.getClienteEmail()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(asientoReservado);

        } catch (Exception e) {
            log.error("Error al reservar asiento: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            asientoService.cancelarReserva(id);
            return ResponseEntity.ok().body("Reserva cancelada exitosamente");

        } catch (Exception e) {
            log.error("Error al cancelar reserva {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarAsientos(@RequestBody GenerarAsientosRequest request) {
        try {
            log.info("Generando {} asientos para función {}", request.getTotalAsientos(), request.getFuncionId());

            asientoService.generarAsientosPorFuncion(request.getFuncionId(), request.getTotalAsientos());

            return ResponseEntity.ok().body("Asientos generados exitosamente");

        } catch (Exception e) {
            log.error("Error al generar asientos: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/funcion/{funcionId}/mapa")
    public ResponseEntity<?> getMapaAsientos(@PathVariable Long funcionId) {
        try {
            List<Asiento> asientos = asientoService.getAsientosPorFuncion(funcionId);

            // Organizar asientos por fila para mostrar como mapa
            MapaAsientosResponse mapa = new MapaAsientosResponse();

            for (Asiento asiento : asientos) {
                String fila = String.valueOf((char)('A' + asiento.getFila() - 1));

                if (!mapa.filas.containsKey(fila)) {
                    mapa.filas.put(fila, new java.util.ArrayList<>());
                }

                mapa.filas.get(fila).add(new AsientoInfo(
                        asiento.getId(),
                        asiento.getNumeroAsiento(),
                        asiento.getEstado().toString(),
                        asiento.getClienteEmail()
                ));
            }

            return ResponseEntity.ok(mapa);

        } catch (Exception e) {
            log.error("Error al obtener mapa de asientos: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DTOs internos
    public static class ReservarAsientoRequest {
        private Long funcionId;
        private String numeroAsiento;
        private String clienteEmail;

        public Long getFuncionId() { return funcionId; }
        public void setFuncionId(Long funcionId) { this.funcionId = funcionId; }

        public String getNumeroAsiento() { return numeroAsiento; }
        public void setNumeroAsiento(String numeroAsiento) { this.numeroAsiento = numeroAsiento; }

        public String getClienteEmail() { return clienteEmail; }
        public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    }

    public static class GenerarAsientosRequest {
        private Long funcionId;
        private Integer totalAsientos;

        public Long getFuncionId() { return funcionId; }
        public void setFuncionId(Long funcionId) { this.funcionId = funcionId; }

        public Integer getTotalAsientos() { return totalAsientos; }
        public void setTotalAsientos(Integer totalAsientos) { this.totalAsientos = totalAsientos; }
    }

    public static class MapaAsientosResponse {
        private java.util.Map<String, java.util.List<AsientoInfo>> filas = new java.util.HashMap<>();

        public java.util.Map<String, java.util.List<AsientoInfo>> getFilas() { return filas; }
        public void setFilas(java.util.Map<String, java.util.List<AsientoInfo>> filas) { this.filas = filas; }
    }

    public static class AsientoInfo {
        private Long id;
        private String numero;
        private String estado;
        private String cliente;

        public AsientoInfo(Long id, String numero, String estado, String cliente) {
            this.id = id;
            this.numero = numero;
            this.estado = estado;
            this.cliente = cliente;
        }

        public Long getId() { return id; }
        public String getNumero() { return numero; }
        public String getEstado() { return estado; }
        public String getCliente() { return cliente; }
    }
}