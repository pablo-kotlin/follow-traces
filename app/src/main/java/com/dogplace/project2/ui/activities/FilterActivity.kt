package com.dogplace.project2.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogplace.project2.data.remote.RestUbicaciones
import com.dogplace.project2.databinding.ActivityFilterBinding
import com.dogplace.project2.entities.Ubicacion
import com.dogplace.project2.ui.adapters.PlaceAdapter

class FilterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)

        val ubicaciones: List<Ubicacion> = emptyList()

        val adapter = PlaceAdapter(ubicaciones)

        val restUbicaciones = RestUbicaciones()
        restUbicaciones.getPlacesByLatLng(
            latitud,
            longitud,
            applicationContext,
            adapter,
            onError = {
                // Manejar error aquí
            }
        )

        mBinding.rvListPlaces.adapter = adapter
        mBinding.rvListPlaces.layoutManager = LinearLayoutManager(this)

        mBinding.btnMapList.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }
    }
}