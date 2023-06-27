package com.dogplace.project2.rest

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.adapters.CommentAdapter
import com.dogplace.project2.common.Constants
import com.dogplace.project2.common.Utils.getToken
import com.dogplace.project2.entities.Valoracion
import org.json.JSONArray
import org.json.JSONException

class RestComments {

    fun getCommentsByPlaceId(placeId: Int, context: Context, adapter: CommentAdapter, onError: (VolleyError) -> Unit) {
        val token = getToken(context)
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.POST, Constants.PATH_GET_COMMENT,
            { response ->
                try {
                    val valoraciones = mutableListOf<Valoracion>()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val commentPlace = jsonObject.getString("commentPlace")
                        val idUsuario = jsonObject.getInt("idUsuario")
                        val username = jsonObject.getString("Username")
                        val idEstablecimiento = jsonObject.getInt("idEstablecimiento")
                        val nombreEstablecimiento = jsonObject.getString("NombreEstablecimiento")
                        val likeValue = jsonObject.getInt("likeValue")
                        val dislikeValue = jsonObject.getInt("dislikeValue")
                        val fechaHoraCreacion = jsonObject.getString("FechaRegistro")
                        valoraciones.add(Valoracion(id, idUsuario, username, commentPlace, idEstablecimiento, nombreEstablecimiento, likeValue, dislikeValue, fechaHoraCreacion))
                    }
                    adapter.setData(valoraciones)
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
                params["ubicacionId"] = placeId.toString()
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

}