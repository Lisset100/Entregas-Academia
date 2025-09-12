package com.mongodb.crud.controller;

import com.mongodb.crud.model.Pintor;
import com.mongodb.crud.service.PintorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pintores")
public class PintorController {

    private final PintorService pintorService;

    public PintorController(PintorService pintorService) {
        this.pintorService = pintorService;
    }

    // Crear un pintor
    @PostMapping
    public ResponseEntity<Pintor> crearPintor(@RequestBody Pintor pintor) {
        Pintor nuevo = pintorService.save(pintor);
        return ResponseEntity.ok(nuevo);
    }

    // Obtener todos los pintores
    @GetMapping
    public ResponseEntity<List<Pintor>> obtenerTodos() {
        List<Pintor> pintores = pintorService.findAll();
        return ResponseEntity.ok(pintores);
    }

    // Obtener pintor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pintor> obtenerPorId(@PathVariable String id) {
        Optional<Pintor> pintor = pintorService.findById(id);
        return pintor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar pintor por ID
    @PutMapping("/{id}")
    public ResponseEntity<Pintor> actualizarPintor(@PathVariable String id, @RequestBody Pintor pintor) {
        Pintor actualizado = pintorService.update(id, pintor);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar pintor por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPintor(@PathVariable String id) {
        pintorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}