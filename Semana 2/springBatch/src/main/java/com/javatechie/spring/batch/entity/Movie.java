package com.javatechie.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MOVIES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Id
    @Column(name = "MOVIE_ID")
    private int id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "GENRE")
    private String genre;

    @Column(name = "RATING")
    private double rating;

    @Column(name = "RELEASE_YEAR")
    private int releaseYear;
}

