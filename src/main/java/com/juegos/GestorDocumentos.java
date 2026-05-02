package com.juegos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GestorDocumentos {

    private final GestorArchivoJson db;

    public GestorDocumentos() {
        this.db = new GestorArchivoJson();
    }

    public Videojuego anadirDocumento(Videojuego nuevo) {
        if (nuevo.getTitulo() == null || nuevo.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("El título es obligatorio");
        }

        List<Videojuego> lista = db.cargarDatos();

        // Unicidad por título
        String tLower = nuevo.getTitulo().trim().toLowerCase(Locale.ROOT);
        boolean existe = lista.stream()
            .anyMatch(v -> v.getTitulo().toLowerCase(Locale.ROOT).equals(tLower));
        if (existe) {
            throw new RuntimeException("Ya existe un juego con ese título");
        }

        int nextId = lista.stream()
            .mapToInt(Videojuego::getId)
            .max().orElse(0) + 1;
        nuevo.setId(nextId);

        lista.add(nuevo);
        db.guardarDatos(lista);
        return nuevo;
    }

    public Videojuego modificarDocumento(int id, Videojuego actualizado) {
        if (actualizado.getTitulo() == null || actualizado.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("El título es obligatorio");
        }

        List<Videojuego> lista = db.cargarDatos();
        Videojuego existente = lista.stream()
            .filter(v -> v.getId() == id)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No se encontró el juego con id=" + id));

        // Comprobar que no duplica título de otro
        String tLower = actualizado.getTitulo().trim().toLowerCase(Locale.ROOT);
        boolean dup = lista.stream()
            .anyMatch(v -> v.getId() != id && v.getTitulo().toLowerCase(Locale.ROOT).equals(tLower));
        if (dup) {
            throw new RuntimeException("Otro juego ya tiene ese título");
        }

        existente.setTitulo(actualizado.getTitulo());
        existente.setDesarrollador(actualizado.getDesarrollador());
        existente.setAnio(actualizado.getAnio());
        existente.setGeneros(actualizado.getGeneros());
        existente.setPlataformas(actualizado.getPlataformas());
        existente.setPrecio(actualizado.getPrecio());
        existente.setDisponible(actualizado.isDisponible());
        existente.setDescripcion(actualizado.getDescripcion());

        db.guardarDatos(lista);
        return existente;
    }

    public void eliminarDocumento(int id) {
        List<Videojuego> lista = db.cargarDatos();
        boolean eliminado = lista.removeIf(v -> v.getId() == id);
        if (!eliminado) {
            throw new RuntimeException("No se encontró el juego con id=" + id);
        }
        db.guardarDatos(lista);
    }

    public List<Videojuego> buscarPorCampo(String campo, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String vLower = valor.toLowerCase(Locale.ROOT);
        List<Videojuego> lista = db.cargarDatos();

        return lista.stream().filter(v -> {
            switch (campo) {
                case "titulo":
                    return v.getTitulo() != null && v.getTitulo().toLowerCase().contains(vLower);
                case "desarrollador":
                    return v.getDesarrollador() != null && v.getDesarrollador().toLowerCase().contains(vLower);
                case "descripcion":
                    return v.getDescripcion() != null && v.getDescripcion().toLowerCase().contains(vLower);
                default:
                    return false;
            }
        }).collect(Collectors.toList());
    }

    public List<Videojuego> obtenerTodos() {
        return db.cargarDatos();
    }

    public Videojuego obtenerPorId(int id) {
        return db.cargarDatos().stream()
            .filter(v -> v.getId() == id)
            .findFirst()
            .orElse(null);
    }
}