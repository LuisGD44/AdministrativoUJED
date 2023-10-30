package com.example.administrativoujed;

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
import com.google.firebase.database.FirebaseDatabase

import com.example.administrativoujed.databinding.ActivityMainCompletarBinding
import com.example.administrativoujed.model.Persona

class MainCompletar : AppCompatActivity() {

    private lateinit var binding: ActivityMainCompletarBinding

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
                // No necesitas guardar la selección del Spinner aquí
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aquí puedes manejar el caso en el que no se haya seleccionado nada
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

            val usuario = Persona(nombre, apellidoPaterno, apellidoMaterno,correo, matricula, turno, rama)
            usuario.nombre = nombre
            usuario.apellidoPaterno = apellidoPaterno
            usuario.apellidoMaterno = apellidoMaterno
            usuario.correo = correo
            usuario.matricula = matricula
            usuario.turno = turno
            usuario.rama = rama



            val usere = Persona(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                matricula,
                turno,
                rama,
                correo
            )


            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val correoMainActivity = sharedPreferences.getString("correo", null)

            val correoIngresado = correo
            if (correoMainActivity != null && correoMainActivity == correoIngresado) {
                // Los correos coinciden, puedes guardar los datos en Firebase
                val usuario = Persona(nombre, apellidoPaterno, apellidoMaterno, correo, matricula, turno, rama)

                // Guardar los datos en Firebase
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("usuarios")

                val key = myRef.push().key

                if (key != null) {
                    myRef.child(key).setValue(usuario)
                }

            Toast.makeText(baseContext,"Se completo tu registro con exito", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainPerfil::class.java)
            startActivity(intent)}else{
                Toast.makeText(baseContext, "El correo debe ser el mismo que usaste para iniciar sesión", Toast.LENGTH_SHORT).show()

            }
        }

    }
}
