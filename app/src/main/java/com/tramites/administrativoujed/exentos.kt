package com.tramites.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.tramites.administrativoujed.databinding.ActivityExentosBinding

class exentos : AppCompatActivity() {
    private var _binding: ActivityExentosBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnHijo: Button = binding.btnHijo
        btnHijo.setOnClickListener {
            val intent = Intent(this, exentohijo::class.java)
            startActivity(intent)
        }

        val btnTrabajador: Button = binding.btnTrabajador
        btnTrabajador.setOnClickListener {
            val intent = Intent(this, exentoTrabajador::class.java)
            startActivity(intent)
        }

        val btnNieto: Button = binding.btnNieto
        btnNieto.setOnClickListener {
            val intent = Intent(this, exentonieto::class.java)
            startActivity(intent)
        }

        val btnDependientes: Button = binding.btnDependientes
        btnDependientes.setOnClickListener {
            val intent = Intent(this, decendientesActivity::class.java)
            startActivity(intent)
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackexe)
        btnBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
