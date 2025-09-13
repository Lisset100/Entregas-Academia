package com.example.galeria.model;

public class Pintura extends Obra {
    private String tecnica;

    public Pintura(String nombre, String artista, int año, String tecnica) {
        super(nombre, artista, año);
        this.tecnica = tecnica;
    }

    @Override
    public String descripcion() {
        return "Pintura: " + getNombre() + " por " + getArtista() + " (" + getAño() + "), técnica: " + tecnica;
    }
}
