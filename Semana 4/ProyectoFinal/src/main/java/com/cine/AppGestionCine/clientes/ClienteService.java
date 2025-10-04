package com.cine.AppGestionCine.clientes;

import com.cine.AppGestionCine.clientes.dto.ActualizarClienteRequest;
import com.cine.AppGestionCine.clientes.dto.CrearClienteRequest;
import com.cine.AppGestionCine.clientes.events.ClienteRegistradoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de gestión de clientes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ApplicationEventPublisher eventPublisher;

    // ════════════════════════════════════════════════════════
    // CREAR CLIENTE
    // ════════════════════════════════════════════════════════

    /**
     * Registrar nuevo cliente
     */
    public Cliente crearCliente(CrearClienteRequest request) {
        log.info("Creando cliente: {} {}", request.getNombre(), request.getApellido());

        // Validar que el email no exista
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + request.getEmail());
        }

        // Crear entidad desde DTO
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setEstado(EstadoCliente.ACTIVO);

        // Guardar
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // Publicar evento
        ClienteRegistradoEvent evento = new ClienteRegistradoEvent(
                clienteGuardado.getId(),
                clienteGuardado.getNombre() + " " + clienteGuardado.getApellido(),
                clienteGuardado.getEmail()
        );

        log.info("Publicando evento ClienteRegistradoEvent para cliente {}", clienteGuardado.getId());
        eventPublisher.publishEvent(evento);

        log.info("Cliente creado exitosamente con ID: {}", clienteGuardado.getId());

        return clienteGuardado;
    }

    // ════════════════════════════════════════════════════════
    // ACTUALIZAR CLIENTE
    // ════════════════════════════════════════════════════════

    /**
     * Actualizar datos de cliente
     * Solo actualiza campos que no sean null en el request
     */
    public Cliente actualizarCliente(Long id, ActualizarClienteRequest request) {
        log.info("Actualizando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        // Actualizar solo campos no nulos
        if (request.getNombre() != null) {
            cliente.setNombre(request.getNombre());
        }

        if (request.getApellido() != null) {
            cliente.setApellido(request.getApellido());
        }

        if (request.getEmail() != null) {
            // Verificar que el nuevo email no esté en uso por otro cliente
            Optional<Cliente> clienteConEmail = clienteRepository.findByEmail(request.getEmail());
            if (clienteConEmail.isPresent() && !clienteConEmail.get().getId().equals(id)) {
                throw new RuntimeException("El email ya está en uso por otro cliente");
            }
            cliente.setEmail(request.getEmail());
        }

        if (request.getTelefono() != null) {
            cliente.setTelefono(request.getTelefono());
        }

        Cliente actualizado = clienteRepository.save(cliente);

        log.info("Cliente {} actualizado exitosamente", id);

        return actualizado;
    }

    // ════════════════════════════════════════════════════════
    // CAMBIAR ESTADO
    // ════════════════════════════════════════════════════════

    /**
     * Activar cliente
     */
    public void activarCliente(Long id) {
        log.info("Activando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        cliente.setEstado(EstadoCliente.ACTIVO);
        clienteRepository.save(cliente);

        log.info("Cliente {} activado", id);
    }

    /**
     * Desactivar cliente
     */
    public void desactivarCliente(Long id) {
        log.info("Desactivando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        cliente.setEstado(EstadoCliente.INACTIVO);
        clienteRepository.save(cliente);

        log.info("Cliente {} desactivado", id);
    }

    // ════════════════════════════════════════════════════════
    // CONSULTAS
    // ════════════════════════════════════════════════════════

    /**
     * Obtener todos los clientes
     */
    @Transactional(readOnly = true)
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Obtener cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Obtener cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Obtener clientes activos
     */
    @Transactional(readOnly = true)
    public List<Cliente> getClientesActivos() {
        return clienteRepository.findByEstado(EstadoCliente.ACTIVO);
    }

    /**
     * Buscar clientes por nombre o apellido
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarClientes(String busqueda) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                busqueda,
                busqueda
        );
    }

    /**
     * Eliminar cliente (soft delete - solo desactiva)
     */
    public void eliminarCliente(Long id) {
        log.info("Eliminando (desactivando) cliente ID: {}", id);
        desactivarCliente(id);
    }
}