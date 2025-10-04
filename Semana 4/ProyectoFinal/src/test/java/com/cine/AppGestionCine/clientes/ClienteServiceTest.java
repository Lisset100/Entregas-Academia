package com.cine.AppGestionCine.clientes;

import com.cine.AppGestionCine.clientes.dto.ActualizarClienteRequest;
import com.cine.AppGestionCine.clientes.dto.CrearClienteRequest;
import com.cine.AppGestionCine.clientes.events.ClienteRegistradoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debe crear cliente correctamente y publicar evento")
    void cuandoCrearCliente_datosValidos_debeCrearYPublicarEvento() {
        // ARRANGE
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setEmail("juan@example.com");
        request.setTelefono("5551234567");

        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocation -> {
                    Cliente cliente = invocation.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        // ACT
        Cliente resultado = clienteService.crearCliente(request);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Pérez", resultado.getApellido());
        assertEquals("juan@example.com", resultado.getEmail());
        assertEquals("5551234567", resultado.getTelefono());
        assertEquals(EstadoCliente.ACTIVO, resultado.getEstado());

        verify(clienteRepository, times(1)).existsByEmail("juan@example.com");
        verify(clienteRepository, times(1)).save(any(Cliente.class));

        ArgumentCaptor<ClienteRegistradoEvent> eventoCaptor =
                ArgumentCaptor.forClass(ClienteRegistradoEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventoCaptor.capture());

        ClienteRegistradoEvent evento = eventoCaptor.getValue();
        assertEquals(1L, evento.getClienteId());
        assertEquals("juan@example.com", evento.getEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción si email ya existe")
    void cuandoCrearCliente_emailExiste_debeLanzarExcepcion() {
        // ARRANGE
        CrearClienteRequest request = new CrearClienteRequest();
        request.setEmail("existente@example.com");

        when(clienteRepository.existsByEmail("existente@example.com"))
                .thenReturn(true);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clienteService.crearCliente(request)
        );

        assertTrue(exception.getMessage().contains("Ya existe un cliente"));
        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Debe actualizar cliente correctamente")
    void cuandoActualizarCliente_datosValidos_debeActualizar() {
        // ARRANGE
        Long clienteId = 1L;

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(clienteId);
        clienteExistente.setNombre("Juan");
        clienteExistente.setEmail("juan@example.com");

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActualizarClienteRequest request = new ActualizarClienteRequest();
        request.setNombre("Juan Carlos");
        request.setTelefono("5559876543");

        // ACT
        Cliente resultado = clienteService.actualizarCliente(clienteId, request);

        // ASSERT
        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("5559876543", resultado.getTelefono());
        verify(clienteRepository, times(1)).save(clienteExistente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar cliente inexistente")
    void cuandoActualizarCliente_noExiste_debeLanzarExcepcion() {
        // ARRANGE
        Long clienteId = 999L;
        ActualizarClienteRequest request = new ActualizarClienteRequest();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> clienteService.actualizarCliente(clienteId, request)
        );

        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe activar cliente correctamente")
    void cuandoActivarCliente_debeActivar() {
        // ARRANGE
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setEstado(EstadoCliente.INACTIVO);

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        clienteService.activarCliente(clienteId);

        // ASSERT
        assertEquals(EstadoCliente.ACTIVO, cliente.getEstado());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Debe desactivar cliente correctamente")
    void cuandoDesactivarCliente_debeDesactivar() {
        // ARRANGE
        Long clienteId = 1L;

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setEstado(EstadoCliente.ACTIVO);

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        clienteService.desactivarCliente(clienteId);

        // ASSERT
        assertEquals(EstadoCliente.INACTIVO, cliente.getEstado());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Debe obtener todos los clientes")
    void cuandoGetAllClientes_debeRetornarLista() {
        // ARRANGE
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombre("Juan");

        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("María");

        when(clienteRepository.findAll())
                .thenReturn(Arrays.asList(cliente1, cliente2));

        // ACT
        List<Cliente> resultado = clienteService.getAllClientes();

        // ASSERT
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener cliente por ID")
    void cuandoGetClienteById_existe_debeRetornar() {
        // ARRANGE
        Long clienteId = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        // ACT
        Optional<Cliente> resultado = clienteService.getClienteById(clienteId);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(clienteId, resultado.get().getId());
    }

    @Test
    @DisplayName("Debe obtener cliente por email")
    void cuandoGetClienteByEmail_existe_debeRetornar() {
        // ARRANGE
        String email = "test@example.com";
        Cliente cliente = new Cliente();
        cliente.setEmail(email);

        when(clienteRepository.findByEmail(email))
                .thenReturn(Optional.of(cliente));

        // ACT
        Optional<Cliente> resultado = clienteService.getClienteByEmail(email);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(email, resultado.get().getEmail());
    }

    @Test
    @DisplayName("Debe buscar clientes por nombre")
    void cuandoBuscarClientes_debeRetornarCoincidencias() {
        // ARRANGE
        String busqueda = "Juan";

        Cliente cliente1 = new Cliente();
        cliente1.setNombre("Juan");

        Cliente cliente2 = new Cliente();
        cliente2.setApellido("Juanez");

        when(clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                busqueda, busqueda))
                .thenReturn(Arrays.asList(cliente1, cliente2));

        // ACT
        List<Cliente> resultado = clienteService.buscarClientes(busqueda);

        // ASSERT
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Debe obtener clientes activos")
    void cuandoGetClientesActivos_debeRetornarSoloActivos() {
        // ARRANGE
        Cliente cliente1 = new Cliente();
        cliente1.setEstado(EstadoCliente.ACTIVO);

        Cliente cliente2 = new Cliente();
        cliente2.setEstado(EstadoCliente.ACTIVO);

        when(clienteRepository.findByEstado(EstadoCliente.ACTIVO))
                .thenReturn(Arrays.asList(cliente1, cliente2));

        // ACT
        List<Cliente> resultado = clienteService.getClientesActivos();

        // ASSERT
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream()
                .allMatch(c -> c.getEstado() == EstadoCliente.ACTIVO));
    }
}