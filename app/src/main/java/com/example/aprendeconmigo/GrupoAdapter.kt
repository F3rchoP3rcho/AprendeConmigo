package com.example.aprendeconmigo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GrupoAdapter(
    private val grupos: MutableList<Grupo>,
    private val onGrupoClick: (Grupo) -> Unit
) : RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {

    private var seleccionado: Grupo? = null

    inner class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreGrupo)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigoGrupo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]
        holder.tvNombre.text = grupo.nombre
        holder.tvCodigo.text = "Código: ${grupo.codigo}"
        holder.itemView.alpha = if (seleccionado?.id == grupo.id) 1f else 0.85f
        holder.itemView.setOnClickListener {
            seleccionado = if (seleccionado?.id == grupo.id) null else grupo
            notifyDataSetChanged()
            onGrupoClick(grupo)
        }
    }

    override fun getItemCount(): Int = grupos.size

    fun actualizar(nuevaLista: List<Grupo>) {
        grupos.clear()
        grupos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    fun obtenerSeleccionado(): Grupo? = seleccionado
}
