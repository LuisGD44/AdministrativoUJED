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

class lentes : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private var presupuestoUri: Uri? = null
    private var talonUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lentes)

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
        subirArchivo(presupuestoUri, storageReference, presupuestoFileName)

        // Subir el archivo de talón de pago
        val talonFileName = "talon_${UUID.randomUUID()}"
        subirArchivo(talonUri, storageReference, talonFileName)

        // Continúa con el código para guardar la información en Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val informacionLentesRef = db.collection("informacionLentes")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "presupuestoUri" to presupuestoUri,  // Aqui debemos remplazar por la url del preouspuesto
            "talonUri" to talonUri,  // igual que lo de arriba
        )

        informacionLentesRef.add(nuevoDocumento)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
            }
    }


    private fun subirArchivo(uri: Uri, storageReference: StorageReference, fileName: String) {
        val fileReference: StorageReference = storageReference.child("lentes/$fileName")

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
    }
}
