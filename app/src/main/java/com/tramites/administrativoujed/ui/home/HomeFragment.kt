package com.tramites.administrativoujed.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.tramites.administrativoujed.CrearCuentaActivity
import com.tramites.administrativoujed.MainActivity
import com.tramites.administrativoujed.MainCompletar
import com.tramites.administrativoujed.R
import com.tramites.administrativoujed.databinding.FragmentHomeBinding
import com.tramites.administrativoujed.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navegation: BottomNavigationView
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var formularioEnviado = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val registarButton: Button = binding.registar

        if (formularioEnviado) {
            registarButton.visibility = View.GONE // Oculta el botón si el formulario ya se ha enviado
        } else {
            registarButton.visibility = View.VISIBLE
        }

        //Nevegar a la pagina de registro

        registarButton.setOnClickListener {
            val intent = Intent(requireContext(), MainCompletar::class.java)
            startActivity(intent)
        }

        //Funcion Cerrar sesion
        val btnCerrar: Button = binding.btnCerrar
        btnCerrar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()


            val loginIntent = Intent(requireContext(), MainActivity::class.java)
            startActivity(loginIntent)
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}