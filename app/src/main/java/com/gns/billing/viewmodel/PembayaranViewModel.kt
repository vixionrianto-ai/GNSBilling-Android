package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.PembayaranHistoryItem
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranResult
import com.gns.billing.repository.PembayaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

// Android hanya menyimpan input operator dan hasil resmi dari Laravel.
// Tidak ada perhitungan billing, status, alokasi, saldo, atau kembalian di sini.
data class PembayaranUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String? = null,
    val metode: String = "Cash",
    val nominal: String = "0",
    val biayaAdmin: String = "0",
    val keterangan: String = "",
    val serverResult: PembayaranResult? = null
)

class PembayaranViewModel : ViewModel() {
    private val repository = PembayaranRepository()

    private val _uiState = MutableStateFlow(PembayaranUiState())
    val uiState: StateFlow<PembayaranUiState> = _uiState

    private val _historyList = MutableStateFlow<List<PembayaranHistoryItem>>(emptyList())
    val historyList: StateFlow<List<PembayaranHistoryItem>> = _historyList

    private val _loadingHistory = MutableStateFlow(false)
    val loadingHistory: StateFlow<Boolean> = _loadingHistory

    fun onMetodeChange(value: String) = _uiState.update { it.copy(metode = value) }
    fun onNominalChange(value: String) = _uiState.update { it.copy(nominal = value) }
    fun onBiayaAdminChange(value: String) = _uiState.update { it.copy(biayaAdmin = value) }
    fun onKeteranganChange(value: String) = _uiState.update { it.copy(keterangan = value) }

    fun submitPembayaran(tagihanId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSuccess = false, message = null, serverResult = null) }
            try {
                val request = PembayaranRequest(
                    tagihan_id = tagihanId,
                    dibayar = _uiState.value.nominal.toDoubleOrNull() ?: 0.0,
                    metode = _uiState.value.metode,
                    biaya_admin = _uiState.value.biayaAdmin.toDoubleOrNull() ?: 0.0,
                    keterangan = _uiState.value.keterangan.ifBlank { null }
                )
                val response = repository.simpanPembayaran(request)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = response.success,
                        message = response.message,
                        serverResult = response.data
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isSuccess = false, message = readableError(e))
                }
            }
        }
    }

    fun loadHistory(page: Int = 1, search: String? = null) {
        viewModelScope.launch {
            _loadingHistory.value = true
            try {
                val response = repository.getHistory(page, search)
                if (response.data != null) _historyList.value = response.data
            } catch (_: Exception) {
                // Screen can display the existing state; transaction rules remain server-side.
            } finally {
                _loadingHistory.value = false
            }
        }
    }

    fun resetMessage() = _uiState.update { it.copy(message = null) }
    fun resetUiState() { _uiState.value = PembayaranUiState() }

    private fun readableError(e: Exception): String = if (e is HttpException) {
        try { JSONObject(e.response()?.errorBody()?.string() ?: "{}").optString("message", "Server error (${e.code()})") }
        catch (_: Exception) { "Server error (${e.code()})" }
    } else e.localizedMessage ?: "Gagal memproses pembayaran."
}
