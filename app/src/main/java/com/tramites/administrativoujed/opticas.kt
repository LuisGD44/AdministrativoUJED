package com.tramites.administrativoujed

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class opticas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opticas)

        val opticaListView: ListView = findViewById(R.id.opticaListView)

        // Datos de las ópticas
        val opticas = arrayOf(
            "OPTICA LAURA\nVictoria #414 SUR ZONA CENTRO\nTEL: 618-811-21-64",
            "OPTICA RENUEVO\n20 DE NOVIEMBRE #104",
            "OPTICA COLONIAL\nCONSTITUCION 107-B-SUR\nTEL: 618-813-93-79\nSUCURSAL VICTORIA #402-A SUR",
            "ANZA OPRICOS\nPATONI #121 NORTE",
            "OPTICA NOVA\nC.VICTORIA  #211\nSUR ENTRE 20 DE NOVIEMBRE Y NEGRETE",
            "OTICA DURANGO\nJUAREZ #201 ZONA CENTRO\nTEL: 618-827-9393\nSUCURSAL: AQUILES SERDAN #923 PTE ENTRE ZARAGOZA E HIDALOGO\nTEL: 618-813-24-19"
        )

        // Crear un adaptador personalizado
        val opticaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opticas)

        // Establecer el adaptador en el ListView
        opticaListView.adapter = opticaAdapter
    }
}
