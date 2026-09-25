package com.example.aprendeconmigo

/** Alumno inscrito en un grupo, con su progreso general (0-100). */
data class Alumno(
    val id: String = "",
    val nombre: String = "",
    val progreso: Int = 0
)
