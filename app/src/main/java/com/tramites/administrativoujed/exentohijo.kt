package com.tramites.administrativoujed

import android.app.Activity
import android.app.AlertDialog
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
import android.provider.Settings

class exentohijo : AppCompatActivity() {
    private lateinit var txtPresencialSpinner: Spinner
    private lateinit var txtEscuelaSpinner: Spinner
    private lateinit var txtEscolarizadoSpinner: Spinner
    private lateinit var txtPeriodoSpinner: Spinner
    private lateinit var actaUri: Uri
    private lateinit var talonUri: Uri
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exentohijo)

        // Inicializar vistas y adaptadores
        txtPresencialSpinner = findViewById(R.id.txtPresencialH)
        txtEscolarizadoSpinner = findViewById(R.id.txtEscolarizadoH)
        txtEscuelaSpinner = findViewById(R.id.txtEscuelaH)
        txtPeriodoSpinner = findViewById(R.id.txtPeridoDepH)

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
        periodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

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
        txtPeriodoSpinner.adapter = periodoAdapter

        // Configurar botones
        val btnTalon = findViewById<Button>(R.id.btnTalonH)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActa = findViewById<Button>(R.id.btnActaH)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        val btnExentoHijo = findViewById<Button>(R.id.btnExentoHijo)
        btnExentoHijo.setOnClickListener {
            enviarDatos()
        }

        val btnBack: ImageButton = findViewById(R.id.btnBackexhijo)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        // Inicializar notificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Solicitar permisos para notificaciones
        solicitarPermisosNotificaciones()
    }





    private fun seleccionarArchivo(pickImageRequest: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, pickImageRequest)
    }

    private fun mostrarDialogoPermisosNotificaciones() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permisos de Notificaciones")
        builder.setMessage("No tienes permiso para mostrar notificaciones. Por favor, actívalo en la configuración.")
        builder.setPositiveButton("Ir a Configuración") { dialog, which ->
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            // Acción al cancelar el diálogo
        }
        builder.setCancelable(false) // No permite cancelar el diálogo tocando fuera de él
        builder.show()
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


    private fun seMostroMensajeNotificaciones(): Boolean {
        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("mostrado", false)
    }

    private fun marcarMensajeNotificacionesMostrado() {
        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("mostrado", true)
        editor.apply()
    }

    private fun solicitarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted && !seMostroMensajeNotificaciones()) {
                mostrarDialogoPermisosNotificaciones()
                marcarMensajeNotificacionesMostrado()
            }
        }
    }




    private fun enviarDatos() {
        val nombreAlumno = findViewById<EditText>(R.id.txtNombreAlumnoH).text.toString()
        val semestre = findViewById<EditText>(R.id.txtSemestreH).text.toString()
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajadorHijo).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matriculaEH).text.toString()
        val presencial = txtPresencialSpinner.selectedItem.toString()
        val escolarizado = txtEscolarizadoSpinner.selectedItem.toString()
        val periodo = txtPeriodoSpinner.selectedItem.toString()
        val escuela = txtEscuelaSpinner.selectedItem.toString()


        if (matricula.isEmpty() || actaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el acta.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaFileName = "acta_${UUID.randomUUID()}"
        val talonFileName = "talon_${UUID.randomUUID()}"

        subirArchivo(actaUri, actaFileName) { actaUrl ->
            subirArchivo(talonUri, talonFileName) { talonUrl ->
                // Después de subir todas las imágenes, agregar los datos a Firestore
                agregarDatosFirestore(nombreAlumno, semestre, matricula, matriculaAlumno, presencial, escolarizado, periodo, escuela, actaUrl, talonUrl)
            }
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String, onSuccess: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("exentohijo/$fileName")

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
        nombreAlumno: String,
        semestre: String,
        matricula: String,
        matriculaAlumno: String,
        presencial: String,
        escolarizado: String,
        periodo: String,
        escuela: String,
        actaUrl: String,
        talonUrl: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val informacionHijosRef = db.collection("informacionHijos")
        val nuevoDocumento = hashMapOf(
            "nombreAlumno" to nombreAlumno,
            "semestre" to semestre,
            "matricula" to matricula,
            "matriculaAlumno" to matriculaAlumno,
            "presencial" to presencial,
            "escolarizado" to escolarizado,
            "periodo" to periodo,
            "escuela" to escuela,
            "actaUrl" to actaUrl,
            "talonUrl" to talonUrl
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
                    .setContentTitle("Solicitud de exento de inscripcion enviada con éxito.")
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
