package com.example.aprendeconmigo

/** Representa un tema de estudio creado por el maestro dentro de un grupo. */
data class Tema(
    val id: String = "",
    val titulo: String = "",
    val grupoId: String = "",
    val grupoNombre: String = ""
)
