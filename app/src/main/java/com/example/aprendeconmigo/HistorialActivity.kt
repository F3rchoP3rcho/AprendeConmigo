package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla "Historial - Maestro": lista los grupos del maestro. Al
 * tocar uno, se abre HistorialGrupoActivity con el avance de cada
 * alumno de ese grupo.
 */
class HistorialActivity : AppCompatActivity() {

    private lateinit var adapter: GrupoAdapter
    private val listaGrupos = mutableListOf<Grupo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvGruposHistorial)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = GrupoAdapter(listaGrupos) { grupo ->
            val intent = Intent(this, HistorialGrupoActivity::class.java)
            intent.putExtra(HistorialGrupoActivity.EXTRA_GRUPO_ID, grupo.id)
            intent.putExtra(HistorialGrupoActivity.EXTRA_GRUPO_NOMBRE, grupo.nombre)
            startActivity(intent)
        }
        rv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarGrupos()
    }

    private fun cargarGrupos() {
        val tvSinGrupos = findViewById<TextView>(R.id.tvSinGruposHistorial)

        if (!FirebaseHelper.estaConfigurado(this)) {
            tvSinGrupos.text = "Firebase todavía no está configurado (falta google-services.json)"
            tvSinGrupos.visibility = TextView.VISIBLE
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid).collection("grupos")
            .get()
            .addOnSuccessListener { snapshot ->
                val grupos = snapshot.documents.map { doc ->
                    Grupo(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        codigo = doc.getString("codigo") ?: ""
                    )
                }
                adapter.actualizar(grupos)
                tvSinGrupos.visibility = if (grupos.isEmpty()) TextView.VISIBLE else TextView.GONE
                if (grupos.isEmpty()) tvSinGrupos.text = "Aún no tienes grupos creados"
            }
    }
}
