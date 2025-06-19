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

    // Estados para los campos del formulario
    private val _date = MutableLiveData("")
    val date: LiveData<String> = _date

    private val _location = MutableLiveData("")
    val location: LiveData<String> = _location

    private val _detail = MutableLiveData("")
    val detail: LiveData<String> = _detail

    // Estados para validación de errores
    private val _dateError = MutableLiveData<String?>(null)
    val dateError: LiveData<String?> = _dateError

    private val _locationError = MutableLiveData<String?>(null)
    val locationError: LiveData<String?> = _locationError

    private val _detailError = MutableLiveData<String?>(null)
    val detailError: LiveData<String?> = _detailError

    // Estados para la UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Estados para habilitar/deshabilitar el botón de envío
    private val _isFormValid = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid

    // Funciones para actualizar los campos
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

    // Validaciones
    private fun validateDate() {
        _dateError.value = if (_date.value.isNullOrBlank()) {
            "La fecha es requerida"
        } else null
    }

    private fun validateLocation() {
        _locationError.value = when {
            _location.value.isNullOrBlank() -> "La ubicación es requerida"
            _location.value!!.length < 10 -> "La ubicación debe tener al menos 10 caracteres"
            else -> null
        }
    }

    private fun validateDetail() {
        _detailError.value = when {
            _detail.value.isNullOrBlank() -> "El detalle es requerido"
            _detail.value!!.length < 10 -> "El detalle debe tener al menos 10 caracteres"
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

    // Función principal para enviar la solicitud
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

                    // Obtener el token JWT del usuario autenticado
                    val token = withContext(Dispatchers.IO) { sessionManager.getToken() }
                    if (token.isNullOrBlank()) {
                        _errorMessage.value = "No se encontró el token de autenticación. Por favor, inicia sesión nuevamente."
                        _isLoading.value = false
                        return@launch
                    }
                    val authHeader = "Bearer $token"

                    val response = api.createSupportRequest(authHeader, request)

                    // Si llegamos aquí, la solicitud fue exitosa
                    _isSuccess.value = true

                } catch (e: Exception) {
                    _errorMessage.value = e.message ?: "Error desconocido al enviar la solicitud"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    // Función para limpiar el estado de éxito (útil para resetear después de mostrar mensaje)
    fun clearSuccess() {
        _isSuccess.value = false
    }

    // Función para limpiar errores
    fun clearErrors() {
        _dateError.value = null
        _locationError.value = null
        _detailError.value = null
        _errorMessage.value = null
    }

    // Función para resetear el formulario
    fun resetForm() {
        _date.value = ""
        _location.value = ""
        _detail.value = ""
        clearErrors()
        _isSuccess.value = false
        _isFormValid.value = false
    }
}