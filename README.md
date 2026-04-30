# Proyecto: Gestión de Videojuegos (JSON)

Resumen
-------
Proyecto de ejemplo que guarda una colección de videojuegos en un único fichero JSON. Incluye POJOs, capa de persistencia con Gson, lógica CRUD y una interfaz Swing simple.

Estructura breve
-----------------
- Fichero de datos: `src/main/resources/videojuegos_db.json` (objeto con `meta` opcional y array `games`).
- Código principal: `src/main/java/com/juegos` (clases: `Videojuego`, `JsonDatabase`, `GestorDocumentos`, `MainFrame`, `Main`).

Formato JSON (resumen)
----------------------
Cada documento representa un videojuego y contiene campos como `id`, `title`, `developer`, `releaseYear`, `genres`, `platforms`, `priceCents`, `available`, `rating`, `description`.
La app lee tanto arrays a nivel superior como objetos con la clave `games`.

Instalación y ejecución
-----------------------
Requisitos: Java 17 y Maven 3.9+

Comandos para compilar y ejecutar:

```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass="com.juegos.Main"
```

Uso básico (resumido)
---------------------
- Añadir: completar los campos obligatorios y pulsar `Añadir`.
- Modificar: seleccionar una fila, editar y pulsar `Modificar`.
- Eliminar: seleccionar y pulsar `Eliminar` (confirmar).
- Buscar: elegir campo y escribir término (búsqueda parcial, case-insensitive).

Notas técnicas y mejoras sugeridas
--------------------------------
- La persistencia está en `JsonDatabase` (Gson, pretty printing). Actualmente la escritura guarda la lista; si quieres que se preserve y actualice explícitamente `meta.nextId`, puedo implementarlo.
- Se recomienda añadir pruebas unitarias y configurar un `fat-jar` (maven-shade) para distribución.

Soporte
-------
Dime si prefieres que vuelva a generar un `docs/TECHNICAL_DOCUMENTATION.md` separado, implemente la preservación de `meta.nextId`, o añada empaquetado en `pom.xml`.

Fin.

# Proyecto: Gestión documental de Videojuegos (JSON + Gson)

Este repositorio contiene una aplicación Java standalone (Swing) para gestionar una base documental de videojuegos almacenada en un único archivo JSON. Está diseñada para Java 17 y Maven 3.9+.

Contenido principal
- `src/main/java/com/juegos/Videojuego.java` — POJO que representa un videojuego (con validaciones, toString, equals/hashCode).
- `src/main/java/com/juegos/JsonDatabase.java` — gestor de lectura/escritura del fichero JSON usando Gson (pretty printing).
- `src/main/java/com/juegos/GestorDocumentos.java` — lógica CRUD (añadir, modificar, eliminar, buscar, obtener todos).
- `src/main/java/com/juegos/MainFrame.java` — interfaz gráfica Swing (JFrame) con formulario, tabla y operaciones.
- `src/main/java/com/juegos/Main.java` — lanzador de la aplicación.
- `src/main/resources/videojuegos_db.json` — archivo JSON único con datos iniciales.
- `TESTING.md` — pasos sugeridos para pruebas manuales.
- `docs/TECHNICAL_DOCUMENTATION.md` — documentación técnica completa (estructura JSON, modelo, comparaciones, manuales y conclusiones).

Uso rápido

1. Compilar:
```powershell
mvn clean compile
```

2. Ejecutar:
```powershell
mvn exec:java -Dexec.mainClass="com.juegos.Main"
```

Si prefieres, usa tu IDE para ejecutar la clase `com.juegos.Main`.

Detalles relevantes
- El JSON está en `src/main/resources/videojuegos_db.json`. La aplicación lee un wrapper con `meta` y `games` si existe, o un array top-level.
- Las operaciones de persistencia usan Gson; `JsonDatabase` lanza `JsonDatabaseException` en errores de I/O o sintaxis JSON.
- `GestorDocumentos` delega la lectura/escritura a `JsonDatabase` y aplica validaciones de negocio (campos obligatorios, unicidad por `title`, etc.).

Próximos pasos sugeridos
- Preservar `meta.nextId` en el fichero JSON al guardar (actualmente la app guarda la lista; si quieres lo adapto para mantener `meta`).
- Generar un `fat-jar` con `maven-shade-plugin` para distribución.
*** Begin Documentation

# Documentación Técnica — Proyecto Gestión de Videojuegos

Última actualización: 30-04-2026

Resumen
-------
Esta documentación describe la estructura JSON usada por la aplicación, el modelo de datos Java (POJOs), la comparación entre formatos (JSON, XML, SQL), instrucciones de instalación y uso, pasos de pruebas manuales y conclusiones.

Contenido obligatorio incluido:

1. Estructura del JSON
2. Modelo de datos (clases Java)
3. Comparación XML vs JSON vs SQL
4. Capturas de funcionamiento (nombres / sugerencias)
5. Manual de usuario
6. Manual de instalación
7. Gestión de tareas GitHub (placeholders)
8. Conclusión personal

---

## 1) Estructura del JSON

La aplicación usa un único fichero JSON por defecto en `src/main/resources/videojuegos_db.json`. El formato aceptado es:

- Objeto raíz con campos opcionales `meta` y `games`.
- `meta`: objeto con metadatos (tema, descripción, nextId, schema descriptivo).
- `games`: array de objetos; cada objeto representa un videojuego.

Campos por documento (tipos):

