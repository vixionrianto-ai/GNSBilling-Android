package com.gns.billing.tagihan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

class TagihanViewModel : ViewModel() {
    private val repository = TagihanRepository()
    private val _tagihanList = MutableStateFlow<List<Tagihan>>(emptyList())
    val tagihanList: StateFlow<List<Tagihan>> = _tagihanList.asStateFlow()
    private val _listJatuhTempo = MutableStateFlow<List<Tagihan>>(emptyList())
    val listJatuhTempo: StateFlow<List<Tagihan>> = _listJatuhTempo.asStateFlow()
    private val _detailTagihan = MutableStateFlow<DetailTagihanResponse?>(null)
    val detailTagihan: StateFlow<DetailTagihanResponse?> = _detailTagihan.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadTagihan(pelangganId: Int = 0, status: String? = null, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            try {
                _tagihanList.value = if (pelangganId > 0) {
                    repository.getTagihan(pelangganId).data ?: emptyList()
                } else {
                    repository.getSemuaTagihan(1, status, search).data ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = parseError(e); _tagihanList.value = emptyList()
            } finally { _isLoading.value = false }
        }
    }

    fun fetchTagihanJatuhTempo() {
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            try { _listJatuhTempo.value = repository.getTagihanJatuhTempoList().data ?: emptyList() }
            catch (e: Exception) { _error.value = parseError(e); _listJatuhTempo.value = emptyList() }
            finally { _isLoading.value = false }
        }
    }

    fun loadDetailTagihan(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true; _error.value = null
            try { _detailTagihan.value = repository.getTagihanDetail(id) }
            catch (e: Exception) { _error.value = parseError(e); _detailTagihan.value = null }
            finally { _isLoading.value = false }
        }
    }

    fun clearError() { _error.value = null }

    private fun parseError(e: Exception): String = if (e is HttpException) {
        try { JSONObject(e.response()?.errorBody()?.string() ?: "{}").optString("message", "Server Error (${e.code()})") }
        catch (_: Exception) { "Server Error (${e.code()})" }
    } else e.localizedMessage ?: "Terjadi kesalahan jaringan"
}
