package com.example.aprendeconmigo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificacionAdapter(
    private val notificaciones: List<Notificacion>
) : RecyclerView.Adapter<NotificacionAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAlumno: TextView = view.findViewById(R.id.tvAlumnoNotif)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensajeNotif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notif = notificaciones[position]
        holder.tvAlumno.text = "${notif.alumnoNombre}: ${notif.grupoNombre}"
        holder.tvMensaje.text = notif.mensaje
    }

    override fun getItemCount(): Int = notificaciones.size
}
