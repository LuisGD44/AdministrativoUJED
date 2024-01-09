package com.tramites.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase

data class Usuario(

    val matricula: String,
    val email: String,
    val nombre: String? = null,
    val apellidoPaterno: String? = null,
    val apellidoMaterno: String? = null,
    val turno: String? = null,
    val rama: String? = null

)


class CrearCuentaActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        val txtNombre_nuevo : TextView = findViewById(R.id.inputCrearCuenta)
        val txtEmail_nuevo : TextView = findViewById(R.id.inputEmail1)
        val txtMatricula: TextView = findViewById(R.id.inputMatricula)
        val txtPassword1 : TextView = findViewById(R.id.inputPassword1)
        val txtPassword2 : TextView = findViewById(R.id.inputRepetirContraseña)
        val btnCrear : Button = findViewById(R.id.btnRegistrar)

        firebaseAuth = FirebaseAuth.getInstance()


        btnCrear.setOnClickListener()
        {
            val pass1 = txtPassword1.text.toString()
            val pass2 = txtPassword2.text.toString()
            val nombre2 = txtNombre_nuevo.text.toString()
            val email = txtEmail_nuevo.text.toString()
            val matricula = txtMatricula.text.toString()


            if (nombre2.isEmpty() || email.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || matricula.isEmpty())
                {
                    Toast.makeText(baseContext, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
                else if (pass1 == pass2)
            {
                crearCuenta(email, pass1, matricula)
                Toast.makeText(baseContext, "Se creo el usuario exitosamente", Toast.LENGTH_SHORT).show()

            }
            else {
                Toast.makeText(baseContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                txtPassword1.requestFocus()
            }
        }

        firebaseAuth= Firebase.auth
    }
    private fun crearCuenta(email: String, password: String, matricula: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(matricula) // Guarda la matrícula como nombre de usuario
                        .build()

                    user?.updateProfile(userProfileChangeRequest)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Usuario creado con éxito
                                // Guardar la información del usuario en Firebase Realtime Database
                                val usuario = Usuario(matricula, email) // Asegúrate de crear la clase Usuario con los campos necesarios
                                guardarUsuarioEnFirebase(usuario)
                                sendEmailVerification()
                                val i = Intent(this, MainActivity::class.java)
                                startActivity(i)
                            } else {
                                Toast.makeText(baseContext, "Algo salió mal al actualizar el perfil", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Correo electrónico ya registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarUsuarioEnFirebase(usuario: Usuario) {
        // Obtén una referencia a tu base de datos Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("usuarios")

        // Utiliza la matrícula como clave para guardar el usuario
        reference.child(usuario.matricula).setValue(usuario)
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