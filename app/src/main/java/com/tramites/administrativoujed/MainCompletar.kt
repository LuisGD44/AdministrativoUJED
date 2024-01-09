package com.tramites.administrativoujed;

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.tramites.administrativoujed.MainPerfil
import com.google.firebase.firestore.FirebaseFirestore
import com.tramites.administrativoujed.model.Persona

class MainCompletar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_completar)

        val spinnerTurno: Spinner = findViewById(R.id.txt_Turno)
        val btnCompletar: Button = findViewById(R.id.btnCompletar)

        // Define las opciones para el Spinner
        val opciones = arrayOf("Mañana", "Intermedio", "Tarde")

        // Crea un adaptador para el Spinner
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)

        // Especifica el diseño de la lista desplegable
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el adaptador al Spinner
        spinnerTurno.adapter = adaptador

        // Define un listener para manejar la selección del usuario
        spinnerTurno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        btnCompletar.setOnClickListener {
            // Obtener los valores de los campos del formulario
            val nombre = findViewById<EditText>(R.id.txt_nombre).text.toString()
            val apellidoPaterno = findViewById<EditText>(R.id.txt_apellidoP).text.toString()
            val apellidoMaterno = findViewById<EditText>(R.id.txt_apellidoM).text.toString()
            val correo = findViewById<EditText>(R.id.txt_correo).text.toString()
            val matricula = findViewById<EditText>(R.id.txt_matricula).text.toString()
            val turno = spinnerTurno.selectedItem.toString()
            val rama = findViewById<EditText>(R.id.txtRama).text.toString()

            // Guardar los datos en Cloud Firestore
            val db = FirebaseFirestore.getInstance()
            val usuariosRef = db.collection("usuarios")

            val nuevoUsuario = hashMapOf(
                "nombre" to nombre,
                "apellidoPaterno" to apellidoPaterno,
                "apellidoMaterno" to apellidoMaterno,
                "correo" to correo,
                "matricula" to matricula,
                "turno" to turno,
                "rama" to rama
                // Puedes agregar más campos según tus necesidades
            )

            usuariosRef.document(matricula).set(nuevoUsuario)
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Datos guardados en Firestore con éxito", Toast.LENGTH_SHORT).show()

                    // Pasar a la siguiente actividad si es necesario
                    val intent = Intent(this, MainPerfil::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
