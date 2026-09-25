package com.example.aprendeconmigo

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla "Notificaciones - Maestro": junta la actividad de alumnos
 * de todos los grupos, leyendo:
 * users/{uid}/grupos/{grupoId}/notificaciones/{id}
 * (campos: alumnoNombre, mensaje)
 *
 * Como el flujo de alumno todavía no está construido, nadie escribe
 * en esta colección todavía: verás la lista vacía y eso es
 * comportamiento esperado, no un error.
 */
class NotificacionesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvNotificaciones)
        rv.layoutManager = LinearLayoutManager(this)

        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        val tvSin = findViewById<TextView>(R.id.tvSinNotificaciones)
        val rv = findViewById<RecyclerView>(R.id.rvNotificaciones)

        if (!FirebaseHelper.estaConfigurado(this)) {
            tvSin.text = "Firebase todavía no está configurado (falta google-services.json)"
            tvSin.visibility = TextView.VISIBLE
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val gruposRef = FirebaseFirestore.getInstance()
            .collection("users").document(uid).collection("grupos")

        gruposRef.get().addOnSuccessListener { gruposSnapshot ->
            if (gruposSnapshot.isEmpty) {
                mostrarVacio(tvSin, "Primero crea un grupo en 'Ser.grupos'")
                return@addOnSuccessListener
            }

            val acumuladas = mutableListOf<Notificacion>()
            var pendientes = gruposSnapshot.size()

            for (grupoDoc in gruposSnapshot.documents) {
                val grupoNombre = grupoDoc.getString("nombre") ?: ""
                grupoDoc.reference.collection("notificaciones").get()
                    .addOnSuccessListener { notifSnapshot ->
                        for (doc in notifSnapshot.documents) {
                            acumuladas.add(
                                Notificacion(
                                    id = doc.id,
                                    alumnoNombre = doc.getString("alumnoNombre") ?: "",
                                    grupoNombre = grupoNombre,
                                    mensaje = doc.getString("mensaje") ?: ""
                                )
                            )
                        }
                        pendientes--
                        if (pendientes == 0) mostrarResultado(rv, tvSin, acumuladas)
                    }
                    .addOnFailureListener {
                        pendientes--
                        if (pendientes == 0) mostrarResultado(rv, tvSin, acumuladas)
                    }
            }
        }
    }

    private fun mostrarResultado(rv: RecyclerView, tvSin: TextView, lista: List<Notificacion>) {
        rv.adapter = NotificacionAdapter(lista)
        tvSin.visibility = if (lista.isEmpty()) TextView.VISIBLE else TextView.GONE
        if (lista.isEmpty()) tvSin.text = "Todavía no hay actividad de tus alumnos"
    }

    private fun mostrarVacio(tvSin: TextView, mensaje: String) {
        tvSin.text = mensaje
        tvSin.visibility = TextView.VISIBLE
    }
}
