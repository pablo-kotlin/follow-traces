package com.dogplace.project2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogplace.project2.adapters.CommentAdapter
import com.dogplace.project2.databinding.ActivityDetailsPlaceBinding
import com.dogplace.project2.entities.Valoracion
import com.dogplace.project2.rest.RestComments

class DetailsPlaceActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDetailsPlaceBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDetailsPlaceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val ubicacionId = intent.getStringExtra("ubicacionId")
        val nombreEstablecimiento = intent.getStringExtra("nombreEstablecimiento")

        val valoraciones: List<Valoracion> = emptyList()

        val adapter = CommentAdapter(valoraciones)

        val restComments = RestComments()
        ubicacionId?.let {
            restComments.getCommentsByPlaceId(
                it.toInt(),
                applicationContext,
                adapter,
                onError = {
                    // Manejar error aquí
                }
            )
        }

        mBinding.rvComments.adapter = adapter
        mBinding.rvComments.layoutManager = LinearLayoutManager(this)

        mBinding.tvNamePlace.text = nombreEstablecimiento

    }
}