package com.tramites.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.tramites.administrativoujed.databinding.ActivityCuotasBinding
import com.tramites.administrativoujed.databinding.ActivityExentosBinding

class cuotasActivity : AppCompatActivity() {
    private var _binding: ActivityCuotasBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCuotasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnBack: ImageButton = binding.btnBackC
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val btnTrabajadorC: Button = binding.btnTrabajadorC
        btnTrabajadorC.setOnClickListener {
            val intent = Intent(this, cuotaTrabajadorActivity::class.java)
            startActivity(intent)
        }


        val btnHijoC: Button = binding.btnHijoC
        btnHijoC.setOnClickListener {
            val intent = Intent(this, cuotaHijoActivity::class.java)
            startActivity(intent)
        }

        val btnNietoC: Button = binding.btnNietoC
        btnNietoC.setOnClickListener {
            val intent = Intent(this, cuotaNietoActivity::class.java)
            startActivity(intent)
        }

        val btnDepndienteC: Button = binding.btnDependientesC
        btnDepndienteC.setOnClickListener {
            val intent = Intent(this, cuotaDependienteActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
