package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Movie;
import org.springframework.batch.item.ItemProcessor;

public class MovieProcessor implements ItemProcessor<Movie, Movie> {

    @Override
    public Movie process(Movie movie) throws Exception {
        // Filtra solo películas con rating >= 7.0
        if(movie.getRating() >= 7.0) {
            return movie; // devuelve la película para que se guarde en DB
        } else {
            return null; // las películas con rating menor se descartan
        }
    }
}
