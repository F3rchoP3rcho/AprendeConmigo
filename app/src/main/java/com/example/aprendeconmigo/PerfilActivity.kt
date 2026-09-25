package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla "Ferch.IA - Maestro" (perfil). Aquí más adelante se puede
 * mostrar el asistente FerchIA que aparece en el Figma.
 */
class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}
