package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.MessageResponse
import com.gns.billing.model.PppSecret
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

    fun loadRouter() = viewModelScope.launch { _loading.value = true; try { _router.value = repository.getRouter().data } catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal memuat router." } finally { _loading.value = false } }
    fun testRouter(routerId: Int) = viewModelScope.launch { _loading.value = true; _connectionMessage.value = null; try { _connectionMessage.value = repository.testRouter(routerId).message ?: "Tes koneksi selesai." } catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal menguji koneksi." } finally { _loading.value = false } }
    fun loadProfiles(routerId: Int) = viewModelScope.launch { try { _profiles.value = repository.getProfiles(routerId).data } catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal memuat profile." } }
    fun loadSecrets(routerId: Int, onResult: (List<PppSecret>) -> Unit) = viewModelScope.launch { try { onResult(repository.getPppSecret(routerId).data) } catch (e: Exception) { _connectionMessage.value = e.localizedMessage ?: "Gagal memuat PPP Secret." } }

    fun createSecret(routerId: Int, username: String, password: String, service: String, profile: String, onResult: (Boolean, String) -> Unit) = viewModelScope.launch { try { val r = repository.createSecret(routerId, username, password, service, profile); onResult(r.success, r.message ?: "Operasi selesai.") } catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal terhubung ke server.") } }
    fun updateSecret(routerId: Int, secret: String, username: String, password: String, service: String, profile: String, disabled: Boolean, onResult: (Boolean, String) -> Unit) = viewModelScope.launch { try { val r = repository.updateSecret(routerId, secret, username, password, service, profile, if (disabled) "true" else "false"); onResult(r.success, r.message ?: "Operasi selesai.") } catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal memperbarui secret.") } }
    fun deleteSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) = operation(onResult) { repository.deleteSecret(routerId, secret) }
    fun enableSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) = operation(onResult) { repository.enableSecret(routerId, secret) }
    fun disableSecret(routerId: Int, secret: String, onResult: (Boolean, String) -> Unit) = operation(onResult) { repository.disableSecret(routerId, secret) }
    fun deleteProfile(routerId: Int, profile: String, onResult: (Boolean, String) -> Unit) = operation(onResult) { repository.deleteProfile(routerId, profile) }
    private fun operation(onResult: (Boolean, String) -> Unit, action: suspend () -> MessageResponse) = viewModelScope.launch { try { val r = action(); onResult(r.success, r.message ?: "Operasi selesai.") } catch (e: Exception) { onResult(false, e.localizedMessage ?: "Gagal terhubung ke server.") } }
    fun clearMessage() { _connectionMessage.value = null }
}
