package com.example.administrativoujed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import com.example.administrativoujed.databinding.ActivityCanastillaBinding
import com.example.administrativoujed.model.InformacionHijo
import com.google.firebase.database.FirebaseDatabase

class canastilla : AppCompatActivity() {
    private var actaNacimientoUri: Uri? = null
    private val PICK_IMAGE_REQUEST_ACTA = 1
    private lateinit var binding: ActivityCanastillaBinding
    private var actaUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanastillaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val btnActaNacimiento = binding.btnActatra
        val btnCanastilla = binding.btnCanastilla

        btnActaNacimiento.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
        }

        btnCanastilla.setOnClickListener {
            enviarDatos()
        }
    }

    fun seleccionarArchivo(pickImageRequest: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            if (requestCode == PICK_IMAGE_REQUEST_ACTA) {
                actaNacimientoUri = selectedImage
            }
        }
    }

    private fun enviarDatos() {
        val matricula = binding.txtMatricula.text.toString()

        if (matricula.isEmpty() || actaNacimientoUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }


        if (actaNacimientoUri != null) {

            val actaFileName = "actaNacimiento_${UUID.randomUUID()}"
            val actaFileReference = subirArchivo(actaNacimientoUri!!, actaFileName)

            if (actaFileReference != null) {
                actaFileReference.putFile(actaNacimientoUri!!)
                    .addOnSuccessListener { taskSnapshot ->

                        actaFileReference.downloadUrl
                            .addOnSuccessListener { uri ->
                                actaUri = uri.toString()


                                val db = FirebaseFirestore.getInstance()
                                val informacionHijosRef = db.collection("informacionHijos")
                                val nuevoDocumento = hashMapOf(
                                    "matricula" to matricula,
                                    "actaUri" to actaUri //aqui trato de jalar el url a firestore
                                )

                                informacionHijosRef.add(nuevoDocumento)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al obtener la URI del archivo: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al subir el archivo de acta de nacimiento: $e", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Error al obtener la referencia del archivo de acta de nacimiento.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // La URI del archivo de acta de nacimiento es nula
            Toast.makeText(this, "La URI del archivo de acta de nacimiento es nula.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirArchivo(uri: Uri, fileName: String): StorageReference? {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val fileReference: StorageReference = storageReference.child("canastilla/$fileName")

        val uploadTask = fileReference.putFile(uri)

        return if (uploadTask.isSuccessful) {
            fileReference
        } else {
            null
        }
    }

    private fun obtenerUriArchivo(fileReference: StorageReference): Uri? {
        return try {
            fileReference.downloadUrl.result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
