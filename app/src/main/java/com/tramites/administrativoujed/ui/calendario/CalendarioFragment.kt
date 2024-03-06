package com.tramites.administrativoujed.ui.calendario

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
import com.tramites.administrativoujed.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CalendarioFragment : Fragment() {

    private lateinit var gridView: GridView
    private val eventosMarcados = mutableListOf(
        // Esto en un futuro se puede hacer desde un formulario en la aplicación web
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

        val firstDayOfWeek = Calendar.getInstance().firstDayOfWeek
        val currentDate = getCurrentDate()
        val days = getDaysOfMonth(currentDate, firstDayOfWeek)
        val calendarAdapter = CalendarAdapter(requireContext(), days)
        gridView.adapter = calendarAdapter

        showEventNotifications(currentDate)
    }

    private fun getCurrentDate(): Date {
        return Date()
    }

    private fun getDaysOfMonth(currentDate: Date, firstDayOfWeek: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.firstDayOfWeek = firstDayOfWeek

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val emptySpaces = (firstDayOfMonth - firstDayOfWeek + 7) % 7

        val daysList = mutableListOf<String>()

        for (i in 1..emptySpaces) {
            daysList.add("")
        }

        for (i in 1..daysInMonth) {
            daysList.add(i.toString())
        }

        return daysList
    }

    private fun showEventNotifications(currentDate: Date) {
        val dateFormat = SimpleDateFormat("d")
        val currentDay = dateFormat.format(currentDate)

        for (evento in eventosMarcados) {
            if (evento.fecha == currentDay) {
                showNotification(evento.descripcion)
            }
        }
    }

    private fun showNotification(message: String) {
    }

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

                if (day.isEmpty()) {
                    // Día vacío
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.BLACK)
                } else if (day == getCurrentDay()) {
                    // Día actual
                    setBackgroundColor(Color.BLUE)
                    setTextColor(Color.WHITE)
                } else {

                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.BLACK)
                }

                //En futuras actualizaciones, se puede implementar un formulario en el apartado web
                //En el cual se ingresen los eventos o festividades
                when (day) {
                    "8" -> text = "7 ♀️"
                    "18" -> text = "18 \uD83C\uDDF2\uD83C\uDDFD"
                    "24" -> text = "34 \uD83D\uDC90"
                    "28" -> text = "28 ✝️"
                    "29" -> text = "29 ✝️"
                    "30" -> text = "30 ✝️"
                    "31" -> text = "31 ✝️"

                }
            }

            return cell
        }

        private fun getCurrentDay(): String {
            val dateFormat = SimpleDateFormat("d")
            return dateFormat.format(Date())
        }
    }
}
