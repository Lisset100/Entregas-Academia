package com.mongodb.crud.controller;

import com.mongodb.crud.model.Pintor;
import com.mongodb.crud.service.PintorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PintorControllerTest {

    @Mock
    private PintorService pintorService; // Simulamos el servicio

    @InjectMocks
    private PintorController pintorController; // Clase que vamos a probar

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testObtenerPorId_Existente() {
        Pintor pintor = new Pintor();
        pintor.setId("123");
        pintor.setNombre("Frida Kahlo");


        when(pintorService.findById("123")).thenReturn(Optional.of(pintor));


        ResponseEntity<Pintor> response = pintorController.obtenerPorId("123");


        assertEquals(200, response.getStatusCodeValue()); // OK
        assertNotNull(response.getBody());
        assertEquals("Frida Kahlo", response.getBody().getNombre());

        verify(pintorService, times(1)).findById("123");
    }
    @Test
    public void testObtenerPorId_NoExistente() {

        when(pintorService.findById("999")).thenReturn(Optional.empty());

        ResponseEntity<Pintor> response = pintorController.obtenerPorId("999");

        assertEquals(404, response.getStatusCodeValue()); // Not Found
        assertNull(response.getBody()); // No hay cuerpo en la respuesta

        verify(pintorService, times(1)).findById("999");
    }
    @Test
    public void testCrearPintor() {
        Pintor pintor = new Pintor();
        pintor.setId("101");
        pintor.setNombre("Vincent van Gogh");

        when(pintorService.save(pintor)).thenReturn(pintor);

        ResponseEntity<Pintor> response = pintorController.crearPintor(pintor);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Vincent van Gogh", response.getBody().getNombre());

        verify(pintorService, times(1)).save(pintor);
    }
    @Test
    public void testActualizarPintor() {

        Pintor pintorActualizado = new Pintor();
        pintorActualizado.setNombre("Leonardo da Vinci");

        Pintor pintorGuardado = new Pintor();
        pintorGuardado.setId("202");
        pintorGuardado.setNombre("Leonardo da Vinci");

        when(pintorService.update("202", pintorActualizado)).thenReturn(pintorGuardado);

        ResponseEntity<Pintor> response = pintorController.actualizarPintor("202", pintorActualizado);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Leonardo da Vinci", response.getBody().getNombre());

        verify(pintorService, times(1)).update("202", pintorActualizado);
    }
    @Test
    public void testEliminarPintor() {
        doNothing().when(pintorService).deleteById("303");

        ResponseEntity<Void> response = pintorController.eliminarPintor("303");

        assertEquals(204, response.getStatusCodeValue()); // Código HTTP No Content

        verify(pintorService, times(1)).deleteById("303");
    }

}
