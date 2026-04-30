package com.juegos;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * GestorDocumentos: lógica de negocio para operaciones CRUD sobre Videojuego.
 * Delegará la persistencia a JsonDatabase.
 */
public class GestorDocumentos {

    private final JsonDatabase db;

    public static class GestorException extends RuntimeException {
        public GestorException(String message) { super(message); }
        public GestorException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Crea un gestor usando la ruta por defecto.
     */
    public GestorDocumentos() {
        this(JsonDatabase.getInstance().getFilePath());
    }

    /**
     * Crea un gestor con ruta personalizada.
     */
    public GestorDocumentos(Path filePath) {
        this.db = JsonDatabase.getInstance(filePath);
    }

    // Validaciones básicas de campos obligatorios
    private void validarCamposObligatorios(Videojuego v) {
        if (v == null) throw new GestorException("Videojuego no puede ser nulo");
        if (v.getTitle() == null || v.getTitle().trim().isEmpty()) throw new GestorException("title es obligatorio");
        if (v.getDeveloper() == null || v.getDeveloper().trim().isEmpty()) throw new GestorException("developer es obligatorio");
        if (v.getReleaseYear() <= 0) throw new GestorException("releaseYear es obligatorio y debe ser positivo");
    }

    private int calcularNextId(List<Videojuego> lista) {
        return lista.stream().map(Videojuego::getId).max(Comparator.naturalOrder()).orElse(0) + 1;
    }

    /**
     * Añade un documento (Videojuego). Asigna id autoincremental y guarda.
     * En este modelo el campo 'title' se considera único (case-insensitive).
     */
    public synchronized Videojuego añadirDocumento(Videojuego nuevo) {
        validarCamposObligatorios(nuevo);

        List<Videojuego> lista = db.cargarDatos();

        // Unicidad por título (ignora mayúsculas/minúsculas)
        String tLower = nuevo.getTitle().trim().toLowerCase(Locale.ROOT);
        boolean existe = lista.stream().anyMatch(v -> v.getTitle() != null && v.getTitle().trim().toLowerCase(Locale.ROOT).equals(tLower));
        if (existe) {
            throw new GestorException("Ya existe un videojuego con el mismo title: " + nuevo.getTitle());
        }

        int id = calcularNextId(lista);
        try {
            nuevo.setId(id);
        } catch (IllegalArgumentException iae) {
            throw new GestorException("ID inválido: " + iae.getMessage(), iae);
        }

        lista.add(nuevo);
        db.guardarDatos(lista);
        return nuevo;
    }

    /**
     * Modifica un documento existente identificado por id. Reemplaza campos con los del objeto 'actualizado'.
     */
    public synchronized Videojuego modificarDocumento(int id, Videojuego actualizado) {
        if (id <= 0) throw new GestorException("id inválido");
        validarCamposObligatorios(actualizado);

        List<Videojuego> lista = db.cargarDatos();
        Optional<Videojuego> opt = lista.stream().filter(v -> v.getId() == id).findFirst();
        if (!opt.isPresent()) throw new GestorException("No se encontró documento con id=" + id);

        // Unicidad: título no debe duplicar otro documento distinto
        String nuevoTituloLower = actualizado.getTitle().trim().toLowerCase(Locale.ROOT);
        boolean dup = lista.stream().anyMatch(v -> v.getId() != id && v.getTitle() != null && v.getTitle().trim().toLowerCase(Locale.ROOT).equals(nuevoTituloLower));
        if (dup) throw new GestorException("Otro videojuego ya tiene el mismo title: " + actualizado.getTitle());

        Videojuego existente = opt.get();
        // Actualizamos campo a campo para mantener coherencia
        existente.setTitle(actualizado.getTitle());
        existente.setDeveloper(actualizado.getDeveloper());
        existente.setReleaseYear(actualizado.getReleaseYear());
        existente.setGenres(actualizado.getGenres());
        existente.setPlatforms(actualizado.getPlatforms());
        existente.setMultiplayer(actualizado.isMultiplayer());
        existente.setPriceCents(actualizado.getPriceCents());
        existente.setAvailable(actualizado.isAvailable());
        existente.setRating(actualizado.getRating());
        existente.setTags(actualizado.getTags());
        existente.setMetadata(actualizado.getMetadata());
        existente.setDescription(actualizado.getDescription());

        db.guardarDatos(lista);
        return existente;
    }

    /**
     * Elimina un documento por id. Devuelve true si se eliminó.
     */
    public synchronized boolean eliminarDocumento(int id) {
        if (id <= 0) throw new GestorException("id inválido");

        List<Videojuego> lista = db.cargarDatos();
        boolean removed = lista.removeIf(v -> v.getId() == id);
        if (!removed) throw new GestorException("No se encontró documento con id=" + id);
        db.guardarDatos(lista);
        return true;
    }

    /**
     * Búsqueda parcial case-insensitive por campo especificado.
     * Campos soportados: title, developer, genres, platforms, tags, description
     */
    public synchronized List<Videojuego> buscarPorCampo(String campo, String valor) {
        if (campo == null || valor == null) return new ArrayList<>();
        String vLower = valor.toLowerCase(Locale.ROOT);
        List<Videojuego> lista = db.cargarDatos();

        switch (campo) {
            case "title":
                return lista.stream().filter(v -> v.getTitle() != null && v.getTitle().toLowerCase(Locale.ROOT).contains(vLower)).collect(Collectors.toList());
            case "developer":
                return lista.stream().filter(v -> v.getDeveloper() != null && v.getDeveloper().toLowerCase(Locale.ROOT).contains(vLower)).collect(Collectors.toList());
            case "genres":
                return lista.stream().filter(v -> v.getGenres() != null && v.getGenres().stream().anyMatch(g -> g.toLowerCase(Locale.ROOT).contains(vLower))).collect(Collectors.toList());
            case "platforms":
                return lista.stream().filter(v -> v.getPlatforms() != null && v.getPlatforms().stream().anyMatch(p -> p.toLowerCase(Locale.ROOT).contains(vLower))).collect(Collectors.toList());
            case "tags":
                return lista.stream().filter(v -> v.getTags() != null && v.getTags().stream().anyMatch(t -> t.toLowerCase(Locale.ROOT).contains(vLower))).collect(Collectors.toList());
            case "description":
                return lista.stream().filter(v -> v.getDescription() != null && v.getDescription().toLowerCase(Locale.ROOT).contains(vLower)).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    public synchronized List<Videojuego> obtenerTodos() {
        return new ArrayList<>(db.cargarDatos());
    }

    public synchronized Videojuego obtenerPorId(int id) {
        if (id <= 0) return null;
        return db.cargarDatos().stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }
}
