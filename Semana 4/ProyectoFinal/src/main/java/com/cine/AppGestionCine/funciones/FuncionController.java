package com.cine.AppGestionCine.funciones;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FuncionController {

    private final FuncionService funcionService;

    @PostMapping
    public ResponseEntity<?> crearFuncion(@RequestBody CrearFuncionRequest request) {
        try {
            log.info("Creando función: {}", request);

            Funcion funcion = funcionService.crearFuncion(
                    request.getPelicula(),
                    request.getFechaHora(),
                    request.getSala(),
                    request.getTotalAsientos(),
                    request.getPrecio()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(funcion);

        } catch (Exception e) {
            log.error("Error al crear función: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Funcion>> getFuncionesEnCartelera() {
        List<Funcion> funciones = funcionService.getFuncionesEnCartelera();
        return ResponseEntity.ok(funciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFuncionById(@PathVariable Long id) {
        Optional<Funcion> funcion = funcionService.getFuncionById(id);

        if (funcion.isPresent()) {
            return ResponseEntity.ok(funcion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Funcion>> buscarPorPelicula(@RequestParam String pelicula) {
        List<Funcion> funciones = funcionService.buscarPorPelicula(pelicula);
        return ResponseEntity.ok(funciones);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Funcion>> getFuncionesDisponibles() {
        List<Funcion> funciones = funcionService.getFuncionesConAsientosDisponibles();
        return ResponseEntity.ok(funciones);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarFuncion(@PathVariable Long id) {
        try {
            funcionService.cancelarFuncion(id);
            return ResponseEntity.ok().body("Función cancelada exitosamente");

        } catch (Exception e) {
            log.error("Error al cancelar función {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DTOs internos
    public static class CrearFuncionRequest {
        private String pelicula;
        private LocalDateTime fechaHora;
        private String sala;
        private Integer totalAsientos;
        private Double precio;

        // Getters y setters
        public String getPelicula() { return pelicula; }
        public void setPelicula(String pelicula) { this.pelicula = pelicula; }

        public LocalDateTime getFechaHora() { return fechaHora; }
        public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

        public String getSala() { return sala; }
        public void setSala(String sala) { this.sala = sala; }

        public Integer getTotalAsientos() { return totalAsientos; }
        public void setTotalAsientos(Integer totalAsientos) { this.totalAsientos = totalAsientos; }

        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }

        @Override
        public String toString() {
            return "CrearFuncionRequest{pelicula='" + pelicula + "', sala='" + sala + "', totalAsientos=" + totalAsientos + "}";
        }
    }
}