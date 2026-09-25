# Cómo activar Firebase en AprendeConmigo

El proyecto ya trae todo el código de Login/Registro (Firebase Authentication)
y de Grupos (Cloud Firestore) listo. Mientras no sigas estos pasos, la app
COMPILA y CORRE, pero al intentar iniciar sesión o crear un grupo verás un
mensaje de "Firebase todavía no está configurado".

## Pasos

1. Entra a https://console.firebase.google.com y abre el proyecto Firebase
   que ya tengan para Aula IA Inclusiva (si no existe, créalo).
2. Dentro del proyecto, agrega una app Android con el package name exacto:
   `com.example.aprendeconmigo`
3. Descarga el archivo `google-services.json` que te da la consola.
4. Cópialo a: `app/google-services.json` (junto a `app/build.gradle.kts`).
5. En `build.gradle.kts` (raíz del proyecto), descomenta esta línea dentro
   del bloque `plugins { }`:
   ```
   alias(libs.plugins.google.services) apply false
   ```
6. En `app/build.gradle.kts`, descomenta esta línea dentro del bloque
   `plugins { }`:
   ```
   alias(libs.plugins.google.services)
   ```
7. En la consola de Firebase, entra a Authentication → Sign-in method y
   activa el proveedor "Correo electrónico/contraseña".
8. En Firestore Database, crea la base de datos (modo de prueba está bien
   para el prototipo) y crea la colección `users` (se llenará sola al
   registrar el primer usuario).
9. Sincroniza Gradle en Android Studio (el elefante/icono "Sync Now").

Después de esto, Registro, Login y Grupos van a leer/escribir datos reales
en Firebase.

## Estructura de datos que usa el código

```
users (colección)
 └── {uid} (documento, uid de Firebase Auth)
      ├── nombre: string
      ├── edad: number
      ├── genero: string
      ├── rol: "maestro" | "estudiante"
      └── grupos (subcolección)
           └── {grupoId}
                ├── nombre: string   (ej. "Grupo-JK34")
                └── codigo: string   (ej. "JK34")
```

## Qué falta para la siguiente fase

- Temario, Historial y Notificaciones del maestro: layouts ya están
  hechos, falta conectar cada uno a Firestore (temas por grupo,
  desempeño de alumnos, actividad reciente).
- Todo el flujo de alumno (Inicio estudiante, tema, submódulos, juego,
  examen, resultados).
- Integración con la API de IA para generar submódulos.
- Accesibilidad (alto contraste, tamaño de texto, lectura en voz alta).
