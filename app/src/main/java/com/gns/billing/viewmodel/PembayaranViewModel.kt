package com.gns.billing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranHistoryItem
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranResult
import com.gns.billing.model.PembayaranSummaryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI hanya menyimpan input operator dan hasil resmi dari server.
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

    private val _uiState = MutableStateFlow(PembayaranUiState())
    val uiState: StateFlow<PembayaranUiState> = _uiState

    fun onMetodeChange(newMetode: String) {
        _uiState.update { it.copy(metode = newMetode) }
    }

    fun onNominalChange(newNominal: String) {
        _uiState.update { it.copy(nominal = newNominal) }
    }

    fun onBiayaAdminChange(newBiayaAdmin: String) {
        _uiState.update { it.copy(biayaAdmin = newBiayaAdmin) }
    }

    fun onKeteranganChange(newKeterangan: String) {
        _uiState.update { it.copy(keterangan = newKeterangan) }
    }

    fun submitPembayaran(tagihanId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    message = null,
                    isSuccess = false,
                    serverResult = null
                )
            }

            try {
                // Tidak ada perhitungan status/kembalian di Android.
                // Laravel menjadi satu-satunya sumber perhitungan pembayaran.
                val request = PembayaranRequest(
                    tagihan_id = tagihanId,
                    dibayar = _uiState.value.nominal.toDoubleOrNull() ?: 0.0,
                    metode = _uiState.value.metode,
                    biaya_admin = _uiState.value.biayaAdmin.toDoubleOrNull() ?: 0.0,
                    keterangan = _uiState.value.keterangan.ifBlank { null }
                )

                val response = RetrofitClient.api.simpanPembayaran(request)
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
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        message = e.localizedMessage ?: "Gagal memproses pembayaran."
                    )
                }
            }
        }
    }

    fun resetMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun resetUiState() {
        _uiState.value = PembayaranUiState()
    }

    private val _historyList = MutableStateFlow<List<PembayaranHistoryItem>>(emptyList())
    val historyList: StateFlow<List<PembayaranHistoryItem>> = _historyList

    private val _summary = MutableStateFlow<PembayaranSummaryResponse?>(null)
    val summary: StateFlow<PembayaranSummaryResponse?> = _summary

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadHistory() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitClient.api.getPembayaranHistory()
                if (response.data != null) {
                    _historyList.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getPembayaranSummary()
                _summary.value = response
                Log.d("DEBUG_SUMMARY", "Response dari server: $response")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
