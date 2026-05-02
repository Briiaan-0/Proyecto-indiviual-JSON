package com.juegos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivoJson {

    private final Path filePath;
    private final Gson gson;

    public GestorArchivoJson() {
        this.filePath = Paths.get("src", "main", "resources", "videojuegos_db.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Videojuego> cargarDatos() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            String content = Files.readString(filePath);
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return gson.fromJson(content, new TypeToken<List<Videojuego>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo JSON: " + e.getMessage());
        }
    }

    public void guardarDatos(List<Videojuego> datos) {
        try {
            Files.createDirectories(filePath.getParent());
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(datos, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo JSON: " + e.getMessage());
        }
    }
}