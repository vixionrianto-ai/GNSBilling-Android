package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranHistoryResponse
import com.gns.billing.model.PembayaranSummaryResponse
import retrofit2.HttpException
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RiwayatPembayaranUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val summary: PembayaranSummaryResponse? = null,
    val history: PembayaranHistoryResponse? = null,
    val searchQuery: String = ""
)

class RiwayatPembayaranViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RiwayatPembayaranUiState())
    val uiState: StateFlow<RiwayatPembayaranUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                // Hanya panggil history, abaikan summary untuk sementara
                val historyRes = RetrofitClient.api.getPembayaranHistory(
                    page = 1,
                    search = search ?: _uiState.value.searchQuery
                )
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    history = historyRes
                )
            } catch (e: Exception) {
                val errorMsg = if (e is HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        JSONObject(errorBody ?: "{}").optString("message", "Error ${e.code()}")
                    } catch (_: Exception) {
                        "Error ${e.code()}"
                    }
                } else {
                    e.localizedMessage ?: "Gagal memuat riwayat pembayaran"
                }

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = errorMsg
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadData(search = query)
    }
}
