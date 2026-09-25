package com.example.aprendeconmigo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla "Temario - Maestro": junta los temas de todos los grupos
 * del maestro (users/{uid}/grupos/{grupoId}/temas) y los muestra en
 * una sola lista, indicando a qué grupo pertenece cada uno.
 */
class TemarioActivity : AppCompatActivity() {

    private lateinit var adapter: TemaAdapter
    private val listaTemas = mutableListOf<Tema>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temario)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvTemas)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = TemaAdapter(listaTemas) { }
        rv.adapter = adapter

        findViewById<TextView>(R.id.btnAgregarTema).setOnClickListener {
            startActivity(Intent(this, CrearTemaActivity::class.java))
        }
        findViewById<TextView>(R.id.btnEliminarTema).setOnClickListener {
            eliminarTemaSeleccionado()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTemas()
    }

    private fun coleccionGrupos() =
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("grupos")

    private fun cargarTemas() {
        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarVacio("Firebase todavía no está configurado (falta google-services.json)")
            return
        }

        coleccionGrupos().get()
            .addOnSuccessListener { gruposSnapshot ->
                if (gruposSnapshot.isEmpty) {
                    mostrarVacio("Primero crea un grupo en 'Ser.grupos'")
                    return@addOnSuccessListener
                }

                val temasAcumulados = mutableListOf<Tema>()
                var pendientes = gruposSnapshot.size()

                for (grupoDoc in gruposSnapshot.documents) {
                    val grupoNombre = grupoDoc.getString("nombre") ?: ""
                    grupoDoc.reference.collection("temas").get()
                        .addOnSuccessListener { temasSnapshot ->
                            for (temaDoc in temasSnapshot.documents) {
                                temasAcumulados.add(
                                    Tema(
                                        id = temaDoc.id,
                                        titulo = temaDoc.getString("titulo") ?: "",
                                        grupoId = grupoDoc.id,
                                        grupoNombre = grupoNombre
                                    )
                                )
                            }
                            pendientes--
                            if (pendientes == 0) {
                                mostrarTemas(temasAcumulados)
                            }
                        }
                        .addOnFailureListener {
                            pendientes--
                            if (pendientes == 0) {
                                mostrarTemas(temasAcumulados)
                            }
                        }
                }
            }
            .addOnFailureListener {
                mostrarVacio("No se pudieron cargar los temas: ${it.localizedMessage}")
            }
    }

    private fun mostrarTemas(temas: List<Tema>) {
        val ordenados = temas.sortedBy { it.grupoNombre }
        adapter.actualizar(ordenados)
        findViewById<TextView>(R.id.tvSinTemas).visibility =
            if (ordenados.isEmpty()) TextView.VISIBLE else TextView.GONE
        if (ordenados.isEmpty()) {
            findViewById<TextView>(R.id.tvSinTemas).text = "Aún no tienes temas creados"
        }
    }

    private fun mostrarVacio(mensaje: String) {
        adapter.actualizar(emptyList())
        findViewById<TextView>(R.id.tvSinTemas).apply {
            text = mensaje
            visibility = TextView.VISIBLE
        }
    }

    private fun eliminarTemaSeleccionado() {
        val tema = adapter.obtenerSeleccionado()
        if (tema == null) {
            Toast.makeText(this, "Selecciona primero un tema de la lista", Toast.LENGTH_SHORT).show()
            return
        }
        coleccionGrupos().document(tema.grupoId).collection("temas").document(tema.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Tema eliminado: ${tema.titulo}", Toast.LENGTH_SHORT).show()
                cargarTemas()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo eliminar: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}
