package com.gns.billing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gns.billing.model.Pelanggan
import com.gns.billing.repository.PelangganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.gns.billing.model.PelangganRequest
import retrofit2.HttpException
import org.json.JSONObject

class PelangganViewModel : ViewModel() {

    private val repository = PelangganRepository()

    private val _pelanggan =
        MutableStateFlow<List<Pelanggan>>(emptyList())
    val pelanggan: StateFlow<List<Pelanggan>> = _pelanggan

    private val _detailPelanggan =
        MutableStateFlow<Pelanggan?>(null)
    val detailPelanggan: StateFlow<Pelanggan?> =
        _detailPelanggan

    private val _loading =
        MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success =
        MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _error =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 1

    private var lastPage = 1

    private var currentSearch = ""

    private var currentStatus = ""

    val page:Int
        get() = currentPage

    val totalPage:Int
        get() = lastPage

    fun refresh() {

        loadPelanggan(
            reset = true,
            search = currentSearch,
            status = currentStatus
        )

    }

    fun search(
        keyword:String
    ){

        currentSearch = keyword

        loadPelanggan(
            reset = true,
            search = keyword,
            status = currentStatus
        )

    }

    fun filterStatus(
        status:String
    ){

        currentStatus = status

        loadPelanggan(
            reset = true,
            search = currentSearch,
            status = status
        )

    }

    fun nextPage(){

        if(currentPage > lastPage){
            return
        }

        loadPelanggan()

    }

    fun previousPage(){

        if(currentPage <= 2){
            return
        }

        currentPage -= 2

        loadPelanggan()

    }

    fun loadPelanggan(

        reset:Boolean = false,

        search:String = currentSearch,

        status:String = currentStatus

    ){

        if(reset){

            currentPage = 1

            lastPage = 1

            currentSearch = search

            currentStatus = status

        }

        viewModelScope.launch {

            _loading.value = true

            try{

                val response =
                    repository.getPelanggan(
                        currentPage,
                        currentSearch,
                        currentStatus
                    )

                if(response.success){

                    _pelanggan.value =
                        response.data

                    lastPage =
                        response.pagination.last_page

                    currentPage++

                }

            }catch (e:Exception){

                _error.value =
                    e.message

            }finally {

                _loading.value = false

            }

        }

    }

    fun getDetailPelanggan(
        id:Int
    ){

        viewModelScope.launch {

            _loading.value = true

            try{

                _detailPelanggan.value =
                    repository.getDetailPelanggan(id)

            }catch (e:Exception){

                _error.value =
                    e.message

            }finally {

                _loading.value = false

            }

        }

    }
    fun tambahPelanggan(

        request: PelangganRequest

    ) {

        viewModelScope.launch {

            _loading.value = true

            _success.value = false

            _error.value = null

            try {

                repository.tambahPelanggan(request)

                _success.value = true

                refresh()

            } catch (e: Exception) {

                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    try {
                        val json = JSONObject(errorBody)
                        _error.value = json.optString("message", e.message())
                    } catch (ex: Exception) {
                        _error.value = "Error ${e.code()}: ${e.message()}"
                    }
                } else {
                    _error.value = e.message
                }

            } finally {

                _loading.value = false

            }

        }

    }
    fun updatePelanggan(

        id: Int,

        request: PelangganRequest

    ) {

        viewModelScope.launch {

            _loading.value = true

            _success.value = false

            _error.value = null

            try {

                repository.updatePelanggan(
                    id,
                    request
                )

                _success.value = true

                refresh()

            } catch (e: Exception) {

                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    try {
                        val json = JSONObject(errorBody)
                        _error.value = json.optString("message", e.message())
                    } catch (ex: Exception) {
                        _error.value = "Error ${e.code()}: ${e.message()}"
                    }
                } else {
                    _error.value = e.message
                }

            } finally {

                _loading.value = false

            }

        }

    }
    fun hapusPelanggan(
        id: Int
    ) {

        viewModelScope.launch {

            _loading.value = true

            _success.value = false

            _error.value = null

            try {

                repository.hapusPelanggan(id)

                _success.value = true

                refresh()

            } catch (e: Exception) {

                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    try {
                        val json = JSONObject(errorBody)
                        _error.value = json.optString("message", e.message())
                    } catch (ex: Exception) {
                        _error.value = "Error ${e.code()}: ${e.message()}"
                    }
                } else {
                    _error.value = e.message
                }

            } finally {

                _loading.value = false

            }

        }

    }

    fun clearState() {

        _success.value = false

        _error.value = null

        _detailPelanggan.value = null

    }

}
