package com.example.aprendeconmigo

import com.google.firebase.FirebaseApp
import android.content.Context

/**
 * Pequeño ayudante para poder compilar y correr la app ANTES de tener
 * configurado app/google-services.json.
 *
 * Mientras ese archivo no exista y el plugin google-services esté
 * comentado en build.gradle.kts, FirebaseApp no está inicializado y
 * cualquier llamada directa a FirebaseAuth.getInstance() o
 * Firebase.firestore lanza un crash. Con esto evitamos ese crash y
 * mostramos un mensaje claro en vez de tronar la app.
 */
object FirebaseHelper {

    fun estaConfigurado(context: Context): Boolean {
        return FirebaseApp.getApps(context).isNotEmpty()
    }
}
