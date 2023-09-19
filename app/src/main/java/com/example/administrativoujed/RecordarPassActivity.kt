package com.example.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecordarPassActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordar_pass)
        val txtmail : TextView =findViewById(R.id.inputEmailCambio)
        val btnCambiar : Button = findViewById(R.id.btnEnviar)
        btnCambiar.setOnClickListener()
        {
            val email = txtmail.text.toString()
            if (email.isEmpty() )
            {
                Toast.makeText(baseContext, "Por favor ingresa un correo electronico", Toast.LENGTH_SHORT).show()
            }
            else
            {
            sendPasswordReset(txtmail.text.toString())
        }   }

        firebaseAuth= Firebase.auth
    }

    private  fun sendPasswordReset(email: String)
    {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(baseContext,"Correo Enviado con exito", Toast.LENGTH_SHORT).show()
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                else
                {
                    Toast.makeText(baseContext, "Algo salio mal", Toast.LENGTH_SHORT).show()

                }
            }
    }
}