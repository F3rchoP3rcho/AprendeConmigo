package com.example.aprendeconmigo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla "Crea el tema de estudio": el maestro escribe el tema y
 * elige a qué grupo va dirigido. Se guarda en:
 * users/{uid}/grupos/{grupoId}/temas/{temaId}
 */
class CrearTemaActivity : AppCompatActivity() {

    private var grupoSeleccionado: Grupo? = null
    private val opcionesGrupo = mutableListOf<Pair<Grupo, TextView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tema)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        cargarGrupos()

        findViewById<Button>(R.id.btnContinuar).setOnClickListener {
            crearTema()
        }
    }

    private fun cargarGrupos() {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorGrupos)

        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarError("Firebase todavía no está configurado")
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid).collection("grupos")
            .get()
            .addOnSuccessListener { snapshot ->
                contenedor.removeAllViews()
                opcionesGrupo.clear()
                if (snapshot.isEmpty) {
                    mostrarError("Primero crea un grupo en 'Ser.grupos'")
                    return@addOnSuccessListener
                }
                for (doc in snapshot.documents) {
                    val grupo = Grupo(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        codigo = doc.getString("codigo") ?: ""
                    )
                    val opcion = TextView(this).apply {
                        text = grupo.nombre
                        textSize = 14f
                        setTextColor(getColor(R.color.ac_text_primary))
                        setBackgroundResource(R.drawable.bg_option_unselected)
                        setPadding(32, 28, 32, 28)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { bottomMargin = 12 }
                    }
                    opcion.setOnClickListener { seleccionarGrupo(grupo) }
                    contenedor.addView(opcion)
                    opcionesGrupo.add(grupo to opcion)
                }
            }
            .addOnFailureListener {
                mostrarError("No se pudieron cargar los grupos: ${it.localizedMessage}")
            }
    }

    private fun seleccionarGrupo(grupo: Grupo) {
        grupoSeleccionado = grupo
        for ((g, vista) in opcionesGrupo) {
            if (g.id == grupo.id) {
                vista.setBackgroundResource(R.drawable.bg_option_selected)
                vista.setTextColor(getColor(R.color.white))
            } else {
                vista.setBackgroundResource(R.drawable.bg_option_unselected)
                vista.setTextColor(getColor(R.color.ac_text_primary))
            }
        }
    }

    private fun crearTema() {
        val titulo = findViewById<EditText>(R.id.etTema).text.toString().trim()
        val grupo = grupoSeleccionado

        if (titulo.isEmpty()) {
            mostrarError("Escribe el tema")
            return
        }
        if (grupo == null) {
            mostrarError("Selecciona un grupo")
            return
        }
        if (!FirebaseHelper.estaConfigurado(this)) {
            mostrarError("Firebase todavía no está configurado")
            return
        }

        val progress = findViewById<ProgressBar>(R.id.progressTema)
        progress.visibility = ProgressBar.VISIBLE

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val nuevoTema = hashMapOf("titulo" to titulo)

        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("grupos").document(grupo.id)
            .collection("temas")
            .add(nuevoTema)
            .addOnSuccessListener {
                progress.visibility = ProgressBar.GONE
                Toast.makeText(this, "Tema creado y compartido con ${grupo.nombre}", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                progress.visibility = ProgressBar.GONE
                mostrarError("No se pudo guardar: ${it.localizedMessage}")
            }
    }

    private fun mostrarError(mensaje: String) {
        val tv = findViewById<TextView>(R.id.tvErrorTema)
        tv.text = mensaje
        tv.visibility = TextView.VISIBLE
    }
}
