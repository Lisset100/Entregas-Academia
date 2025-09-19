package com.example.galeria.model;

public abstract class Obra {
    private String nombre;
    private String artista;
    private int año;

    public Obra(String nombre, String artista, int año) {
        this.nombre = nombre;
        this.artista = artista;
        this.año = año;
    }

    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public int getAño() { return año; }

    // Metodo al cual se le aplicará el polimorfismo
    public abstract String descripcion();
}
