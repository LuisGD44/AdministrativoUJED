package com.tramites.administrativoujed

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class exentonieto : AppCompatActivity() {
    private lateinit var txtPresencialNieto: Spinner
    private lateinit var txtPeriodo: Spinner
    private lateinit var txtEscuela: Spinner
    private lateinit var txtEscolarizadoNieto: Spinner
    private lateinit var actaHijoUri: Uri
    private lateinit var actaNietoUri: Uri
    private lateinit var talonUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exentonieto)

        txtPresencialNieto = findViewById(R.id.txtPresencialNieto)
        txtEscolarizadoNieto = findViewById(R.id.txtEscolarizadoNieto)
        txtPeriodo = findViewById(R.id.txtPeriodoNieto)
        txtEscuela = findViewById(R.id.txtEscuelaN)

        val presencialAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.presencial,
            android.R.layout.simple_spinner_item
        )
        presencialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtPresencialNieto.adapter = presencialAdapter

        val periodoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periodo,
            android.R.layout.simple_spinner_item
        )
        periodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtPeriodo.adapter = periodoAdapter

        val escuelaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escuela,
            android.R.layout.simple_spinner_item
        )
        escuelaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscuela.adapter = presencialAdapter


        val escolarizadoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escolarizado,
            android.R.layout.simple_spinner_item
        )
        escolarizadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscolarizadoNieto.adapter = escolarizadoAdapter

        val btnTalon = findViewById<Button>(R.id.btnTalon)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        // Agregar un OnClickListener al botón para seleccionar el acta de nacimiento del hijo
        val btnActaHijo = findViewById<Button>(R.id.actaHijo)
        btnActaHijo.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_HIJO)
        }

        // Agregar un OnClickListener al botón para seleccionar el acta de nacimiento del nieto
        val btnActaNieto = findViewById<Button>(R.id.actaNieto)
        btnActaNieto.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_NIETO)
        }

        // Agregar un OnClickListener al botón para enviar datos
        val btnExentoNieto = findViewById<Button>(R.id.btnExentoHijo)
        btnExentoNieto.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackexNieto)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun seleccionarArchivo(pickImageRequest: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_ACTA_HIJO -> {
                    actaHijoUri = data.data!!
                }
                PICK_IMAGE_REQUEST_ACTA_NIETO -> {
                    actaNietoUri = data.data!!
                }
                PICK_IMAGE_REQUEST_TALON -> {
                    talonUri = data.data!!
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajador).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matriculaAlunmo).text.toString()
        val nombreAlumno = findViewById<EditText>(R.id.txt_NombreAlumno).text.toString()
        val semestre = findViewById<EditText>(R.id.txt_semestre).text.toString()
        val escuela = txtEscuela.selectedItem.toString()
        val periodo = txtPeriodo.selectedItem.toString()
        val presencialNieto = txtPresencialNieto.selectedItem.toString()
        val escolarizadoNieto = txtEscolarizadoNieto.selectedItem.toString()

        if (matricula.isEmpty() || matriculaAlumno.isEmpty() || semestre.isEmpty() || escuela.isEmpty() || actaHijoUri == null || actaNietoUri == null || talonUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione los archivos.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaHijoFileName = "acta_hijo_${UUID.randomUUID()}"
        val actaNietoFileName = "acta_nieto_${UUID.randomUUID()}"
        val talonFileName = "talon_${UUID.randomUUID()}"

        // Subir todas las imágenes
        val urls = mutableListOf<String>()

        // Subir actaHijo
        subirArchivo(actaHijoUri, actaHijoFileName) { url ->
            urls.add(url)

            // Subir actaNieto
            subirArchivo(actaNietoUri, actaNietoFileName) { url ->
                urls.add(url)

                // Subir talon
                subirArchivo(talonUri, talonFileName) { url ->
                    urls.add(url)

                    // Después de subir todas las imágenes, agregar los datos a Firestore
                    agregarDatosFirestore(matricula, matriculaAlumno, nombreAlumno,semestre, escuela,periodo, presencialNieto, escolarizadoNieto,  urls)
                }
            }
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentonieto/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // Obtener la URL de la imagen después de que se haya subido con éxito
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    // Llamar a la función onSuccess con la URL de la imagen
                    onSuccess(imageUrl)
                }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al obtener la URL de la imagen: $e", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el archivo: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun agregarDatosFirestore(
        matricula: String,
        matriculaAlumno: String,
        nombreAlumno: String,
        semestre: String,
        escuela: String,
        periodo: String,
        presencialNieto: String,
        escolarizadoNieto: String,
        urls: List<String>
    ) {
        val db = FirebaseFirestore.getInstance()
        val informacionExentoNietoRef = db.collection("informacionExentoNieto")

        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "matriculaAlumno" to matriculaAlumno,
            "nombreAlumno" to nombreAlumno,
            "semestre" to semestre,
            "escuela" to escuela,
            "periodo" to periodo,
            "presencialNieto" to presencialNieto,
            "escolarizadoNieto" to escolarizadoNieto,
            "actaHijoUri" to urls.getOrNull(0),
            "actaNietoUri" to urls.getOrNull(1),
            "talonUri" to urls.getOrNull(2)
        )

        informacionExentoNietoRef.add(nuevoDocumento)
            .addOnSuccessListener {
                // Crear un intent para la actividad a la que deseas ir
                val intent = Intent(this, exentos::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE)

                // Crear un NotificationCompat.Builder
                val builder = NotificationCompat.Builder(this, "Notificacion_trabajador")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Solicitud de exento de cuota enviada con éxito.")
                    .setContentText("Haz clic para ver el status de este tramite.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                // Verificar la versión de Android y crear un canal de notificación si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "Notificacion_trabajador",
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



    companion object {
        private const val PICK_IMAGE_REQUEST_ACTA_HIJO = 1
        private const val PICK_IMAGE_REQUEST_ACTA_NIETO = 2
        private const val PICK_IMAGE_REQUEST_TALON = 3
    }
}
