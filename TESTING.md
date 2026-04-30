# Pruebas manuales — Aplicación Gestión de Videojuegos

Este documento describe los datos iniciales y los pasos para las pruebas manuales solicitadas. Captura pantallas en la carpeta `screenshots/` o similar.

---

## Datos iniciales

El archivo `src/main/resources/videojuegos_db.json` contiene 5 registros de ejemplo con variedad de casos (títulos largos, caracteres especiales, campos opcionales ausentes).

IDs incluidos: 1..5 (meta.nextId = 6).

---

## Preparación

- Compilar el proyecto:

```powershell
mvn clean compile
```

- Ejecutar la aplicación (desde IDE o usando plugin exec):

```powershell
mvn exec:java -Dexec.mainClass="com.juegos.Main"
```

Si no tienes `exec-maven-plugin` configurado, ejecuta desde tu IDE (Run -> Main).

---

## Pruebas manuales y capturas recomendadas

1) Inicio de aplicación: carga correcta del JSON
   - Acción: Inicia la app.
   - Resultado esperado: La tabla muestra 5 registros.
   - Captura: `01_inicio_carga.png` (ventana con la tabla y contador en estado).

2) Añadir documento
   - Acción: Rellenar los campos (Título, Developer, Año, etc.) y pulsar `Añadir`.
   - Resultado esperado: Ventana de éxito, nueva fila aparece en la tabla y el archivo JSON se actualiza (verificar en `videojuegos_db.json` — NOTA: la implementación actual sobrescribe con un array y puede eliminar `meta`; esto es esperado en la versión actual).
   - Capturas: `02_añadir_campos.png`, `02_añadir_resultado.png`, `02_añadir_json.png` (captura del fichero actualizado si procede).

3) Modificar documento
   - Acción: Seleccionar una fila, editar campos y pulsar `Modificar`.
   - Resultado esperado: Mensaje de éxito; al reiniciar la app los cambios persisten.
   - Capturas: `03_modificar_before.png`, `03_modificar_after.png`.

4) Eliminar documento
   - Acción: Seleccionar fila y pulsar `Eliminar` (confirmar en diálogo).
   - Resultado esperado: Mensaje de confirmación; fila desaparece; JSON actualizado.
   - Capturas: `04_eliminar_confirm.png`, `04_eliminar_after.png`.

5) Búsqueda
   - Acción: Elegir campo (ej. `title`), escribir término en el campo de búsqueda y pulsar `Buscar` (o usar la búsqueda en tiempo real escribiendo).
   - Resultado esperado: La tabla muestra sólo los resultados que contienen el término (búsqueda parcial, case-insensitive). Si no hay resultados se muestra el mensaje "No se encontraron documentos".
   - Capturas: `05_busqueda_result.png`, `05_busqueda_noresult.png`.

6) Validaciones
   - Acción: Intentar añadir/editar con campos obligatorios vacíos, año no numérico o precio inválido, o introducir `<script>` o `<` en campos.
   - Resultado esperado: La aplicación rechaza la entrada, muestra un mensaje de error claro y no modifica el JSON.
   - Capturas: `06_validacion_error.png` (mensaje de error), `06_validacion_nochange.png` (JSON sin cambios).

7) Persistencia tras reinicio
   - Acción: Cerrar la app y volver a abrirla.
   - Resultado esperado: Los cambios realizados (añadir/modificar/eliminar) permanecen en la tabla.
   - Captura: `07_reinicio_persistencia.png`.

---

## Notas y observaciones

- La implementación actual de persistencia escribe y lee listas de `Videojuego`. Si el fichero comienza con un wrapper `{ "meta":..., "games": [...] }` la aplicación lee `games`, pero al guardar la app escribe un array puro. Si necesitas preservar `meta` (por ejemplo `nextId`) lo puedo implementar en el `JsonDatabase` y actualizar `GestorDocumentos` para usar `meta.nextId`.
- Capturas: toma las imágenes con suficiente resolución y nómbralas como se indica. Adjunta las capturas en el repositorio en `screenshots/` si quieres que las incluya en la documentación.

---

Si quieres, procedo a:
- 1) implementar preservación de `meta.nextId` y usarlo para el autoincremento en `GestorDocumentos`, o
- 2) añadir `screenshots/` y generar imágenes de ejemplo (no puedo tomar capturas desde aquí). Indica lo que prefieres.
