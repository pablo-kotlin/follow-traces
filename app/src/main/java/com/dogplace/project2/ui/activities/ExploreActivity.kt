package com.dogplace.project2.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.R
import com.dogplace.project2.common.Constants
import com.dogplace.project2.common.Utils
import com.dogplace.project2.databinding.ActivityExploreBinding
import com.dogplace.project2.entities.Ubicacion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONException

private const val MY_LOCATION_REQUEST_CODE = 100

class ExploreActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mBinding: ActivityExploreBinding

    private lateinit var googleMap: GoogleMap
    private lateinit var exploreMap: MapView

    private lateinit var listaUbicaciones: List<Ubicacion>
    private val marcadores = mutableListOf<Marker>()

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        exploreMap = mBinding.exploreMap
        exploreMap.onCreate(savedInstanceState)
        exploreMap.getMapAsync(this)

        getUbicaciones(
            onSuccess = { ubicaciones ->
                listaUbicaciones = ubicaciones
                crearMarcadores()
            },
            onError = {
                Log.i("msg", it.toString())
            })

        val buttonMap = mapOf(
            mBinding.btnShop to "Tienda",
            mBinding.btnRestaurant to "Restaurante",
            mBinding.btnAccommodation to "Alojamiento",
            mBinding.btnGreenZone to "Zona Verde",
            mBinding.btnOthers to "Otros"
        )

        buttonMap.forEach { (button, nombreMarcador) ->
            buttonFeatures(button, nombreMarcador)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Habilitar la capa de mi ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_LOCATION_REQUEST_CODE
            )
        }

        // Obtener la ubicación actual
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                mBinding.btnFilter.setOnClickListener {
                    val intent = Intent(this, FilterActivity::class.java)
                    intent.putExtra("latitud", latLng.latitude)
                    intent.putExtra("longitud", latLng.longitude)
                    startActivity(intent)
                }
            }
        }

        // Personaliza la ventana de información del marcador
        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null // Usa la ventana de información predeterminada pero con contenido personalizado
            }

            override fun getInfoContents(marker: Marker): View {
                // No muestra el snippet en la ventana de información del marcador
                val infoTitleView = TextView(this@ExploreActivity)
                infoTitleView.text = marker.title
                return infoTitleView
            }
        })

        // Establece un OnMarkerClickListener en el mapa
        googleMap.setOnMarkerClickListener { marker ->
            // Cambia la visibilidad del cardView a VISIBLE
            mBinding.cardView.visibility = View.VISIBLE

            // Actualiza el texto del TextView tvCardTitle con el ID de la ubicación
            val ubicacionId = marker.snippet // Extrae el ID de la ubicación del snippet
            val nombreEstablecimiento = marker.title

            mostrarDatosCardview(ubicacionId, nombreEstablecimiento)

            true
        }

        // Establece un OnMapClickListener en el mapa
        googleMap.setOnMapClickListener {
            // Cambia la visibilidad del cardView a GONE
            mBinding.cardView.visibility = View.GONE
        }

    }

    private fun getUbicaciones(
        onSuccess: (List<Ubicacion>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        requestQueue = Volley.newRequestQueue(this)

        val token = Utils.getToken(this)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_EXPLORE,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val ubicaciones = Utils.parseUbicaciones(jsonArray)
                    onSuccess(ubicaciones)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            onError
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["access_token"] = token
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun crearMarcadores() {
        for (ubicacion in listaUbicaciones) {

            val iconResId = getIconResource(ubicacion.tipoEstablecimiento)

            val markerOptions = MarkerOptions()
                .position(LatLng(ubicacion.latitud!!, ubicacion.longitud!!))
                .title(ubicacion.nombreEstablecimiento)
                .snippet("${ubicacion.id}")
                .icon(Utils.getBitmapDescriptor(this, iconResId))

            val marker = googleMap.addMarker(markerOptions)
            marker?.let { marcadores.add(it) }
        }
    }

    private fun actualizarVisibilidadMarcadores(tipoEstablecimiento: String? = null) {
        for (marker in marcadores) {
            val ubicacion = listaUbicaciones[marcadores.indexOf(marker)]
            if (ubicacion.tipoEstablecimiento == tipoEstablecimiento) {
                marker.isVisible = !marker.isVisible
            }
        }
    }

    private fun buttonFeatures(button: CompoundButton, nombreMarcador: String) {
        button.setOnClickListener {
            actualizarVisibilidadMarcadores(nombreMarcador)
        }
    }


    private fun mostrarDatosCardview(ubicacionId: String?, nombreEstablecimiento: String?) {
        requestQueue = Volley.newRequestQueue(this)
        val token = Utils.getToken(this)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_GET_COMMENT,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    // Comprobar si commentPlace está vacío
                    val commentPlace = jsonArray.getJSONObject(0).getString("commentPlace")
                    mBinding.tvLastComment.text = if (commentPlace.isEmpty()) {
                        // Si está vacío, establecer el texto en letra cursiva
                        val italicText =
                            "<i>No se ha escrito ninguna valoración por el momento.</i>"
                        HtmlCompat.fromHtml(italicText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        // Si no está vacío, concatenar "Último comentario: " al principio
                        val lastCommentText = "<i>Última reseña: </i>$commentPlace"
                        HtmlCompat.fromHtml(lastCommentText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                    mBinding.tvCardTitle.text = nombreEstablecimiento
                    mBinding.tvLikes.text = jsonArray.getJSONObject(0).getString("NumLikes")
                    mBinding.tvDislikes.text = jsonArray.getJSONObject(0).getString("NumDislikes")
                    when (jsonArray.getJSONObject(0).getString("TipoEstablecimiento")) {
                        "Restaurante" -> mBinding.imgCard.setImageResource(R.drawable.ic_restaurant)
                        "Tienda" -> mBinding.imgCard.setImageResource(R.drawable.ic_shopping)
                        "Zona Verde" -> mBinding.imgCard.setImageResource(R.drawable.ic_green_zone)
                        "Alojamiento" -> mBinding.imgCard.setImageResource(R.drawable.ic_accommodation)
                        else -> mBinding.imgCard.setImageResource(R.drawable.ic_addplace)
                    }

                    mBinding.btnSeeMore.setOnClickListener {
                        val intent = Intent(this, DetailsPlaceActivity::class.java)
                        intent.putExtra("ubicacionId", ubicacionId)
                        intent.putExtra("nombreEstablecimiento", nombreEstablecimiento)
                        startActivity(intent)
                    }

                    mBinding.btnAddComment.setOnClickListener {
                        if (nombreEstablecimiento != null) {
                            showCommentDialog(nombreEstablecimiento)
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["access_token"] = token
                params["ubicacionId"] = ubicacionId.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun showCommentDialog(nombreEstablecimiento: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnAccept = dialogView.findViewById<Button>(R.id.btnAccept)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnLikeCard = dialogView.findViewById<ToggleButton>(R.id.btnLikeCard)
        val btnDislikeCard = dialogView.findViewById<ToggleButton>(R.id.btnDislikeCard)

        btnLikeCard.setOnCheckedChangeListener { buttonView, isChecked ->
            val newIcon = if (isChecked) R.drawable.ic_like_green_big else R.drawable.ic_like_big
            buttonView.setCompoundDrawablesWithIntrinsicBounds(newIcon, 0, 0, 0)
            if (isChecked && btnDislikeCard.isChecked) {
                btnDislikeCard.isChecked = false
                val newIconDislike = R.drawable.ic_dislike_big
                btnDislikeCard.setCompoundDrawablesWithIntrinsicBounds(newIconDislike, 0, 0, 0)
            }
        }

        btnDislikeCard.setOnCheckedChangeListener { buttonView, isChecked ->
            val newIcon =
                if (isChecked) R.drawable.ic_dislike_red_big else R.drawable.ic_dislike_big
            buttonView.setCompoundDrawablesWithIntrinsicBounds(newIcon, 0, 0, 0)
            if (isChecked && btnLikeCard.isChecked) {
                btnLikeCard.isChecked = false
                val newIconLike = R.drawable.ic_like_big
                btnLikeCard.setCompoundDrawablesWithIntrinsicBounds(newIconLike, 0, 0, 0)
            }
        }

        btnAccept.setOnClickListener {
            val editTextComment = dialogView.findViewById<TextInputLayout>(R.id.input_place_name)
            editTextComment.post {
                editTextComment.requestFocus()
            }
            val comment = editTextComment.editText?.text.toString()

            val likeValue = if (btnLikeCard.isChecked) 1 else 0

            val dislikeValue = if (btnDislikeCard.isChecked) 1 else 0

            Utils.saveComment(
                nombreEstablecimiento,
                comment,
                likeValue,
                dislikeValue,
                applicationContext
            )
            Log.i("Valor", likeValue.toString() + dislikeValue.toString())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getIconResource(tipoEstablecimiento: String): Int {
        return when (tipoEstablecimiento) {
            "Restaurante" -> R.drawable.ic_restaurant
            "Alojamiento" -> R.drawable.ic_accommodation
            "Zona Verde" -> R.drawable.ic_green_zone
            "Tienda" -> R.drawable.ic_shopping
            else -> R.drawable.ic_addplace
        }
    }

    override fun onResume() {
        super.onResume()
        exploreMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        exploreMap.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exploreMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        exploreMap.onLowMemory()
    }
}