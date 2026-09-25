package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla "¿Quién va a usar la app?": el usuario elige su rol
 * (estudiante o maestro/tutor). Ese rol se guarda junto con el resto
 * del perfil en la siguiente pantalla (CrearPerfilActivity).
 */
class SeleccionPersonaActivity : AppCompatActivity() {

    private var rolSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_persona)

        val optEstudiante = findViewById<TextView>(R.id.optEstudiante)
        val optMaestro = findViewById<TextView>(R.id.optMaestro)
        val btnContinuar = findViewById<TextView>(R.id.btnContinuar)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        optEstudiante.setOnClickListener {
            rolSeleccionado = ROL_ESTUDIANTE
            optEstudiante.setBackgroundResource(R.drawable.bg_option_selected)
            optEstudiante.setTextColor(getColor(R.color.white))
            optMaestro.setBackgroundResource(R.drawable.bg_option_unselected)
            optMaestro.setTextColor(getColor(R.color.ac_text_primary))
            habilitarContinuar(btnContinuar)
        }

        optMaestro.setOnClickListener {
            rolSeleccionado = ROL_MAESTRO
            optMaestro.setBackgroundResource(R.drawable.bg_option_selected)
            optMaestro.setTextColor(getColor(R.color.white))
            optEstudiante.setBackgroundResource(R.drawable.bg_option_unselected)
            optEstudiante.setTextColor(getColor(R.color.ac_text_primary))
            habilitarContinuar(btnContinuar)
        }

        btnContinuar.setOnClickListener {
            val rol = rolSeleccionado ?: return@setOnClickListener
            val intent = Intent(this, CrearPerfilActivity::class.java)
            intent.putExtra(EXTRA_ROL, rol)
            startActivity(intent)
        }
    }

    private fun habilitarContinuar(boton: TextView) {
        boton.isEnabled = true
        boton.setBackgroundResource(R.drawable.bg_button_primary)
    }

    companion object {
        const val EXTRA_ROL = "extra_rol"
        const val ROL_ESTUDIANTE = "estudiante"
        const val ROL_MAESTRO = "maestro"
    }
}
