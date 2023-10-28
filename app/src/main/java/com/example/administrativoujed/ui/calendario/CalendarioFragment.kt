package com.example.administrativoujed.ui.calendario

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.administrativoujed.R

class CalendarioFragment : Fragment() {

    companion object {
        fun newInstance() = CalendarioFragment()
    }

    private lateinit var viewModel: CalendarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalendarioViewModel::class.java)
        // TODO: Use the ViewModel
    }

}