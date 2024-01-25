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
        val spinnerRama: Spinner = findViewById(R.id.txtRama)
        val spinnerTipo: Spinner = findViewById(R.id.txtTipo)
        val btnCompletar: Button = findViewById(R.id.btnCompletar)

        val unidadesAcademicas = listOf(
          "Unidad Academica",  "Adscripcion", "ABOGADO GENERAL", "BIBLIOTECA CENTRAL SGA", "BUFETE JURIDICO", "CENTRO DE DESARROLLO CULTURAL",
            "CENTRO DE DESARROLLO DEPORTE UNIV. (NUEVO)", "CENTRO DE GRADUADOS", "COLEGIO DE CIENCIAS Y HUMANIDADES",
            "COMPRAS SGAD", "COMUNICACION SOCIAL (NUEVA)", "CONTRALORIA", "COORD TELECOMUNICACION INFORMATICA SGAD",
            "COORDINACION DE OBRAS SGAD", "CORRESPONDENCIA Y MENSAJERIA SGAD", "DIR. DE EXTENSION Y VINCULACION S.(NUEVA)",
            "DIR.DESARROLLO Y GESTION DE REC HUM SGAD", "DIRECCION INSTITUCIONAL DEL POSGRADO E INVESTIGACION",
            "DIRECCION PLANEACION INSTITUCIONAL SGA", "DIRECCION SERVICIOS EDUCATIVOS SGA", "DIRECCION SERVICIOS ESCOLARES DSE",
            "EDITORIAL (NUEVA)", "ESC PREPARATORIA DIURNA", "ESCUELA DE CIENCIAS Y TECNOLOGÍAS", "ESCUELA DE LENGUAS",
            "ESCUELA DE PINTURA ESCULTURA ARTESANIAS", "ESCUELA PREPARATORIA NOCTURNA", "ESCUELA SUPERIOR DE MUSICA",
            "FAC DE MEDICINA VETERINARIA Y ZOOTECNIA", "FACULTAD CIENCIAS QUIMICAS DURANGO",
            "FACULTAD DE CIENCIAS DE LA CULTURA FISICA Y DEPORTE", "FACULTAD DE CIENCIAS EXACTAS",
            "FACULTAD DE CIENCIAS FORESTALES", "FACULTAD DE DERECHO Y CIENCIAS POLITICAS",
            "FACULTAD DE ECONOMIA, CONTADURIA Y ADMINISTRACION", "FACULTAD DE ENFERMERIA Y OBSTETRICIA",
            "FACULTAD DE MEDICINA Y NUTRICION", "FACULTAD DE ODONTOLOGIA",
            "FACULTAD DE PSICOLOGIA Y TERAPIA DE LA COMUNICACION HUMANA", "FACULTAD DE TRABAJO SOCIAL", "HOSPITAL VETERINARIO",
            "INST. DE CIENCIAS SOCIALES", "INST. DE INVEST. HISTORICAS", "INST. DE INVEST. JURIDICAS",
            "INST. DE SILVICULT. E IND. MAD", "INSTITUTO DE BELLAS ARTES", "INSTITUTO DE INVESTIGACION CIENTIFICA",
            "LIBRERIA (NUEVA)", "MUSEO REGIONAL", "RADIO UNIVERSIDAD (NUEVA)", "RECTORIA",
            "RELACIONES LABORALES (NUEVA)", "RELACIONES PUBLICAS SGAD", "SERVICIOS GENERALES SGAD",
            "SISTEMA UNIVERSIDAD VIRTUAL SGA", "SPAUJED", "STAUJED", "STEUJED", "SUBSECRETARIA GENERAL ACADEMICA",
            "SUBSECRETARIA GENERAL ADMINISTRATIVA", "TESORERIA", "TV UJED (NUEVA)"
        )
        // Define las opciones para el Spinner
        val opciones = arrayOf("Turno","Mañana", "Intermedio", "Tarde")

        val tipo = arrayOf("Tipo trabajador","Base", "Jubilado")

        // Crea un adaptador para el Spinner
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        val unidadesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidadesAcademicas)
        val tipoTrabajador = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipo)

        // Especifica el diseño de la lista desplegable
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoTrabajador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el adaptador al Spinner
        spinnerTurno.adapter = adaptador
        spinnerRama.adapter = unidadesAdapter
        spinnerTipo.adapter = tipoTrabajador


        // Define un listener para manejar la selección del usuario
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Manejar la selección del spinnerTurno
                if (parent == spinnerTurno) {
                    // Lógica para spinnerTurno
                } else if (parent == spinnerRama) {
                    // Lógica para spinnerRama
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(baseContext, "Por favor selecioona una opcion", Toast.LENGTH_SHORT).show()            }
        }

        spinnerTurno.onItemSelectedListener = spinnerListener
        spinnerRama.onItemSelectedListener = spinnerListener
        spinnerTipo.onItemSelectedListener = spinnerListener

        btnCompletar.setOnClickListener {
            // Obtener los valores de los campos del formulario
            val nombre = findViewById<EditText>(R.id.txt_nombre).text.toString()
            val apellidoPaterno = findViewById<EditText>(R.id.txt_apellidoP).text.toString()
            val apellidoMaterno = findViewById<EditText>(R.id.txt_apellidoM).text.toString()
            val correo = findViewById<EditText>(R.id.txt_correo).text.toString()
            val matricula = findViewById<EditText>(R.id.txt_matricula).text.toString()
            val turno = spinnerTurno.selectedItem.toString()
            val adscripcion = spinnerRama.selectedItem.toString()
            val tipoTrabajador = spinnerTipo.selectedItem.toString()


            // Guardar los datos en Cloud Firestore
            val db = FirebaseFirestore.getInstance()
            val usuariosRef = db.collection("usuarios")

            val nuevoUsuario = hashMapOf(
                "nombre" to nombre,
                "apellido_paterno" to apellidoPaterno,
                "apellido_materno" to apellidoMaterno,
                "correo" to correo,
                "matricula" to matricula,
                "turno" to turno,
                "adscripcion" to adscripcion,
                "tipoTrabajador" to tipoTrabajador
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
