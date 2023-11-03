package com.example.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar
import com.example.administrativoujed.databinding.ActivityExentosBinding
import com.example.administrativoujed.databinding.ActivityPerfilBinding
import com.example.administrativoujed.databinding.FragmentDashboardBinding


class exentos : AppCompatActivity() {
    private var _binding: ActivityExentosBinding? = null


    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exentos)


        val btnHijo: Button = findViewById(R.id.btnHijo)
        btnHijo.setOnClickListener {
            val intent = Intent(this, exentohijo::class.java)
            startActivity(intent)
        }
        val btnTrabajador: Button = findViewById(R.id.btnTrabajador)
        btnTrabajador.setOnClickListener {
            val intent = Intent(this, exentoTrabajador::class.java)
            startActivity(intent)
        }
    }


}