package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.Pelanggan
import com.gns.billing.model.PelangganRequest
import com.gns.billing.repository.PelangganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class PelangganViewModel : ViewModel() {
    private val repository = PelangganRepository()
    private val _pelanggan = MutableStateFlow<List<Pelanggan>>(emptyList())
    val pelanggan: StateFlow<List<Pelanggan>> = _pelanggan
    private val _detailPelanggan = MutableStateFlow<Pelanggan?>(null)
    val detailPelanggan: StateFlow<Pelanggan?> = _detailPelanggan
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 1
    private var lastPage = 1
    private var currentSearch = ""
    private var currentStatus = ""

    fun refresh() = loadPelanggan(true)
    fun search(keyword: String) { currentSearch = keyword; loadPelanggan(true) }
    fun filterStatus(status: String) { currentStatus = status; loadPelanggan(true) }

    fun loadPelanggan(reset: Boolean = false) {
        if (reset) currentPage = 1
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getPelanggan(currentPage, currentSearch, currentStatus)
                if (response.success) {
                    _pelanggan.value = if (currentPage == 1) response.data else _pelanggan.value + response.data
                    lastPage = response.pagination.last_page
                    if (currentPage < lastPage) currentPage++
                }
            } catch (e: Exception) { _error.value = readableError(e) }
            finally { _loading.value = false }
        }
    }

    fun nextPage() { if (currentPage <= lastPage) loadPelanggan() }
    fun previousPage() { if (currentPage > 1) { currentPage = (currentPage - 2).coerceAtLeast(1); loadPelanggan(true) } }

    fun getDetailPelanggan(id: Int) {
        viewModelScope.launch {
            _loading.value = true; _error.value = null
            try { _detailPelanggan.value = repository.getDetailPelanggan(id) }
            catch (e: Exception) { _error.value = readableError(e) }
            finally { _loading.value = false }
        }
    }

    fun tambahPelanggan(request: PelangganRequest) = mutate { repository.tambahPelanggan(request) }
    fun updatePelanggan(id: Int, request: PelangganRequest) = mutate { repository.updatePelanggan(id, request) }
    fun hapusPelanggan(id: Int) = mutate { repository.hapusPelanggan(id) }

    private fun mutate(action: suspend () -> Any?) {
        viewModelScope.launch {
            _loading.value = true; _success.value = false; _error.value = null
            try { action(); _success.value = true; loadPelanggan(true) }
            catch (e: Exception) { _error.value = readableError(e) }
            finally { _loading.value = false }
        }
    }

    private fun readableError(e: Exception): String {
        if (e is HttpException) {
            return try { JSONObject(e.response()?.errorBody()?.string() ?: "{}").optString("message", "Server error (${e.code()})") }
            catch (_: Exception) { "Server error (${e.code()})" }
        }
        return e.localizedMessage ?: "Terjadi kesalahan"
    }

    fun clearState() { _success.value = false; _error.value = null; _detailPelanggan.value = null }
}
