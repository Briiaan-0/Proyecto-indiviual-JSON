# Proyecto: Gestion de Videojuegos (Base de Datos Documental JSON)

Aplicacion Java con interfaz grafica Swing que gestiona una coleccion de videojuegos usando un unico archivo JSON como base de datos documental. Desarrollado con Java 17, Maven y Gson.

---

## 1. Estructura del JSON

El archivo src/main/resources/videojuegos_db.json es la base de datos completa. Es un array plano de objetos; cada objeto representa un videojuego.

Ejemplo:

[
  {
    "id": 1,
    "titulo": "Aventuras en Pixelandia",
    "desarrollador": "IndieStudio",
    "anio": 2021,
    "generos": ["Aventura", "Plataformas"],
    "plataformas": ["Windows", "Linux"],
    "precio": 14.99,
    "disponible": true,
    "descripcion": "Plataformas clasicas con puzzles."
  }
]

Campos por documento:

- id: int - Identificador unico autoincremental
- titulo: string - Nombre del videojuego
- desarrollador: string - Estudio desarrollador
- anio: int - Ano de lanzamiento
- generos: array de strings - Lista de generos
- plataformas: array de strings - Lista de plataformas
- precio: number - Precio en euros
- disponible: boolean - Esta a la venta?
- descripcion: string - Descripcion del juego

No se usa wrapper con meta ni games. El JSON es directamente un array para simplificar la lectura y escritura.

---

## 2. Modelo de datos (clases Java)

Ubicacion: src/main/java/com/juegos

Clases principales:

- Videojuego: POJO con los campos del JSON (getters y setters simples)
- JsonDatabase: Lee y escribe el archivo JSON usando Gson
- GestorDocumentos: Logica CRUD y validaciones basicas
- MainFrame: Interfaz grafica Swing (formulario, tabla, botones)
- Main: Punto de entrada de la aplicacion

Diagrama de flujo:

Usuario -&gt; MainFrame -&gt; GestorDocumentos -&gt; JsonDatabase -&gt; videojuegos_db.json

---

## 3. Comparacion: JSON vs XML vs SQL

a) Representacion en XML (equivalente):

&lt;videojuegos&gt;
    &lt;videojuego&gt;
        &lt;id&gt;1&lt;/id&gt;
        &lt;titulo&gt;Aventuras en Pixelandia&lt;/titulo&gt;
        &lt;desarrollador&gt;IndieStudio&lt;/desarrollador&gt;
        &lt;anio&gt;2021&lt;/anio&gt;
        &lt;generos&gt;
            &lt;genero&gt;Aventura&lt;/genero&gt;
            &lt;genero&gt;Plataformas&lt;/genero&gt;
        &lt;/generos&gt;
        &lt;plataformas&gt;
            &lt;plataforma&gt;Windows&lt;/plataforma&gt;
            &lt;plataforma&gt;Linux&lt;/plataforma&gt;
        &lt;/plataformas&gt;
        &lt;precio&gt;14.99&lt;/precio&gt;
        &lt;disponible&gt;true&lt;/disponible&gt;
        &lt;descripcion&gt;Plataformas clasicas con puzzles.&lt;/descripcion&gt;
    &lt;/videojuego&gt;
&lt;/videojuegos&gt;

b) Modelo relacional (SQL):

Tabla videojuegos:
- id (clave primaria)
- titulo
- desarrollador
- anio
- precio
- disponible
- descripcion

Tablas adicionales para listas:
- generos: id, videojuego_id (clave foranea), nombre
- plataformas: id, videojuego_id (clave foranea), nombre

c) Comparativa:

JSON (este proyecto):
- Estructura: Array de objetos
- Legibilidad: Alta, sintaxis ligera
- Relaciones: Embebidas (arrays dentro del objeto)
- Esquema: Flexible, sin restricciones rigidas
- Consultas: Recorrido en codigo con streams
- Persistencia: Archivo de texto unico

XML:
- Estructura: Arbol de etiquetas
- Legibilidad: Verboso, muchas etiquetas
- Relaciones: Embebidas
- Esquema: Flexible, validable con XSD
- Consultas: XPath o XQuery
- Persistencia: Archivo de texto unico

SQL:
- Estructura: Tablas relacionales
- Legibilidad: Requiere conocer el esquema
- Relaciones: Claves foraneas (FK)
- Esquema: Rigido, tipado fuerte
- Consultas: SQL (SELECT, JOIN, etc.)
- Persistencia: Motor de base de datos

En este proyecto se eligio JSON porque permite guardar la informacion de forma directa, legible y sin necesidad de un gestor de bases de datos externo.

