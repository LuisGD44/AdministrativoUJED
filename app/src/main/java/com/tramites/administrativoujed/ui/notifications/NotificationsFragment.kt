package com.tramites.administrativoujed.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.tramites.administrativoujed.R

class NotificationsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: VacantesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Inicializar el RecyclerView
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Configura el adaptador del RecyclerView
        adapter = VacantesAdapter(emptyList()) // Comienza con una lista vacía
        recyclerView.adapter = adapter

        // Inicializar Firestore
        firestore = FirebaseFirestore.getInstance()

        // Obtener y mostrar los datos de Firestore
        obtenerDatosFirestore()

        return root
    }

    private fun obtenerDatosFirestore() {
        val vacantesCollection = firestore.collection("vacantesDisponibles")

        // Obtener los documentos de la colección
        vacantesCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Procesar los documentos
                val vacantesList: MutableList<Vacante> = mutableListOf()
                for (document: DocumentSnapshot in task.result!!) {
                    val ramaTecnica: String? = document.getString("rama_tecnica")
                    val salario: String? = document.getString("salario")
                    val unidadAcademica: String? = document.getString("unidad_academica")

                    // Agregar cada vacante a la lista
                    vacantesList.add(Vacante(ramaTecnica, salario, unidadAcademica))
                }

                // Actualizar datos del adaptador
                adapter.actualizarVacantes(vacantesList)
            } else {
                // Manejar el error
                Log.e("Firestore", "Error al obtener los datos", task.exception)
                Toast.makeText(context, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class Vacante(val ramaTecnica: String?, val salario: String?, val unidadAcademica: String?)

    class VacantesAdapter(private var vacantesList: List<Vacante>) :
        RecyclerView.Adapter<VacantesAdapter.VacanteViewHolder>() {

        class VacanteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textRamaTecnica: TextView = itemView.findViewById(R.id.textRamaTecnica)
            val textSalario: TextView = itemView.findViewById(R.id.textSalario)
            val textUnidadAcademica: TextView = itemView.findViewById(R.id.textUnidadAcademica)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacanteViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.notificacion_item, parent, false)
            return VacanteViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: VacanteViewHolder, position: Int) {
            val vacante = vacantesList[position]
            holder.textRamaTecnica.text = vacante.ramaTecnica
            holder.textSalario.text = "Salario: ${vacante.salario}"
            holder.textUnidadAcademica.text = "Unidad Académica: ${vacante.unidadAcademica}"
        }

        override fun getItemCount(): Int {
            return vacantesList.size
        }

        fun actualizarVacantes(nuevaLista: List<Vacante>) {
            vacantesList = nuevaLista
            notifyDataSetChanged()
        }
    }
}


