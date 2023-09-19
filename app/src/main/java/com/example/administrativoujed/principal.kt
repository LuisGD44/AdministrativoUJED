package com.example.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class principal : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        firebaseAuth= Firebase.auth
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
         R.id.menu_salir ->{
             sinOut()
         }
        }
        return super.onOptionsItemSelected(item)
    }

    //Funcion para no poder regresar
    override fun onBackPressed() {
        return
    }

    private  fun sinOut()
    {
        firebaseAuth.signOut()
        val i= Intent(this,MainActivity::class.java)
        startActivity(i)
    }
}