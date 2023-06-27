package com.dogplace.project2.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dogplace.project2.R
import com.dogplace.project2.entities.Valoracion

class CommentAdapter(private var valoraciones: List<Valoracion>) :
    RecyclerView.Adapter<CommentAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_place_detail, parent, false)
    ) {
        val usuarioTextView: TextView = itemView.findViewById(R.id.username)
        val comentarioTextView: TextView = itemView.findViewById(R.id.tvLastComment)
        val dislikesTextView: TextView = itemView.findViewById(R.id.tvDislikes)
        val likesTextView: TextView = itemView.findViewById(R.id.tvLikes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        return RegistroViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val valoracion = valoraciones[position]

        // Crear un SpannableStringBuilder para construir el texto con diferentes estilos
        val comentarioBuilder = SpannableStringBuilder()
        comentarioBuilder.append(valoracion.commentPlace)

        // Agregar la fecha en cursiva y en gris claro
        val fechaStart = comentarioBuilder.length
        comentarioBuilder.append(" ${valoracion.fechaHoraCreacion}")
        comentarioBuilder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(holder.itemView.context, R.color.gray_light)),
            fechaStart,
            comentarioBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        comentarioBuilder.setSpan(
            StyleSpan(Typeface.ITALIC),
            fechaStart,
            comentarioBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Establecer el texto construido en comentarioTextView
        holder.comentarioTextView.text = comentarioBuilder
        holder.usuarioTextView.text = valoracion.username + " dijo: "
        holder.likesTextView.text = valoracion.likeValue.toString()
        holder.dislikesTextView.text = valoracion.dislikeValue.toString()
    }

    override fun getItemCount(): Int {
        return valoraciones.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newValoraciones: List<Valoracion>) {
        this.valoraciones = newValoraciones
        notifyDataSetChanged()
    }
}



