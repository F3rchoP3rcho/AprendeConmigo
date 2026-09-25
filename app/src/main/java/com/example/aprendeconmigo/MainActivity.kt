package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de bienvenida (Inicio / Splash).
 * Si ya hay una sesión activa de Firebase, saltamos directo al dashboard
 * correspondiente; si no, mandamos a la persona a Sign In.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            irALaSiguientePantalla()
        }, 900)
    }

    private fun irALaSiguientePantalla() {
        val usuarioActual = if (FirebaseHelper.estaConfigurado(this)) {
            FirebaseAuth.getInstance().currentUser
        } else {
            null
        }
        val destino = if (usuarioActual != null) {
            // TODO: cuando tengamos el rol guardado en Firestore, decidir aquí
            // si va a DashboardMaestroActivity o a un dashboard de estudiante.
            Intent(this, DashboardMaestroActivity::class.java)
        } else {
            Intent(this, SignInActivity::class.java)
        }
        startActivity(destino)
        finish()
    }
}
