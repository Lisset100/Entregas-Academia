package com.cine.AppGestionCine.asientos;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "asientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long funcionId;

    @Column(nullable = false)
    private String numeroAsiento; // Ej: "A1", "B5"

    @Column(nullable = false)
    private Integer fila;

    @Column(nullable = false)
    private Integer columna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsiento estado = EstadoAsiento.LIBRE;

    private String clienteEmail;

    public Asiento(Long funcionId, String numeroAsiento, Integer fila, Integer columna) {
        this.funcionId = funcionId;
        this.numeroAsiento = numeroAsiento;
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoAsiento.LIBRE;
    }
}