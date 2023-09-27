package com.example.administrativoujed

import com.example.administrativoujed.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.administrativoujed.databinding.ActivityCalendarioBinding
import com.example.administrativoujed.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.administrativoujed.Inicio
import com.example.administrativoujed.databinding.ActivityMainPerfilBinding
import androidx.fragment.app.replace


class MainPerfil : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Inicio())


        binding.bottom_navigation.setOnItemSelectedListener { item: MenuItem ->
                when (item.itemId){
                R.id.inicio -> replaceFragment(Inicio())
                R.id.tramites -> replaceFragment(Tramites())
                R.id.notificaciones -> replaceFragment(Notificaciones())
                R.id.calendario -> replaceFragment(Calendario())

                else -> {

                }

            }
            true
        }

    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }



    //funcion desloguarse con boton atras
    private var backButtonPressedCount = 0
    private val maxBackButtonPressCount = 3


    override fun onBackPressed() {
        if (backButtonPressedCount < maxBackButtonPressCount - 1) {
            backButtonPressedCount++
            Toast.makeText(this, "Presiona Atrás ${maxBackButtonPressCount - backButtonPressedCount} veces más para cerrar sesion.", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

}