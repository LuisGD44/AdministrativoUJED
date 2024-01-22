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

class exentoTrabajador : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private lateinit var txtSemestre: EditText
    private  lateinit var txtMatriculaEscolar: EditText
    private lateinit var txtEscuelaSpinner: Spinner
    private lateinit var txtEscolarizadoSpinner: Spinner
    private var actaUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exento_trabajador)

        txtMatriculaEscolar = findViewById(R.id.txt_matriculaEscolarTra)
        txtMatricula = findViewById(R.id.txt_matriculaTra)
        txtSemestre = findViewById(R.id.txtSemestreTra)
        txtEscolarizadoSpinner = findViewById(R.id.txtEscolarizadoTra)
        txtEscuelaSpinner = findViewById(R.id.txtEscuelaTra)


        val btnActaNacimiento = findViewById<Button>(R.id.btnActatra)
        btnActaNacimiento.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        val btnExentoHijo = findViewById<Button>(R.id.btnExentoHijo)
        btnExentoHijo.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackexTrabajador)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val escuelaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escuela,
            android.R.layout.simple_spinner_item
        )
        escuelaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        val escolarizadoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escolarizado,
            android.R.layout.simple_spinner_item
        )
        escolarizadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        txtEscolarizadoSpinner.adapter = escolarizadoAdapter
        txtEscuelaSpinner.adapter = escuelaAdapter
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
                PICK_IMAGE_REQUEST_ACTA -> {
                    actaUri = data.data
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = txtMatricula.text.toString()
        val matriculaEscolar = txtMatriculaEscolar.text.toString()
        val semestreTra = txtSemestre.text.toString()
        val escolarizado = txtEscolarizadoSpinner.selectedItem.toString()
        val escuela = txtEscuelaSpinner.selectedItem.toString()
        val actaUri = this.actaUri

        if (matricula.isEmpty() || actaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el archivo requerido.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaFileName = "acta_${UUID.randomUUID()}"
        subirArchivo(actaUri, actaFileName) { actaUrl ->
            agregarDatosFirestore(matricula, actaUrl, matriculaEscolar, escuela, escolarizado, semestreTra)
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentos/$fileName")

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

    private fun agregarDatosFirestore(matricula: String, actaUrl: String, matriculaEscolar: String, escuela: String, escolarizado: String, semestreTra: String) {
        val db = FirebaseFirestore.getInstance()
        val informacionExentosRef = db.collection("informacionExentos")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "matriculaEscolar" to matriculaEscolar,
            "semestreTra" to semestreTra,
            "escuela" to escuela,
            "escolarizado" to escolarizado,
            "actaUrl" to actaUrl
        )

        informacionExentosRef.add(nuevoDocumento)
            .addOnSuccessListener {
                // Crear un intent para la actividad a la que deseas ir
                val intent = Intent(this, MainPerfil::class.java)
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
        private const val PICK_IMAGE_REQUEST_ACTA = 1
    }
}