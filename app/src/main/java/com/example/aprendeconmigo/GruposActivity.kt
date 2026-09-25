package com.example.aprendeconmigo

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

/**
 * Pantalla "Ser.grupos": lista los grupos del maestro, permite
 * agregar uno nuevo (genera un código de clase) y eliminar el
 * seleccionado. Todo respaldado en Firestore, bajo:
 * users/{uid}/grupos/{grupoId}
 */
class GruposActivity : AppCompatActivity() {

    private lateinit var adapter: GrupoAdapter
    private val listaGrupos = mutableListOf<Grupo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvGrupos)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = GrupoAdapter(listaGrupos) { }
        rv.adapter = adapter

        findViewById<TextView>(R.id.btnAgregarGrupo).setOnClickListener {
            agregarGrupo()
        }
        findViewById<TextView>(R.id.btnEliminarGrupo).setOnClickListener {
            eliminarGrupoSeleccionado()
        }

        cargarGrupos()
    }

    private fun coleccionGrupos() =
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("grupos")

    private fun cargarGrupos() {
        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarSinFirebase()
            return
        }
        coleccionGrupos().get()
            .addOnSuccessListener { snapshot ->
                val grupos = snapshot.documents.map { doc ->
                    Grupo(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        codigo = doc.getString("codigo") ?: ""
                    )
                }
                adapter.actualizar(grupos)
                findViewById<TextView>(R.id.tvSinGrupos).visibility =
                    if (grupos.isEmpty()) TextView.VISIBLE else TextView.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudieron cargar los grupos: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun agregarGrupo() {
        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarSinFirebase()
            return
        }
        val codigo = generarCodigo()
        val nombre = "Grupo-$codigo"
        val nuevoGrupo = hashMapOf(
            "nombre" to nombre,
            "codigo" to codigo
        )
        coleccionGrupos().add(nuevoGrupo)
            .addOnSuccessListener {
                Toast.makeText(this, "Grupo creado: $nombre", Toast.LENGTH_SHORT).show()
                cargarGrupos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo crear el grupo: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun eliminarGrupoSeleccionado() {
        val grupo = adapter.obtenerSeleccionado()
        if (grupo == null) {
            Toast.makeText(this, "Selecciona primero un grupo de la lista", Toast.LENGTH_SHORT).show()
            return
        }
        coleccionGrupos().document(grupo.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Grupo eliminado: ${grupo.nombre}", Toast.LENGTH_SHORT).show()
                cargarGrupos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo eliminar: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun mostrarSinFirebase() {
        findViewById<TextView>(R.id.tvSinGrupos).apply {
            text = "Firebase todavía no está configurado (falta google-services.json)"
            visibility = TextView.VISIBLE
        }
    }

    private fun generarCodigo(): String {
        val letras = ('A'..'Z')
        val numeros = ('0'..'9')
        val caracteres = letras + numeros
        return (1..4).map { caracteres[Random.nextInt(caracteres.size)] }.joinToString("")
    }
}
