package com.cine.AppGestionCine.funciones;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "funciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pelicula;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String sala;

    @Column(nullable = false)
    private Integer totalAsientos;

    @Column(nullable = false)
    private Integer asientosDisponibles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFuncion estado = EstadoFuncion.EN_CARTELERA;

    @Column(nullable = false)
    private Double precio;

    public Funcion(String pelicula, LocalDateTime fechaHora, String sala, Integer totalAsientos, Double precio) {
        this.pelicula = pelicula;
        this.fechaHora = fechaHora;
        this.sala = sala;
        this.totalAsientos = totalAsientos;
        this.asientosDisponibles = totalAsientos; // Inicialmente todos disponibles
        this.precio = precio;
        this.estado = EstadoFuncion.EN_CARTELERA;
        }}