package com.example.administrativoujed.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.administrativoujed.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

// ...

class NotificationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_notifications, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        firestore = FirebaseFirestore.getInstance()

        // Configura el RecyclerView y obtén los datos de Firestore
        setupRecyclerView()
        obtenerDatosFirestore()

        return root
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun obtenerDatosFirestore() {
        val vacantesCollection = firestore.collection("vacantesDisponibles")

        // Obtener los documentos de la colección
        vacantesCollection.get().addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
            override fun onComplete(task: com.google.android.gms.tasks.Task<QuerySnapshot>) {
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

                    // Configura el adaptador del RecyclerView
                    val adapter = VacantesAdapter(vacantesList)
                    recyclerView.adapter = adapter
                } else {
                    // Manejar el error
                    // Puedes agregar un TextView adicional en el layout para mostrar errores
                }
            }
        })
    }

    data class Vacante(val ramaTecnica: String?, val salario: String?, val unidadAcademica: String?)

    class VacantesAdapter(private val vacantesList: List<Vacante>) :
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
    }
}



