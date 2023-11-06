package com.example.administrativoujed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class dental : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private var presupuestoUri: Uri? = null
    private var talonUri: Uri? = null
    private var actaMatrimonioUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dental)

        txtMatricula = findViewById(R.id.txt_matriculaTra)

        // Agregar un OnClickListener al botón para seleccionar el presupuesto
        val btnPresupuesto = findViewById<Button>(R.id.btnPreosu)
        btnPresupuesto.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_PRESUPUESTO)
        }

        // Agregar un OnClickListener al botón para seleccionar el talón de pago
        val btnTalon = findViewById<Button>(R.id.btntalondental)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        // Agregar un OnClickListener al botón para seleccionar el acta de matrimonio
        val btnActaMatrimonio = findViewById<Button>(R.id.btnActamatrimonio)
        btnActaMatrimonio.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_MATRIMONIO)
        }

        // Agregar un OnClickListener al botón para enviar datos
        val btnExentoHijo = findViewById<Button>(R.id.btnExentoHijo)
        btnExentoHijo.setOnClickListener {
            enviarDatos()
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
                PICK_IMAGE_REQUEST_ACTA_MATRIMONIO -> {
                    actaMatrimonioUri = data.data
                }
            }
        }
    }

    private fun enviarDatos() {
        // Obtener otros datos del formulario (matrícula, spinner, etc.)
        val matricula = txtMatricula.text.toString()
        val presupuestoUri = this.presupuestoUri
        val talonUri = this.talonUri

        if (matricula.isEmpty() || presupuestoUri == null || talonUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione los archivos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir el archivo de presupuesto
        val presupuestoFileName = "presupuesto_${UUID.randomUUID()}"
        subirArchivo(presupuestoUri, presupuestoFileName)

        // Subir el archivo de talón de pago
        val talonFileName = "talon_${UUID.randomUUID()}"
        subirArchivo(talonUri, talonFileName)

        // Subir el archivo de acta de matrimonio (si existe)
        val actaMatrimonioUri = this.actaMatrimonioUri
        if (actaMatrimonioUri != null) {
            val actaMatrimonioFileName = "actaMatrimonio_${UUID.randomUUID()}"
            subirArchivo(actaMatrimonioUri, actaMatrimonioFileName)
        }

        // Continúa con el código para guardar la información en Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val informacionDentalRef = db.collection("informacionDental")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "presupuestoUri" to "URL_DEL_PRESUPUESTO",  //debo de jalar la url del preosupuesto
            "talonUri" to "URL_DEL_TALON",  // Debo de jalar el url del talon
        )

        informacionDentalRef.add(nuevoDocumento)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }


    private fun subirArchivo(uri: Uri, fileName: String) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("dental/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Archivo subido con éxito.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el archivo: $e", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_PRESUPUESTO = 1
        private const val PICK_IMAGE_REQUEST_TALON = 2
        private const val PICK_IMAGE_REQUEST_ACTA_MATRIMONIO = 3
    }
}
