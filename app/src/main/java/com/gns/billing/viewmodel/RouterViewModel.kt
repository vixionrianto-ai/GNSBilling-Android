package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.Router
import com.gns.billing.repository.RouterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouterViewModel : ViewModel() {

    private val repository = RouterRepository()

    private val _router = MutableStateFlow<List<Router>>(emptyList())
    val router: StateFlow<List<Router>> = _router

    private val _profiles = MutableStateFlow<List<String>>(emptyList())
    val profiles: StateFlow<List<String>> = _profiles

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadRouter() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getRouter()
                if (response.success) {
                    _router.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadProfiles(routerId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getProfiles(routerId)
                if (response.success) {
                    _profiles.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createSecret(
        routerId: Int,
        username: String,
        password: String,
        profile: String,
        onResult: (Boolean,String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Menggunakan repository agar konsisten dengan fungsi lainnya
                val response = repository.createSecret(routerId, username, password, profile)
                if (response.success) {
                    onResult(true, "Secret PPPoE berhasil dibuat di MikroTik!")
                } else {
                    onResult(false, response.message ?: "Gagal membuat secret.")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Gagal terhubung ke server.")
            }
        }
    }
}