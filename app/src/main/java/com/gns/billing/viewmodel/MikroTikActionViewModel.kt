package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MikroTikActionUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class MikroTikActionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MikroTikActionUiState())
    val uiState: StateFlow<MikroTikActionUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.update { MikroTikActionUiState() }
    }

    fun bukaIsolir(pelangganId: Int) {
        executeAction { RetrofitClient.api.bukaIsolir(pelangganId) }
    }

    fun isolir(pelangganId: Int) {
        executeAction { RetrofitClient.api.isolirPelanggan(pelangganId) }
    }

    fun disconnect(pelangganId: Int) {
        executeAction { RetrofitClient.api.disconnectSession(pelangganId) }
    }

    private fun executeAction(action: suspend () -> com.gns.billing.model.MessageResponse) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, successMessage = null, errorMessage = null) }
            try {
                val response = action()
                // Menyesuaikan dengan struktur MessageResponse (biasanya ada status/success atau message)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = response.message.ifEmpty { "Aksi MikroTik berhasil dieksekusi." }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Terjadi kesalahan koneksi ke server."
                    )
                }
            }
        }
    }
}