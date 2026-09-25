package com.example.aprendeconmigo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumnoProgresoAdapter(
    private val alumnos: List<Alumno>
) : RecyclerView.Adapter<AlumnoProgresoAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val tvPorcentaje: TextView = view.findViewById(R.id.tvPorcentajeAlumno)
        val progress: ProgressBar = view.findViewById(R.id.progressAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno_progreso, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = alumnos[position]
        holder.tvNombre.text = alumno.nombre
        holder.tvPorcentaje.text = "${alumno.progreso}%"
        holder.progress.progress = alumno.progreso
    }

    override fun getItemCount(): Int = alumnos.size
}
