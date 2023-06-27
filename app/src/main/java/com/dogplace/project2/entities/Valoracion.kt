package com.dogplace.project2.entities

data class Valoracion(

    val id: Int,
    val idUsuario: Int,
    val username: String,
    val commentPlace: String,
    val idEstablecimiento: Int,
    val nombreEstablecimiento: String,
    val likeValue: Int,
    val dislikeValue: Int,
    val fechaHoraCreacion: String
)
