package com.cine.AppGestionCine.funciones;

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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.batch.job.enabled=false"
})
class FuncionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FuncionService funcionService;

    @Test
    @DisplayName("GET /api/funciones debe retornar lista de funciones")
    void cuandoGetFunciones_debeRetornarListaExitosamente() throws Exception {
        Funcion funcion1 = new Funcion();
        funcion1.setId(1L);
        funcion1.setPelicula("Avengers");
        funcion1.setAsientosDisponibles(50);
        funcion1.setEstado(EstadoFuncion.EN_CARTELERA);

        Funcion funcion2 = new Funcion();
        funcion2.setId(2L);
        funcion2.setPelicula("Spider-Man");
        funcion2.setAsientosDisponibles(30);
        funcion2.setEstado(EstadoFuncion.EN_CARTELERA);

        List<Funcion> funciones = Arrays.asList(funcion1, funcion2);

        when(funcionService.getFuncionesEnCartelera()).thenReturn(funciones);

        mockMvc.perform(get("/api/funciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].pelicula", is("Avengers")))
                .andExpect(jsonPath("$[1].pelicula", is("Spider-Man")));

        verify(funcionService, times(1)).getFuncionesEnCartelera();
    }

    @Test
    @DisplayName("GET /api/funciones/{id} debe retornar función específica")
    void cuandoGetFuncionById_funcionExiste_debeRetornarFuncion() throws Exception {
        Long funcionId = 1L;

        Funcion funcion = new Funcion();
        funcion.setId(funcionId);
        funcion.setPelicula("The Batman");
        funcion.setSala("Sala IMAX");
        funcion.setPrecio(180.0);

        when(funcionService.getFuncionById(funcionId))
                .thenReturn(Optional.of(funcion));

        mockMvc.perform(get("/api/funciones/{id}", funcionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pelicula", is("The Batman")))
                .andExpect(jsonPath("$.sala", is("Sala IMAX")))
                .andExpect(jsonPath("$.precio", is(180.0)));

        verify(funcionService, times(1)).getFuncionById(funcionId);
    }

    @Test
    @DisplayName("GET /api/funciones/{id} debe retornar 404 si no existe")
    void cuandoGetFuncionById_funcionNoExiste_debeRetornar404() throws Exception {
        Long funcionId = 999L;

        when(funcionService.getFuncionById(funcionId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/funciones/{id}", funcionId))
                .andExpect(status().isNotFound());

        verify(funcionService, times(1)).getFuncionById(funcionId);
    }

    @Test
    @DisplayName("POST /api/funciones debe crear función correctamente")
    void cuandoPostFuncion_datosValidos_debeCrearExitosamente() throws Exception {
        FuncionController.CrearFuncionRequest request = new FuncionController.CrearFuncionRequest();
        request.setPelicula("Inception");
        request.setFechaHora(LocalDateTime.of(2025, 9, 30, 20, 0));
        request.setSala("Sala 1");
        request.setTotalAsientos(50);
        request.setPrecio(150.0);

        Funcion funcionCreada = new Funcion();
        funcionCreada.setId(1L);
        funcionCreada.setPelicula("Inception");
        funcionCreada.setAsientosDisponibles(50);

        when(funcionService.crearFuncion(
                anyString(),
                any(LocalDateTime.class),
                anyString(),
                anyInt(),
                anyDouble()
        )).thenReturn(funcionCreada);

        mockMvc.perform(post("/api/funciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pelicula", is("Inception")))
                .andExpect(jsonPath("$.asientosDisponibles", is(50)));

        verify(funcionService, times(1)).crearFuncion(
                eq("Inception"),
                any(LocalDateTime.class),
                eq("Sala 1"),
                eq(50),
                eq(150.0)
        );
    }

    @Test
    @DisplayName("PUT /api/funciones/{id}/cancelar debe cancelar función")
    void cuandoCancelarFuncion_debeRetornarExito() throws Exception {
        Long funcionId = 1L;

        doNothing().when(funcionService).cancelarFuncion(funcionId);

        mockMvc.perform(put("/api/funciones/{id}/cancelar", funcionId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("cancelada exitosamente")));

        verify(funcionService, times(1)).cancelarFuncion(funcionId);
    }

    @Test
    @DisplayName("PUT /api/funciones/{id}/cancelar debe manejar errores")
    void cuandoCancelarFuncion_conError_debeRetornar400() throws Exception {
        Long funcionId = 999L;

        doThrow(new RuntimeException("Función no encontrada"))
                .when(funcionService).cancelarFuncion(funcionId);

        mockMvc.perform(put("/api/funciones/{id}/cancelar", funcionId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error")));

        verify(funcionService, times(1)).cancelarFuncion(funcionId);
    }

    @Test
    @DisplayName("GET /api/funciones/disponibles debe retornar solo funciones con asientos")
    void cuandoGetDisponibles_debeRetornarSoloConAsientos() throws Exception {
        Funcion funcion1 = new Funcion();
        funcion1.setId(1L);
        funcion1.setPelicula("Avengers");
        funcion1.setAsientosDisponibles(30);

        Funcion funcion2 = new Funcion();
        funcion2.setId(2L);
        funcion2.setPelicula("Spider-Man");
        funcion2.setAsientosDisponibles(45);

        List<Funcion> disponibles = Arrays.asList(funcion1, funcion2);

        when(funcionService.getFuncionesConAsientosDisponibles())
                .thenReturn(disponibles);

        mockMvc.perform(get("/api/funciones/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].asientosDisponibles", greaterThan(0)))
                .andExpect(jsonPath("$[1].asientosDisponibles", greaterThan(0)));

        verify(funcionService, times(1)).getFuncionesConAsientosDisponibles();
    }
}