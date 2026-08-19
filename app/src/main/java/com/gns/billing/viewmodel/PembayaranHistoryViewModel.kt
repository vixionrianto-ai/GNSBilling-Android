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

class PembayaranHistoryViewModel : ViewModel() {
    private val repository = PembayaranRepository()

    private val _items = MutableStateFlow<List<PembayaranItem>>(emptyList())
    val items: StateFlow<List<PembayaranItem>> = _items
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _lastPage = MutableStateFlow(1)
    val lastPage: StateFlow<Int> = _lastPage
    private var page = 1
    private var search = ""

    fun load(reset: Boolean = false, search: String = this.search) {
        if (reset) page = 1
        this.search = search
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getHistory(page, this@PembayaranHistoryViewModel.search.ifBlank { null })
                val data = response.data
                if (response.success && data != null) {
                    _items.value = if (page == 1) data.data else _items.value + data.data
                    _lastPage.value = data.last_page
                } else {
                    _error.value = response.message ?: "Gagal memuat pembayaran"
                }
            } catch (e: Exception) {
                _error.value = readableError(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun nextPage() {
        if (!_loading.value && page < _lastPage.value) {
            page++
            load()
        }
    }

    fun refresh() = load(true, search)

    private fun readableError(e: Exception): String = if (e is HttpException) {
        try { JSONObject(e.response()?.errorBody()?.string() ?: "{}").optString("message", "Server error (${e.code()})") }
        catch (_: Exception) { "Server error (${e.code()})" }
    } else e.localizedMessage ?: "Terjadi kesalahan jaringan"
}