- `id`: int (autoincremental, único)
- `title`: string
- `developer`: string
- `releaseYear`: int
- `genres`: array[string]
- `platforms`: array[string]
- `multiplayer`: boolean
- `priceCents`: int (precio en centavos)
- `available`: boolean
- `rating`: object { score: number, votes: int }
- `tags`: array[string]
- `metadata`: object (languages: array[string], esrb: string, sizeMB: int, ...)
- `description`: string

Ejemplo (fragmento del fichero `src/main/resources/videojuegos_db.json` incluido en este repositorio):

```json
{
	"meta": { "theme": "videojuegos", "nextId": 6 },
	"games": [
		{
			"id": 1,
			"title": "Aventuras en Pixelandia",
			"developer": "IndieStudio",
			"releaseYear": 2021,
			"genres": ["Aventura", "Plataformas"],
			"platforms": ["Windows", "Linux"],
			"multiplayer": false,
			"priceCents": 1499,
			"available": true,
			"rating": { "score": 8.4, "votes": 1245 },
			"tags": ["pixel", "retro"],
			"metadata": { "languages": ["es", "en"] },
			"description": "Plataformas clásicas con puzzles."
		}
	]
}
```

> Nota: la aplicación lee tanto top-level arrays como objetos wrapper con `games`. En la versión actual la escritura puede sobrescribir con un array puro; si prefieres preservar `meta` se puede adaptar `JsonDatabase` para mantener y actualizar `meta.nextId`.

---

## 2) Modelo de datos (clases Java)

Clases principales (ubicación: `src/main/java/com/juegos`):

- `Videojuego` (POJO)
	- Campos: `id`, `title`, `developer`, `releaseYear`, `genres`, `platforms`, `multiplayer`, `priceCents`, `available`, `rating`, `tags`, `metadata`, `description`.
	- Constructor vacío, getters/setters con validaciones, `toString()`, `equals()` y `hashCode()` por `id`.

- `Videojuego.Rating` y `Videojuego.Metadata` (clases anidadas).

- `JsonDatabase`
	- Lectura/escritura JSON con Gson (`cargarDatos()` y `guardarDatos()`), manejo de errores y singleton.

- `GestorDocumentos`
	- Lógica CRUD y validaciones de negocio. Usa `JsonDatabase` para persistencia.

- `MainFrame` y `Main`
	- Interfaz Swing y lanzador de UI.

Diagrama simplificado (textual):

`MainFrame -> GestorDocumentos -> JsonDatabase -> fichero JSON`

---

## 3) Comparación: XML vs JSON vs SQL

a) Ejemplo en XML (equivalente al JSON anterior):

```xml
<library>
	<games>
		<game>
			<id>1</id>
			<title>Aventuras en Pixelandia</title>
			<developer>IndieStudio</developer>
			<!-- ... -->
		</game>
	</games>
</library>
```

b) Modelo SQL (tablas sugeridas):

- `games` (id PK, title, developer, release_year, multiplayer, price_cents, available, description)
- `genres` (id, game_id FK -> games.id, genre)
- `platforms` (id, game_id FK, platform)

c) Ventajas/desventajas de JSON documental:

- Ventajas: flexibilidad, mapping directo a POJOs, legibilidad, portable.
- Desventajas: ausencia de esquema estricto, consultas relacionales complejas, concurrencia/transacciones manuales.

---

## 4) Capturas de funcionamiento (nombres / sugerencias)

Guarda capturas en `screenshots/` con los siguientes nombres:

- `01_inicio_carga.png`
- `02_añadir_campos.png`, `02_añadir_resultado.png`, `02_añadir_json.png`
- `03_modificar_before.png`, `03_modificar_after.png`
- `04_eliminar_confirm.png`, `04_eliminar_after.png`
- `05_busqueda_result.png`, `05_busqueda_noresult.png`
- `06_validacion_error.png`
- `07_reinicio_persistencia.png`

> Nota: las capturas deben tomarse localmente; aquí se indican los nombres y dónde guardarlas.

---

## 5) Manual de usuario (operaciones principales)

- **Añadir**: rellenar campos obligatorios (`Título`, `Developer`, `Año`) y pulsar `Añadir`.
- **Modificar**: seleccionar fila, editar campos y pulsar `Modificar`.
- **Eliminar**: seleccionar fila y pulsar `Eliminar` (confirmar en diálogo).
- **Buscar**: seleccionar campo y escribir término; búsqueda parcial y case-insensitive.
- **Limpiar**: restaura la tabla completa y limpia los campos.

Mensajes y validaciones: la UI muestra `JOptionPane` con errores claros y el `statusLabel` refleja el estado.

---

## 6) Manual de instalación

Requisitos:

- Java 17 (JDK 17)
- Maven 3.9+

Compilar y ejecutar:

```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass="com.juegos.Main"
```

Generar JAR ejecutable: configura `maven-shade-plugin` o `maven-assembly-plugin` en `pom.xml` y ejecuta `mvn package`.

---

## 7) Gestión de tareas GitHub

Usa Issues/Projects para gestionar tareas. Inserta capturas del tablero en `docs/screenshots/board_issues.png` si quieres documentar el flujo.

---

## 8) Conclusión personal

Aprendizajes:

- Manejo de Gson para serialización/deserialización.
- Diseño de validaciones en la capa de negocio y UI.
- Uso de Swing para una UI sencilla y gestión de persistencia a archivo.

Dificultades y mejoras futuras:

- Preservar `meta.nextId` al guardar y usarlo para autoincremento.
- Añadir tests unitarios, empaquetado en `fat-jar` y mejoras de concurrencia.

---

Soporte / próximos pasos

Si quieres que implemente preservación de `meta.nextId`, añada un `fat-jar` o incluya capturas de ejemplo, dime cuál de las opciones prefieres.

*** End Documentation

