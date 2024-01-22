package com.tramites.administrativoujed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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
import android.widget.ImageButton
import androidx.core.app.NotificationCompat


class decendientesActivity : AppCompatActivity() {
    private lateinit var txtPresencialde: Spinner
    private lateinit var txtEscolarizadode: Spinner
    private lateinit var txtEscuelade: Spinner
    private lateinit var talonUri: Uri
    private lateinit var actaHijoUri: Uri
    private lateinit var actaCartaUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decendientes)

        txtPresencialde = findViewById(R.id.txtPresencialde)
        txtEscolarizadode = findViewById(R.id.txtEscolarizadode)
        txtEscuelade = findViewById(R.id.txtEscuelade)

        val presencialAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.presencial,
            android.R.layout.simple_spinner_item
        )
        presencialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtPresencialde.adapter = presencialAdapter

        val escolarizadoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escolarizado,
            android.R.layout.simple_spinner_item
        )
        escolarizadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscolarizadode.adapter = escolarizadoAdapter


        val escuelaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escuela,
            android.R.layout.simple_spinner_item
        )
        escuelaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        txtEscuelade.adapter = escuelaAdapter

        val btnTalon = findViewById<Button>(R.id.btnTalon)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActa = findViewById<Button>(R.id.btnActa)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_HIJO)
        }

        val btnCarta = findViewById<Button>(R.id.btnCarta)
        btnCarta.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_CARTA)
        }

        val btnExentoDec = findViewById<Button>(R.id.btnexentoDec)
        btnExentoDec.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackexDependientes)
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
                PICK_IMAGE_REQUEST_ACTA_HIJO -> {
                    actaHijoUri = data.data!!
                }
                PICK_IMAGE_REQUEST_ACTA_CARTA -> {
                    actaCartaUri = data.data!!
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajador).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matriculaAlunmo).text.toString()
        val semestre = findViewById<EditText>(R.id.txt_semestre).text.toString()
        val nombreDependiente = findViewById<EditText>(R.id.txtDependiente).text.toString()
        val presencialDec = txtPresencialde.selectedItem.toString()
        val escolarizadoDec = txtEscolarizadode.selectedItem.toString()
        val escuela = txtEscuelade.selectedItem.toString()

        if (matricula.isEmpty() || matriculaAlumno.isEmpty() || semestre.isEmpty()  || talonUri == null || actaHijoUri == null || actaCartaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val talonFileName = "talon_${UUID.randomUUID()}"
        val actaHijoFileName = "acta_hijo_${UUID.randomUUID()}"
        val actaCartaFileName = "acta_carta_${UUID.randomUUID()}"

        subirArchivo(talonUri, talonFileName) { talonUrl ->
            subirArchivo(actaHijoUri, actaHijoFileName) { actaHijoUrl ->
                subirArchivo(actaCartaUri, actaCartaFileName) { actaCartaUrl ->
                    val db = FirebaseFirestore.getInstance()
                    val informacionExentoDecRef = db.collection("informacionExentoDec")
                    val nuevoDocumento = hashMapOf(
                        "matricula" to matricula,
                        "matriculaAlumno" to matriculaAlumno,
                        "semestre" to semestre,
                        "escuela" to escuela,
                        "presencialDec" to presencialDec,
                        "nombreDependiente" to nombreDependiente,
                        "escolarizadoDec" to escolarizadoDec,
                        "talonUri" to talonUrl,
                        "actaHijoUri" to actaHijoUrl,
                        "actaCartaUri" to actaCartaUrl
                    )

                    informacionExentoDecRef.add(nuevoDocumento)
                        .addOnSuccessListener {

                            val intent = Intent(this, MainPerfil::class.java)
                            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                PendingIntent.FLAG_IMMUTABLE)

                            // Crear un NotificationCompat.Builder
                            val builder = NotificationCompat.Builder(this, "Notificacion_dependiente")
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("Solicitud de exento de inscripcion enviada con éxito.")
                                .setContentText("Haz clic para ver el status de este tramite.")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                            // Verificar la versión de Android y crear un canal de notificación si es necesario
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(
                                    "Notificacion_dependiente",
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
    }

    private fun subirArchivo(uri: Uri, fileName: String, onComplete: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentoDependiente/$fileName")

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
        private const val PICK_IMAGE_REQUEST_TALON = 1
        private const val PICK_IMAGE_REQUEST_ACTA_HIJO = 2
        private const val PICK_IMAGE_REQUEST_ACTA_CARTA = 3
    }
}

