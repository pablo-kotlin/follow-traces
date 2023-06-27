package com.dogplace.project2.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dogplace.project2.R
import com.dogplace.project2.entities.Ubicacion
import java.text.DecimalFormat

class PlaceAdapter(private var ubicaciones: List<Ubicacion>) :
    RecyclerView.Adapter<PlaceAdapter.UbicacionesViewHolder>() {

    class UbicacionesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_list_place, parent, false)
    ) {
        val placeTitle: TextView = itemView.findViewById(R.id.tvCardTitle)
        val distance: TextView = itemView.findViewById(R.id.tvDistance)
        val numLikes: TextView = itemView.findViewById(R.id.tvLikes)
        val numDislikes: TextView = itemView.findViewById(R.id.tvDislikes)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbicacionesViewHolder {
        return UbicacionesViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UbicacionesViewHolder, position: Int) {
        val ubicacion = ubicaciones[position]

        holder.placeTitle.text = ubicacion.nombreEstablecimiento

        if (ubicacion.distancia!! > 1000) {
            val distanciaEnKilometros = ubicacion.distancia.toDouble() / 1000
            val decimalFormat = DecimalFormat("#,##0.00")
            val distanciaFormateada = decimalFormat.format(distanciaEnKilometros)
            holder.distance.text = "Distancia: $distanciaFormateada km."
        } else {
            val decimalFormat = DecimalFormat("#,##0")
            val distanciaFormateada = decimalFormat.format(ubicacion.distancia)
            holder.distance.text = "Distancia: $distanciaFormateada metros."
        }

        holder.numLikes.text = ubicacion.numLikes.toString()
        holder.numDislikes.text = ubicacion.numDislikes.toString()

        when (ubicacion.tipoEstablecimiento) {
            "Restaurante" -> holder.imgIcon.setImageResource(R.drawable.ic_restaurant)
            "Tienda" -> holder.imgIcon.setImageResource(R.drawable.ic_shopping)
            "Zona Verde" -> holder.imgIcon.setImageResource(R.drawable.ic_green_zone)
            "Alojamiento" -> holder.imgIcon.setImageResource(R.drawable.ic_accommodation)
            else -> holder.imgIcon.setImageResource(R.drawable.ic_addplace)
        }
    }

    override fun getItemCount(): Int {
        return ubicaciones.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newUbicaciones: List<Ubicacion>) {
        this.ubicaciones = newUbicaciones
        notifyDataSetChanged()
    }
    
}



