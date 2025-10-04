package com.cine.AppGestionCine.asientos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false"
})
class AsientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AsientoService asientoService;

    @Test
    @DisplayName("GET /api/asientos/funcion/{id} debe retornar asientos de la función")
    void cuandoGetAsientosPorFuncion_debeRetornarLista() throws Exception {
        Long funcionId = 1L;

        Asiento asiento1 = new Asiento();
        asiento1.setId(1L);
        asiento1.setNumeroAsiento("A1");
        asiento1.setEstado(EstadoAsiento.LIBRE);

        Asiento asiento2 = new Asiento();
        asiento2.setId(2L);
        asiento2.setNumeroAsiento("A2");
        asiento2.setEstado(EstadoAsiento.RESERVADO);
        asiento2.setClienteEmail("juan@example.com");

        List<Asiento> asientos = Arrays.asList(asiento1, asiento2);

        when(asientoService.getAsientosPorFuncion(funcionId))
                .thenReturn(asientos);

        mockMvc.perform(get("/api/asientos/funcion/{funcionId}", funcionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroAsiento", is("A1")))
                .andExpect(jsonPath("$[0].estado", is("LIBRE")))
                .andExpect(jsonPath("$[1].numeroAsiento", is("A2")))
                .andExpect(jsonPath("$[1].estado", is("RESERVADO")))
                .andExpect(jsonPath("$[1].clienteEmail", is("juan@example.com")));

        verify(asientoService, times(1)).getAsientosPorFuncion(funcionId);
    }

    @Test
    @DisplayName("GET /api/asientos/funcion/{id}/disponibles debe retornar solo libres")
    void cuandoGetAsientosDisponibles_debeRetornarSoloLibres() throws Exception {
        Long funcionId = 1L;

        Asiento asiento1 = new Asiento();
        asiento1.setId(1L);
        asiento1.setNumeroAsiento("A1");
        asiento1.setEstado(EstadoAsiento.LIBRE);

        Asiento asiento2 = new Asiento();
        asiento2.setId(3L);
        asiento2.setNumeroAsiento("A3");
        asiento2.setEstado(EstadoAsiento.LIBRE);

        List<Asiento> disponibles = Arrays.asList(asiento1, asiento2);

        when(asientoService.getAsientosDisponibles(funcionId))
                .thenReturn(disponibles);

        mockMvc.perform(get("/api/asientos/funcion/{funcionId}/disponibles", funcionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].estado", everyItem(is("LIBRE"))));

        verify(asientoService, times(1)).getAsientosDisponibles(funcionId);
    }

    @Test
    @DisplayName("POST /api/asientos/reservar debe reservar exitosamente")
    void cuandoReservarAsiento_datosValidos_debeReservar() throws Exception {
        AsientoController.ReservarAsientoRequest request = new AsientoController.ReservarAsientoRequest();
        request.setFuncionId(1L);
        request.setNumeroAsiento("A5");
        request.setClienteEmail("maria@example.com");

        Asiento asientoReservado = new Asiento();
        asientoReservado.setId(5L);
        asientoReservado.setFuncionId(1L);
        asientoReservado.setNumeroAsiento("A5");
        asientoReservado.setEstado(EstadoAsiento.RESERVADO);
        asientoReservado.setClienteEmail("maria@example.com");

        when(asientoService.reservarAsiento(anyLong(), anyString(), anyString()))
                .thenReturn(asientoReservado);

        mockMvc.perform(post("/api/asientos/reservar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.numeroAsiento", is("A5")))
                .andExpect(jsonPath("$.estado", is("RESERVADO")))
                .andExpect(jsonPath("$.clienteEmail", is("maria@example.com")));

        verify(asientoService, times(1)).reservarAsiento(1L, "A5", "maria@example.com");
    }

    @Test
    @DisplayName("POST /api/asientos/reservar debe retornar 400 si asiento no disponible")
    void cuandoReservarAsiento_noDisponible_debeRetornar400() throws Exception {
        AsientoController.ReservarAsientoRequest request = new AsientoController.ReservarAsientoRequest();
        request.setFuncionId(1L);
        request.setNumeroAsiento("A5");
        request.setClienteEmail("maria@example.com");

        when(asientoService.reservarAsiento(anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("El asiento A5 no está disponible"));

        mockMvc.perform(post("/api/asientos/reservar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error")));

        verify(asientoService, times(1)).reservarAsiento(1L, "A5", "maria@example.com");
    }

    @Test
    @DisplayName("PUT /api/asientos/{id}/cancelar debe cancelar reserva")
    void cuandoCancelarReserva_debeRetornarExito() throws Exception {
        Long asientoId = 5L;

        doNothing().when(asientoService).cancelarReserva(asientoId);

        mockMvc.perform(put("/api/asientos/{id}/cancelar", asientoId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("cancelada exitosamente")));

        verify(asientoService, times(1)).cancelarReserva(asientoId);
    }

    @Test
    @DisplayName("PUT /api/asientos/{id}/cancelar debe manejar errores")
    void cuandoCancelarReserva_asientoNoReservado_debeRetornar400() throws Exception {
        Long asientoId = 5L;

        doThrow(new RuntimeException("El asiento no está reservado"))
                .when(asientoService).cancelarReserva(asientoId);

        mockMvc.perform(put("/api/asientos/{id}/cancelar", asientoId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error")));

        verify(asientoService, times(1)).cancelarReserva(asientoId);
    }

    @Test
    @DisplayName("POST /api/asientos/generar debe generar asientos")
    void cuandoGenerarAsientos_debeRetornarExito() throws Exception {
        AsientoController.GenerarAsientosRequest request = new AsientoController.GenerarAsientosRequest();
        request.setFuncionId(1L);
        request.setTotalAsientos(50);

        doNothing().when(asientoService).generarAsientosPorFuncion(anyLong(), anyInt());

        mockMvc.perform(post("/api/asientos/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("generados exitosamente")));

        verify(asientoService, times(1)).generarAsientosPorFuncion(1L, 50);
    }
}