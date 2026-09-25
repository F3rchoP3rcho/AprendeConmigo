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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla "Crea tu perfil": guarda nombre, edad, género y rol del
 * usuario en Firestore, en la colección "users/{uid}".
 */
class CrearPerfilActivity : AppCompatActivity() {

    private var generoSeleccionado: String = "hombre"
    private lateinit var rol: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_perfil)

        rol = intent.getStringExtra(SeleccionPersonaActivity.EXTRA_ROL)
            ?: SeleccionPersonaActivity.ROL_MAESTRO

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEdad = findViewById<EditText>(R.id.etEdad)
        val optHombre = findViewById<TextView>(R.id.optHombre)
        val optMujer = findViewById<TextView>(R.id.optMujer)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val progress = findViewById<ProgressBar>(R.id.progressPerfil)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        optHombre.setOnClickListener {
            generoSeleccionado = "hombre"
            optHombre.setBackgroundResource(R.drawable.bg_option_selected)
            optHombre.setTextColor(getColor(R.color.white))
            optMujer.setBackgroundResource(R.drawable.bg_option_unselected)
            optMujer.setTextColor(getColor(R.color.ac_text_primary))
        }

        optMujer.setOnClickListener {
            generoSeleccionado = "mujer"
            optMujer.setBackgroundResource(R.drawable.bg_option_selected)
            optMujer.setTextColor(getColor(R.color.white))
            optHombre.setBackgroundResource(R.drawable.bg_option_unselected)
            optHombre.setTextColor(getColor(R.color.ac_text_primary))
        }

        btnContinuar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val edadTexto = etEdad.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = "Ingresa tu nombre"
                return@setOnClickListener
            }
            if (edadTexto.isEmpty()) {
                etEdad.error = "Ingresa tu edad"
                return@setOnClickListener
            }

            guardarPerfil(nombre, edadTexto.toIntOrNull() ?: 0, progress)
        }
    }

    private fun guardarPerfil(nombre: String, edad: Int, progress: ProgressBar) {
        if (!FirebaseHelper.estaConfigurado(this)) {
            Toast.makeText(
                this,
                "Firebase todavía no está configurado (falta google-services.json)",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val perfil = hashMapOf(
            "nombre" to nombre,
            "edad" to edad,
            "genero" to generoSeleccionado,
            "rol" to rol
        )

        progress.visibility = ProgressBar.VISIBLE

        db.collection("users").document(uid).set(perfil)
            .addOnSuccessListener {
                progress.visibility = ProgressBar.GONE
                val destino = if (rol == SeleccionPersonaActivity.ROL_MAESTRO) {
                    Intent(this, DashboardMaestroActivity::class.java)
                } else {
                    // La vista de estudiante se construirá en una fase siguiente.
                    Toast.makeText(
                        this,
                        "Perfil de estudiante guardado. La vista de alumno se hará en la siguiente fase.",
                        Toast.LENGTH_LONG
                    ).show()
                    Intent(this, SignInActivity::class.java)
                }
                startActivity(destino)
                finish()
            }
            .addOnFailureListener { error ->
                progress.visibility = ProgressBar.GONE
                Toast.makeText(this, "No se pudo guardar: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}
