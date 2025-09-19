package com.mongodb.crud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pintores")
public class Pintor {

    @Id
    private String id;
    private String nombre;
    private String nacionalidad;
    private String estilo;
    private String fechaNacimiento;
    private String fechaFallecimiento;
    private List<String> obrasFamosas;
    private List<String> imagenes; // URLs de obras famosas

    // Constructor vacío
    public Pintor() {}

    // Constructor con todos los campos
    public Pintor(String nombre, String nacionalidad, String estilo, String fechaNacimiento,
                  String fechaFallecimiento, List<String> obrasFamosas, List<String> imagenes) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.estilo = estilo;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaFallecimiento = fechaFallecimiento;
        this.obrasFamosas = obrasFamosas;
        this.imagenes = imagenes;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public String getEstilo() { return estilo; }
    public void setEstilo(String estilo) { this.estilo = estilo; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getFechaFallecimiento() { return fechaFallecimiento; }
    public void setFechaFallecimiento(String fechaFallecimiento) { this.fechaFallecimiento = fechaFallecimiento; }

    public List<String> getObrasFamosas() { return obrasFamosas; }
    public void setObrasFamosas(List<String> obrasFamosas) { this.obrasFamosas = obrasFamosas; }

    public List<String> getImagenes() { return imagenes; }
    public void setImagenes(List<String> imagenes) { this.imagenes = imagenes; }
}
