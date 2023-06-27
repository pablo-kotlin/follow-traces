package com.dogplace.project2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dogplace.project2.common.Constants.PATH_LOGIN
import com.dogplace.project2.common.Constants.PATH_SIGNUP
import com.dogplace.project2.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONException
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding
    private var isNewUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val loginButton = mBinding.btnLogin
        val btnSignIn = mBinding.btnSignIn

        btnSignIn.setOnClickListener {
            isNewUser = !isNewUser
            updateSignInButton()
        }

        loginButton.setOnClickListener {
            if (isNewUser) {
                setUpNewUser()
            } else {
                checkUsernameAndPassword()
            }
        }
    }

    private fun updateSignInButton() {
        if (isNewUser) {
            mBinding.btnSignIn.text = getString(R.string.back)
            mBinding.passwordConfirm.visibility = View.VISIBLE
            mBinding.emailSignUp.visibility = View.VISIBLE
            mBinding.btnLogin.text = getString(R.string.btn_create)
        } else {
            mBinding.btnSignIn.text = getString(R.string.new_user)
            mBinding.passwordConfirm.visibility = View.INVISIBLE
            mBinding.emailSignUp.visibility = View.INVISIBLE
            mBinding.btnLogin.text = getString(R.string.btn_access)
        }
    }

    private fun setUpNewUser() {

        val username: String = mBinding.username.text.toString().trim()
        var password: String = mBinding.password.text.toString().trim()
        val confirmPassword: String = mBinding.passwordConfirm.text.toString().trim()
        val userEmail: String = mBinding.emailSignUp.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userEmail.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_LONG).show()
        } else {
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            } else {
                // Generar un salt aleatorio
                val salt = BCrypt.gensalt()

                // Cifrar la contraseña con el salt
                val hashedPassword = BCrypt.hashpw(password, salt)

                password = hashedPassword

                // Hacer la solicitud HTTP para insertar los datos en la base de datos
                val stringRequest = object : StringRequest(
                    Method.POST, PATH_SIGNUP,
                    Response.Listener { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            val status = jsonObject.getString("status")
                            val message = jsonObject.getString("message")
                           if (status == "ok") {
                                val builder = MaterialAlertDialogBuilder(this)
                                builder.setMessage("Usuario registrado correctamente")
                                builder.setPositiveButton("ACEPTAR") { dialog, _ ->
                                    // Cierra la actividad actual y vuelve a la MainActivity
                                    dialog.dismiss()
                                    checkUsernameAndPassword()
                                    mBinding.passwordConfirm.visibility = View.INVISIBLE
                                    mBinding.emailSignUp.visibility = View.INVISIBLE
                                }
                                builder.show()
                            } else {
                                // Usuario ya existe en la base de datos
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this, "Error en la conexión", Toast.LENGTH_LONG).show()
                    }) {
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["username"] = username
                        params["password"] = password
                        params["email"] = userEmail
                        return params
                    }
                }
                Volley.newRequestQueue(this).add(stringRequest)
            }
        }
    }

    private fun checkUsernameAndPassword() {
        val username: String = mBinding.username.text.toString().trim()
        val password: String = mBinding.password.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_LONG).show()
        } else {
            val stringRequest = object : StringRequest(
                Method.POST, PATH_LOGIN,
                Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response)
                        val status = jsonResponse.getInt("status")
                        if (status == 1) {
                            // El usuario y la contraseña son correctos
                            val token = jsonResponse.getString("token")
                            saveToken(token)
                            mBinding.username.text?.clear()
                            mBinding.password.text?.clear()
                            mBinding.passwordConfirm.text?.clear()
                            mBinding.emailSignUp.text?.clear()
                            // Cierra la actividad actual y va a la MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // El usuario y/o la contraseña son incorrectos
                            Toast.makeText(
                                this,
                                "Nombre de usuario o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this, "Error en la conexión", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password
                    return params
                }
            }
            Volley.newRequestQueue(this).add(stringRequest)
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }
}

