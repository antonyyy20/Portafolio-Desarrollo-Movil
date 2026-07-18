package com.taller.adivinanumero.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.taller.adivinanumero.R
import com.taller.adivinanumero.viewmodel.GameViewModel
import com.taller.adivinanumero.viewmodel.GameState

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel

    // Views
    private lateinit var tvTimer: TextView
    private lateinit var tvIntentos: TextView
    private lateinit var tvHint: TextView
    private lateinit var tvRango: TextView
    private lateinit var etNumero: EditText
    private lateinit var btnValidar: Button
    private lateinit var btnNuevo: Button
    private lateinit var rvRanking: ListView
    private lateinit var tvNumeroSecreto: TextView
    private lateinit var imgHint: ImageView

    // Dots de intentos
    private lateinit var dot1: TextView
    private lateinit var dot2: TextView
    private lateinit var dot3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        observeViewModel()
        setupListeners()
    }

    private fun initViews() {
        tvTimer        = findViewById(R.id.tvTimer)
        tvIntentos     = findViewById(R.id.tvIntentos)
        tvHint         = findViewById(R.id.tvHint)
        tvRango        = findViewById(R.id.tvRango)
        etNumero       = findViewById(R.id.etNumero)
        btnValidar     = findViewById(R.id.btnValidar)
        btnNuevo       = findViewById(R.id.btnNuevo)
        tvNumeroSecreto = findViewById(R.id.tvNumeroSecreto)
        dot1           = findViewById(R.id.dot1)
        dot2           = findViewById(R.id.dot2)
        dot3           = findViewById(R.id.dot3)
    }

    private fun observeViewModel() {
        // Tiempo del cronómetro
        viewModel.timerText.observe(this) { time ->
            tvTimer.text = time
        }

        // Texto de hint / feedback
        viewModel.hintText.observe(this) { hint ->
            tvHint.text = hint
        }

        // Rango visible
        viewModel.rangeText.observe(this) { range ->
            tvRango.text = range
        }

        // Estado del juego
        viewModel.gameState.observe(this) { state ->
            when (state) {
                is GameState.Playing -> {
                    updateDotsUI(state.intentoActual)
                    btnValidar.isEnabled = true
                    etNumero.isEnabled = true
                    btnNuevo.isEnabled = false
                    tvNumeroSecreto.visibility = android.view.View.GONE
                    setHintBackground(R.color.hint_neutral)
                }
                is GameState.Higher -> {
                    updateDotsUI(state.intentoActual, failIndex = state.intentoActual - 1)
                    setHintBackground(R.color.hint_higher)
                    etNumero.setText("")
                }
                is GameState.Lower -> {
                    updateDotsUI(state.intentoActual, failIndex = state.intentoActual - 1)
                    setHintBackground(R.color.hint_lower)
                    etNumero.setText("")
                }
                is GameState.Win -> {
                    updateDotsUI(state.intentoActual, winIndex = state.intentoActual - 1)
                    setHintBackground(R.color.hint_win)
                    finishGame(won = true)
                }
                is GameState.Lose -> {
                    updateDotsUI(3, failAll = true)
                    setHintBackground(R.color.hint_lose)
                    tvNumeroSecreto.visibility = android.view.View.VISIBLE
                    tvNumeroSecreto.text = "El número era: ${state.secreto}"
                    finishGame(won = false)
                }
            }
        }

        // Ranking
        viewModel.ranking.observe(this) { lista ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                lista.mapIndexed { i, item ->
                    val medal = when (i) { 0 -> "🥇" 1 -> "🥈" 2 -> "🥉" else -> "#${i + 1}" }
                    val tiempo = "${(item.tiempoSegundos / 60).toString().padStart(2, '0')}:${(item.tiempoSegundos % 60).toString().padStart(2, '0')}"
                    "$medal ${item.nombre}  —  ${item.intentos} int.  $tiempo"
                }
            )
            // Si tuvieras un ListView en el layout, lo conectarías aquí:
            // rvRanking.adapter = adapter
        }
    }

    private fun setupListeners() {
        // Solo permite números en el EditText
        etNumero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Limpia caracteres no numéricos si el usuario pega texto
                val limpio = s?.filter { it.isDigit() }?.toString() ?: ""
                if (s.toString() != limpio) {
                    etNumero.setText(limpio)
                    etNumero.setSelection(limpio.length)
                }
            }
        })

        // Teclado: acción "Hecho" equivale a Validar
        etNumero.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                intentarValidar()
                true
            } else false
        }

        btnValidar.setOnClickListener { intentarValidar() }

        btnNuevo.setOnClickListener { viewModel.nuevaPartida() }
    }

    private fun intentarValidar() {
        val texto = etNumero.text.toString()
        if (texto.isBlank()) {
            etNumero.error = "Ingresa un número"
            return
        }
        val numero = texto.toIntOrNull()
        if (numero == null || numero < 1 || numero > 100) {
            etNumero.error = "Debe ser entre 1 y 100"
            return
        }
        etNumero.error = null
        viewModel.validar(numero)
    }

    private fun finishGame(won: Boolean) {
        btnValidar.isEnabled = false
        etNumero.isEnabled = false
        btnNuevo.isEnabled = true

        if (won) {
            // Pedir nombre para el ranking
            val input = EditText(this)
            input.hint = "Tu nombre (máx 16 chars)"
            input.inputType = android.text.InputType.TYPE_CLASS_TEXT
            AlertDialog.Builder(this)
                .setTitle("🏆 ¡Ganaste!")
                .setMessage("¿Con qué nombre quieres aparecer en el ranking?")
                .setView(input)
                .setPositiveButton("Guardar") { _, _ ->
                    val nombre = input.text.toString().trim().take(16).ifBlank { "Jugador" }
                    viewModel.guardarPuntaje(nombre)
                }
                .setNegativeButton("Sin guardar", null)
                .show()
        }
    }

    private fun updateDotsUI(
        intentoActual: Int,
        failIndex: Int = -1,
        winIndex: Int = -1,
        failAll: Boolean = false
    ) {
        val dots = listOf(dot1, dot2, dot3)
        dots.forEachIndexed { i, dot ->
            when {
                failAll -> dot.setDotStyle(DotStyle.FAIL)
                i == winIndex -> dot.setDotStyle(DotStyle.WIN)
                i == failIndex -> dot.setDotStyle(DotStyle.FAIL)
                i == intentoActual -> dot.setDotStyle(DotStyle.ACTIVE)
                i < intentoActual -> dot.setDotStyle(DotStyle.FAIL)
                else -> dot.setDotStyle(DotStyle.IDLE)
            }
        }
    }

    private fun setHintBackground(colorRes: Int) {
        tvHint.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }

    enum class DotStyle { IDLE, ACTIVE, WIN, FAIL }

    private fun TextView.setDotStyle(style: DotStyle) {
        when (style) {
            DotStyle.IDLE   -> { setBackgroundResource(R.drawable.dot_idle);   setTextColor(ContextCompat.getColor(context, R.color.dot_idle_text)) }
            DotStyle.ACTIVE -> { setBackgroundResource(R.drawable.dot_active); setTextColor(ContextCompat.getColor(context, R.color.dot_active_text)) }
            DotStyle.WIN    -> { setBackgroundResource(R.drawable.dot_win);    setTextColor(ContextCompat.getColor(context, R.color.dot_win_text)) }
            DotStyle.FAIL   -> { setBackgroundResource(R.drawable.dot_fail);   setTextColor(ContextCompat.getColor(context, R.color.dot_fail_text)) }
        }
    }
}
