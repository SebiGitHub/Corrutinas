package com.example.corrutinas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTiempo: EditText
    private lateinit var buttonIniciar: Button
    private lateinit var buttonDetener: Button
    private lateinit var progressBar: ProgressBar

    private var job: Job? = null  // Corrutina que controla el temporizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTiempo = findViewById(R.id.editTextTiempo)
        buttonIniciar = findViewById(R.id.buttonIniciar)
        buttonDetener = findViewById(R.id.buttonDetener)
        progressBar = findViewById(R.id.progressBar)

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

    private fun detenerTemporizador() {
        job?.cancel()  // Cancela la corrutina si está activa
        Toast.makeText(this, "Temporizador detenido", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarProgreso(progreso: Int) {
        progressBar.progress = progreso
    }
}
