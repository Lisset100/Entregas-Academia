package com.example.galeria.service;

import com.example.galeria.model.Obra;
import com.example.galeria.repository.ObraRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GaleriaServiceImpl implements GaleriaService {

    private final ObraRepository obraRepository;

    // Inyección de dependencias por constructor
    public GaleriaServiceImpl(ObraRepository obraRepository) {
        this.obraRepository = obraRepository;
    }

    @Override
    public void agregarObra(Obra obra) {
        obraRepository.save(obra);
    }

    @Override
    public List<Obra> listarObras() {
        return obraRepository.findAll();
    }
}
