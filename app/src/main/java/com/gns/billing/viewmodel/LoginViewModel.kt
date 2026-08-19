package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.LoginState
import com.gns.billing.repository.AuthRepository
import com.gns.billing.session.SessionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = repository.login(email.trim(), password)
                val data = response.body()?.data
                if (response.isSuccessful && data != null) {
                    SessionProvider.token = data.token
                    _loginState.value = LoginState.Success(response.body()!!)
                } else {
                    val message = try {
                        JSONObject(response.errorBody()?.string() ?: "{}").optString(
                            "message", "Login gagal (${response.code()})"
                        )
                    } catch (_: Exception) {
                        "Login gagal (${response.code()})"
                    }
                    _loginState.value = LoginState.Error(message)
                }
            } catch (e: Exception) {
                val message = if (e is HttpException) "Server error (${e.code()})"
                else e.localizedMessage ?: "Gagal terhubung ke server"
                _loginState.value = LoginState.Error(message)
            }
        }
    }
}
