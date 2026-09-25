package com.example.aprendeconmigo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TemaAdapter(
    private val temas: MutableList<Tema>,
    private val onTemaClick: (Tema) -> Unit
) : RecyclerView.Adapter<TemaAdapter.TemaViewHolder>() {

    private var seleccionado: Tema? = null

    inner class TemaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloTema)
        val tvGrupo: TextView = view.findViewById(R.id.tvGrupoDelTema)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tema, parent, false)
        return TemaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemaViewHolder, position: Int) {
        val tema = temas[position]
        holder.tvTitulo.text = tema.titulo
        holder.tvGrupo.text = tema.grupoNombre
        holder.itemView.alpha = if (seleccionado?.id == tema.id) 1f else 0.85f
        holder.itemView.setOnClickListener {
            seleccionado = if (seleccionado?.id == tema.id) null else tema
            notifyDataSetChanged()
            onTemaClick(tema)
        }
    }

    override fun getItemCount(): Int = temas.size

    fun actualizar(nuevaLista: List<Tema>) {
        temas.clear()
        temas.addAll(nuevaLista)
        seleccionado = null
        notifyDataSetChanged()
    }

    fun obtenerSeleccionado(): Tema? = seleccionado
}
