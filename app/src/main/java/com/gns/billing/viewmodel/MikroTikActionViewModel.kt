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

    fun testRouter(routerId: Int) {
        executeAction { RetrofitClient.api.testRouter(routerId) }
    }

    fun disconnectSecret(routerId: Int, secret: String) {
        executeAction { RetrofitClient.api.disconnectSecret(routerId, secret) }
    }

    fun enableSecret(routerId: Int, secret: String) {
        executeAction { RetrofitClient.api.enableSecret(routerId, secret) }
    }

    fun disableSecret(routerId: Int, secret: String) {
        executeAction { RetrofitClient.api.disableSecret(routerId, secret) }
    }

    private fun executeAction(action: suspend () -> com.gns.billing.model.MessageResponse) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, successMessage = null, errorMessage = null) }
            try {
                val response = action()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = if (response.success) response.message.ifEmpty { "Aksi berhasil." } else null,
                        errorMessage = if (!response.success) response.message.ifEmpty { "Aksi gagal." } else null
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
