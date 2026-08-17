package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.MeResponse
import com.gns.billing.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _profile =
        MutableStateFlow<MeResponse?>(null)

    val profile: StateFlow<MeResponse?> =
        _profile

    fun getProfile() {

        viewModelScope.launch {

            try {

                val response =
                    repository.getProfile()

                _profile.value = response

            } catch (e: Exception) {

                e.printStackTrace()

            }

        }

    }

}