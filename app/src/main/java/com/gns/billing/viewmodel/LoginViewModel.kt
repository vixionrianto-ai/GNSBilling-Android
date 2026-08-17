package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.LoginState
import com.gns.billing.repository.AuthRepository
import retrofit2.HttpException
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {

        viewModelScope.launch {

            _loginState.value = LoginState.Loading

            try {

                val response = repository.login(email, password)

                if (response.isSuccessful && response.body() != null) {

                    _loginState.value =
                        LoginState.Success(response.body()!!)

                } else {

                    _loginState.value =
                        LoginState.Error("Email atau Password salah")

                }

            } catch (e: Exception) {

                val errorMsg = if (e is HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        JSONObject(errorBody ?: "{}").optString("message", "Login gagal (${e.code()})")
                    } catch (_: Exception) {
                        "Login gagal (${e.code()})"
                    }
                } else {
                    e.localizedMessage ?: "Gagal terhubung ke server"
                }

                _loginState.value = LoginState.Error(errorMsg)

            }

        }

    }
}