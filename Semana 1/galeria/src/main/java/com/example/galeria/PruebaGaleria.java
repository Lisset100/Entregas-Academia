package com.example.galeria;

import com.example.galeria.model.Escultura;
import com.example.galeria.model.Obra;
import com.example.galeria.model.Pintura;
import com.example.galeria.service.GaleriaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PruebaGaleria implements CommandLineRunner {

    private final GaleriaService galeriaService;

    public PruebaGaleria(GaleriaService galeriaService) {
        this.galeriaService = galeriaService;
    }

    @Override
    public void run(String... args) throws Exception {
        Obra p1 = new Pintura("La noche estrellada", "Van Gogh", 1889, "Óleo");
        Obra e1 = new Escultura("El Pensador", "Rodin", 1902, "Bronce");

        galeriaService.agregarObra(p1);
        galeriaService.agregarObra(e1);

        galeriaService.listarObras().forEach(o -> System.out.println(o.descripcion()));
    }
}
