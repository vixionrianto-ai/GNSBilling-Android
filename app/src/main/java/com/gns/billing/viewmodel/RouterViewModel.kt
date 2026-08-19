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
    private val _connectionMessage = MutableStateFlow<String?>(null)
    val connectionMessage: StateFlow<String?> = _connectionMessage

    fun loadRouter() {
        viewModelScope.launch {
            _loading.value = true
            try { _router.value = repository.getRouter().data }
            catch (e: Exception) { _connectionMessage.value = e.localizedMessage }
            finally { _loading.value = false }
        }
    }

    fun testRouter(routerId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _connectionMessage.value = null
            try {
                val response = repository.testRouter(routerId)
                _connectionMessage.value = response.message ?: "Tes koneksi selesai."
            } catch (e: Exception) {
                _connectionMessage.value = e.localizedMessage ?: "Gagal menguji koneksi."
            } finally { _loading.value = false }
        }
    }

    fun loadProfiles(routerId: Int) {
        viewModelScope.launch {
            try { _profiles.value = repository.getProfiles(routerId).data }
            catch (e: Exception) { _connectionMessage.value = e.localizedMessage }
        }
    }

    fun createSecret(routerId: Int, username: String, password: String, profile: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createSecret(routerId, username, password, profile)
                onResult(response.success, response.message ?: if (response.success) "Secret PPPoE berhasil dibuat." else "Gagal membuat secret.")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Gagal terhubung ke server.")
            }
        }
    }

    fun clearMessage() { _connectionMessage.value = null }
}
