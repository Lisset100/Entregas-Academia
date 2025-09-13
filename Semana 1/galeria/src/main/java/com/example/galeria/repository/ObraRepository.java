package com.example.galeria.repository;

import com.example.galeria.model.Obra;
import java.util.ArrayList;
import java.util.List;

public interface ObraRepository {
    void save(Obra obra);
    List<Obra> findAll();
}
