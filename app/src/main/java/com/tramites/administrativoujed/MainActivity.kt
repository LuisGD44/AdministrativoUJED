package com.tramites.administrativoujed

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tramites.administrativoujed.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import  com.tramites.administrativoujed.RecordarPassActivity


class MainActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth:FirebaseAuth
    private  lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val NOTIFICATION_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solicitarPermisosNotificacion()

        val btnIngresar : Button = findViewById(R.id.btnIngresar)
        val txtEmail : EditText = findViewById(R.id.inputEmail)
        val txtPassword : EditText = findViewById(R.id.inputPassword)
        val btnCrearCuenta: TextView = findViewById(R.id.btnCrearCuenta)
        val btnOlvidar : TextView = findViewById(R.id.btnOlvidar)

        firebaseAuth= Firebase.auth
        btnIngresar.setOnClickListener()
        {
            //Validacion para ver si hay valores dentro de los campos
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(baseContext, "Por favor ingrese  email y/o contraseña ", Toast.LENGTH_SHORT).show()
            }
            else
            {
                //Llamado de la funcion singIn al boton de ingresar
                signIn(txtEmail.text.toString(), txtPassword.text.toString())


                txtEmail.text.clear()
                txtPassword.text.clear()
            }
        }
        btnCrearCuenta.setOnClickListener()
        {
            val i = Intent(this, CrearCuentaActivity::class.java)
            startActivity(i)
        }
        btnOlvidar.setOnClickListener()
        {
            val i = Intent(this, RecordarPassActivity::class.java)
            startActivity(i)
        }
    }
    private fun solicitarPermisosNotificacion() {
        // Verificar y solicitar permisos si es necesario
        if (ContextCompat.checkSelfPermission(
                this,
                "com.google.android.c2dm.permission.RECEIVE"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf("com.google.android.c2dm.permission.RECEIVE"),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    private fun signIn(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).
        addOnCompleteListener(this) { task ->
            if (task.isSuccessful){
                val user = firebaseAuth.currentUser
                val verfica = user?.isEmailVerified
                if (verfica==true){

                    Toast.makeText(baseContext,"Credenciales Correctas", Toast.LENGTH_SHORT).show()


                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("correo", email)
                    editor.apply()

                    val i = Intent(this, MainPerfil::class.java)
                    startActivity(i)

                }else{
                    Toast.makeText(baseContext,"No se autentico el correo", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(baseContext, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                //redireccion a contraseña incorrecta
            }
        }
    }


}