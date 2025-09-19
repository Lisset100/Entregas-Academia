package com.example.galeria.service;

import com.example.galeria.model.Obra;
import java.util.List;

public interface GaleriaService {
    void agregarObra(Obra obra);
    List<Obra> listarObras();
}
