package com.mongodb.crud.service;
import com.mongodb.crud.model.Pintor;

import java.util.List;
import java.util.Optional;

public interface PintorService {

    Pintor save(Pintor pintor);

    List<Pintor> findAll();

    Optional<Pintor> findById(String id);

    Pintor update(String id, Pintor pintor);

    void deleteById(String id);
}