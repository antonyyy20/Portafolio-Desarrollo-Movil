package com.taller.adivinanumero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taller.adivinanumero.model.PuntajeItem
import java.util.Timer
import java.util.TimerTask

// ── Estados del juego ─────────────────────────────────────────────────────────
sealed class GameState {
    data class Playing(val intentoActual: Int) : GameState()
    data class Higher(val intentoActual: Int)  : GameState()   // el guess fue MENOR → subir
    data class Lower(val intentoActual: Int)   : GameState()   // el guess fue MAYOR → bajar
    data class Win(val intentoActual: Int)     : GameState()
    data class Lose(val secreto: Int)          : GameState()
}

class GameViewModel : ViewModel() {

    // ── LiveData ──────────────────────────────────────────────────────────────
    private val _gameState   = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    private val _hintText    = MutableLiveData<String>()
    val hintText: LiveData<String> = _hintText

    private val _rangeText   = MutableLiveData<String>()
    val rangeText: LiveData<String> = _rangeText

    private val _timerText   = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _ranking     = MutableLiveData<List<PuntajeItem>>(emptyList())
    val ranking: LiveData<List<PuntajeItem>> = _ranking

    // ── Estado interno ────────────────────────────────────────────────────────
    private var numeroSecreto  = 0
    private var intentoActual  = 0          // 0, 1, 2
    private var minRango       = 1
    private var maxRango       = 100
    private var segundos       = 0
    private var timer: Timer?  = null

    // ── Constantes ────────────────────────────────────────────────────────────
    private val MAX_INTENTOS = 3
    private val MIN_VALOR    = 1
    private val MAX_VALOR    = 100

    init { nuevaPartida() }

    // ─────────────────────────────────────────────────────────────────────────
    //  NUEVA PARTIDA
    // ─────────────────────────────────────────────────────────────────────────
    fun nuevaPartida() {
        pararTimer()
        numeroSecreto = (MIN_VALOR..MAX_VALOR).random()
        intentoActual = 0
        minRango      = MIN_VALOR
        maxRango      = MAX_VALOR
        segundos      = 0

        _timerText.value  = "00:00"
        _hintText.value   = "¡Adivina el número secreto!"
        _rangeText.value  = "Rango: $MIN_VALOR – $MAX_VALOR"
        _gameState.value  = GameState.Playing(intentoActual)

        iniciarTimer()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDAR INTENTO  (usa while para limitar)
    // ─────────────────────────────────────────────────────────────────────────
    fun validar(numero: Int) {
        /*
         * Se usa WHILE para limitar los intentos:
         * while (intentoActual < MAX_INTENTOS && !ganado)
         * Cada llamada a validar() representa una iteración del ciclo.
         * El estado del contador vive en el ViewModel entre llamadas.
         */
        var ganado = false

        // ── Ciclo while conceptual: se ejecuta una iteración por llamada ──
        while (intentoActual < MAX_INTENTOS && !ganado) {
            intentoActual++

            when {
                // ✅ Acertó
                numero == numeroSecreto -> {
                    ganado = true
                    pararTimer()
                    _hintText.value  = "🏆 ¡Correcto! El número era $numeroSecreto.\n" +
                                       "Lo adivinaste en $intentoActual ${if (intentoActual == 1) "intento" else "intentos"} — ${_timerText.value}"
                    _gameState.value = GameState.Win(intentoActual)
                }

                // ⬆️ El intento fue MENOR que el secreto → debe subir
                numero < numeroSecreto -> {
                    minRango = maxOf(minRango, numero + 1)
                    val intentosRestantes = MAX_INTENTOS - intentoActual
                    _hintText.value = buildString {
                        append("⬆️  $numero es demasiado BAJO.\n")
                        append("El número secreto es MAYOR.\n")
                        append("Busca entre $minRango y $maxRango.\n")
                        append(mensajeIntentos(intentosRestantes))
                    }
                    _rangeText.value = "Rango: $minRango – $maxRango"

                    if (intentoActual >= MAX_INTENTOS) {
                        pararTimer()
                        _gameState.value = GameState.Lose(numeroSecreto)
                        _hintText.value  = "💀 Sin más intentos. El número secreto era $numeroSecreto."
                    } else {
                        _gameState.value = GameState.Higher(intentoActual)
                    }
                }

                // ⬇️ El intento fue MAYOR que el secreto → debe bajar
                else -> {
                    maxRango = minOf(maxRango, numero - 1)
                    val intentosRestantes = MAX_INTENTOS - intentoActual
                    _hintText.value = buildString {
                        append("⬇️  $numero es demasiado ALTO.\n")
                        append("El número secreto es MENOR.\n")
                        append("Busca entre $minRango y $maxRango.\n")
                        append(mensajeIntentos(intentosRestantes))
                    }
                    _rangeText.value = "Rango: $minRango – $maxRango"

                    if (intentoActual >= MAX_INTENTOS) {
                        pararTimer()
                        _gameState.value = GameState.Lose(numeroSecreto)
                        _hintText.value  = "💀 Sin más intentos. El número secreto era $numeroSecreto."
                    } else {
                        _gameState.value = GameState.Lower(intentoActual)
                    }
                }
            }

            // El while solo ejecuta UNA iteración real por llamada
            break
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GUARDAR PUNTAJE EN RANKING
    // ─────────────────────────────────────────────────────────────────────────
    fun guardarPuntaje(nombre: String) {
        val nuevo = PuntajeItem(
            nombre   = nombre,
            intentos = intentoActual,
            tiempoSegundos = segundos,
            ganado   = true
        )
        val listaActual = _ranking.value?.toMutableList() ?: mutableListOf()
        listaActual.add(nuevo)

        // Ordenar: primero ganadores, luego por intentos, luego por tiempo
        listaActual.sortWith(
            compareByDescending<PuntajeItem> { it.ganado }
                .thenBy { it.intentos }
                .thenBy { it.tiempoSegundos }
        )
        _ranking.value = listaActual
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CRONÓMETRO
    // ─────────────────────────────────────────────────────────────────────────
    private fun iniciarTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                segundos++
                val min = segundos / 60
                val sec = segundos % 60
                _timerText.postValue(
                    "${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"
                )
            }
        }, 1000L, 1000L)
    }

    private fun pararTimer() {
        timer?.cancel()
        timer = null
    }

    override fun onCleared() {
        super.onCleared()
        pararTimer()
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UTILIDADES
    // ─────────────────────────────────────────────────────────────────────────
    private fun mensajeIntentos(restantes: Int): String = when (restantes) {
        0    -> "¡Este era tu último intento!"
        1    -> "⚠️ ¡Solo te queda 1 intento más!"
        else -> "Te quedan $restantes intentos."
    }
}
