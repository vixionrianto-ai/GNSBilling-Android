package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.PembayaranItem
import com.gns.billing.repository.PembayaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class PembayaranDetailViewModel : ViewModel() {
    private val repository = PembayaranRepository()
    private val _data = MutableStateFlow<PembayaranItem?>(null)
    val data: StateFlow<PembayaranItem?> = _data
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(id: Int) {
        if (id <= 0) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDetail(id)
                if (response.success && response.data != null) _data.value = response.data
                else _error.value = response.message ?: "Detail pembayaran tidak tersedia"
            } catch (e: Exception) {
                _error.value = if (e is HttpException) {
                    try { JSONObject(e.response()?.errorBody()?.string() ?: "{}").optString("message", "Server error (${e.code()})") }
                    catch (_: Exception) { "Server error (${e.code()})" }
                } else e.localizedMessage ?: "Terjadi kesalahan jaringan"
            } finally { _loading.value = false }
        }
    }
}
