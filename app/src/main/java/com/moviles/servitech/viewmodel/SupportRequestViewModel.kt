package com.moviles.servitech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.network.requests.supportRequest.CreateSupportRequest
import com.moviles.servitech.network.services.SupportRequestApiService
import com.moviles.servitech.model.SupportRequest
import com.moviles.servitech.core.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupportRequestViewModel @Inject constructor(
    private val api: SupportRequestApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    // States for form fields
    private val _date = MutableLiveData("")
    val date: LiveData<String> = _date

    private val _location = MutableLiveData("")
    val location: LiveData<String> = _location

    private val _detail = MutableLiveData("")
    val detail: LiveData<String> = _detail

    // States for error validation
    private val _dateError = MutableLiveData<String?>(null)
    val dateError: LiveData<String?> = _dateError

    private val _locationError = MutableLiveData<String?>(null)
    val locationError: LiveData<String?> = _locationError

    private val _detailError = MutableLiveData<String?>(null)
    val detailError: LiveData<String?> = _detailError

    // States for UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // States to enable/disable submit button
    private val _isFormValid = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    // Functions to update fields
    fun updateDate(newDate: String) {
        _date.value = newDate
        validateDate()
        checkFormValidity()
    }

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
        validateLocation()
        checkFormValidity()
    }

    fun updateDetail(newDetail: String) {
        _detail.value = newDetail
        validateDetail()
        checkFormValidity()
    }

    // Validations
    private fun validateDate() {
        _dateError.value = if (_date.value.isNullOrBlank()) {
            "Date is required"
        } else null
    }

    private fun validateLocation() {
        _locationError.value = when {
            _location.value.isNullOrBlank() -> "Location is required"
            _location.value!!.length < 10 -> "Location must have at least 10 characters"
            else -> null
        }
    }

    private fun validateDetail() {
        _detailError.value = when {
            _detail.value.isNullOrBlank() -> "Detail is required"
            _detail.value!!.length < 10 -> "Detail must have at least 10 characters"
            else -> null
        }
    }

    private fun checkFormValidity() {
        _isFormValid.value = _dateError.value == null &&
                _locationError.value == null &&
                _detailError.value == null &&
                !_date.value.isNullOrBlank() &&
                !_location.value.isNullOrBlank() &&
                !_detail.value.isNullOrBlank()
    }

    // Main function to submit the request
    fun submitSupportRequest() {
        if (_isFormValid.value == true) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                try {
                    val request = CreateSupportRequest(
                        date = _date.value!!,
                        location = _location.value!!,
                        detail = _detail.value!!
                    )

                    // Get JWT token from authenticated user
                    val token = withContext(Dispatchers.IO) { sessionManager.getToken() }
                    if (token.isNullOrBlank()) {
                        _errorMessage.value = "Authentication token not found. Please log in again."
                        _isLoading.value = false
                        return@launch
                    }
                    val authHeader = "Bearer $token"

                    val response = api.createSupportRequest(authHeader, request)

                    // If we reach here, the request was successful
                    _isSuccess.value = true

                } catch (e: Exception) {
                    _errorMessage.value = e.message ?: "Unknown error sending request"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    // Function to clear success state (useful to reset after showing message)
    fun clearSuccess() {
        _isSuccess.value = false
    }

    // Function to clear errors
    fun clearErrors() {
        _dateError.value = null
        _locationError.value = null
        _detailError.value = null
        _errorMessage.value = null
    }

    // Function to reset form
    fun resetForm() {
        _date.value = ""
        _location.value = ""
        _detail.value = ""
        clearErrors()
        _isSuccess.value = false
        _isFormValid.value = false
    }
}