---

## 4. Capturas de funcionamiento

Guarda las capturas en una carpeta screenshots/ con estos nombres:

01_inicio.png - Ventana principal con datos cargados desde el JSON
02_anadir_campos.png - Campos rellenos antes de pulsar Anadir
02_anadir_resultado.png - El nuevo juego aparece en la tabla
02_anadir_json.png - El archivo JSON actualizado con el nuevo registro
03_modificar_antes.png - Fila seleccionada con datos originales
03_modificar_despues.png - Tabla actualizada tras la modificacion
04_eliminar_confirmar.png - Dialogo de confirmacion de eliminacion
04_eliminar_despues.png - Tabla sin el registro eliminado
05_busqueda_resultado.png - Resultados de una busqueda con coincidencias
05_busqueda_vacia.png - Busqueda sin resultados
06_error_validacion.png - Mensaje de error (ejemplo: titulo vacio)
07_reinicio.png - App cerrada y vuelta a abrir; los datos persisten

---

## 5. Manual de usuario

Anadir un videojuego:
1. Rellena los campos: Titulo, Desarrollador, Ano, Generos (separados por coma), Plataformas (separadas por coma), Precio y Descripcion.
2. Marca o desmarca la casilla Disponible.
3. Pulsa el boton Anadir.
4. Aparecera el nuevo juego en la tabla y se guardara automaticamente en el JSON.

Modificar un videojuego:
1. Haz clic en una fila de la tabla para cargar sus datos en el formulario.
2. Edita los campos que quieras cambiar.
3. Pulsa Modificar.
4. La tabla y el JSON se actualizaran.

Eliminar un videojuego:
1. Selecciona una fila de la tabla.
2. Pulsa Eliminar.
3. Confirma en el cuadro de dialogo.
4. El juego desaparecera de la tabla y del JSON.

Buscar:
1. Selecciona en el desplegable el campo por el que quieres buscar (titulo, desarrollador o descripcion).
2. Escribe el texto a buscar.
3. Pulsa Buscar.
4. La tabla mostrara solo los resultados que contengan ese texto (busqueda parcial, sin distinguir mayusculas ni minusculas).
5. Pulsa Limpiar busqueda para ver todos los registros de nuevo.

Limpiar campos:
- Pulsa Limpiar campos para vaciar el formulario y deseleccionar la tabla.

---

## 6. Manual de instalacion

Requisitos:
- Java 17 (JDK)
- Maven 3.9 o superior
- VS Code (u otro IDE Java) con la extension Extension Pack for Java

Pasos:
1. Clona o descarga este repositorio.
2. Abre la carpeta del proyecto en VS Code.
3. Abre una terminal y ejecuta:
   mvn clean compile
4. Para ejecutar la aplicacion:
   mvn exec:java -Dexec.mainClass="com.juegos.Main"
   O pulsa F5 en VS Code si tienes configurado el launch.json.

Generar JAR ejecutable (opcional):
Si quieres distribuir la aplicacion sin el codigo fuente, anade al pom.xml el plugin maven-shade-plugin y ejecuta:
   mvn package
Se generara un .jar en la carpeta target/.

---

## 7. Gestion de tareas en GitHub

Este proyecto se ha gestionado mediante Issues de GitHub. Las tareas definidas incluyen:

- Crear estructura del proyecto con Maven
- Definir clase Videojuego (modelo de datos)
- Implementar lectura y escritura JSON con Gson
- Crear logica CRUD (GestorDocumentos)
- Disenar interfaz grafica Swing
- Implementar busqueda de documentos
- Validar datos de entrada
- Redactar documentacion y capturas

Inserta aqui una captura de tu tablero de Issues de GitHub: screenshots/github_issues.png

---

## 8. Conclusion personal

Este proyecto me ha servido para comprender como funciona una base de datos documental sin necesidad de un sistema gestor complejo. Al usar un simple archivo JSON, he podido enfocarme en la logica de negocio (CRUD, validaciones, busqueda) y en la interfaz de usuario, sin depender de configuraciones externas como MySQL o PostgreSQL.

He aprendido a:
- Usar Gson para convertir objetos Java a JSON y viceversa.
- Separar responsabilidades: modelo, persistencia, logica e interfaz.
- Gestionar un flujo de trabajo con GitHub Issues.

Como mejora futura, se podria anadir un empaquetado automatico en JAR ejecutable o incluir filtros de busqueda mas avanzados, pero para los requisitos academicos actuales la aplicacion cumple con todas las funcionalidades solicitadas.