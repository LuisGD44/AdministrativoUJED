package com.tramites.administrativoujed

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

class dental : AppCompatActivity() {
    private lateinit var txtMatricula: EditText
    private var presupuestoUri: Uri? = null
    private var talonUri: Uri? = null
    private var actaMatrimonioUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dental)

        txtMatricula = findViewById(R.id.txt_matriculaTra)

        val btnPresupuesto = findViewById<Button>(R.id.btnPreosu)
        btnPresupuesto.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_PRESUPUESTO)
        }

        val btnTalon = findViewById<Button>(R.id.btntalondental)
        btnTalon.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_TALON)
        }

        val btnActaMatrimonio = findViewById<Button>(R.id.btnActamatrimonio)
        btnActaMatrimonio.setOnClickListener {
            seleccionarArchivo(PICK_IMAGE_REQUEST_ACTA_MATRIMONIO)
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
                PICK_IMAGE_REQUEST_ACTA_MATRIMONIO -> {
                    actaMatrimonioUri = data.data
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

        val presupuestoFileName = "presupuesto_${UUID.randomUUID()}"
        subirArchivo(presupuestoUri, presupuestoFileName) { presupuestoUrl ->
            val talonFileName = "talon_${UUID.randomUUID()}"
            subirArchivo(talonUri, talonFileName) { talonUrl ->
                val actaMatrimonioUri = this.actaMatrimonioUri
                if (actaMatrimonioUri != null) {
                    val actaMatrimonioFileName = "actaMatrimonio_${UUID.randomUUID()}"
                    subirArchivo(actaMatrimonioUri, actaMatrimonioFileName) { actaMatrimonioUrl ->
                        val db = FirebaseFirestore.getInstance()
                        val informacionDentalRef = db.collection("informacionDental")
                        val nuevoDocumento = hashMapOf(
                            "matricula" to matricula,
                            "presupuestoUri" to presupuestoUrl,
                            "talonUri" to talonUrl,
                            "actaMatrimonioUri" to actaMatrimonioUrl
                        )

                        informacionDentalRef.add(nuevoDocumento)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar en Firestore: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val db = FirebaseFirestore.getInstance()
                    val informacionDentalRef = db.collection("informacionDental")
                    val nuevoDocumento = hashMapOf(
                        "matricula" to matricula,
                        "presupuestoUri" to presupuestoUrl,
                        "talonUri" to talonUrl
                    )

                    informacionDentalRef.add(nuevoDocumento)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Datos enviados con éxito.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainPerfil::class.java)
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
        val fileReference: StorageReference = storageReference.child("dental/$fileName")

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
        private const val PICK_IMAGE_REQUEST_PRESUPUESTO = 1
        private const val PICK_IMAGE_REQUEST_TALON = 2
        private const val PICK_IMAGE_REQUEST_ACTA_MATRIMONIO = 3
    }
}

