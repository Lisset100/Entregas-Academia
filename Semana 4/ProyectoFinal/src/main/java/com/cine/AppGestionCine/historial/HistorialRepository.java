package com.cine.AppGestionCine.historial;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface HistorialRepository extends MongoRepository<HistorialReserva, String> {
    //                                                                          ↑
    //                                              MongoDB usa String para IDs

    // Queries derivadas (funcionan igual que JPA)
    List<HistorialReserva> findByFuncionId(Long funcionId);

    List<HistorialReserva> findByClienteEmail(String clienteEmail);

    List<HistorialReserva> findByTipoOperacion(TipoOperacion tipoOperacion);

    // Query por rango de fechas
    List<HistorialReserva> findByTimestampBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    // Query personalizada con MongoDB query syntax
    @Query("{ 'funcionId': ?0, 'tipoOperacion': ?1 }")
    List<HistorialReserva> findByFuncionAndTipo(Long funcionId, TipoOperacion tipo);

    // Contar operaciones por tipo
    long countByTipoOperacion(TipoOperacion tipo);

    // Buscar por cliente y tipo de operación
    List<HistorialReserva> findByClienteEmailAndTipoOperacion(
            String email,
            TipoOperacion tipo
    );

    // Obtener últimos N registros ordenados por fecha
    List<HistorialReserva> findTop10ByOrderByTimestampDesc();
}