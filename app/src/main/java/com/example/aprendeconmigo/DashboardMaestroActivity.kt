package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.Gravity

/**
 * Dashboard principal del Maestro: saludo, accesos rápidos (Grupos,
 * Ferch.IA, Historial) y menú lateral con Perfil, Notificaciones,
 * Configuración, Temario, Historial y Cerrar sesión.
 */
class DashboardMaestroActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_maestro)

        drawerLayout = findViewById(R.id.drawerLayout)

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        cargarNombreMaestro()

        // Accesos rápidos del contenido central
        findViewById<TextView>(R.id.btnGrupos).setOnClickListener {
            startActivity(Intent(this, GruposActivity::class.java))
        }
        findViewById<TextView>(R.id.btnFerchIa).setOnClickListener {
            startActivity(Intent(this, TemarioActivity::class.java))
        }
        findViewById<TextView>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // Barra inferior
        findViewById<ImageButton>(R.id.navNotificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
        findViewById<ImageButton>(R.id.navGrupos).setOnClickListener {
            startActivity(Intent(this, GruposActivity::class.java))
        }
        findViewById<ImageButton>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Menú lateral
        findViewById<TextView>(R.id.menuPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        findViewById<TextView>(R.id.menuNotificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
        findViewById<TextView>(R.id.menuConfiguracion).setOnClickListener {
            Toast.makeText(this, "Configuración: pendiente de siguiente fase", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.menuSerGrupos).setOnClickListener {
            startActivity(Intent(this, GruposActivity::class.java))
        }
        findViewById<TextView>(R.id.menuTemario).setOnClickListener {
            startActivity(Intent(this, TemarioActivity::class.java))
        }
        findViewById<TextView>(R.id.menuHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
        findViewById<TextView>(R.id.menuCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarNombreMaestro() {
        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val tvNombreMaestro = findViewById<TextView>(R.id.tvNombreMaestro)

        if (!FirebaseHelper.estaConfigurado(this)) {
            tvSaludo.text = "Hola, Maestr@. Bienvenido a AprendeConmigo"
            tvNombreMaestro.text = "Maestr@"
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: "Maestr@"
                tvSaludo.text = "Hola, Maestr@ $nombre. ¡Que comience la diversión!"
                tvNombreMaestro.text = nombre
            }
    }

    private fun cerrarSesion() {
        if (FirebaseHelper.estaConfigurado(this)) {
            FirebaseAuth.getInstance().signOut()
        }
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }
}
