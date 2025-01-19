package com.example.corrutinas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    // Declaración de elementos de la interfaz
    private lateinit var editTextTiempo: EditText
    private lateinit var buttonIniciar: Button
    private lateinit var buttonDetener: Button
    private lateinit var buttonCalcularPrimos: Button
    private lateinit var buttonCargarEstado: Button
    private lateinit var textViewEstado: TextView
    private lateinit var progressBar: ProgressBar

    private var job: Job? = null  // Corrutina que controla el temporizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de los elementos de la interfaz
        editTextTiempo = findViewById(R.id.editTextTiempo)
        buttonIniciar = findViewById(R.id.buttonIniciar)
        buttonDetener = findViewById(R.id.buttonDetener)
        progressBar = findViewById(R.id.progressBar)
        buttonCalcularPrimos = findViewById(R.id.buttonCalcularPrimos)
        buttonCargarEstado = findViewById(R.id.buttonCargarEstado)
        textViewEstado = findViewById(R.id.textViewEstado)

        // Botón para calcular números primos con el número ingresado
        buttonCalcularPrimos.setOnClickListener {
            val numero = editTextTiempo.text.toString().toIntOrNull()
            if (numero != null && numero > 1) {
                iniciarCalculoComplejo(numero)
            } else {
                Toast.makeText(this, "Introduce un número válido mayor que 1", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para cargar el estado del temporizador desde un archivo
        buttonCargarEstado.setOnClickListener {
            lifecycleScope.launch {
                val estado = leerEstadoTemporizador()
                textViewEstado.text = "Estado: $estado"
            }
        }

        // Botón para iniciar el temporizador
        buttonIniciar.setOnClickListener {
            val segundos = editTextTiempo.text.toString().toIntOrNull()
            if (segundos != null && segundos > 0) {
                iniciarTemporizador(segundos)
            } else {
                Toast.makeText(this, "Introduce un tiempo válido en segundos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para detener el temporizador
        buttonDetener.setOnClickListener {
            detenerTemporizador()
        }
    }

    // Función para iniciar el temporizador de cuenta regresiva
    private fun iniciarTemporizador(segundos: Int) {
        if (progressBar.progress != 0) {
            reiniciarTemporizador()
            progressBar.progress = 0
        } else {
            Toast.makeText(this@MainActivity, "¡Iniciando temporizador!", Toast.LENGTH_LONG).show()
        }

        job = lifecycleScope.launch {
            for (segundo in segundos downTo 0) {
                actualizarProgreso((segundo * 100) / segundos) // Actualiza la barra de progreso
                delay(1000) // Espera 1 segundo
            }
            Toast.makeText(this@MainActivity, "¡Tiempo completado!", Toast.LENGTH_LONG).show()
        }
    }

    // Reinicia el temporizador cancelando la corrutina
    private fun reiniciarTemporizador() {
        job?.cancel()
        Toast.makeText(this, "Temporizador reiniciado", Toast.LENGTH_SHORT).show()
    }

    // Actualiza la barra de progreso
    private fun actualizarProgreso(progreso: Int) {
        progressBar.progress = progreso
    }

    // Guarda el estado del temporizador en un archivo
    private suspend fun guardarEstadoTemporizador(estado: String) {
        withContext(Dispatchers.IO) {
            val archivo = File(applicationContext.filesDir, "estado_temporizador.txt")
            archivo.writeText(estado)
        }
    }

    // Detiene el temporizador y guarda su estado
    private fun detenerTemporizador() {
        job?.cancel()
        val estado = "Temporizador detenido en: ${editTextTiempo.text}"
        Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            guardarEstadoTemporizador(estado)
        }
    }

    // Función que calcula los números primos hasta un número dado
    private suspend fun calcularNumerosPrimos(hasta: Int): List<Int> {
        return withContext(Dispatchers.Default) {
            val primos = mutableListOf<Int>()
            for (i in 2..hasta) {
                if (esPrimo(i)) primos.add(i)
            }
            primos
        }
    }

    // Función para verificar si un número es primo
    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    // Lee el estado del temporizador desde el archivo
    private suspend fun leerEstadoTemporizador(): String {
        return withContext(Dispatchers.IO) {
            val archivo = File(applicationContext.filesDir, "estado_temporizador.txt")
            if (archivo.exists()) {
                archivo.readText()
            } else {
                "No hay estado guardado."
            }
        }
    }

    // Inicia el cálculo de números primos en una corrutina
    private fun iniciarCalculoComplejo(numero: Int) {
        lifecycleScope.launch {
            val resultado = calcularNumerosPrimos(numero)
            Toast.makeText(this@MainActivity, "Números primos encontrados: ${resultado.size}", Toast.LENGTH_LONG).show()
        }
    }
}
