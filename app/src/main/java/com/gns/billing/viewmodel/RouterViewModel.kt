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
    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage

    fun loadRouter() {
        viewModelScope.launch {
            _loading.value = true
            try { _router.value = repository.getRouter().data }
            catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal memuat router." }
            finally { _loading.value = false }
        }
    }

    fun testRouter(routerId: Int) {
        viewModelScope.launch {
            _loading.value = true; _connectionMessage.value = null
            try { _connectionMessage.value = repository.testRouter(routerId).message ?: "Tes koneksi selesai." }
            catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal menguji koneksi." }
            finally { _loading.value = false }
        }
    }

    fun loadProfiles(routerId: Int) {
        viewModelScope.launch {
            try { _profiles.value = repository.getProfiles(routerId).data }
            catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal memuat profile." }
        }
    }

    fun createSecret(routerId: Int, username: String, password: String, profile: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createSecret(routerId, username, password, profile)
                onResult(response.success, response.message ?: "Operasi selesai.")
                if (response.success) loadProfiles(routerId)
            } catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal terhubung ke server.") }
        }
    }

    fun deleteSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { val r = repository.deleteSecret(routerId, secret); onResult(r.success, r.message ?: "Operasi selesai."); if (r.success) loadProfiles(routerId) }
            catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal menghapus secret.") }
        }
    }

    fun enableSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { val r = repository.enableSecret(routerId, secret); onResult(r.success, r.message ?: "Operasi selesai.") }
            catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal mengaktifkan secret.") }
        }
    }

    fun disableSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { val r = repository.disableSecret(routerId, secret); onResult(r.success, r.message ?: "Operasi selesai.") }
            catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal menonaktifkan secret.") }
        }
    }

    fun deleteProfile(routerId: Int, profile: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try { val r = repository.deleteProfile(routerId, profile); onResult(r.success, r.message ?: "Operasi selesai."); if (r.success) loadProfiles(routerId) }
            catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal menghapus profile.") }
        }
    }

    fun clearMessage() { _connectionMessage.value = null; _operationMessage.value = null }
}
