package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Login real con Firebase Authentication (correo + contraseña).
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var progress: ProgressBar
    private var correo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        correo = intent.getStringExtra(SignInActivity.EXTRA_CORREO) ?: ""

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvCorreoMostrado).text = correo

        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvErrorLogin)
        progress = findViewById(R.id.progressLogin)

        findViewById<Button>(R.id.btnIniciarSesion).setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val password = etPassword.text.toString()

        if (correo.isBlank() || password.isBlank()) {
            mostrarError("Ingresa tu correo y contraseña")
            return
        }

        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarError(
                "Firebase todavía no está configurado en este proyecto " +
                    "(falta google-services.json). Revisa README-FIREBASE.md"
            )
            return
        }

        tvError.visibility = TextView.GONE
        progress.visibility = ProgressBar.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener { tarea ->
                progress.visibility = ProgressBar.GONE
                if (tarea.isSuccessful) {
                    Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    // TODO: consultar el rol del usuario en Firestore y mandar
                    // al dashboard correcto (maestro o estudiante).
                    startActivity(Intent(this, DashboardMaestroActivity::class.java))
                    finish()
                } else {
                    mostrarError(
                        tarea.exception?.localizedMessage
                            ?: "Correo o contraseña incorrectos"
                    )
                }
            }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = TextView.VISIBLE
    }
}
