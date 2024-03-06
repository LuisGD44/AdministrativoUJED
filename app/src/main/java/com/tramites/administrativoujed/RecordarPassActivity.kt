package com.tramites.administrativoujed

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tramites.administrativoujed.databinding.ActivityRecordarPassBinding

class RecordarPassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordarPassBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordarPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accede a la vista a través de 'binding'
        binding.btnBackRecordar?.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnEnviar.setOnClickListener {
            val email = binding.inputEmailCambio.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(baseContext, "Por favor ingresa un correo electrónico", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordReset(email)
            }
        }
    }


    private fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Correo Enviado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra la actividad actual después de iniciar la actividad principal
            }
            .addOnFailureListener { e ->
                Toast.makeText(baseContext, "Algo salió mal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
