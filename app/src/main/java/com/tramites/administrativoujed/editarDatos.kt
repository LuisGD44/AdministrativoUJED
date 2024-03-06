package com.tramites.administrativoujed

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class editarDatos : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosRef = db.collection("usuarios")
    private lateinit var userId: String

    // Declarar los EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellidoPaterno: EditText
    private lateinit var editTextApellidoMaterno: EditText
    private lateinit var edittextCorreo: EditText
    private lateinit var editMatricula: EditText

    // Declarar los Spinners
    private lateinit var spinnerTurno: Spinner
    private lateinit var spinnerAdscripcion: Spinner
    private lateinit var spinnerTipoTrabajador: Spinner

    private lateinit var turnoArray: Array<String>
    private lateinit var adscripcionArray: Array<String>
    private lateinit var tipoTrabajadorArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_datos)

        // Obtener los arrays de strings
        turnoArray = resources.getStringArray(R.array.turno)
        adscripcionArray = resources.getStringArray(R.array.unidadAcademica)
        tipoTrabajadorArray = resources.getStringArray(R.array.tipoTrabajador)

        // Inicializar los EditText
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellidoPaterno = findViewById(R.id.editTextApellidoPaterno)
        editTextApellidoMaterno = findViewById(R.id.editTextApellidoMaterno)
        edittextCorreo = findViewById(R.id.edittextCorreo)
        editMatricula = findViewById(R.id.editMatricula)

        // Inicializar los Spinners
        spinnerTurno = findViewById(R.id.spinnerTurno)
        spinnerAdscripcion = findViewById(R.id.spinnerAdscripcion)
        spinnerTipoTrabajador = findViewById(R.id.spinnerTipoTrabajador)

        // Inicializar adaptadores para los Spinners

        val btnBack: ImageButton = findViewById(R.id.btnBackEditar)
        btnBack.setOnClickListener {
            onBackPressed()
        }



        val turnoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, turnoArray)
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTurno.adapter = turnoAdapter

        val adscripcionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, adscripcionArray)
        adscripcionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAdscripcion.adapter = adscripcionAdapter

        val tipoTrabajadorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoTrabajadorArray)
        tipoTrabajadorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoTrabajador.adapter = tipoTrabajadorAdapter

        // Manejar el clic del botón Guardar
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            // Obtener la matrícula ingresada por el usuario
            val matricula = editMatricula.text.toString()

            // Verificar que la matrícula no esté vacía
            if (TextUtils.isEmpty(matricula)) {
                Toast.makeText(this, "Por favor, ingrese su matrícula", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Realizar la búsqueda del ID del usuario en Firestore a partir de la matrícula
            usuariosRef.whereEqualTo("matricula", matricula)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Obtener el ID del usuario encontrado
                        userId = document.id

                        // Obtener los valores de los EditText y Spinners
                        val nombre = editTextNombre.text.toString()
                        val apellidoPaterno = editTextApellidoPaterno.text.toString()
                        val apellidoMaterno = editTextApellidoMaterno.text.toString()
                        val correo = edittextCorreo.text.toString()
                        val nuevoTurno = spinnerTurno.selectedItem.toString()
                        val nuevaAdscripcion = spinnerAdscripcion.selectedItem.toString()
                        val nuevoTipoTrabajador = spinnerTipoTrabajador.selectedItem.toString()

                        // Crear un nuevo mapa mutable con los datos actualizados
                        val nuevosDatos: MutableMap<String, Any> = hashMapOf(
                            "nombre" to nombre,
                            "apellido_paterno" to apellidoPaterno,
                            "apellido_materno" to apellidoMaterno,
                            "correo" to correo,
                            "turno" to nuevoTurno,
                            "adscripcion" to nuevaAdscripcion,
                            "tipoTrabajador" to nuevoTipoTrabajador
                        )

                        // Actualizar los datos en Firestore
                        usuariosRef.document(userId).update(nuevosDatos)
                            .addOnSuccessListener {
                                // Datos actualizados exitosamente
                                Toast.makeText(this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Error al actualizar los datos
                                Toast.makeText(this, "Error al actualizar los datos: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Error al buscar el usuario por matrícula
                    Toast.makeText(this, "Error al buscar el usuario: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
