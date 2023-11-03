package com.example.administrativoujed

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.administrativoujed.databinding.ActivityDescuentosBinding
import com.example.administrativoujed.databinding.FragmentDashboardBinding

class descuentos : AppCompatActivity() {

    private var _binding: ActivityDescuentosBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_descuentos)

        val btndental: Button = findViewById(R.id.btnDental)
        btndental.setOnClickListener {
            val intent = Intent(this, dental::class.java)
            startActivity(intent)
        }

        val btnlentes: Button = findViewById(R.id.btnLentes)
        btnlentes.setOnClickListener {
            val intent = Intent(this, lentes::class.java)
            startActivity(intent)
        }
    }


}