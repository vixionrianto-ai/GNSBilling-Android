package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.DashboardResponse
import com.gns.billing.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

class DashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    private val _dashboard =
        MutableStateFlow<DashboardResponse?>(null)
    val dashboard: StateFlow<DashboardResponse?> = _dashboard

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadDashboard() {

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {

                val response =
                    repository.getDashboard()

                _dashboard.value = response

            } catch (e: Exception) {
                val errorMsg = if (e is HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        JSONObject(errorBody ?: "{}").optString("message", "Server Error (${e.code()})")
                    } catch (_: Exception) {
                        "Server Error (${e.code()})"
                    }
                } else {
                    e.localizedMessage ?: "Gagal memuat data dashboard"
                }
                _error.value = errorMsg
                e.printStackTrace()

            } finally {
                _isLoading.value = false
            }

        }

    }

}