package com.dogplace.project2.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.R
import com.dogplace.project2.common.Constants
import com.dogplace.project2.common.Utils
import com.dogplace.project2.databinding.ActivityAddPlaceBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val MY_LOCATION_REQUEST_CODE = 200

class AddPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mBinding: ActivityAddPlaceBinding

    private lateinit var googleMap: GoogleMap
    private lateinit var addPlaceMap: MapView

    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        addPlaceMap = mBinding.addPlaceMap
        addPlaceMap.onCreate(savedInstanceState)
        addPlaceMap.getMapAsync(this)

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

        // Obtener la ubicación actual y agregar un marcador
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                selectedLocation = latLng
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng).title("Ubicación actual"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
            }
        }

        googleMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            googleMap.clear() // Elimina los marcadores anteriores del mapa
            googleMap.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))

            mBinding.placeData.visibility = android.view.View.VISIBLE
            mBinding.btnSave.visibility = android.view.View.VISIBLE
        }

        mBinding.btnSave.setOnClickListener {
            if (selectedLocation != null) {
                savePlace(selectedLocation!!)
            } else {
                Snackbar.make(
                    mBinding.root,
                    "Por favor, selecciona una ubicación en el mapa",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        mBinding.btnLike.setOnCheckedChangeListener { buttonView, isChecked ->
            val newIcon = if (isChecked) R.drawable.ic_like_green_big else R.drawable.ic_like_big
            buttonView.setCompoundDrawablesWithIntrinsicBounds(newIcon, 0, 0, 0)

            if (isChecked && mBinding.btnDislike.isChecked) {
                mBinding.btnDislike.isChecked = false
                val newIconDislike = R.drawable.ic_dislike_big
                mBinding.btnDislike.setCompoundDrawablesWithIntrinsicBounds(newIconDislike, 0, 0, 0)
            }
        }

        mBinding.btnDislike.setOnCheckedChangeListener { buttonView, isChecked ->
            val newIcon =
                if (isChecked) R.drawable.ic_dislike_red_big else R.drawable.ic_dislike_big
            buttonView.setCompoundDrawablesWithIntrinsicBounds(newIcon, 0, 0, 0)

            if (isChecked && mBinding.btnLike.isChecked) {
                mBinding.btnLike.isChecked = false
                val newIconLike = R.drawable.ic_like_big
                mBinding.btnLike.setCompoundDrawablesWithIntrinsicBounds(newIconLike, 0, 0, 0)
            }
        }

    }

    private fun savePlace(latLng: LatLng) {
        val namePlace: String = mBinding.tietPlaceName.text.toString().trim()
        val typePlace: String = mBinding.spinnerPlaceType.selectedItem.toString()
        val likeValue = if (mBinding.btnLike.isChecked) 1 else 0
        val dislikeValue = if (mBinding.btnDislike.isChecked) 1 else 0
        val commentPlace: String = mBinding.tietPlaceComments.text.toString().trim()

        if (namePlace.isEmpty()) {
            Snackbar.make(
                mBinding.root,
                "Por favor, completa todos los campos",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            val token = Utils.getToken(this)
            lifecycleScope.launch {
                val placeExists = comprobarSiExiste(namePlace)
                withContext(Dispatchers.Main) {
                    if (!placeExists) {
                        // Hacer la solicitud HTTP para insertar los datos en la base de datos
                        val stringRequest = object : StringRequest(
                            Method.POST, Constants.PATH_ADD_PLACE,
                            Response.Listener {
                                try {
                                    // Inserta la valoración del usuario en la base de datos
                                    if (commentPlace.isNotEmpty() || likeValue == 1 || dislikeValue == 1) {
                                        Utils.saveComment(
                                            namePlace,
                                            commentPlace,
                                            likeValue,
                                            dislikeValue,
                                            this@AddPlaceActivity
                                        )
                                    } else {
                                        Utils.saveComment(
                                            namePlace,
                                            "",
                                            0,
                                            0,
                                            this@AddPlaceActivity
                                        )
                                    }
                                    // Muestra un diálogo de agradecimiento
                                    val builder = MaterialAlertDialogBuilder(this@AddPlaceActivity)
                                    builder.setMessage("Gracias por añadir este establecimiento a Dogplace")
                                    builder.setPositiveButton("ACEPTAR") { dialog, _ ->
                                        // Cierra la actividad actual y vuelve a la MainActivity
                                        dialog.dismiss()
                                        finish()
                                    }
                                    builder.show()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            Response.ErrorListener {
                                Log.i("ERROR: ", "Error al hacer la solicitud HTTP")
                                Snackbar.make(
                                    mBinding.root,
                                    "Error en la conexión",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }) {
                            override fun getParams(): Map<String, String> {
                                val params = HashMap<String, String>()
                                params["NombreEstablecimiento"] = namePlace
                                params["Latitud"] = latLng.latitude.toString()
                                params["Longitud"] = latLng.longitude.toString()
                                params["tipoEstablecimiento"] = typePlace
                                params["access_token"] = token
                                return params
                            }
                        }
                        Volley.newRequestQueue(this@AddPlaceActivity).add(stringRequest)
                    } else {
                        runOnUiThread {
                            val builder = AlertDialog.Builder(this@AddPlaceActivity)
                            builder.setMessage("Hemos detectado que este lugar ya ha sido añadido por otro usuario. Puedes valorarlo en la pestaña de exploración.")
                            builder.setPositiveButton("Aceptar") { _, _ -> }
                            builder.show()
                        }
                    }
                }
            }
        }
    }

    private suspend fun comprobarSiExiste(namePlace: String): Boolean {
        val params = HashMap<String, String>()
        params["NombreEstablecimiento"] = namePlace
        params["access_token"] = Utils.getToken(this)

        val response = makeNetworkRequest(Constants.PATH_EXPLORE, params)

        val jsonObject = JSONObject(response)
        return jsonObject.getBoolean("UbicacionRepetida")
    }

    private suspend fun makeNetworkRequest(url: String, params: HashMap<String, String>): String {
        return suspendCancellableCoroutine { continuation ->
            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    continuation.resume(response)
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    return params
                }
            }
            Volley.newRequestQueue(this).add(stringRequest)
        }
    }

    override fun onResume() {
        super.onResume()
        addPlaceMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        addPlaceMap.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        addPlaceMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        addPlaceMap.onLowMemory()
    }
}