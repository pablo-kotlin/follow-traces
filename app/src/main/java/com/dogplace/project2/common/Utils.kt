package com.dogplace.project2.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.entities.Ubicacion
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import org.json.JSONArray
import org.json.JSONException

object Utils {

    fun getToken(context: Context): String {
        val sharedPreferences =
            context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }

    fun cleanOldTokens(context: Context) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val token = getToken(context)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_DELETE_TOKEN,
            { response ->
                try {
                    Log.i("MSG", response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("ERROR", error.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["access_token"] = token
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    fun parseUbicaciones(response: JSONArray): List<Ubicacion> {
        val ubicaciones = mutableListOf<Ubicacion>()

        for (i in 0 until response.length()) {
            val jsonObject = response.getJSONObject(i)
            val id = jsonObject.getInt("Id")
            val latitud = jsonObject.getDouble("Latitud")
            val longitud = jsonObject.getDouble("Longitud")
            val nombre = jsonObject.getString("NombreEstablecimiento")
            val usuario = jsonObject.getString("IdUsuario")
            val tipoEstablecimiento = jsonObject.getString("TipoEstablecimiento")

            val establecimiento = Ubicacion(id, latitud, longitud, nombre, usuario, tipoEstablecimiento, null, null, null)
            ubicaciones.add(establecimiento)
        }

        return ubicaciones
    }

    fun getBitmapDescriptor(context: Context, drawableRes: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, drawableRes)!!
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun saveComment(namePlace: String, commentPlace: String, likeValue: Int, dislikeValue: Int, context: Context){

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val token = getToken(context)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_ADD_COMMENT,
            { response ->
                try {
                    Log.i("MSG", response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.i("ERROR", error.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["access_token"] = token
                params["namePlace"] = namePlace
                params["commentPlace"] = commentPlace
                params["likeValue"] = likeValue.toString()
                params["dislikeValue"] = dislikeValue.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

}
