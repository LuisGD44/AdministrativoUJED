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

class canastilla : AppCompatActivity() {
    private lateinit var matricula: EditText
    private lateinit var actaUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canastilla)

        matricula = findViewById(R.id.txt_matricula)

        val btnActa = findViewById<Button>(R.id.btnActatra)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        val btnEnviarDatos = findViewById<Button>(R.id.btnCanastilla)
        btnEnviarDatos.setOnClickListener {
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
                    actaUri = data.data!!
                }
            }
        }
    }

    private fun enviarDatos() {
        val matriculaText = matricula.text.toString()

        if (matriculaText.isEmpty() || actaUri == null) {
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
                "actaUri" to actaUrl
            )

            canastillaRef.add(nuevoDocumento)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainPerfil::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
                }
        }
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

