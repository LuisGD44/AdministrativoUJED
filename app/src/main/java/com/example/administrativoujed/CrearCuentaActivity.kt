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

class CrearCuentaActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
        val txtNombre_nuevo : TextView = findViewById(R.id.inputCrearCuenta)
        val txtEmail_nuevo : TextView = findViewById(R.id.inputEmail1)
        val txtPassword1 : TextView = findViewById(R.id.inputPassword1)
        val txtPassword2 : TextView = findViewById(R.id.inputRepetirContraseña)
        val btnCrear : Button = findViewById(R.id.btnRegistrar)
        btnCrear.setOnClickListener()
        {
            val pass1 = txtPassword1.text.toString()
            val pass2 = txtPassword2.text.toString()
            val nombre2 = txtNombre_nuevo.text.toString()
            val email = txtEmail_nuevo.text.toString()

            if (nombre2.isEmpty() || email.isEmpty() || pass1.isEmpty() || pass2.isEmpty())
                {
                    Toast.makeText(baseContext, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
                else if (pass1 == pass2)
            {
                crearCuenta(txtEmail_nuevo.text.toString(), txtPassword1.text.toString())
                Toast.makeText(baseContext, "Se creo el usuario exitosamente", Toast.LENGTH_SHORT).show()

            }
            else
            {
                Toast.makeText(baseContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                txtPassword1.requestFocus()
            }
        }

        firebaseAuth= Firebase.auth
    }
    private fun crearCuenta( email: String, password:String)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener (this){ task ->
                if (task.isSuccessful){
                    sendEmailVerification()
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
                else
                {
                Toast.makeText(baseContext, "Algo salio mal", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun sendEmailVerification()
    {
        val  user = firebaseAuth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this){task ->
            if (task.isSuccessful)
            {
                Toast.makeText(baseContext, "Se envio un email de verificacion", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(baseContext, "Algo salio mal", Toast.LENGTH_SHORT).show()
            }
        }


    }
}