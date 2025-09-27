package com.mongodb.crud.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PintorTest {

    @Test
    void testConstructorVacio() {
        Pintor pintor = new Pintor();
        assertNotNull(pintor);
    }

    @Test
    void testConstructorCompletoYGetters() {
        List<String> obras = List.of("La persistencia de la memoria");
        List<String> imagenes = List.of("dali1.jpg");

        Pintor pintor = new Pintor(
                "Salvador Dalí",
                "Español",
                "Surrealismo",
                "1904",
                "1989",
                obras,
                imagenes
        );

        assertEquals("Salvador Dalí", pintor.getNombre());
        assertEquals("Español", pintor.getNacionalidad());
        assertEquals("Surrealismo", pintor.getEstilo());
        assertEquals("1904", pintor.getFechaNacimiento());
        assertEquals("1989", pintor.getFechaFallecimiento());
        assertEquals(obras, pintor.getObrasFamosas());
        assertEquals(imagenes, pintor.getImagenes());
    }

    @Test
    void testSetters() {
        Pintor pintor = new Pintor();

        pintor.setId("1");
        pintor.setNombre("Pablo Picasso");
        pintor.setNacionalidad("Español");
        pintor.setEstilo("Cubismo");
        pintor.setFechaNacimiento("1881");
        pintor.setFechaFallecimiento("1973");
        List<String> obras = List.of("Guernica");
        List<String> imagenes = List.of("picasso1.jpg");
        pintor.setObrasFamosas(obras);
        pintor.setImagenes(imagenes);

        assertEquals("1", pintor.getId());
        assertEquals("Pablo Picasso", pintor.getNombre());
        assertEquals("Español", pintor.getNacionalidad());
        assertEquals("Cubismo", pintor.getEstilo());
        assertEquals("1881", pintor.getFechaNacimiento());
        assertEquals("1973", pintor.getFechaFallecimiento());
        assertEquals(obras, pintor.getObrasFamosas());
        assertEquals(imagenes, pintor.getImagenes());
    }
}
