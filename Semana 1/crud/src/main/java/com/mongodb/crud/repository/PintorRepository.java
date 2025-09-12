package com.mongodb.crud.repository;

import com.mongodb.crud.model.Pintor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PintorRepository extends MongoRepository<Pintor, String> {

}