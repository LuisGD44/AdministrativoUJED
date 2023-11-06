package com.example.administrativoujed

import android.app.Activity
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

class exentoTrabajador : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private var actaUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exento_trabajador)

        txtMatricula = findViewById(R.id.txt_matriculaTra)

        // Agregar un OnClickListener al botón para seleccionar el acta de nacimiento
        val btnActaNacimiento = findViewById<Button>(R.id.btnActatra)
        btnActaNacimiento.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
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
                PICK_IMAGE_REQUEST_ACTA -> {
                    actaUri = data.data
                }
            }
        }
    }

    private fun enviarDatos() {
        val matricula = txtMatricula.text.toString()
        val actaUri = this.actaUri

        if (matricula.isEmpty() || actaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el archivo requerido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir el archivo de acta de nacimiento a Firebase Storage
        val actaFileName = "acta_${UUID.randomUUID()}"
        subirArchivo(actaUri, actaFileName)

        // Continúa con el código para guardar la información en Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val informacionExentosRef = db.collection("informacionExentos")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "actaUri" to actaUri,
        )

        informacionExentosRef.add(nuevoDocumento)
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
        val fileReference: StorageReference = storageReference.child("exentos/$fileName")

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Archivo subido con éxito.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el archivo: $e", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_ACTA = 1
    }
}


