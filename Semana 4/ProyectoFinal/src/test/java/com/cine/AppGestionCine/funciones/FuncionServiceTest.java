package com.cine.AppGestionCine.funciones;

import com.cine.AppGestionCine.funciones.events.FuncionCanceladaEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para FuncionService
 *
 * ¿Qué estamos probando?
 * - Lógica de negocio del servicio
 * - Sin usar base de datos real
 * - Simulando dependencias con Mockito
 */
class FuncionServiceTest {

    // @Mock = Crear un objeto "falso" para simular dependencias
    @Mock
    private FuncionRepository funcionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    // @InjectMocks = Inyectar los mocks en el servicio que queremos probar
    @InjectMocks
    private FuncionService funcionService;

    /**
     * @BeforeEach = Este método se ejecuta ANTES de cada test
     * Es como "preparar la mesa" antes de cocinar
     */
    @BeforeEach
    void setUp() {
        // Inicializar Mockito para que funcionen las anotaciones
        MockitoAnnotations.openMocks(this);
    }

    // ============================================
    // TEST 1: Crear Función Exitosamente
    // ============================================

    @Test
    @DisplayName("Debe crear función correctamente con todos los datos válidos")
    void cuandoCrearFuncion_conDatosValidos_debeCrearExitosamente() {
        // ──────────────────────────────────────────────────────────
        // PATRÓN AAA (Arrange-Act-Assert)
        // ──────────────────────────────────────────────────────────

        // 1. ARRANGE (Preparar) - Preparar datos de prueba
        String pelicula = "Avengers: Endgame";
        LocalDateTime fechaHora = LocalDateTime.of(2025, 9, 28, 20, 0);
        String sala = "Sala 1";
        Integer totalAsientos = 50;
        Double precio = 150.0;

        // Crear función esperada (lo que debería retornar el repository)
        Funcion funcionEsperada = new Funcion(pelicula, fechaHora, sala, totalAsientos, precio);
        funcionEsperada.setId(1L);

        // Configurar el comportamiento del mock
        // "Cuando llamen a save(), retorna funcionEsperada"
        when(funcionRepository.save(any(Funcion.class))).thenReturn(funcionEsperada);

        // 2. ACT (Actuar) - Ejecutar el método que queremos probar
        Funcion resultado = funcionService.crearFuncion(pelicula, fechaHora, sala, totalAsientos, precio);

        // 3. ASSERT (Afirmar) - Verificar que todo salió como esperábamos
        assertNotNull(resultado, "La función creada no debe ser null");
        assertEquals(1L, resultado.getId(), "El ID debe ser 1");
        assertEquals(pelicula, resultado.getPelicula(), "La película debe coincidir");
        assertEquals(totalAsientos, resultado.getTotalAsientos(), "Total asientos debe coincidir");
        assertEquals(totalAsientos, resultado.getAsientosDisponibles(), "Asientos disponibles debe ser igual al total");
        assertEquals(EstadoFuncion.EN_CARTELERA, resultado.getEstado(), "Estado debe ser EN_CARTELERA");

        // Verificar que el repository fue llamado exactamente 1 vez
        verify(funcionRepository, times(1)).save(any(Funcion.class));
    }

    // ============================================
    // TEST 2: Cancelar Función Exitosamente
    // ============================================

    @Test
    @DisplayName("Debe cancelar función y publicar evento correctamente")
    void cuandoCancelarFuncion_conIdValido_debeCancelarYPublicarEvento() {
        // ARRANGE
        Long funcionId = 1L;
        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setPelicula("Spider-Man");
        funcion.setSala("Sala 2");
        funcion.setEstado(EstadoFuncion.EN_CARTELERA);
        funcion.setAsientosDisponibles(50);

        // Mock: cuando busquen la función, retornarla
        when(funcionRepository.findById(funcionId)).thenReturn(Optional.of(funcion));
        // Mock: cuando guarden, retornar la función modificada
        when(funcionRepository.save(any(Funcion.class))).thenReturn(funcion);

        // ACT
        funcionService.cancelarFuncion(funcionId);

        // ASSERT
        assertEquals(EstadoFuncion.CANCELADA, funcion.getEstado(), "Estado debe cambiar a CANCELADA");
        assertEquals(0, funcion.getAsientosDisponibles(), "Asientos disponibles debe ser 0");

        // Verificar que se guardó en BD
        verify(funcionRepository, times(1)).save(funcion);

        // Verificar que se publicó el evento
        verify(eventPublisher, times(1)).publishEvent(any(FuncionCanceladaEvent.class));
    }

