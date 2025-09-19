package com.example.galeria.repository;

import com.example.galeria.model.Obra;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ObraRepositoryImpl implements ObraRepository {

    private final List<Obra> obras = new ArrayList<>();

    @Override
    public void save(Obra obra) {
        obras.add(obra);
    }

    @Override
    public List<Obra> findAll() {
        return obras;
    }
}
