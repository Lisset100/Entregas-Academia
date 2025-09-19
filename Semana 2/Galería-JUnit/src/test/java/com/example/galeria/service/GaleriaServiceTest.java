package com.example.galeria.service;

import com.example.galeria.model.Escultura;
import com.example.galeria.model.Obra;
import com.example.galeria.model.Pintura;
import com.example.galeria.repository.ObraRepositoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GaleriaServiceTest {

    @Test
    public void testAgregarYListarObras() {
        ObraRepositoryImpl repo = new ObraRepositoryImpl();
        GaleriaService service = new GaleriaServiceImpl(repo);

        Obra pintura = new Pintura("La noche estrellada", "Van Gogh", 1889, "Óleo");
        service.agregarObra(pintura);

        assertEquals(1, service.listarObras().size());
        assertEquals("La noche estrellada", service.listarObras().get(0).getNombre());
    }

    @Test
    public void testAgregarVariasObras() {
        ObraRepositoryImpl repo = new ObraRepositoryImpl();
        GaleriaService service = new GaleriaServiceImpl(repo);

        Obra p1 = new Pintura("La noche estrellada", "Van Gogh", 1889, "Óleo");
        Obra e1 = new Escultura("El Pensador", "Rodin", 1902, "Bronce");

        service.agregarObra(p1);
        service.agregarObra(e1);

        assertEquals(2, service.listarObras().size());
    }

    @Test
    public void testDescripcionPolimorfica() {
        ObraRepositoryImpl repo = new ObraRepositoryImpl();
        GaleriaService service = new GaleriaServiceImpl(repo);

        Obra pintura = new Pintura("La persistencia de la memoria", "Dalí", 1931, "Óleo");
        Obra escultura = new Escultura("David", "Miguel Ángel", 1504, "Mármol");

        service.agregarObra(pintura);
        service.agregarObra(escultura);

        assertEquals("Pintura: La persistencia de la memoria por Dalí (1931), técnica: Óleo",
                service.listarObras().get(0).descripcion());
        assertEquals("Escultura: David por Miguel Ángel (1504), material: Mármol",
                service.listarObras().get(1).descripcion());
    }

}
