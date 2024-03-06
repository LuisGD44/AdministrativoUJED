package com.tramites.administrativoujed.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tramites.administrativoujed.MainCompletar
import com.tramites.administrativoujed.R
import com.tramites.administrativoujed.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.tramites.administrativoujed.MainActivity
import com.tramites.administrativoujed.editarDatos

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Leer el estado de la bandera de registro completo
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val registroCompleto = sharedPreferences.getBoolean("registroCompleto", false)

        if (registroCompleto) {
            // Si el formulario de registro está completo, mostramos el botón "Editar datos"
            binding.registrar.visibility = View.VISIBLE
            binding.registrar.text = "Editar datos"
        } else {
            // Si el formulario de registro no está completo, mostramos el botón de registro
            binding.registrar.visibility = View.VISIBLE
            binding.registrar.text = "Completar Registro"
        }

        binding.registrar.setOnClickListener {
            if (registroCompleto) {
                // Navegar a la página de edición de datos
                val intent = Intent(requireActivity(), editarDatos::class.java)
                startActivity(intent)
            } else {
                // Navegar a la página de completar registro
                val intent = Intent(requireActivity(), MainCompletar::class.java)
                startActivity(intent)
            }
        }

        binding.btnCerrar.setOnClickListener {
            // Cerrar sesión y volver a la actividad de inicio de sesión
            FirebaseAuth.getInstance().signOut()
            val loginIntent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(loginIntent)
            requireActivity().finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
