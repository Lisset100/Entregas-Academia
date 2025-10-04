package com.cine.AppGestionCine.clientes;

import com.cine.AppGestionCine.clientes.dto.ActualizarClienteRequest;
import com.cine.AppGestionCine.clientes.dto.ClienteResponse;
import com.cine.AppGestionCine.clientes.dto.CrearClienteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    // ════════════════════════════════════════════════════════
    // CREAR CLIENTE
    // ════════════════════════════════════════════════════════

    @PostMapping
    public ResponseEntity<?> crearCliente(
            @Valid @RequestBody CrearClienteRequest request) {
        //  ↑
        // @Valid activa las validaciones del DTO
        // Si falla, lanza MethodArgumentNotValidException

        try {
            log.info("Creando cliente: {}", request.getEmail());

            Cliente cliente = clienteService.crearCliente(request);
            ClienteResponse response = ClienteResponse.fromEntity(cliente);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error al crear cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════
    // LISTAR CLIENTES
    // ════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAllClientes() {
        log.info("Obteniendo todos los clientes");

        List<ClienteResponse> clientes = clienteService.getAllClientes()
                .stream()
                .map(ClienteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ClienteResponse>> getClientesActivos() {
        log.info("Obteniendo clientes activos");

        List<ClienteResponse> clientes = clienteService.getClientesActivos()
                .stream()
                .map(ClienteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientes);
    }

    // ════════════════════════════════════════════════════════
    // OBTENER POR ID
    // ════════════════════════════════════════════════════════

    @GetMapping("/{id}")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        log.info("Obteniendo cliente ID: {}", id);

        Optional<Cliente> cliente = clienteService.getClienteById(id);

        if (cliente.isPresent()) {
            ClienteResponse response = ClienteResponse.fromEntity(cliente.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getClienteByEmail(@PathVariable String email) {
        log.info("Obteniendo cliente por email: {}", email);

        Optional<Cliente> cliente = clienteService.getClienteByEmail(email);

        if (cliente.isPresent()) {
            ClienteResponse response = ClienteResponse.fromEntity(cliente.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ════════════════════════════════════════════════════════
    // BUSCAR CLIENTES
    // ════════════════════════════════════════════════════════

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscarClientes(
            @RequestParam String q) {
        log.info("Buscando clientes: {}", q);

        List<ClienteResponse> clientes = clienteService.buscarClientes(q)
                .stream()
                .map(ClienteResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientes);
    }

    // ════════════════════════════════════════════════════════
    // ACTUALIZAR CLIENTE
    // ════════════════════════════════════════════════════════

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {

        try {
            log.info("Actualizando cliente ID: {}", id);

            Cliente cliente = clienteService.actualizarCliente(id, request);
            ClienteResponse response = ClienteResponse.fromEntity(cliente);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al actualizar cliente {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════
    // CAMBIAR ESTADO
    // ════════════════════════════════════════════════════════

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activarCliente(@PathVariable Long id) {
        try {
            clienteService.activarCliente(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente activado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id) {
        try {
            clienteService.desactivarCliente(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente desactivado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════
    // ELIMINAR (Soft Delete)
    // ════════════════════════════════════════════════════════

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{id}/reservas")
    public ResponseEntity<?> getReservasCliente(@PathVariable Long id) {
        try {
            log.info("Obteniendo reservas del cliente {}", id);

            // Verificar que el cliente existe
            Optional<Cliente> clienteOpt = clienteService.getClienteById(id);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Obtener reservas del cliente
            // Nota: Necesitamos inyectar AsientoService (ver abajo)
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Endpoint disponible. Requiere integración con AsientoService"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    // ════════════════════════════════════════════════════════
    // MANEJO DE ERRORES DE VALIDACIÓN
    // ════════════════════════════════════════════════════════

    /**
     * Maneja errores de validación (@Valid)
     * Retorna HTTP 400 con detalles de qué campos fallaron
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        // Extraer todos los errores de validación
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            errores.put(nombreCampo, mensajeError);
        });

        log.warn("Errores de validación: {}", errores);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validación fallida");
        response.put("detalles", errores);

        return ResponseEntity.badRequest().body(response);
    }
}