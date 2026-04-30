package com.juegos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JsonDatabase: gestión de persistencia en un único archivo JSON usando Gson.
 * Singleton simple para evitar conflictos de escritura concurrente.
 */
public class JsonDatabase {

    private static final Path DEFAULT_PATH = Paths.get("src", "main", "resources", "videojuegos_db.json");

    private static volatile JsonDatabase instance;

    private final Path filePath;
    private final Gson gson;

    /**
     * Excepción runtime para errores de la base JSON.
     */
    public static class JsonDatabaseException extends RuntimeException {
        public JsonDatabaseException(String message, Throwable cause) {
            super(message, cause);
        }

        public JsonDatabaseException(String message) {
            super(message);
        }
    }

    private JsonDatabase(Path filePath) {
        this.filePath = filePath == null ? DEFAULT_PATH : filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Obtiene la instancia singleton usando la ruta por defecto.
     */
    public static JsonDatabase getInstance() {
        return getInstance(DEFAULT_PATH);
    }

    /**
     * Obtiene la instancia singleton con ruta personalizada.
     * La primera invocación determina la ruta usada por la instancia.
     */
    public static JsonDatabase getInstance(Path path) {
        if (instance == null) {
            synchronized (JsonDatabase.class) {
                if (instance == null) {
                    instance = new JsonDatabase(path);
                }
            }
        }
        return instance;
    }

    /**
     * Carga la lista de videojuegos desde el archivo JSON.
     * Si el archivo no existe devuelve una lista vacía.
     * Maneja JsonSyntaxException y IOException y los envuelve en JsonDatabaseException.
     */
    public List<Videojuego> cargarDatos() {
        synchronized (this) {
            if (!Files.exists(filePath)) {
                // Archivo inexistente: comportamiento esperado, devolver lista vacía
                return new ArrayList<>();
            }
            Type listType = new TypeToken<List<Videojuego>>() {}.getType();
            try {
                String content = Files.readString(filePath);
                if (content == null || content.trim().isEmpty()) {
                    return new ArrayList<>();
                }

                JsonElement root = gson.fromJson(content, JsonElement.class);
                if (root.isJsonArray()) {
                    return gson.fromJson(root, listType);
                } else if (root.isJsonObject()) {
                    JsonObject obj = root.getAsJsonObject();
                    // Si existe el campo 'games' usamos ese array
                    if (obj.has("games") && obj.get("games").isJsonArray()) {
                        JsonArray arr = obj.getAsJsonArray("games");
                        return gson.fromJson(arr, listType);
                    }
                    // Si no hay 'games' pero el objeto se puede mapear a lista, intentar fall back
                    // (no habitual) -> devolver lista vacía
                    return new ArrayList<>();
                } else {
                    return new ArrayList<>();
                }
            } catch (JsonSyntaxException jse) {
                String msg = "Error de sintaxis JSON al leer '" + filePath + "' : " + jse.getMessage();
                throw new JsonDatabaseException(msg, jse);
            } catch (IOException ioe) {
                String msg = "I/O error leyendo '" + filePath + "' : " + ioe.getMessage();
                throw new JsonDatabaseException(msg, ioe);
            }
        }
    }

    /**
     * Guarda la lista de videojuegos en el archivo JSON en formato pretty printing.
     * Crea directorios padres si es necesario.
     */
    public void guardarDatos(List<Videojuego> datos) {
        if (datos == null) {
            throw new IllegalArgumentException("datos no puede ser nulo");
        }

        synchronized (this) {
            try {
                File parent = filePath.toFile().getParentFile();
                if (parent != null && !parent.exists()) {
                    boolean created = parent.mkdirs();
                    if (!created && !parent.exists()) {
                        throw new JsonDatabaseException("No se pudo crear el directorio: " + parent.getAbsolutePath());
                    }
                }

                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    gson.toJson(datos, writer);
                }
            } catch (IOException ioe) {
                String msg = "I/O error escribiendo '" + filePath + "' : " + ioe.getMessage();
                throw new JsonDatabaseException(msg, ioe);
            }
        }
    }

    /**
     * Lectura auxiliar que devuelve una lista inmutable (útil para exponer sin permitir modificaciones externas).
     */
    public List<Videojuego> cargarDatosInmutable() {
        List<Videojuego> datos = cargarDatos();
        return Collections.unmodifiableList(datos);
    }

    /**
     * Ruta del archivo manejado por esta instancia.
     */
    public Path getFilePath() {
        return filePath;
    }
}
