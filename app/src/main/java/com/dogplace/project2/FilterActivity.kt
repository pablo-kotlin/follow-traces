package com.dogplace.project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dogplace.project2.adapters.PlaceAdapter
import com.dogplace.project2.databinding.ActivityFilterBinding
import com.dogplace.project2.entities.Ubicacion
import com.dogplace.project2.rest.RestUbicaciones

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
