package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.Paket
import com.gns.billing.repository.PaketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.gns.billing.model.PaketRequest

class PaketViewModel : ViewModel() {

    private val repository = PaketRepository()

    private val _paket = MutableStateFlow<List<Paket>>(emptyList())
    val paket: StateFlow<List<Paket>> = _paket

    private val _detailPaket = MutableStateFlow<Paket?>(null)
    val detailPaket: StateFlow<Paket?> = _detailPaket

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPaket() {

        viewModelScope.launch {

            _loading.value = true

            try {

                val response = repository.getPaket()

                if (response.success) {
                    _paket.value = response.data
                }

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }

        }
    }

    fun clearState() {

        _success.value = false
        _error.value = null
        _detailPaket.value = null

    }

    fun tambahPaket(
        request: PaketRequest
    ) {

        viewModelScope.launch {

            _loading.value = true
            _success.value = false
            _error.value = null

            try {

                repository.tambahPaket(request)

                _success.value = true
                _detailPaket.value = null

                loadPaket()

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }

        }

    }

    fun updatePaket(
        id: Int,
        request: PaketRequest
    ) {

        viewModelScope.launch {

            _loading.value = true
            _success.value = false
            _error.value = null

            try {

                repository.updatePaket(id, request)

                _success.value = true
                _detailPaket.value = null

                loadPaket()

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }

        }

    }

    fun hapusPaket(
        id: Int
    ) {

        viewModelScope.launch {

            _loading.value = true
            _success.value = false
            _error.value = null

            try {

                repository.hapusPaket(id)

                _success.value = true
                _detailPaket.value = null

                loadPaket()

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }

        }
    }

    fun getDetailPaket(id: Int) {

        viewModelScope.launch {

            _loading.value = true
            _success.value = false
            _error.value = null

            try {

                _detailPaket.value =
                    repository.getDetailPaket(id)

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }

        }

    }

    fun clearDetail() {

        _detailPaket.value = null

    }
}

