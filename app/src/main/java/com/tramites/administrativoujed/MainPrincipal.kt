package com.tramites.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainPrincipal : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navegation: BottomNavigationView


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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
          R.id.btnSalir-> {
              sinOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/
    // Función para cerrar sesión
    private fun sinOut() {
        firebaseAuth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}