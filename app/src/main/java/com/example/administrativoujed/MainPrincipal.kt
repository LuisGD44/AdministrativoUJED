package com.example.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainPrincipal : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navegation: BottomNavigationView

    private val onNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.frameContainer1 -> {
                // Crea un Intent para iniciar la actividad MainPrincipal (o la actividad deseada)
                val intent = Intent(this, MainPrincipal::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.frameContainer2 -> {
                // Crea un Intent para iniciar la actividad MainTramites
                val intent = Intent(this, MainTramites::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.frameContainer3 -> {
                // Crea un Intent para iniciar la actividad MainNotificaciones
                val intent = Intent(this, MainNotificaciones::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.frameContainer4 -> {
                // Crea un Intent para iniciar la actividad Calendario
                val intent = Intent(this, Calendario::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth = Firebase.auth

        navegation = findViewById(R.id.navAbajo)
        navegation.setOnNavigationItemSelectedListener(onNavMenu)

        supportFragmentManager.commit {
            replace<nav_down>(R.id.frameContainer1)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_salir -> {
                sinOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Función para cerrar sesión
    private fun sinOut() {
        firebaseAuth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}