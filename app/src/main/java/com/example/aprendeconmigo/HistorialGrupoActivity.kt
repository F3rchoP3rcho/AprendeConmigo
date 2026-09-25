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
 * Avance de alumnos de un grupo específico. Lee de:
 * users/{uid}/grupos/{grupoId}/alumnos/{alumnoId}  (campos: nombre, progreso)
 *
 * Esta colección todavía no la llena nadie porque el flujo de
 * alumno no está construido; por eso, hasta que exista esa parte de
 * la app, aquí se verá la lista vacía (comportamiento esperado, no
 * es un error).
 */
class HistorialGrupoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_grupo)

        val grupoId = intent.getStringExtra(EXTRA_GRUPO_ID) ?: return
        val grupoNombre = intent.getStringExtra(EXTRA_GRUPO_NOMBRE) ?: ""

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvTituloGrupo).text = grupoNombre

        val rv = findViewById<RecyclerView>(R.id.rvAlumnos)
        rv.layoutManager = LinearLayoutManager(this)

        cargarAlumnos(grupoId)
    }

    private fun cargarAlumnos(grupoId: String) {
        val tvSinAlumnos = findViewById<TextView>(R.id.tvSinAlumnos)
        val rv = findViewById<RecyclerView>(R.id.rvAlumnos)

        if (!FirebaseHelper.estaConfigurado(this)) {
            tvSinAlumnos.text = "Firebase todavía no está configurado (falta google-services.json)"
            tvSinAlumnos.visibility = TextView.VISIBLE
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("grupos").document(grupoId)
            .collection("alumnos")
            .get()
            .addOnSuccessListener { snapshot ->
                val alumnos = snapshot.documents.map { doc ->
                    Alumno(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        progreso = (doc.getLong("progreso") ?: 0L).toInt()
                    )
                }
                rv.adapter = AlumnoProgresoAdapter(alumnos)
                tvSinAlumnos.visibility = if (alumnos.isEmpty()) TextView.VISIBLE else TextView.GONE
            }
            .addOnFailureListener {
                tvSinAlumnos.text = "No se pudo cargar: ${it.localizedMessage}"
                tvSinAlumnos.visibility = TextView.VISIBLE
            }
    }

    companion object {
        const val EXTRA_GRUPO_ID = "extra_grupo_id"
        const val EXTRA_GRUPO_NOMBRE = "extra_grupo_nombre"
    }
}
