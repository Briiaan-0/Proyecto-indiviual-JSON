package com.juegos;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import com.google.gson.annotations.SerializedName;

public class Videojuego implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("developer")
    private String developer;

    @SerializedName("releaseYear")
    private int releaseYear;

    @SerializedName("genres")
    private List<String> genres;

    @SerializedName("platforms")
    private List<String> platforms;

    @SerializedName("multiplayer")
    private boolean multiplayer;

    @SerializedName("priceCents")
    private int priceCents;

    @SerializedName("available")
    private boolean available;

    @SerializedName("rating")
    private Rating rating;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("metadata")
    private Metadata metadata;

    @SerializedName("description")
    private String description;

    // Constructor vacío requerido
    public Videojuego() {
    }

    // Getters y setters con validaciones básicas
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id debe ser un entero positivo");
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title no puede ser nulo o vacío");
        }
        this.title = title.trim();
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        if (developer == null || developer.trim().isEmpty()) {
            throw new IllegalArgumentException("developer no puede ser nulo o vacío");
        }
        this.developer = developer.trim();
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear < 1950 || releaseYear > 2100) {
            throw new IllegalArgumentException("releaseYear fuera de rango razonable (1950-2100)");
        }
        this.releaseYear = releaseYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        if (genres == null) {
            throw new IllegalArgumentException("genres no puede ser nulo");
        }
        this.genres = genres;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        if (platforms == null) {
            throw new IllegalArgumentException("platforms no puede ser nulo");
        }
        this.platforms = platforms;
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }

    public void setMultiplayer(boolean multiplayer) {
        this.multiplayer = multiplayer;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(int priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("priceCents no puede ser negativo");
        }
        this.priceCents = priceCents;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("rating no puede ser nulo");
        }
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String platformsStr = (platforms == null || platforms.isEmpty()) ? "N/A" : String.join(", ", platforms);
        String genresStr = (genres == null || genres.isEmpty()) ? "N/A" : String.join(", ", genres);
        String price = String.format("%.2f €", priceCents / 100.0);
        String ratingStr = (rating == null) ? "N/A" : String.format("%.1f (%d votos)", rating.getScore(), rating.getVotes());
        return String.format("%s (%d) — %s\nGéneros: %s — Plataformas: %s\nPrecio: %s — Rating: %s", 
                title != null ? title : "<sin título>", releaseYear, developer != null ? developer : "<sin developer>", genresStr, platformsStr, price, ratingStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Videojuego)) return false;
        Videojuego that = (Videojuego) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // Clase anidada para rating
    public static class Rating implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("score")
        private double score;

        @SerializedName("votes")
        private int votes;

        public Rating() {
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            if (score < 0.0 || score > 10.0) {
                throw new IllegalArgumentException("score debe estar entre 0.0 y 10.0");
            }
            this.score = score;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            if (votes < 0) {
                throw new IllegalArgumentException("votes no puede ser negativo");
            }
            this.votes = votes;
        }
    }

    // Clase anidada para metadata (puede extenderse según necesidad)
    public static class Metadata implements Serializable {
        private static final long serialVersionUID = 1L;

        @SerializedName("languages")
        private List<String> languages;

        @SerializedName("esrb")
        private String esrb;

        @SerializedName("sizeMB")
        private Integer sizeMB;

        public Metadata() {
        }

        public List<String> getLanguages() {
            return languages;
        }

        public void setLanguages(List<String> languages) {
            this.languages = languages;
        }

        public String getEsrb() {
            return esrb;
        }

        public void setEsrb(String esrb) {
            this.esrb = esrb;
        }

        public Integer getSizeMB() {
            return sizeMB;
        }

        public void setSizeMB(Integer sizeMB) {
            if (sizeMB != null && sizeMB < 0) {
                throw new IllegalArgumentException("sizeMB no puede ser negativo");
            }
            this.sizeMB = sizeMB;
        }
    }
}
