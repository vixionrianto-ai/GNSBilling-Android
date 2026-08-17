package com.gns.billing.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.api.RetrofitClient
import com.gns.billing.model.PembayaranHistoryItem
import com.gns.billing.model.PembayaranRequest
import com.gns.billing.model.PembayaranSummaryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. State lengkap sesuai kebutuhan PembayaranScreen.kt
data class PembayaranUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String? = null,
    val metode: String = "Cash",
    val nominal: String = "0"
)

class PembayaranViewModel : ViewModel() {

    // ==========================================
    // STATE & FUNGSI UNTUK PEMBAYARAN SCREEN
    // ==========================================
    private val _uiState = MutableStateFlow(PembayaranUiState())
    val uiState: StateFlow<PembayaranUiState> = _uiState

    // Dipanggil oleh Dropdown Metode Pembayaran di PembayaranScreen.kt
    fun onMetodeChange(newMetode: String) {
        _uiState.update { it.copy(metode = newMetode) }
    }

    // Dipanggil sebelum proses pembayaran di PembayaranScreen.kt
    fun onNominalChange(newNominal: String) {
        _uiState.update { it.copy(nominal = newNominal) }
    }

    // Dipanggil oleh tombol "Proses & Lunasi Pembayaran"
    fun submitPembayaran(tagihanId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            try {
                // Membaca metode & nominal dari state saat ini
                val currentMetode = _uiState.value.metode
                val currentNominal = _uiState.value.nominal

                // PERBAIKAN: Konversi String ke Double menggunakan .toDoubleOrNull() ?: 0.0
                val request = PembayaranRequest(
                    tagihan_id = tagihanId,
                    dibayar = currentNominal.toDoubleOrNull() ?: 0.0,
                    metode = currentMetode
                )

                val response = RetrofitClient.api.simpanPembayaran(request)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = response.success,
                        message = response.message
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

    // Dipanggil di LaunchedEffect PembayaranScreen.kt setelah Toast muncul
    fun resetMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun resetUiState() {
        _uiState.value = PembayaranUiState()
    }

    // ==========================================
    // STATE & FUNGSI UNTUK LAPORAN SCREEN
    // ==========================================
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