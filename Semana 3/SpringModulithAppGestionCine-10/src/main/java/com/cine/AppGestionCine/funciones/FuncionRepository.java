package com.cine.AppGestionCine.funciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    List<Funcion> findByEstado(EstadoFuncion estado);

    List<Funcion> findByPeliculaContainingIgnoreCase(String pelicula);

    @Query("SELECT f FROM Funcion f WHERE f.fechaHora BETWEEN :fechaInicio AND :fechaFin AND f.estado = :estado")
    List<Funcion> findFuncionesByFechaRango(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estado") EstadoFuncion estado
    );

    @Query("SELECT f FROM Funcion f WHERE f.asientosDisponibles > 0 AND f.estado = 'EN_CARTELERA'")
    List<Funcion> findFuncionesConAsientosDisponibles();
}