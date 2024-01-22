package com.tramites.administrativoujed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class cuotaNietoActivity : AppCompatActivity() {
    private lateinit var txtPresencialNietoC: Spinner
    private lateinit var txtEscolarizadoNietoC: Spinner
    private lateinit var txtPeriodoNietoC: Spinner
    private lateinit var txtEscuelaNietoC: Spinner
    private lateinit var actaHijoUri: Uri
    private lateinit var actaNietoUri: Uri
    private lateinit var talonUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuota_nieto)

        txtPresencialNietoC = findViewById(R.id.txtPresencialNietoC)
        txtEscolarizadoNietoC = findViewById(R.id.txtEscolarizadoNietoC)
        txtPeriodoNietoC = findViewById(R.id.txtPeriodoNietoC)
        txtEscuelaNietoC = findViewById(R.id.txtEscuelaNietoC)

        val presencialAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.presencial,
            android.R.layout.simple_spinner_item
        )
        presencialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtPresencialNietoC.adapter = presencialAdapter

        val escuelaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escuela,
            android.R.layout.simple_spinner_item
        )
        escuelaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscuelaNietoC.adapter = escuelaAdapter

        val periodoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periodo,
            android.R.layout.simple_spinner_item
        )
        periodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtPeriodoNietoC.adapter = periodoAdapter

        val escolarizadoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escolarizado,
            android.R.layout.simple_spinner_item
        )
        escolarizadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscolarizadoNietoC.adapter = escolarizadoAdapter

        val btnTalonNietoC = findViewById<Button>(R.id.btnTalonNietoC)
        btnTalonNietoC.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActaHijoNietoC = findViewById<Button>(R.id.actaHijoNietoC)
        btnActaHijoNietoC.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_HIJO)
        }

        val btnActaNieto = findViewById<Button>(R.id.actaNietoC)
        btnActaNieto.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_NIETO)
        }

        val btnExentoNieto = findViewById<Button>(R.id.btnExentoHijoNietoC)
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
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajadorNietoC).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matriculaAlunmoNietoC).text.toString()
        val semestre = findViewById<EditText>(R.id.txt_semestreNietoC).text.toString()
        val escuela = txtEscuelaNietoC.selectedItem.toString()
        val periodo = txtPeriodoNietoC.selectedItem.toString()
        val presencialNieto = txtPresencialNietoC.selectedItem.toString()
        val escolarizadoNieto = txtEscolarizadoNietoC.selectedItem.toString()

        if (matricula.isEmpty() || periodo.isEmpty() || matriculaAlumno.isEmpty() || semestre.isEmpty() || escuela.isEmpty() || actaHijoUri == null || actaNietoUri == null || talonUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione los archivos.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaHijoFileName = "acta_hijo_${UUID.randomUUID()}"
        val actaNietoFileName = "acta_nieto_${UUID.randomUUID()}"
        val talonFileName = "talon_${UUID.randomUUID()}"

        val urls = mutableListOf<String>()

        subirArchivo(actaHijoUri, actaHijoFileName) { url ->
            urls.add(url)

            subirArchivo(actaNietoUri, actaNietoFileName) { url ->
                urls.add(url)

                subirArchivo(talonUri, talonFileName) { url ->
                    urls.add(url)

                    agregarDatosFirestore(matricula, matriculaAlumno, periodo, semestre, escuela, presencialNieto, escolarizadoNieto, urls)
                }
            }
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentonietoCuota/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
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
        semestre: String,
        escuela: String,
        periodo: String,
        presencialNieto: String,
        escolarizadoNieto: String,
        urls: List<String>
    ) {
        val db = FirebaseFirestore.getInstance()
        val informacionExentoNietoRef = db.collection("informacionExentoNietoCuota")

        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "matriculaAlumno" to matriculaAlumno,
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

                mostrarNotificacion()
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, cuotasActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarNotificacion() {
        // Crear un intent para la actividad a la que deseas ir
        val intent = Intent(this, exentos::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Crear un NotificationCompat.Builder
        val builder = NotificationCompat.Builder(this, "Notificacion_nieto")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Solicitud de exento de inscripción para nieto enviada con éxito.")
            .setContentText("Haz clic para ver el estado de este trámite.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

        // Verificar la versión de Android y crear un canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Notificacion_nieto",
                "Nombre del canal",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Obtener el NotificationManager y mostrar la notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, builder.build())
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_ACTA_HIJO = 1
        private const val PICK_IMAGE_REQUEST_ACTA_NIETO = 2
        private const val PICK_IMAGE_REQUEST_TALON = 3
    }
}
