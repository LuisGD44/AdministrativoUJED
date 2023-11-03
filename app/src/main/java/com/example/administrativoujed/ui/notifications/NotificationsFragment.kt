package com.example.administrativoujed.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.administrativoujed.R
import com.example.administrativoujed.ui.calendario.CalendarioFragment
import com.example.administrativoujed.ui.notifications.NotificationsFragment


class NotificationsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_notifications, container, false)
        val notificationTextView = rootView.findViewById<TextView>(R.id.text_notifications)

        val eventosMarcados = arguments?.getSerializable("eventosMarcados") as? MutableList<CalendarioFragment.EventoMarcado>

        if (eventosMarcados != null) {
            // Convierte la lista de eventos en un texto
            val notificationText = eventosMarcados.joinToString("\n") { it.descripcion }

            // Muestra las notificaciones en el TextView
            notificationTextView.text = notificationText
        }

        return rootView
    }
}
