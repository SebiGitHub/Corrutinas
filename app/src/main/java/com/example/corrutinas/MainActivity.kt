package com.example.corrutinas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.File


class MainActivity : AppCompatActivity() {

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

        editTextTiempo = findViewById(R.id.editTextTiempo)
        buttonIniciar = findViewById(R.id.buttonIniciar)
        buttonDetener = findViewById(R.id.buttonDetener)
        progressBar = findViewById(R.id.progressBar)

        // Inicializar los nuevos botones y TextView
        buttonCalcularPrimos = findViewById(R.id.buttonCalcularPrimos)
        buttonCargarEstado = findViewById(R.id.buttonCargarEstado)
        textViewEstado = findViewById(R.id.textViewEstado)

        // Botón para calcular primos
        buttonCalcularPrimos.setOnClickListener {
            iniciarCalculoComplejo()
        }

        // Botón para cargar el estado del temporizador guardado
        buttonCargarEstado.setOnClickListener {
            lifecycleScope.launch {
                val estado = leerEstadoTemporizador()
                textViewEstado.text = "Estado: $estado"
            }
        }

        buttonIniciar.setOnClickListener {
            val segundos = editTextTiempo.text.toString().toIntOrNull()
            if (segundos != null && segundos > 0) {
                iniciarTemporizador(segundos)
            } else {
                Toast.makeText(this, "Introduce un tiempo válido en segundos", Toast.LENGTH_SHORT).show()
            }
        }

        buttonDetener.setOnClickListener {
            detenerTemporizador()
        }
    }

    private fun iniciarTemporizador(segundos: Int) {
        // Reinicia el progreso y cancela cualquier temporizador anterior
        if (progressBar.progress != 0){
            reiniciarTemporizador()
            progressBar.progress = 0
        }else{
            Toast.makeText(this@MainActivity, "¡Iniciando temporizador!", Toast.LENGTH_LONG).show()
        }


        job = lifecycleScope.launch {
            for (segundo in segundos downTo 0) {
                actualizarProgreso((segundo * 100) / segundos)
                delay(1000)
            }
            Toast.makeText(this@MainActivity, "¡Tiempo completado!", Toast.LENGTH_LONG).show()
        }
    }

    private fun reiniciarTemporizador() {
        job?.cancel()  // Cancela la corrutina si está activa
        Toast.makeText(this, "Temporizador reiniciado", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarProgreso(progreso: Int) {
        progressBar.progress = progreso
    }


    private suspend fun guardarEstadoTemporizador(estado: String) {
        withContext(Dispatchers.IO) {
            val archivo = File(applicationContext.filesDir, "estado_temporizador.txt")
            archivo.writeText(estado)  // Guardamos el estado en el archivo
        }
    }

    private fun detenerTemporizador() {
        job?.cancel()  // Cancela la corrutina si está activa
        val estado = "Temporizador detenido en: ${editTextTiempo.text}"
        Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()

        // Guardar el estado del temporizador en un archivo
        lifecycleScope.launch {
            guardarEstadoTemporizador(estado)
        }
    }

    private suspend fun calcularNumerosPrimos(hasta: Int): List<Int> {
        return withContext(Dispatchers.Default) {
            val primos = mutableListOf<Int>()
            for (i in 2..hasta) {
                if (esPrimo(i)) primos.add(i)
            }
            primos
        }
    }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    // Método para leer el estado guardado del temporizador
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

    private fun iniciarCalculoComplejo() {
        lifecycleScope.launch {
            val resultado = calcularNumerosPrimos(10000)  // Ejemplo de rango grande
            Toast.makeText(this@MainActivity, "Números primos encontrados: ${resultado.size}", Toast.LENGTH_LONG).show()
        }
    }


}
