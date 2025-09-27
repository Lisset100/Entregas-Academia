package com.mongodb.crud.service;

import com.mongodb.crud.model.Pintor;
import com.mongodb.crud.repository.PintorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PintorServiceTest {

    @Mock
    private PintorRepository pintorRepository; // Mockito simula esta dependencia

    @InjectMocks
    private PintorServiceImpl pintorService; // Clase donde se inyecta el mock

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks antes de cada test
    }

    @Test
    public void testFindById() {
        Pintor pintor = new Pintor();
        pintor.setId("123");
        pintor.setNombre("Frida Kahlo");

        when(pintorRepository.findById("123")).thenReturn(Optional.of(pintor));

        Optional<Pintor> resultado = pintorService.findById("123");

        assertTrue(resultado.isPresent());
        assertEquals("Frida Kahlo", resultado.get().getNombre());

        verify(pintorRepository, times(1)).findById("123");
    }
    @Test
public void testSavePintor(){
        Pintor pintor= new Pintor();
        pintor.setId("456");
        pintor.setNombre("Pablo Picasso");
        when(pintorRepository.save(pintor)).thenReturn(pintor);
        Pintor resultado = pintorService.save(pintor);
        assertNotNull(resultado);
        assertEquals("Pablo Picasso", resultado.getNombre());
        verify(pintorRepository, times(1)).save(pintor);


    }


    @Test
    public void testFindAllPintores() {
        // 1️⃣ Preparar datos de prueba
        Pintor p1 = new Pintor();
        p1.setId("1");
        p1.setNombre("Frida Kahlo");

        Pintor p2 = new Pintor();
        p2.setId("2");
        p2.setNombre("Diego Rivera");

        List<Pintor> listaPintores = Arrays.asList(p1, p2);

        when(pintorRepository.findAll()).thenReturn(listaPintores);

        List<Pintor> resultado = pintorService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Frida Kahlo", resultado.get(0).getNombre());
        assertEquals("Diego Rivera", resultado.get(1).getNombre());

        verify(pintorRepository, times(1)).findAll();
    }
    @Test
    void testActualizarPintor() {
        Pintor pintorOriginal = new Pintor();
        pintorOriginal.setId("1");
        pintorOriginal.setNombre("Picasso");

        Pintor pintorActualizado = new Pintor();
        pintorActualizado.setNombre("Pablo Picasso");

        when(pintorRepository.findById("1")).thenReturn(Optional.of(pintorOriginal));

        when(pintorRepository.save(any(Pintor.class))).thenAnswer(i -> i.getArgument(0));

        Pintor resultado = pintorService.update("1", pintorActualizado);

        assertEquals("Pablo Picasso", resultado.getNombre());

        verify(pintorRepository).findById("1");
        verify(pintorRepository).save(pintorOriginal);
    }
    @Test
    public void testDeleteById() {
        pintorService.deleteById("123");

        verify(pintorRepository, times(1)).deleteById("123");
    }
}


