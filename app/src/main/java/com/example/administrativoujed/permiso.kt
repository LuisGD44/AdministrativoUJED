package com.example.administrativoujed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.administrativoujed.ui.dashboard.DashboardFragment
import com.google.firebase.firestore.FirebaseFirestore

class permiso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permiso)

        val btnExentoDec = findViewById<Button>(R.id.btnpermisos)
        btnExentoDec.setOnClickListener {
            enviarDatos()
        }
    }
    private fun enviarDatos(){
        val txt_matricula = findViewById<EditText>(R.id.txt_matriculaPermiso).text.toString()
        val txt_puesto = findViewById<EditText>(R.id.txt_puesto).text.toString()
        val txt_fecha = findViewById<EditText>(R.id.txt_fecha).text.toString()
        val txt_observaciones = findViewById<EditText>(R.id.txt_observaciones).text.toString()
        val txt_permiso = findViewById<EditText>(R.id.txt_permiso).text.toString()

        if (txt_matricula.isEmpty() || txt_puesto.isEmpty() || txt_fecha.isEmpty() || txt_observaciones.isEmpty() || txt_permiso.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }
        val db = FirebaseFirestore.getInstance()
        val informacionPermisoRef = db.collection("informacionPermiso")
        val nuevoDocumento = hashMapOf(
            "txt_matricula" to txt_matricula,
            "txt_puesto" to txt_puesto,
            "txt_fecha" to txt_fecha,
            "txt_observaciones" to txt_observaciones,
            "txt_permiso" to txt_permiso,

        )
        informacionPermisoRef.add(nuevoDocumento)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardFragment::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }
}