package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailPembayaranUiState(
    val isLoading: Boolean = false,
    val detail: PembayaranItem? = null,
    val error: String? = null
)

class DetailPembayaranViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DetailPembayaranUiState())
    val uiState: StateFlow<DetailPembayaranUiState> = _uiState.asStateFlow()

    fun fetchDetailPembayaran(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.getDetailPembayaran(id)
                if (response.success && response.data != null) {
                    _uiState.update { it.copy(isLoading = false, detail = response.data) }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = response.message ?: "Gagal memuat detail")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage ?: "Terjadi kesalahan")
                }
            }
        }
    }
}
