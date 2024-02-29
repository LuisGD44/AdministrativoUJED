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

class lentes : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private lateinit var txtOptica: Spinner
    private var presupuestoUri: Uri? = null
    private var talonUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lentes)


        txtOptica = findViewById(R.id.txtOptica)

        txtMatricula = findViewById(R.id.txt_matriculaLen)

        val btnPresupuesto = findViewById<Button>(R.id.btnPreosu)
        btnPresupuesto.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_PRESUPUESTO)
        }

        val btnTalon = findViewById<Button>(R.id.btntalondental)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnExentoHijo = findViewById<Button>(R.id.btnExentoHijo)
        btnExentoHijo.setOnClickListener {
            enviarDatos()
        }

        val opticaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.opticas,
            android.R.layout.simple_spinner_item
        )
        opticaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtOptica.adapter = opticaAdapter

        val btnBack: ImageButton = findViewById(R.id.btnBack)
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
                PICK_IMAGE_REQUEST_PRESUPUESTO -> {
                    presupuestoUri = data.data
                }
                PICK_IMAGE_REQUEST_TALON -> {
                    talonUri = data.data
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = txtMatricula.text.toString()
        val optica = txtOptica.selectedItem.toString()
        val presupuestoUri = this.presupuestoUri
        val talonUri = this.talonUri

        if (matricula.isEmpty() || presupuestoUri == null || talonUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione los archivos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir los archivos a Firebase Storage y guardar información en Firebase Firestore
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        // Subir el archivo de presupuesto
        val presupuestoFileName = "presupuesto_${UUID.randomUUID()}"
        subirArchivo(presupuestoUri, storageReference, presupuestoFileName) { presupuestoUrl ->
            // Subir el archivo de talón de pago
            val talonFileName = "talon_${UUID.randomUUID()}"
            subirArchivo(talonUri, storageReference, talonFileName) { talonUrl ->
                // Continúa con el código para guardar la información en Firebase Firestore
                val db = FirebaseFirestore.getInstance()
                val informacionLentesRef = db.collection("informacionLentes")
                val nuevoDocumento = hashMapOf(
                    "matricula" to matricula,
                    "optica" to optica,
                    "presupuestoUri" to presupuestoUrl,
                    "talonUri" to talonUrl
                )

                informacionLentesRef.add(nuevoDocumento)
                    .addOnSuccessListener {
                        // Crear un intent para la actividad a la que deseas ir
                        val intent = Intent(this, MainPerfil::class.java)
                        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_IMMUTABLE)

                        // Crear un NotificationCompat.Builder
                        val builder = NotificationCompat.Builder(this, "Notificacion_lentes")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Solicitud de descuento en optica enviada con éxito.")
                            .setContentText("Haz clic para ver el status de este tramite.")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                        // Verificar la versión de Android y crear un canal de notificación si es necesario
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel = NotificationChannel(
                                "Notificacion_lentes",
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
    }

    private fun subirArchivo(uri: Uri, storageReference: StorageReference, fileName: String, onComplete: (String) -> Unit) {
        val fileReference: StorageReference = storageReference.child("lentes/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                // Obtener la URL del archivo subido
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el archivo: $e", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_PRESUPUESTO = 1
        private const val PICK_IMAGE_REQUEST_TALON = 2
    }
}
