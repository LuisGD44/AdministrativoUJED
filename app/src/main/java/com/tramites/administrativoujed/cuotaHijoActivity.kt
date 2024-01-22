package com.tramites.administrativoujed

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class cuotaHijoActivity : AppCompatActivity() {
    private lateinit var txtPresencialSpinner: Spinner
    private lateinit var txtEscuelaSpinner: Spinner
    private lateinit var txtEscolarizadoSpinner: Spinner
    private lateinit var txtPeridoSpinner: Spinner
    private lateinit var actaUri: Uri
    private lateinit var talonUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuota_hijo)

        txtPresencialSpinner = findViewById(R.id.txtPresencialC)
        txtEscolarizadoSpinner = findViewById(R.id.txtEscolarizadoC)
        txtEscuelaSpinner = findViewById(R.id.txtEscuelaC)
        txtPeridoSpinner = findViewById(R.id.txtPeriodoHijoC)

        val presencialAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.presencial,
            android.R.layout.simple_spinner_item
        )
        presencialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val periodoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.periodo,
            android.R.layout.simple_spinner_item
        )

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

        txtPresencialSpinner.adapter = presencialAdapter
        txtEscolarizadoSpinner.adapter = escolarizadoAdapter
        txtEscuelaSpinner.adapter = escuelaAdapter
        txtPeridoSpinner.adapter = periodoAdapter

        val btnTalon = findViewById<Button>(R.id.btnTalonC)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActa = findViewById<Button>(R.id.btnActaC)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        val btnExentoHijo = findViewById<Button>(R.id.btnExentoHijoC)
        btnExentoHijo.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackexhijoC)
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
                PICK_IMAGE_REQUEST_TALON -> {
                    talonUri = data.data!!
                }
                PICK_IMAGE_REQUEST_ACTA -> {
                    actaUri = data.data!!
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajadorC).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matriculaC).text.toString()
        val presencial = txtPresencialSpinner.selectedItem.toString()
        val escolarizado = txtEscolarizadoSpinner.selectedItem.toString()
        val escuela = txtEscuelaSpinner.selectedItem.toString()
        val periodo = txtPeridoSpinner.selectedItem.toString()

        if (matricula.isEmpty() || actaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el acta.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaFileName = "acta_${UUID.randomUUID()}"
        val talonFileName = "talon_${UUID.randomUUID()}"

        subirArchivo(actaUri, actaFileName) { actaUrl ->
            subirArchivo(talonUri, talonFileName) { talonUrl ->
                // Después de subir todas las imágenes, agregar los datos a Firestore
                agregarDatosFirestore(matricula, matriculaAlumno, actaUrl, talonUrl, presencial, escuela, escolarizado, periodo)
            }
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentohijoCuota/$fileName")

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
        actaUrl: String,
        talonUrl: String,
        periodo: String,
        presencial: String,
        escolarizado: String,
        escuela: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val informacionHijosRef = db.collection("informacionHijosCuota")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "matriculaAlumno" to matriculaAlumno,
            "periodo" to periodo,
            "actaUrl" to actaUrl,
            "talonUrl" to talonUrl,
            "presencial" to presencial,
            "escuela" to escolarizado,
            "escolarizado" to escuela
            //Por alguna razon tuve que invertir escuela y escolarizado para que se almacenara de manera correcta
        )

        informacionHijosRef.add(nuevoDocumento)
            .addOnSuccessListener {
                // Crear un intent para la actividad a la que deseas ir
                val intent = Intent(this, MainPerfil::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE)

                // Crear un NotificationCompat.Builder
                val builder = NotificationCompat.Builder(this, "Notificacion_exentoHijo")
                    .setSmallIcon(R.drawable.ic_notification) // Puedes personalizar el ícono
                    .setContentTitle("Solicitud de exento de cuota enviada con éxito.")
                    .setContentText("Haz clic para ver el status de este tramite.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                // Verificar la versión de Android y crear un canal de notificación si es necesario
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        "Notificacion_exentoHijo",
                        "Nombre del canal",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }

                // Obtener el NotificationManager y mostrar la notificación
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1, builder.build())

                Toast.makeText(this, "Exento solicitado con éxito.", Toast.LENGTH_SHORT).show()

                // Iniciar la nueva actividad
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_TALON = 1
        private const val PICK_IMAGE_REQUEST_ACTA = 2

    }
}