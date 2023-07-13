package edu.mx.ti.idgs.ddi.tarea07_200559.presentation

data class WeatherResponse(
    val main: Main
)

data class Main(
    val temp: Float
)