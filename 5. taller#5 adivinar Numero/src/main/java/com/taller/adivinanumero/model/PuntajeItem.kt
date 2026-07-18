package com.taller.adivinanumero.model

/**
 * Representa un puntaje guardado en el ranking.
 *
 * @param nombre          Nombre del jugador
 * @param intentos        Cantidad de intentos que usó
 * @param tiempoSegundos  Tiempo total en segundos
 * @param ganado          true si adivinó el número
 */
data class PuntajeItem(
    val nombre         : String,
    val intentos       : Int,
    val tiempoSegundos : Int,
    val ganado         : Boolean
)
