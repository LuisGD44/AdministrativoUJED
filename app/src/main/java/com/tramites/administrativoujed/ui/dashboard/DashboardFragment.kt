package com.tramites.administrativoujed.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tramites.administrativoujed.canastilla
import com.tramites.administrativoujed.databinding.FragmentDashboardBinding
import com.tramites.administrativoujed.descuentos
import com.tramites.administrativoujed.exentos
import com.tramites.administrativoujed.permiso
import com.tramites.administrativoujed.vacantesActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val btnExento: Button = binding.btnExento
        btnExento.setOnClickListener{
            val intent = Intent(requireContext(), exentos::class.java)
            startActivity(intent)
        }
        val btnCanasta: Button = binding.btncanasta
        btnCanasta.setOnClickListener{
            val intent = Intent(requireContext(), canastilla::class.java)
            startActivity(intent)
        }
        val btnDescuentos: Button = binding.btnDescuentos
        btnDescuentos.setOnClickListener{
            val intent = Intent(requireContext(), descuentos::class.java)
            startActivity(intent)
        }
        val btnPermisos: Button = binding.btnPermiso
        btnPermisos.setOnClickListener{
            val intent = Intent(requireContext(), permiso::class.java)
            startActivity(intent)
        }
        val btnVacantes: Button = binding.btnMaterial
        btnVacantes.setOnClickListener{
            val intent = Intent(requireContext(), vacantesActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}