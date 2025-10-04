package com.cine.AppGestionCine.asientos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    List<Asiento> findByFuncionId(Long funcionId);

    List<Asiento> findByFuncionIdAndEstado(Long funcionId, EstadoAsiento estado);

    @Query("SELECT COUNT(a) FROM Asiento a WHERE a.funcionId = :funcionId AND a.estado = 'LIBRE'")
    Long countAsientosDisponiblesByFuncion(@Param("funcionId") Long funcionId);

    Optional<Asiento> findByFuncionIdAndNumeroAsiento(Long funcionId, String numeroAsiento);

    List<Asiento> findByClienteEmailAndEstado(String clienteEmail, EstadoAsiento estado);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN false ELSE true END FROM Asiento a " +
            "WHERE a.funcionId = :funcionId AND a.numeroAsiento = :numeroAsiento AND a.estado = 'LIBRE'")
    boolean isAsientoDisponible(@Param("funcionId") Long funcionId, @Param("numeroAsiento") String numeroAsiento);
}