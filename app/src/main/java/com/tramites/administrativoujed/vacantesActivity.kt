package com.tramites.administrativoujed

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class vacantesActivity : AppCompatActivity() {
    private val unidadesAcademicas = listOf(
       "Unidad Academica", "ABOGADO GENERAL", "BIBLIOTECA CENTRAL SGA", "BUFETE JURIDICO", "CENTRO DE DESARROLLO CULTURAL",
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

    private val comentarios = listOf(
        "Comentarios",
        "Ascenso en la misma unidad académica",
        "Moverme de Unidad académica",
        "Cambio de actividad"
    )

    private val db = FirebaseFirestore.getInstance()
    private val vacantesRef = db.collection("vacantes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vacantes)

        val txtFecha = findViewById<EditText>(R.id.editTextFecha)
        val spinnerUnidadAcademica = findViewById<Spinner>(R.id.spinnerUnidadAcademica)
        val spinnerComentario = findViewById<Spinner>(R.id.spinnerComentario)
        val btnEnviar = findViewById<Button>(R.id.btnExentoHijo)

        txtFecha.setOnClickListener{
            mostrarDatePicker()
        }

        // Crear un ArrayAdapter para el Spinner de Unidad Académica
        val adapterUA = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidadesAcademicas)
        adapterUA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnidadAcademica.adapter = adapterUA

        // Crear un ArrayAdapter para el Spinner de Comentario
        val adapterComentario = ArrayAdapter(this, android.R.layout.simple_spinner_item, comentarios)
        adapterComentario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerComentario.adapter = adapterComentario

        btnEnviar.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackVacantes)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun enviarDatos() {
        val matricula = findViewById<EditText>(R.id.matricula).text.toString()
        val fecha = findViewById<EditText>(R.id.editTextFecha).text.toString()
        val perfil = findViewById<EditText>(R.id.editTextPerfil).text.toString()
        val unidadAcademica = findViewById<Spinner>(R.id.spinnerUnidadAcademica).selectedItem.toString()
        val comentario = findViewById<Spinner>(R.id.spinnerComentario).selectedItem.toString()

        if (fecha.isEmpty() || perfil.isEmpty() || unidadAcademica.isEmpty() || comentario.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "fecha" to fecha,
            "perfil" to perfil,
            "unidadAcademica" to unidadAcademica,
            "comentario" to comentario
        )

        vacantesRef.add(nuevoDocumento)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                // Puedes agregar aquí la lógica para redirigir a otra actividad si es necesario
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val añoActual = calendar.get(Calendar.YEAR)
        val mesActual = calendar.get(Calendar.MONTH)
        val diaActual = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, año, mes, dia ->
                val fechaSeleccionada = Calendar.getInstance()
                fechaSeleccionada.set(año, mes, dia)

                val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val fechaFormateada = formatoFecha.format(fechaSeleccionada.time)

                val txtFecha = findViewById<EditText>(R.id.txt_fecha)
                txtFecha.setText(fechaFormateada)
            },
            añoActual,
            mesActual,
            diaActual
        )

        datePickerDialog.show()
    }
}
