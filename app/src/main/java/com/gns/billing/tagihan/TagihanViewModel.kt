package com.gns.billing.tagihan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

data class TagihanJatuhTempo(
    val pelangganId: Int,
    val namaPelanggan: String,
    val noHp: String,
    val listBulanTunggakan: List<String>,
    val totalTunggakan: Double
)

class TagihanViewModel : ViewModel() {

    private val repository = TagihanRepository()

    private val _tagihanList = MutableStateFlow<List<Tagihan>>(emptyList())
    val tagihanList: StateFlow<List<Tagihan>> = _tagihanList.asStateFlow()

    private val _listJatuhTempo = MutableStateFlow<List<TagihanJatuhTempo>>(emptyList())
    val listJatuhTempo: StateFlow<List<TagihanJatuhTempo>> = _listJatuhTempo.asStateFlow()

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
                val response = repository.getTagihanJatuhTempoList()
                val allTagihan = response.data ?: emptyList()

                val groupedByPelanggan = allTagihan.groupBy { it.pelanggan_id ?: 0 }
                val hasilJatuhTempo = mutableListOf<TagihanJatuhTempo>()

                for ((pelangganId, tagihansPelanggan) in groupedByPelanggan) {
                    if (pelangganId == 0) continue

                    val sample = tagihansPelanggan.first()
                    val nama = sample.pelanggan_nama ?: "Pelanggan #$pelangganId"
                    val noHp = sample.pelanggan_no_hp ?: ""

                    val listPeriode = tagihansPelanggan.map { it.periode.ifEmpty { "-" } }
                    val totalTunggakan = tagihansPelanggan.sumOf { if (it.sisa > 0.0) it.sisa else it.total }

                    hasilJatuhTempo.add(
                        TagihanJatuhTempo(
                            pelangganId = pelangganId,
                            namaPelanggan = nama,
                            noHp = noHp,
                            listBulanTunggakan = listPeriode,
                            totalTunggakan = totalTunggakan
                        )
                    )
                }

                _listJatuhTempo.value = hasilJatuhTempo
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
                val response = repository.getTagihanDetail(id)
                _detailTagihan.value = response
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