    // ============================================
    // TEST 3: Error al Cancelar Función Inexistente
    // ============================================

    @Test
    @DisplayName("Debe lanzar excepción cuando se intenta cancelar función inexistente")
    void cuandoCancelarFuncion_conIdInexistente_debeLanzarExcepcion() {
        // ARRANGE
        Long funcionIdInexistente = 999L;

        // Mock: cuando busquen la función, retornar vacío (no existe)
        when(funcionRepository.findById(funcionIdInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Verificar que se lanza una excepción
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> funcionService.cancelarFuncion(funcionIdInexistente),
                "Debe lanzar RuntimeException"
        );

        // Verificar el mensaje de la excepción
        assertTrue(
                exception.getMessage().contains("no encontrada"),
                "El mensaje debe indicar que no se encontró la función"
        );

        // Verificar que NO se intentó guardar nada
        verify(funcionRepository, never()).save(any(Funcion.class));

        // Verificar que NO se publicó evento
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ============================================
    // TEST 4: Error al Cancelar Función Ya Cancelada
    // ============================================

    @Test
    @DisplayName("Debe lanzar excepción cuando se intenta cancelar función ya cancelada")
    void cuandoCancelarFuncion_yaEstaCancelada_debeLanzarExcepcion() {
        // ARRANGE
        Long funcionId = 1L;
        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setEstado(EstadoFuncion.CANCELADA); // Ya está cancelada

        when(funcionRepository.findById(funcionId)).thenReturn(Optional.of(funcion));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> funcionService.cancelarFuncion(funcionId),
                "Debe lanzar RuntimeException"
        );

        assertTrue(
                exception.getMessage().contains("ya está cancelada"),
                "El mensaje debe indicar que ya está cancelada"
        );

        // No debe intentar guardar ni publicar evento
        verify(funcionRepository, never()).save(any(Funcion.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ============================================
    // TEST 5: Actualizar Asientos Disponibles
    // ============================================

    @Test
    @DisplayName("Debe actualizar asientos disponibles correctamente al decrementar")
    void cuandoActualizarAsientos_decrementar_debeActualizarCorrectamente() {
        // ARRANGE
        Long funcionId = 1L;
        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setAsientosDisponibles(50);

        when(funcionRepository.findById(funcionId)).thenReturn(Optional.of(funcion));
        when(funcionRepository.save(any(Funcion.class))).thenReturn(funcion);

        // ACT
        funcionService.actualizarAsientosDisponibles(funcionId, -1); // Decrementar 1

        // ASSERT
        assertEquals(49, funcion.getAsientosDisponibles(), "Debe decrementar a 49");
        verify(funcionRepository, times(1)).save(funcion);
    }

    @Test
    @DisplayName("Debe actualizar asientos disponibles correctamente al incrementar")
    void cuandoActualizarAsientos_incrementar_debeActualizarCorrectamente() {
        // ARRANGE
        Long funcionId = 1L;
        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setAsientosDisponibles(48);

        when(funcionRepository.findById(funcionId)).thenReturn(Optional.of(funcion));
        when(funcionRepository.save(any(Funcion.class))).thenReturn(funcion);

        // ACT
        funcionService.actualizarAsientosDisponibles(funcionId, 1); // Incrementar 1

        // ASSERT
        assertEquals(49, funcion.getAsientosDisponibles(), "Debe incrementar a 49");
        verify(funcionRepository, times(1)).save(funcion);
    }

    @Test
    @DisplayName("No debe permitir asientos disponibles negativos")
    void cuandoActualizarAsientos_resultadoNegativo_noDebeActualizar() {
        // ARRANGE
        Long funcionId = 1L;
        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setAsientosDisponibles(1); // Solo 1 disponible

        when(funcionRepository.findById(funcionId)).thenReturn(Optional.of(funcion));

        // ACT
        funcionService.actualizarAsientosDisponibles(funcionId, -5); // Intentar decrementar 5

        // ASSERT
        assertEquals(1, funcion.getAsientosDisponibles(), "No debe cambiar si quedaría negativo");
        verify(funcionRepository, never()).save(any(Funcion.class));
    }
}