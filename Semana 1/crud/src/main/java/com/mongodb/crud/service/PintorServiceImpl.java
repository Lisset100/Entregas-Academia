package com.mongodb.crud.service;

import com.mongodb.crud.model.Pintor;
import com.mongodb.crud.repository.PintorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PintorServiceImpl implements PintorService {

    private final PintorRepository pintorRepository;

    public PintorServiceImpl(PintorRepository pintorRepository) {
        this.pintorRepository = pintorRepository;
    }

    @Override
    public Pintor save(Pintor pintor) {
        return pintorRepository.save(pintor);
    }

    @Override
    public List<Pintor> findAll() {
        return pintorRepository.findAll();
    }

    @Override
    public Optional<Pintor> findById(String id) {
        return pintorRepository.findById(id);
    }

    @Override
    public Pintor update(String id, Pintor pintorActualizado) {
        return pintorRepository.findById(id)
                .map(p -> {
                    p.setNombre(pintorActualizado.getNombre());
                    p.setNacionalidad(pintorActualizado.getNacionalidad());
                    p.setEstilo(pintorActualizado.getEstilo());
                    p.setFechaNacimiento(pintorActualizado.getFechaNacimiento());
                    p.setFechaFallecimiento(pintorActualizado.getFechaFallecimiento());
                    p.setObrasFamosas(pintorActualizado.getObrasFamosas());
                    p.setImagenes(pintorActualizado.getImagenes());
                    return pintorRepository.save(p);
                })
                .orElse(null);
    }

    @Override
    public void deleteById(String id) {
        pintorRepository.deleteById(id);
    }
}
