package com.cine.AppGestionCine.clientes;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por email (único)
    Optional<Cliente> findByEmail(String email);

    // Verificar si existe email (para validación)
    boolean existsByEmail(String email);

    // Buscar clientes por estado
    List<Cliente> findByEstado(EstadoCliente estado);

    // Buscar clientes por nombre (like)
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre,
            String apellido
    );
}