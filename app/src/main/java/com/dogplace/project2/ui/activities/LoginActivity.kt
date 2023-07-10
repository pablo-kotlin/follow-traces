package com.dogplace.project2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dogplace.project2.R
import com.dogplace.project2.common.PrivateConstants
import com.dogplace.project2.data.UserRepository
import com.dogplace.project2.data.remote.ApiService
import com.dogplace.project2.data.remote.UserRepositoryImpl
import com.dogplace.project2.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginBinding
    private var isNewUser: Boolean = false

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(PrivateConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)
    private val userRepository: UserRepository = UserRepositoryImpl(apiService)

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

    private fun setUpNewUser() = lifecycleScope.launch {

        val username: String = mBinding.username.text.toString().trim()
        var password: String = mBinding.password.text.toString().trim()
        val confirmPassword: String = mBinding.passwordConfirm.text.toString().trim()
        val userEmail: String = mBinding.emailSignUp.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userEmail.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Por favor, rellene todos los campos",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (password != confirmPassword) {
                Toast.makeText(
                    this@LoginActivity,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Generar un salt aleatorio
                val salt = BCrypt.gensalt()
                // Cifrar la contraseña con el salt
                val hashedPassword = BCrypt.hashpw(password, salt)
                password = hashedPassword
                // Hacer la solicitud HTTP para insertar los datos en la base de datos
                try {
                    val response = userRepository.signUp(username, password, userEmail)
                    if (response.isSuccessful) {
                        val jsonObject = JSONObject(response.body()?.string()!!)
                        val status = jsonObject.getString("status")
                        val message = jsonObject.getString("message")
                        if (status == "ok") {
                            MaterialAlertDialogBuilder(this@LoginActivity)
                                .setMessage("Usuario registrado correctamente")
                                .setPositiveButton("ACEPTAR") { dialog, _ ->
                                    // Cierra la actividad actual y vuelve a la MainActivity
                                    dialog.dismiss()
                                    checkUsernameAndPassword()
                                    mBinding.passwordConfirm.visibility = View.INVISIBLE
                                    mBinding.emailSignUp.visibility = View.INVISIBLE
                                }
                                .show()
                        } else {
                            // Usuario ya existe en la base de datos
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Manejar el error
                        Toast.makeText(
                            this@LoginActivity,
                            "Error en la conexión",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    // Manejar la excepción
                    Toast.makeText(this@LoginActivity, "Error en la conexión", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun checkUsernameAndPassword() = lifecycleScope.launch {
        val username: String = mBinding.username.text.toString().trim()
        val password: String = mBinding.password.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                "Rellene todos los campos, por favor",
                Toast.LENGTH_LONG
            ).show()
        } else {
            try {
                val response = userRepository.login(username, password)
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body()?.string()!!)
                    val status = jsonObject.getInt("status")
                    if (status == 1) {
                        // El usuario y la contraseña son correctos
                        val token = jsonObject.getString("token")
                        saveToken(token)
                        mBinding.username.text?.clear()
                        mBinding.password.text?.clear()
                        mBinding.passwordConfirm.text?.clear()
                        mBinding.emailSignUp.text?.clear()
                        // Cierra la actividad actual y va a la MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // El usuario y/o la contraseña son incorrectos
                        Toast.makeText(
                            this@LoginActivity,
                            "Nombre de usuario o contraseña incorrectos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Manejar el error
                    Toast.makeText(this@LoginActivity, "Error en la conexión", Toast.LENGTH_LONG)
                        .show()
                }
            } catch (e: Exception) {
                // Manejar la excepción
                Toast.makeText(this@LoginActivity, "Error en la conexión", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveToken(token: String) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            "MyEncryptedSharedPreferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPreferences.edit().putString("token", token).apply()
    }
}