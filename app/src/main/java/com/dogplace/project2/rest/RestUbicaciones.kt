package com.dogplace.project2.rest

import android.content.Context
import android.os.Looper
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.adapters.PlaceAdapter
import com.dogplace.project2.common.Constants
import com.dogplace.project2.common.Utils
import com.dogplace.project2.entities.Ubicacion
import org.json.JSONArray
import org.json.JSONException

class RestUbicaciones {

    fun getPlacesByLatLng(placeLat: Double, placeLng: Double, context: Context, adapter: PlaceAdapter, onError: (VolleyError) -> Unit) {
        val token = Utils.getToken(context)
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_PLACE_BY_DISTANCE,
            { response ->
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    Log.i("INFO", "Estamos en el hilo principal de la UI.")
                } else {
                    Log.i("INFO", "No estamos en el hilo principal de la UI.")
                }
                try {
                    val ubicaciones = mutableListOf<Ubicacion>()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getInt("Id")
                        val nombreEstablecimiento = jsonObject.getString("NombreEstablecimiento")
                        val tipoEstablecimiento = jsonObject.getString("TipoEstablecimiento")
                        val numLikes = jsonObject.getInt("NumLikes")
                        val numDislikes = jsonObject.getInt("NumDislikes")
                        val distancia = jsonObject.getDouble("Distancia")
                        val ubicacion = Ubicacion(
                            id,null,null, nombreEstablecimiento, null, tipoEstablecimiento, numLikes, numDislikes, distancia)
                        ubicaciones.add(ubicacion)
                    }
                    adapter.setData(ubicaciones)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("ERROR", error.toString())
                onError(error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["access_token"] = token
                params["userLat"] = placeLat.toString()
                params["userLng"] = placeLng.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

}