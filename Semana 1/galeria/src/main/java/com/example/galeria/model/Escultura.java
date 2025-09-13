package com.example.galeria.model;

public class Escultura extends Obra {
    private String material;

    public Escultura(String nombre, String artista, int año, String material) {
        super(nombre, artista, año);
        this.material = material;
    }

    @Override
    public String descripcion() {
        return "Escultura: " + getNombre() + " por " + getArtista() + " (" + getAño() + "), material: " + material;
    }
}
