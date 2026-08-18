package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.WhatsAppLogItem
import com.gns.billing.model.WhatsAppStatistics
import com.gns.billing.repository.WhatsAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WhatsAppHistoryViewModel : ViewModel() {
    private val repository = WhatsAppRepository()

    private val _logs = MutableStateFlow<List<WhatsAppLogItem>>(emptyList())
    val logs: StateFlow<List<WhatsAppLogItem>> = _logs

    private val _statistics = MutableStateFlow(WhatsAppStatistics())
    val statistics: StateFlow<WhatsAppStatistics> = _statistics

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _jenis = MutableStateFlow<String?>(null)
    val jenis: StateFlow<String?> = _jenis

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getHistory(
                    search = _search.value.ifBlank { null },
                    status = _status.value,
                    jenis = _jenis.value
                )
                if (response.success) {
                    _logs.value = response.data
                    _statistics.value = response.statistics
                } else {
                    _error.value = response.message ?: "Gagal mengambil riwayat WhatsApp."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal mengambil riwayat WhatsApp."
            } finally {
                _loading.value = false
            }
        }
    }

    fun setSearch(value: String) {
        _search.value = value
    }

    fun setStatus(value: String?) {
        _status.value = value
        load()
    }

    fun setJenis(value: String?) {
        _jenis.value = value
        load()
    }

    fun clearFilters() {
        _search.value = ""
        _status.value = null
        _jenis.value = null
        load()
    }
}