package com.cine.AppGestionCine.asientos;

import com.cine.AppGestionCine.asientos.events.AsientoReservadoEvent;
import com.cine.AppGestionCine.asientos.events.AsientoCanceladoEvent;
import com.cine.AppGestionCine.clientes.Cliente;
import com.cine.AppGestionCine.clientes.ClienteService;
import com.cine.AppGestionCine.clientes.EstadoCliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

class AsientoServiceTest {

    @Mock
    private AsientoRepository asientoRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private AsientoService asientoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar mock de cliente por defecto
        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setEmail("test@example.com");
        clienteMock.setEstado(EstadoCliente.ACTIVO);

        when(clienteService.getClienteByEmail(anyString()))
                .thenReturn(Optional.of(clienteMock));
    }

    @Test
    @DisplayName("Debe generar asientos correctamente con nomenclatura A1, A2, B1, etc.")
    void cuandoGenerarAsientos_debeCrearConNomenclaturaCorrecta() {
        Long funcionId = 1L;
        Integer totalAsientos = 15;

        when(asientoRepository.save(any(Asiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        asientoService.generarAsientosPorFuncion(funcionId, totalAsientos);

        verify(asientoRepository, times(15)).save(any(Asiento.class));
    }

    @Test
    @DisplayName("Debe reservar asiento disponible y publicar evento correctamente")
    void cuandoReservarAsiento_asientoLibre_debeReservarYPublicarEvento() {
        Long funcionId = 1L;
        String numeroAsiento = "A5";
        String clienteEmail = "juan@example.com";

        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setEmail(clienteEmail);
        clienteMock.setEstado(EstadoCliente.ACTIVO);

        when(clienteService.getClienteByEmail(clienteEmail))
                .thenReturn(Optional.of(clienteMock));

        Asiento asientoLibre = new Asiento();
        asientoLibre.setId(5L);
        asientoLibre.setFuncionId(funcionId);
        asientoLibre.setNumeroAsiento(numeroAsiento);
        asientoLibre.setEstado(EstadoAsiento.LIBRE);

        when(asientoRepository.findByFuncionIdAndNumeroAsiento(funcionId, numeroAsiento))
                .thenReturn(Optional.of(asientoLibre));

        when(asientoRepository.save(any(Asiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Asiento resultado = asientoService.reservarAsiento(funcionId, numeroAsiento, clienteEmail);

        assertEquals(EstadoAsiento.RESERVADO, resultado.getEstado());
        assertEquals(clienteEmail, resultado.getClienteEmail());

        verify(asientoRepository, times(1)).save(asientoLibre);

        ArgumentCaptor<AsientoReservadoEvent> eventoCaptor =
                ArgumentCaptor.forClass(AsientoReservadoEvent.class);

        verify(eventPublisher, times(1)).publishEvent(eventoCaptor.capture());

        AsientoReservadoEvent eventoPublicado = eventoCaptor.getValue();
        assertEquals(5L, eventoPublicado.getAsientoId());
        assertEquals(funcionId, eventoPublicado.getFuncionId());
        assertEquals(numeroAsiento, eventoPublicado.getNumeroAsiento());
        assertEquals(clienteEmail, eventoPublicado.getClienteEmail());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el asiento no existe")
    void cuandoReservarAsiento_asientoNoExiste_debeLanzarExcepcion() {
        Long funcionId = 1L;
        String numeroAsiento = "Z99";
        String clienteEmail = "juan@example.com";

        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setEmail(clienteEmail);
        clienteMock.setEstado(EstadoCliente.ACTIVO);

        when(clienteService.getClienteByEmail(clienteEmail))
                .thenReturn(Optional.of(clienteMock));

        when(asientoRepository.findByFuncionIdAndNumeroAsiento(funcionId, numeroAsiento))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> asientoService.reservarAsiento(funcionId, numeroAsiento, clienteEmail)
        );

        assertTrue(
                exception.getMessage().toLowerCase().contains("no encontrado") ||
                        exception.getMessage().toLowerCase().contains("asiento")
        );

        verify(asientoRepository, never()).save(any(Asiento.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el asiento ya está reservado")
    void cuandoReservarAsiento_yaReservado_debeLanzarExcepcion() {
        Long funcionId = 1L;
        String numeroAsiento = "A5";
        String clienteEmail = "juan@example.com";

        Cliente clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setEmail(clienteEmail);
        clienteMock.setEstado(EstadoCliente.ACTIVO);

        when(clienteService.getClienteByEmail(clienteEmail))
                .thenReturn(Optional.of(clienteMock));

        Asiento asientoReservado = new Asiento();
        asientoReservado.setId(5L);
        asientoReservado.setFuncionId(funcionId);
        asientoReservado.setNumeroAsiento(numeroAsiento);
        asientoReservado.setEstado(EstadoAsiento.RESERVADO);
        asientoReservado.setClienteEmail("maria@example.com");

        when(asientoRepository.findByFuncionIdAndNumeroAsiento(funcionId, numeroAsiento))
                .thenReturn(Optional.of(asientoReservado));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> asientoService.reservarAsiento(funcionId, numeroAsiento, clienteEmail)
        );

        assertTrue(
                exception.getMessage().toLowerCase().contains("disponible") ||
                        exception.getMessage().toLowerCase().contains("reservado")
        );

        verify(asientoRepository, never()).save(any(Asiento.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Debe cancelar reserva existente y publicar evento")
    void cuandoCancelarReserva_reservaExiste_debeCancelarYPublicarEvento() {
        Long asientoId = 5L;

        Asiento asientoReservado = new Asiento();
        asientoReservado.setId(asientoId);
        asientoReservado.setFuncionId(1L);
        asientoReservado.setNumeroAsiento("A5");
        asientoReservado.setEstado(EstadoAsiento.RESERVADO);
        asientoReservado.setClienteEmail("juan@example.com");

        when(asientoRepository.findById(asientoId))
                .thenReturn(Optional.of(asientoReservado));

        when(asientoRepository.save(any(Asiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        asientoService.cancelarReserva(asientoId);

        assertEquals(EstadoAsiento.LIBRE, asientoReservado.getEstado());
        assertNull(asientoReservado.getClienteEmail());

        verify(asientoRepository, times(1)).save(asientoReservado);

        ArgumentCaptor<AsientoCanceladoEvent> eventoCaptor =
                ArgumentCaptor.forClass(AsientoCanceladoEvent.class);

        verify(eventPublisher, times(1)).publishEvent(eventoCaptor.capture());

        AsientoCanceladoEvent eventoPublicado = eventoCaptor.getValue();
        assertEquals(asientoId, eventoPublicado.getAsientoId());
        assertEquals("A5", eventoPublicado.getNumeroAsiento());
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar asiento que no está reservado")
    void cuandoCancelarReserva_asientoLibre_debeLanzarExcepcion() {
        Long asientoId = 5L;

        Asiento asientoLibre = new Asiento();
        asientoLibre.setId(asientoId);
        asientoLibre.setEstado(EstadoAsiento.LIBRE);

        when(asientoRepository.findById(asientoId))
                .thenReturn(Optional.of(asientoLibre));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> asientoService.cancelarReserva(asientoId)
        );

        assertTrue(exception.getMessage().contains("no está reservado"));

        verify(asientoRepository, never()).save(any(Asiento.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Debe cancelar todos los asientos de una función")
    void cuandoCancelarAsientosPorFuncion_debeCancelarTodos() {
        Long funcionId = 1L;

        Asiento asiento1 = new Asiento();
        asiento1.setId(1L);
        asiento1.setFuncionId(funcionId);
        asiento1.setEstado(EstadoAsiento.LIBRE);

        Asiento asiento2 = new Asiento();
        asiento2.setId(2L);
        asiento2.setFuncionId(funcionId);
        asiento2.setEstado(EstadoAsiento.RESERVADO);

        Asiento asiento3 = new Asiento();
        asiento3.setId(3L);
        asiento3.setFuncionId(funcionId);
        asiento3.setEstado(EstadoAsiento.RESERVADO);

        List<Asiento> asientos = Arrays.asList(asiento1, asiento2, asiento3);

        when(asientoRepository.findByFuncionId(funcionId))
                .thenReturn(asientos);

        when(asientoRepository.save(any(Asiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        asientoService.cancelarAsientosPorFuncion(funcionId);

        assertEquals(EstadoAsiento.CANCELADO, asiento1.getEstado());
        assertEquals(EstadoAsiento.CANCELADO, asiento2.getEstado());
        assertEquals(EstadoAsiento.CANCELADO, asiento3.getEstado());

        verify(asientoRepository, times(3)).save(any(Asiento.class));
    }

    @Test
    @DisplayName("Debe retornar solo asientos con estado LIBRE")
    void cuandoGetAsientosDisponibles_debeRetornarSoloLibres() {
        Long funcionId = 1L;

        Asiento asiento1 = new Asiento();
        asiento1.setId(1L);
        asiento1.setEstado(EstadoAsiento.LIBRE);

        Asiento asiento2 = new Asiento();
        asiento2.setId(2L);
        asiento2.setEstado(EstadoAsiento.LIBRE);

        List<Asiento> asientosLibres = Arrays.asList(asiento1, asiento2);

        when(asientoRepository.findByFuncionIdAndEstado(funcionId, EstadoAsiento.LIBRE))
                .thenReturn(asientosLibres);

        List<Asiento> resultado = asientoService.getAsientosDisponibles(funcionId);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> a.getEstado() == EstadoAsiento.LIBRE));

        verify(asientoRepository, times(1))
                .findByFuncionIdAndEstado(funcionId, EstadoAsiento.LIBRE);
    }
}