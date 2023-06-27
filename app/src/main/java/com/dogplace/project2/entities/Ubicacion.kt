package com.dogplace.project2.entities

data class Ubicacion(
    val id: Int,
    val latitud: Double?,
    val longitud: Double?,
    val nombreEstablecimiento: String,
    val usuario: String?,
    val tipoEstablecimiento: String,
    val numLikes: Int?,
    val numDislikes: Int?,
    val distancia: Double?

)