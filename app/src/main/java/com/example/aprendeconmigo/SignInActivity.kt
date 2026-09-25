package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla "Sign In" del Figma: pide el correo y decide si la persona
 * ya tiene cuenta (la manda a Login) o si va a crear una nueva
 * (la manda a Registro).
 */
class SignInActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var tvErrorCorreo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        etCorreo = findViewById(R.id.etCorreo)
        tvErrorCorreo = findViewById(R.id.tvErrorCorreo)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val tvYaTienesCuenta = findViewById<TextView>(R.id.tvYaTienesCuenta)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)

        btnContinuar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tvErrorCorreo.text = "Ingresa un correo válido"
                tvErrorCorreo.visibility = TextView.VISIBLE
                return@setOnClickListener
            }
            tvErrorCorreo.visibility = TextView.GONE
            // Por ahora mandamos siempre a Registro (crear cuenta nueva).
            // Si más adelante quieren distinguir "correo ya registrado",
            // aquí se puede consultar Firebase Auth (fetchSignInMethodsForEmail).
            val intent = Intent(this, RegistroActivity::class.java)
            intent.putExtra(EXTRA_CORREO, correo)
            startActivity(intent)
        }

        tvYaTienesCuenta.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(EXTRA_CORREO, correo)
            startActivity(intent)
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(
                this,
                "Inicio con Google: pendiente de configurar en una siguiente fase",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val EXTRA_CORREO = "extra_correo"
    }
}
