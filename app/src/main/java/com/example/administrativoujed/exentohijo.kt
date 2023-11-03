package com.example.administrativoujed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class exentohijo : AppCompatActivity() {
    private lateinit var txtPresencialSpinner: Spinner
    private lateinit var txtEscolarizadoSpinner: Spinner
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
    }
}