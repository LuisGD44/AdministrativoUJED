package com.tramites.administrativoujed

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.tramites.administrativoujed.ui.dashboard.DashboardFragment
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class permiso : AppCompatActivity() {
    private val puestos = listOf(
        "Puesto","ARCHIVISTA", "AFANADOR", "ASISTENTE DENTAL", "BIBLIOTECARIO", "CAJERO (A)",
        "CAPTURISTA", "CHOFER", "CORRALERO", "GUIA", "JARDINERO", "LABORATORISTA",
        "MENSAJERO", "ORDEÑADOR", "PASTURERO", "PEON DE CAMPO", "POLIVALENTE",
        "SECRETARIA", "SERVICIOS GENERALES", "TRACTORISTA", "VELADOR EVENTUAL", "VIGILANTE", "VIVERISTA"
    )

    private val tiposPermiso = listOf(
        "Tipo de permiso",  "PERMISO SIN GOCE DE SUELDO", "COMISIÓN SINDICAL", "CUIDADO MATERNAL", "FALTAS",
        "GRAVIDEZ", "INCAPACIDAD TEMPORAL", "PERMISO ADICIONAL", "PERMISO CON GOCE DE SUELDO",
        "PERMISO ECONÓMICO", "PERMISO EXAMEN PROFESIONAL", "PERMISO FUNERALES", "PERMISO SINDICAL",
        "PERMISO ASISTENCIA", "VACACIONES LAB. INSALUBRES"
    )

    private val turnos = listOf("Turno","MATUTINO", "VESPERTINO", "INTERMEDIO")

    private val unidadesAcademicas = listOf(
        "Unidad Academica",  "ABOGADO GENERAL", "BIBLIOTECA CENTRAL SGA", "BUFETE JURIDICO", "CENTRO DE DESARROLLO CULTURAL",
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

    private val db = FirebaseFirestore.getInstance()
    private val informacionPermisoRef = db.collection("informacionPermiso")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permiso)

        val txtFecha = findViewById<EditText>(R.id.txt_fecha)

        txtFecha.setOnClickListener {
            mostrarDatePicker()
        }


        val spinnerPuesto = findViewById<Spinner>(R.id.txt_puesto)
        val spinnerPermiso = findViewById<Spinner>(R.id.txt_permiso)
        val spinnerTurno = findViewById<Spinner>(R.id.Turno)
        val spinnerUA = findViewById<Spinner>(R.id.UA)

        // Crear un ArrayAdapter para el Spinner de Puesto
        val adapterPuesto = ArrayAdapter(this, android.R.layout.simple_spinner_item, puestos)
        adapterPuesto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPuesto.adapter = adapterPuesto

        // Crear un ArrayAdapter para el Spinner de Tipo de Permiso
        val adapterPermiso = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposPermiso)
        adapterPermiso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPermiso.adapter = adapterPermiso

        // Crear un ArrayAdapter para el Spinner de Turno
        val adapterTurno = ArrayAdapter(this, android.R.layout.simple_spinner_item, turnos)
        adapterTurno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTurno.adapter = adapterTurno

        // Crear un ArrayAdapter para el Spinner de Unidad Académica
        val adapterUA = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidadesAcademicas)
        adapterUA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUA.adapter = adapterUA

        val btnExentoDec = findViewById<Button>(R.id.btnpermisos)
        btnExentoDec.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackper)
        btnBack.setOnClickListener {
            onBackPressed()
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

    private fun enviarDatos() {
        val matricula = findViewById<EditText>(R.id.txt_matriculaPermiso).text.toString()
        val txt_puesto = findViewById<Spinner>(R.id.txt_puesto).selectedItem.toString()
        val txt_fecha = findViewById<EditText>(R.id.txt_fecha).text.toString()
        val txt_dias = findViewById<EditText>(R.id.txt_dias).text.toString()
        val txt_observaciones = findViewById<EditText>(R.id.txt_observaciones).text.toString()
        val txt_permiso = findViewById<Spinner>(R.id.txt_permiso).selectedItem.toString()
        val txt_turno = findViewById<Spinner>(R.id.Turno).selectedItem.toString()
        val txt_ua = findViewById<Spinner>(R.id.UA).selectedItem.toString()


        if (matricula.isEmpty() || txt_puesto.isEmpty() || txt_fecha.isEmpty() ||
            txt_observaciones.isEmpty() || txt_permiso.isEmpty() || txt_turno.isEmpty() || txt_ua.isEmpty() || txt_dias.isEmpty()
        ) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }




        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "txt_puesto" to txt_puesto,
            "txt_dias" to txt_dias,
            "txt_fecha" to txt_fecha,
            "txt_observaciones" to txt_observaciones,
            "txt_permiso" to txt_permiso,
            "txt_turno" to txt_turno,
            "txt_ua" to txt_ua
        )

        informacionPermisoRef.add(nuevoDocumento)
            .addOnSuccessListener {
                // Crear un intent para la actividad a la que deseas ir
                val intent = Intent(this, MainPerfil::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE)

                // Crear un NotificationCompat.Builder
                val builder = NotificationCompat.Builder(this, "Notificacion_Permiso")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Solicitud de permiso enviada con éxito.")
                    .setContentText("Haz clic para ver el status de este tramite.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                // Verificar la versión de Android y crear un canal de notificación si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "Notificacion_Permiso",
                        "Nombre del canal",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }

                // Obtener el NotificationManager y mostrar la notificación
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, builder.build())

                Toast.makeText(this, "Solicitud de exento  enviada con éxito.", Toast.LENGTH_SHORT).show()

                // Iniciar la nueva actividad
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }


}
