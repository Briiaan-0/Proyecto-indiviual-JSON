package com.juegos;

import java.io.Serializable;
import java.util.List;

public class Videojuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String titulo;
    private String desarrollador;
    private int anio;
    private List<String> generos;
    private List<String> plataformas;
    private double precio;
    private boolean disponible;
    private String descripcion;

    public Videojuego() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDesarrollador() { return desarrollador; }
    public void setDesarrollador(String desarrollador) { this.desarrollador = desarrollador; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }

    public List<String> getPlataformas() { return plataformas; }
    public void setPlataformas(List<String> plataformas) { this.plataformas = plataformas; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return titulo + " (" + anio + ") — " + desarrollador;
    }
}