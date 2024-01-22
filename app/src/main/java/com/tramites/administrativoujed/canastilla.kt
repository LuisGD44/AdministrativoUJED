package com.tramites.administrativoujed

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class canastilla : AppCompatActivity() {
    private lateinit var matricula: EditText
    private lateinit var sexo: Spinner
    private lateinit var txtFecha: EditText
    private lateinit var actaUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canastilla)

        matricula = findViewById(R.id.txt_matricula)
        sexo = findViewById(R.id.txtSexo)
        txtFecha = findViewById(R.id.txtfecha)

        txtFecha.setOnClickListener {
            mostrarDatePicker()
        }


        val btnActa = findViewById<Button>(R.id.btnActatra)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        val btnEnviarDatos = findViewById<Button>(R.id.btnCanastilla)
        btnEnviarDatos.setOnClickListener {
            enviarDatos()
        }
        val btnBack: ImageButton = findViewById(R.id.btnBackca)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        val escuelaAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sexo,
            android.R.layout.simple_spinner_item
        )
        escuelaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sexo.adapter = escuelaAdapter
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
                    actaUri = data.data!!
                }
            }
        }
    }

    private fun enviarDatos() {
        val matriculaText = matricula.text.toString()
        val sexo = sexo.selectedItem.toString()
        val fecha = txtFecha.text.toString()

        if (matriculaText.isEmpty() || actaUri == null || sexo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el archivo.", Toast.LENGTH_SHORT).show()
            return
        }

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference

        // Subir el archivo de acta
        val actaFileName = "acta_${UUID.randomUUID()}"
        subirArchivo(actaUri, storageReference, actaFileName) { actaUrl ->
            // Continúa con el código para guardar la información en Firebase Firestore
            val db = FirebaseFirestore.getInstance()
            val canastillaRef = db.collection("canastilla")
            val nuevoDocumento = hashMapOf(
                "matricula" to matriculaText,
                "sexo" to sexo,
                "Fecha" to fecha,
                "actaUri" to actaUrl
            )

            canastillaRef.add(nuevoDocumento)
                .addOnSuccessListener {
                    // Crear un intent para la actividad a la que deseas ir
                    val intent = Intent(this, MainPerfil::class.java)
                    val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE)

                    // Crear un NotificationCompat.Builder
                    val builder = NotificationCompat.Builder(this, "Notificacion_canastilla")
                        .setSmallIcon(R.drawable.ic_notification) // Puedes personalizar el ícono
                        .setContentTitle("Solicitud de canastilla enviada con éxito.")
                        .setContentText("Haz clic para ver el status de este tramite.")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true) // Cierra la notificación al hacer clic en ella

                    // Verificar la versión de Android y crear un canal de notificación si es necesario
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            "Notificacion_canastilla",
                            "Nombre del canal",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)
                    }

                    // Obtener el NotificationManager y mostrar la notificación
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, builder.build())

                    Toast.makeText(this, "Canastilla solicitada con éxito.", Toast.LENGTH_SHORT).show()

                    // Iniciar la nueva actividad
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
                }
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

                val txtFecha = findViewById<EditText>(R.id.txtfecha)
                txtFecha.setText(fechaFormateada)
            },
            añoActual,
            mesActual,
            diaActual
        )

        datePickerDialog.show()
    }


    private fun subirArchivo(uri: Uri, storageReference: StorageReference, fileName: String, onComplete: (String) -> Unit) {
        val fileReference: StorageReference = storageReference.child("canastilla/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el archivo: $e", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_ACTA = 1
    }
}

