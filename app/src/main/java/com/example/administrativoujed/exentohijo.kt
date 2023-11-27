package com.example.administrativoujed

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

class exentohijo : AppCompatActivity() {
    private lateinit var txtPresencialSpinner: Spinner
    private lateinit var txtEscolarizadoSpinner: Spinner
    private lateinit var actaUri: Uri
    private lateinit var talonUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exentohijo)

        txtPresencialSpinner = findViewById(R.id.txtPresencial)
        txtEscolarizadoSpinner = findViewById(R.id.txtEscolarizado)

        val presencialAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.presencial,
            android.R.layout.simple_spinner_item
        )
        presencialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val escolarizadoAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Escolarizado,
            android.R.layout.simple_spinner_item
        )
        escolarizadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        txtPresencialSpinner.adapter = presencialAdapter
        txtEscolarizadoSpinner.adapter = escolarizadoAdapter

        val btnTalon = findViewById<Button>(R.id.btnTalon)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActa = findViewById<Button>(R.id.btnActa)
        btnActa.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA)
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
        val matricula = findViewById<EditText>(R.id.txt_matriculaTrabajador).text.toString()
        val matriculaAlumno = findViewById<EditText>(R.id.txt_matricula).text.toString()
        val presencial = txtPresencialSpinner.selectedItem.toString()
        val escolarizado = txtEscolarizadoSpinner.selectedItem.toString()

        if (matricula.isEmpty() || actaUri == null) {
            Toast.makeText(this, "Por favor, complete todos los campos y seleccione el acta.", Toast.LENGTH_SHORT).show()
            return
        }

        val actaFileName = "acta_${UUID.randomUUID()}"
        val talonFileName = "talon_${UUID.randomUUID()}"

        subirArchivo(actaUri, actaFileName) { actaUrl ->
            subirArchivo(talonUri, talonFileName) { talonUrl ->
                // Después de subir todas las imágenes, agregar los datos a Firestore
                agregarDatosFirestore(matricula, matriculaAlumno, actaUrl, talonUrl, presencial, escolarizado)
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
        matricula: String,
        matriculaAlumno: String,
        actaUrl: String,
        talonUrl: String,
        presencial: String,
        escolarizado: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val informacionHijosRef = db.collection("informacionHijos")
        val nuevoDocumento = hashMapOf(
            "matricula" to matricula,
            "matriculaAlumno" to matriculaAlumno,
            "actaUrl" to actaUrl,
            "talonUrl" to talonUrl,
            "presencial" to presencial,
            "escolarizado" to escolarizado
        )

        informacionHijosRef.add(nuevoDocumento)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, exentos::class.java)
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
