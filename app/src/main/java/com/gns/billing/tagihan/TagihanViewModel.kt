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

    fun loadTagihan(pelangganId: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = if (pelangganId > 0) {
                    repository.getTagihan(pelangganId)
                } else {
                    repository.getSemuaTagihan()
                }
                _tagihanList.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = parseError(e)
                _tagihanList.value = emptyList()
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTagihanJatuhTempo() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Server sudah menentukan tagihan mana yang belum lunas.
                // Android hanya menampilkan data yang dikirim API.
                val response = repository.getTagihanJatuhTempoList()
                _listJatuhTempo.value = response.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = parseError(e)
                _listJatuhTempo.value = emptyList()
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDetailTagihan(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _detailTagihan.value = repository.getTagihanDetail(id)
            } catch (e: Exception) {
                _error.value = parseError(e)
                _detailTagihan.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseError(e: Exception): String {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                JSONObject(errorBody ?: "{}").optString("message", "Server Error (${e.code()})")
            } catch (_: Exception) {
                "Server Error (${e.code()})"
            }
        } else {
            e.localizedMessage ?: "Terjadi kesalahan jaringan"
        }
    }
}
