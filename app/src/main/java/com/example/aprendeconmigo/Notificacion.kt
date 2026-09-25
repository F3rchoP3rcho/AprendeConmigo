package com.example.aprendeconmigo

/** Notificación de actividad de un alumno dentro de un grupo. */
data class Notificacion(
    val id: String = "",
    val alumnoNombre: String = "",
    val grupoNombre: String = "",
    val mensaje: String = ""
)
