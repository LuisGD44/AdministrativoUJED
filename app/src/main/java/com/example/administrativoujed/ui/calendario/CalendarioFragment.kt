package com.example.administrativoujed.ui.calendario

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.administrativoujed.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CalendarioFragment : Fragment() {

    private lateinit var gridView: GridView
    private val eventosMarcados = mutableListOf(
        //Esto en un futuro se puede hacer desde un formulario en la aplicacion web
        EventoMarcado("2", "Dia de muertos"),
        EventoMarcado("20", "Revolucion Mexicana"),
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendario, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridView = view.findViewById(R.id.gridViewCalendar)

        val currentDate = getCurrentDate()
        // Llena la lista de días con los días del mes
        val days = getDaysOfMonth(currentDate)

        val calendarAdapter = CalendarAdapter(requireContext(), days)
        gridView.adapter = calendarAdapter

        // Muestra notificaciones de eventos marcados
        showEventNotifications(currentDate)
    }


    private fun getCurrentDate(): Date {
        return Date()
    }

    private fun getDaysOfMonth(currentDate: Date): List<String> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        return (1..daysInMonth).map { it.toString() }
    }

    private fun showEventNotifications(currentDate: Date) {
        val dateFormat = SimpleDateFormat("d")
        val currentDay = dateFormat.format(currentDate)

        for (evento in eventosMarcados) {
            if (evento.fecha == currentDay) {
                // Mostrar notificación para el evento marcado
                showNotification(evento.descripcion)
            }
        }
    }
    private fun showNotification(message: String) {
        // Implementa aquí la lógica para mostrar notificaciones en tu aplicación.
        // Puedes utilizar el sistema de notificaciones de Android, como las notificaciones locales o FCM.
        // Aquí deberías mostrar la notificación en el NotificationFragment, pero necesitas establecer la comunicación
        // entre fragmentos para hacerlo.
    }

    // Resto del código de tu fragmento...

    data class EventoMarcado(val fecha: String, val descripcion: String)

    class CalendarAdapter(context: Context, days: List<String>) : BaseAdapter() {

        private val mContext: Context = context
        private val mDays: List<String> = days

        override fun getCount(): Int {
            return mDays.size
        }

        override fun getItem(position: Int): Any {
            return mDays[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val day = getItem(position) as String
            val cell = convertView ?: TextView(mContext)

            (cell as? TextView)?.apply {
                text = day
                gravity = Gravity.CENTER
                setPadding(8, 8, 8, 8)

                if (day == getCurrentDay()) {
                    setBackgroundColor(Color.BLUE)
                    setTextColor(Color.WHITE)
                }

                if (day == "2") {
                    text = "2 \uD83D\uDC80"
                }
                if (day == "20") {
                    text = "20 \uD83C\uDDF2\uD83C\uDDFD"
                }

            }

            return cell
        }

        private fun getCurrentDay(): String {
            val dateFormat = SimpleDateFormat("dd")
            return dateFormat.format(Date())
        }
    }
}



