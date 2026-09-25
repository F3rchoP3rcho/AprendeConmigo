package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Registro real con Firebase Authentication (correo + contraseña).
 * Al terminar, continúa hacia "¿Quién va a usar la app?".
 */
class RegistroActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private lateinit var etConfirm: EditText
    private lateinit var tvError: TextView
    private lateinit var progress: ProgressBar
    private var correo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        correo = intent.getStringExtra(SignInActivity.EXTRA_CORREO) ?: ""

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvCorreoMostrado).text = correo

        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirmPassword)
        tvError = findViewById(R.id.tvErrorRegistro)
        progress = findViewById(R.id.progressRegistro)

        findViewById<Button>(R.id.btnCrearCuenta).setOnClickListener {
            crearCuenta()
        }
    }

    private fun crearCuenta() {
        val password = etPassword.text.toString()
        val confirm = etConfirm.text.toString()

        if (correo.isBlank()) {
            mostrarError("Falta el correo, regresa a la pantalla anterior")
            return
        }
        if (password.length < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (password != confirm) {
            mostrarError("Las contraseñas no coinciden")
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

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { tarea ->
                progress.visibility = ProgressBar.GONE
                if (tarea.isSuccessful) {
                    val intent = Intent(this, SeleccionPersonaActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    mostrarError(
                        tarea.exception?.localizedMessage
                            ?: "No se pudo crear la cuenta"
                    )
                }
            }
    }

    private fun mostrarError(mensaje: String) {
        tvError.text = mensaje
        tvError.visibility = TextView.VISIBLE
    }
}
