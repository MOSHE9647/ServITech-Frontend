package com.moviles.servitech.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.common.PhoneUtils.formatPhoneForDisplay
import com.moviles.servitech.common.PhoneUtils.normalizePhoneInput
import com.moviles.servitech.model.Image
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.repositories.RepairRequestResult
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.services.RepairRequestService
import com.moviles.servitech.services.validation.RepairRequestValidation
import com.moviles.servitech.viewmodel.utils.FieldState
import com.moviles.servitech.viewmodel.utils.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepairRequestViewModel @Inject constructor(
    private val repairRequestValidation: RepairRequestValidation,
    private val repairRequestService: RepairRequestService,
) : ViewModel() {

    // StateFlow for the list of Repair Requests.
    private val _repairRequests = MutableStateFlow<List<RepairRequest>>(emptyList())
    val repairRequests: StateFlow<List<RepairRequest>> = _repairRequests

    private val _repairRequest = MutableStateFlow<RepairRequest?>(null)
    val repairRequest: StateFlow<RepairRequest?> = _repairRequest

    // LiveData for the Repair Request input fields and their error states.
    private val customerNameState = FieldState<String>()
    val customerName: LiveData<String> = customerNameState.data
    val customerNameError: LiveData<Boolean> = customerNameState.error
    val customerNameErrorMsg: LiveData<String?> = customerNameState.errorMessage

    private val customerPhoneState = FieldState<String>()
    val customerPhone: LiveData<String> = customerPhoneState.data
    val customerPhoneError: LiveData<Boolean> = customerPhoneState.error
    val customerPhoneErrorMsg: LiveData<String?> = customerPhoneState.errorMessage

    private val customerEmailState = FieldState<String>()
    val customerEmail: LiveData<String> = customerEmailState.data
    val customerEmailError: LiveData<Boolean> = customerEmailState.error
    val customerEmailErrorMsg: LiveData<String?> = customerEmailState.errorMessage

    private val articleNameState = FieldState<String>()
    val articleName: LiveData<String> = articleNameState.data
    val articleNameError: LiveData<Boolean> = articleNameState.error
    val articleNameErrorMsg: LiveData<String?> = articleNameState.errorMessage

    private val articleTypeState = FieldState<String>()
    val articleType: LiveData<String> = articleTypeState.data
    val articleTypeError: LiveData<Boolean> = articleTypeState.error
    val articleTypeErrorMsg: LiveData<String?> = articleTypeState.errorMessage

    private val articleBrandState = FieldState<String>()
    val articleBrand: LiveData<String> = articleBrandState.data
    val articleBrandError: LiveData<Boolean> = articleBrandState.error
    val articleBrandErrorMsg: LiveData<String?> = articleBrandState.errorMessage

    private val articleModelState = FieldState<String>()
    val articleModel: LiveData<String> = articleModelState.data
    val articleModelError: LiveData<Boolean> = articleModelState.error
    val articleModelErrorMsg: LiveData<String?> = articleModelState.errorMessage

    private val articleSerialState = FieldState<String>()
    val articleSerial: LiveData<String> = articleSerialState.data
    val articleSerialError: LiveData<Boolean> = articleSerialState.error
    val articleSerialErrorMsg: LiveData<String?> = articleSerialState.errorMessage

    private val articleAccesoriesState = FieldState<String>()
    val articleAccesories: LiveData<String> = articleAccesoriesState.data
    val articleAccesoriesError: LiveData<Boolean> = articleAccesoriesState.error
    val articleAccesoriesErrorMsg: LiveData<String?> = articleAccesoriesState.errorMessage

    private val articleProblemState = FieldState<String>()
    val articleProblem: LiveData<String> = articleProblemState.data
    val articleProblemError: LiveData<Boolean> = articleProblemState.error
    val articleProblemErrorMsg: LiveData<String?> = articleProblemState.errorMessage

    private val repairStatusState = FieldState<String>()
    val repairStatus: LiveData<String> = repairStatusState.data
    val repairStatusError: LiveData<Boolean> = repairStatusState.error
    val repairStatusErrorMsg: LiveData<String?> = repairStatusState.errorMessage

    private val repairDetailsState = FieldState<String>()
    val repairDetails: LiveData<String> = repairDetailsState.data
    val repairDetailsError: LiveData<Boolean> = repairDetailsState.error
    val repairDetailsErrorMsg: LiveData<String?> = repairDetailsState.errorMessage

    private val repairPriceState = FieldState<Double>()
    val repairPrice: LiveData<Double> = repairPriceState.data
    val repairPriceError: LiveData<Boolean> = repairPriceState.error
    val repairPriceErrorMsg: LiveData<String?> = repairPriceState.errorMessage

    private val receivedAtState = FieldState<String>()
    val receivedAt: LiveData<String> = receivedAtState.data
    val receivedAtError: LiveData<Boolean> = receivedAtState.error
    val receivedAtErrorMsg: LiveData<String?> = receivedAtState.errorMessage

    private val repairedAtState = FieldState<String>()
    val repairedAt: LiveData<String> = repairedAtState.data
    val repairedAtError: LiveData<Boolean> = repairedAtState.error
    val repairedAtErrorMsg: LiveData<String?> = repairedAtState.errorMessage

    private val imagesListState = FieldState<List<Image>>()
    val imagesList: LiveData<List<Image>> = imagesListState.data
    val imagesListError: LiveData<Boolean> = imagesListState.error
    val imagesListErrorMsg: LiveData<String?> = imagesListState.errorMessage

    private val imagesNamesState = FieldState<String>()
    val imagesNames: LiveData<String> = imagesNamesState.data
    val imagesNamesError: LiveData<Boolean> = imagesNamesState.error
    val imagesNamesErrorMsg: LiveData<String?> = imagesNamesState.errorMessage

    // LiveData for the Repair Request send enable state, which is true if all fields are valid.
    @RequiresApi(Build.VERSION_CODES.O)
    private val _repairRequestSendEnable = MediatorLiveData<Boolean>().apply {
        addSource(customerNameState.data) { value = isFormValid() }
        addSource(customerPhoneState.data) { value = isFormValid() }
        addSource(customerEmailState.data) { value = isFormValid() }
        addSource(articleNameState.data) { value = isFormValid() }
        addSource(articleTypeState.data) { value = isFormValid() }
        addSource(articleBrandState.data) { value = isFormValid() }
        addSource(articleModelState.data) { value = isFormValid() }
        addSource(articleSerialState.data) { value = isFormValid() }
        addSource(articleAccesoriesState.data) { value = isFormValid() }
        addSource(articleProblemState.data) { value = isFormValid() }
        addSource(repairStatusState.data) { value = isFormValid() }
        addSource(repairDetailsState.data) { value = isFormValid() }
        addSource(repairPriceState.data) { value = isFormValid() }
        addSource(receivedAtState.data) { value = isFormValid() }
        addSource(repairedAtState.data) { value = isFormValid() }
        addSource(imagesNamesState.data) { value = isFormValid() }
        addSource(imagesListState.data) { value = isFormValid() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val repairRequestSendEnable: LiveData<Boolean> = _repairRequestSendEnable

    // LiveData for the View Model state, which can be Loading, Success, or Error.
    private val _viewModelState = MutableLiveData<ViewModelState>()
    val viewModelState: LiveData<ViewModelState> = _viewModelState

    /**
     * Event handler for when the customer name field changes.
     * It updates the customer name state, validates the name,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new customer name value entered by the user.
     */
    fun onCustomerNameChanged(value: String) {
        customerNameState.data.value = value
        val validation = repairRequestValidation.validateCustomerName(value)
        customerNameState.error.value = !validation.isValid
        customerNameState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the customer phone field changes.
     * It updates the customer phone state, validates the phone number,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new customer phone value entered by the user.
     */
    fun onCustomerPhoneChanged(value: String) {
        customerPhoneState.data.value = value
        val validation = repairRequestValidation.validateCustomerPhone(value)
        customerPhoneState.error.value = !validation.isValid
        customerPhoneState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the customer email field changes.
     * It updates the customer email state, validates the email,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new customer email value entered by the user.
     */
    fun onCustomerEmailChanged(value: String) {
        customerEmailState.data.value = value
        val validation = repairRequestValidation.validateCustomerEmail(value)
        customerEmailState.error.value = !validation.isValid
        customerEmailState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article name field changes.
     * It updates the article name state, validates the name,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article name value entered by the user.
     */
    fun onArticleNameChanged(value: String) {
        articleNameState.data.value = value
        val validation = repairRequestValidation.validateArticleName(value)
        articleNameState.error.value = !validation.isValid
        articleNameState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article type field changes.
     * It updates the article type state, validates the type,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article type value entered by the user.
     */
    fun onArticleTypeChanged(value: String) {
        articleTypeState.data.value = value
        val validation = repairRequestValidation.validateArticleType(value)
        articleTypeState.error.value = !validation.isValid
        articleTypeState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article brand field changes.
     * It updates the article brand state, validates the brand,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article brand value entered by the user.
     */
    fun onArticleBrandChanged(value: String) {
        articleBrandState.data.value = value
        val validation = repairRequestValidation.validateArticleBrand(value)
        articleBrandState.error.value = !validation.isValid
        articleBrandState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article model field changes.
     * It updates the article model state, validates the model,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article model value entered by the user.
     */
    fun onArticleModelChanged(value: String) {
        articleModelState.data.value = value
        val validation = repairRequestValidation.validateArticleModel(value)
        articleModelState.error.value = !validation.isValid
        articleModelState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article serial field changes.
     * It updates the article serial state, validates the serial number,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article serial value entered by the user.
     */
    fun onArticleSerialChanged(value: String) {
        articleSerialState.data.value = value
        val validation = repairRequestValidation.validateArticleSerial(value)
        articleSerialState.error.value = !validation.isValid
        articleSerialState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article accessories field changes.
     * It updates the article accessories state, validates the accessories,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article accessories value entered by the user.
     */
    fun onArticleAccesoriesChanged(value: String) {
        articleAccesoriesState.data.value = value
        val validation = repairRequestValidation.validateAccessories(value)
        articleAccesoriesState.error.value = !validation.isValid
        articleAccesoriesState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the article problem field changes.
     * It updates the article problem state, validates the problem description,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new article problem value entered by the user.
     */
    fun onArticleProblemChanged(value: String) {
        articleProblemState.data.value = value
        val validation = repairRequestValidation.validateProblem(value)
        articleProblemState.error.value = !validation.isValid
        articleProblemState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the repair status field changes.
     * It updates the repair status state, validates the status,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new repair status value entered by the user.
     */
    fun onRepairStatusChanged(value: String) {
        repairStatusState.data.value = value
        val validation = repairRequestValidation.validateRepairStatus(value)
        repairStatusState.error.value = !validation.isValid
        repairStatusState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the repair details field changes.
     * It updates the repair details state, validates the details,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new repair details value entered by the user.
     */
    fun onRepairDetailsChanged(value: String) {
        repairDetailsState.data.value = value
        val validation = repairRequestValidation.validateRepairDetails(value)
        repairDetailsState.error.value = !validation.isValid
        repairDetailsState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the repair price field changes.
     * It updates the repair price state, validates the price,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new repair price value entered by the user.
     */
    fun onRepairPriceChanged(value: Double) {
        repairPriceState.data.value = value
        val validation = repairRequestValidation.validateRepairPrice(value)
        repairPriceState.error.value = !validation.isValid
        repairPriceState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the received at field changes.
     * It updates the received at state, validates the timestamp,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new received at timestamp value entered by the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onReceivedAtChanged(value: String) {
        receivedAtState.data.value = value
        val validation = repairRequestValidation.validateReceivedAt(value)
        receivedAtState.error.value = !validation.isValid
        receivedAtState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the repaired at field changes.
     * It updates the repaired at state, validates the timestamp,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new repaired at timestamp value entered by the user.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onRepairedAtChanged(value: String) {
        repairedAtState.data.value = value
        val validation =
            repairRequestValidation.validateRepairedAt(value, receivedAtState.data.value)
        repairedAtState.error.value = !validation.isValid
        repairedAtState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the images list changes.
     * It updates the images list state, validates the list,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new list of images selected by the user.
     */
    fun onImagesListChanged(value: List<Image>) {
        imagesListState.data.value = value
        val validation = repairRequestValidation.validateImagesList(value)
        imagesListState.error.value = !validation.isValid
        imagesListState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the images names field changes.
     * It updates the images names state, validates the names,
     * and updates the error state accordingly using
     * the [RepairRequestValidation] service.
     *
     * @param value The new images names value entered by the user.
     */
    fun onImagesNamesChanged(value: String) {
        imagesNamesState.data.value = value
        val validation = repairRequestValidation.validateImagesNames(value)
        imagesNamesState.error.value = !validation.isValid
        imagesNamesState.errorMessage.value = validation.errorMessage
    }

    /**
     * Fetches all repair requests from the service.
     * It updates the ViewModel state to Loading, then attempts to retrieve the data.
     *
     * If the request is successful, it updates the list of repair requests
     * and sets the ViewModel state to Success.
     *
     * If there is an error, it sets the ViewModel state to Error
     * and attempts to retrieve the data from the local data source
     */
    fun getAllRepairRequests() {
        viewModelScope.launch {
            // Resetting the viewModelState to Loading
            _viewModelState.value = ViewModelState.Loading

            // Attempt to fetch all repair requests using the service
            when (val result = repairRequestService.getAllRepairRequests()) {
                is RepairRequestResult.Success -> {
                    // If the request is successful, update the list of repair requests
                    _repairRequests.value = result.data
                    _viewModelState.value = ViewModelState.Success(result.data)
                }

                is RepairRequestResult.Error -> {
                    // If there is an error, set the ViewModel state to Error
                    // and update the repair requests list using the local data source
                    // if available.
                    val localResult = repairRequestService.getAllRepairRequests(DataSource.Local)
                    _repairRequests.value = when {
                        localResult is RepairRequestResult.Success -> localResult.data
                        else -> emptyList()
                    }
                    _viewModelState.value = ViewModelState.Error(result.message)
                }
            }
        }
    }

    /**
     * Fetches a specific repair request by its receipt number or ID.
     * It updates the ViewModel state to Loading, then attempts to retrieve the data.
     *
     * If the request is successful, it updates the ViewModel state to Success
     * with the retrieved repair request data.
     *
     * If there is an error, it sets the ViewModel state to Error
     * and updates the error states and messages for each field based on the error list.
     *
     * @param receiptNumber The receipt number of the repair request to fetch.
     * @param id Optional ID of the repair request to fetch.
     */
    fun getRepairRequestByReceiptNumberOrID(
        receiptNumber: String?,
        id: Long? = null
    ) {
        viewModelScope.launch {
            // Resetting the viewModelState to Loading
            _viewModelState.value = ViewModelState.Loading

            // Attempt to fetch a specific repair request by receipt number or ID using the service
            when (val result =
                repairRequestService.getRepairRequestByReceiptNumberOrID(receiptNumber, id)) {
                is RepairRequestResult.Success -> {
                    // If the request is successful, update the ViewModel state to Success
                    _repairRequest.value = result.data
                    _viewModelState.value = ViewModelState.Success(result.data)
                }

                is RepairRequestResult.Error -> {
                    // If there is an error, set the ViewModel state to Error
                    updateErrorStatesAndMessages(result.fieldErrors)
                    _viewModelState.value = ViewModelState.Error(result.message)
                }
            }
        }
    }

    /**
     * Creates a new repair request using the provided [RepairRequest] object.
     * It formats the phone number, and sends the request to the service.
     *
     * If the request is successful, it updates the ViewModel state to Success
     * and refreshes the list of repair requests.
     *
     * If there are validation errors, it updates the error states and messages
     * for each field based on the error list returned by the service.
     *
     * @param rawRepairRequest The [RepairRequest] object containing the data to be sent.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRepairRequest(rawRepairRequest: RepairRequest) {
        viewModelScope.launch {
            // Resetting the viewModelState to Loading
            _viewModelState.value = ViewModelState.Loading

            // Resetting error states and messages
            resetErrors()

            // Normalize and format the phone number for repair request creation
            val normalized = normalizePhoneInput(rawRepairRequest.customerPhone)
            val formatted = formatPhoneForDisplay(normalized)
            val repairRequest = rawRepairRequest.copy(customerPhone = formatted)
            Log.d("RepairRequestViewModel", "Repair Request: $repairRequest")

            // Attempt to create the repair request using the service
            when (val result = repairRequestService.createRepairRequest(repairRequest)) {
                is RepairRequestResult.Success -> {
                    // If the request is successful, update the ViewModel state to Success
                    getAllRepairRequests() // Refresh the list of repair requests
                    _viewModelState.value = ViewModelState.Success<Any>(result.data)
                }

                is RepairRequestResult.Error -> {
                    // If there is an error, update the error state and message for each field
                    // using the first error message from the fieldErrors list of the result.
                    updateErrorStatesAndMessages(result.fieldErrors)

                    // Sets the viewModelState to Error
                    _viewModelState.value = ViewModelState.Error(result.message)
                }
            }
        }
    }

    /**
     * Updates an existing repair request using the provided [RepairRequest] object.
     * It formats the phone number, and sends the request to the service.
     *
     * If the request is successful, it updates the ViewModel state to Success
     * and refreshes the list of repair requests.
     *
     * If there are validation errors, it updates the error states and messages
     * for each field based on the error list returned by the service.
     *
     * @param repairRequest The [RepairRequest] object containing the data to be updated.
     */
    fun updateRepairRequest(repairRequest: RepairRequest) {
        viewModelScope.launch {
            // Resetting the viewModelState to Loading
            _viewModelState.value = ViewModelState.Loading

            // Resetting error states and messages
            resetErrors()

            // Attempt to update the repair request using the service
            when (val result = repairRequestService.updateRepairRequest(repairRequest)) {
                is RepairRequestResult.Success -> {
                    // If the request is successful, update the ViewModel state to Success
                    getAllRepairRequests() // Refresh the list of repair requests
                    _viewModelState.value = ViewModelState.Success<Any>(result.data)
                }

                is RepairRequestResult.Error -> {
                    // If there is an error, update the error state and message for each field
                    // using the first error message from the fieldErrors list of the result.
                    updateErrorStatesAndMessages(result.fieldErrors)

                    // Sets the viewModelState to Error
                    _viewModelState.value = ViewModelState.Error(result.message)
                }
            }
        }
    }

    fun deleteUpdateRequest(repairRequest: RepairRequest) {
        viewModelScope.launch {
            // Resetting the viewModelState to Loading
            _viewModelState.value = ViewModelState.Loading

            // Attempt to delete the repair request using the service
            when (val result = repairRequestService.deleteRepairRequest(repairRequest)) {
                is RepairRequestResult.Success -> {
                    // If the request is successful, update the ViewModel state to Success
                    getAllRepairRequests() // Refresh the list of repair requests
                    _viewModelState.value = ViewModelState.Success<Any>(result.data)
                }

                is RepairRequestResult.Error -> {
                    // If there is an error, set the ViewModel state to Error
                    _viewModelState.value = ViewModelState.Error(result.message)
                }
            }
        }
    }

    /**
     * Updates the error states and messages for each field based on the provided error list.
     * This function iterates through the error list and sets the corresponding error state
     * and error message for each field.
     *
     * @param errorList A map containing field names as keys and error messages as values.
     */
    private fun updateErrorStatesAndMessages(errorList: Map<String, String>) {
        for (error in errorList) {
            when (error.key) {
                "customer_email" -> {
                    customerEmailState.error.value = true
                    customerEmailState.errorMessage.value = error.value
                }

                "article_name" -> {
                    articleNameState.error.value = true
                    articleNameState.errorMessage.value = error.value
                }

                "article_type" -> {
                    articleTypeState.error.value = true
                    articleTypeState.errorMessage.value = error.value
                }

                "article_brand" -> {
                    articleBrandState.error.value = true
                    articleBrandState.errorMessage.value = error.value
                }

                "article_model" -> {
                    articleModelState.error.value = true
                    articleModelState.errorMessage.value = error.value
                }

                "article_serial" -> {
                    articleSerialState.error.value = true
                    articleSerialState.errorMessage.value = error.value
                }

                "accessories" -> {
                    articleAccesoriesState.error.value = true
                    articleAccesoriesState.errorMessage.value = error.value
                }

                "problem" -> {
                    articleProblemState.error.value = true
                    articleProblemState.errorMessage.value = error.value
                }

                "repair_status" -> {
                    repairStatusState.error.value = true
                    repairStatusState.errorMessage.value = error.value
                }

                "repair_details" -> {
                    repairDetailsState.error.value = true
                    repairDetailsState.errorMessage.value = error.value
                }

                "repair_price" -> {
                    repairPriceState.error.value = true
                    repairPriceState.errorMessage.value = error.value
                }

                "received_at" -> {
                    receivedAtState.error.value = true
                    receivedAtState.errorMessage.value = error.value
                }

                "repaired_at" -> {
                    repairedAtState.error.value = true
                    repairedAtState.errorMessage.value = error.value
                }

                "images" -> {
                    imagesNamesState.error.value = true
                    imagesNamesState.errorMessage.value = error.value
                }
            }
        }
    }

    /**
     * Validates the Repair Request form fields by checking each field's value
     * against the validation rules defined in the [RepairRequestValidation] service.
     *
     * If any field is invalid, it updates the corresponding error state
     * and error message.
     *
     * @return Boolean indicating whether the form is valid or not.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isFormValid(): Boolean {
        val customerName = customerNameState.data.value.orEmpty()
        val customerPhone = customerPhoneState.data.value.orEmpty()
        val customerEmail = customerEmailState.data.value.orEmpty()
        val articleName = articleNameState.data.value.orEmpty()
        val articleType = articleTypeState.data.value.orEmpty()
        val articleBrand = articleBrandState.data.value.orEmpty()
        val articleModel = articleModelState.data.value.orEmpty()
        val articleSerial = articleSerialState.data.value.orEmpty()
        val articleAccesories = articleAccesoriesState.data.value.orEmpty()
        val articleProblem = articleProblemState.data.value.orEmpty()
        val repairStatus = repairStatusState.data.value.orEmpty()
        val repairDetails = repairDetailsState.data.value.orEmpty()
        val repairPrice = repairPriceState.data.value ?: 0.0
        val receivedAt = receivedAtState.data.value.orEmpty()
        val repairedAt = repairedAtState.data.value.orEmpty()
        val imagesNames = imagesNamesState.data.value.orEmpty()

        return repairRequestValidation.validateCustomerName(customerName).isValid &&
                repairRequestValidation.validateCustomerPhone(customerPhone).isValid &&
                repairRequestValidation.validateCustomerEmail(customerEmail).isValid &&
                repairRequestValidation.validateArticleName(articleName).isValid &&
                repairRequestValidation.validateArticleType(articleType).isValid &&
                repairRequestValidation.validateArticleBrand(articleBrand).isValid &&
                repairRequestValidation.validateArticleModel(articleModel).isValid &&
                repairRequestValidation.validateArticleSerial(articleSerial).isValid &&
                repairRequestValidation.validateAccessories(articleAccesories).isValid &&
                repairRequestValidation.validateProblem(articleProblem).isValid &&
                repairRequestValidation.validateRepairStatus(repairStatus).isValid &&
                repairRequestValidation.validateRepairDetails(repairDetails).isValid &&
                repairRequestValidation.validateRepairPrice(repairPrice).isValid &&
                repairRequestValidation.validateReceivedAt(receivedAt).isValid &&
                repairRequestValidation.validateRepairedAt(repairedAt, receivedAt).isValid &&
                repairRequestValidation.validateImagesNames(imagesNames).isValid
    }

    fun resetErrors() {
        customerNameState.error.value = false
        customerNameState.errorMessage.value = null
        customerPhoneState.error.value = false
        customerPhoneState.errorMessage.value = null
        customerEmailState.error.value = false
        customerEmailState.errorMessage.value = null
        articleNameState.error.value = false
        articleNameState.errorMessage.value = null
        articleTypeState.error.value = false
        articleTypeState.errorMessage.value = null
        articleBrandState.error.value = false
        articleBrandState.errorMessage.value = null
        articleModelState.error.value = false
        articleModelState.errorMessage.value = null
        articleSerialState.error.value = false
        articleSerialState.errorMessage.value = null
        articleAccesoriesState.error.value = false
        articleAccesoriesState.errorMessage.value = null
        articleProblemState.error.value = false
        articleProblemState.errorMessage.value = null
        repairStatusState.error.value = false
        repairStatusState.errorMessage.value = null
        repairDetailsState.error.value = false
        repairDetailsState.errorMessage.value = null
        repairPriceState.error.value = false
        repairPriceState.errorMessage.value = null
        receivedAtState.error.value = false
        receivedAtState.errorMessage.value = null
        repairedAtState.error.value = false
        repairedAtState.errorMessage.value = null
        imagesListState.error.value = false
        imagesListState.errorMessage.value = null
        imagesNamesState.error.value = false
        imagesNamesState.errorMessage.value = null
    }
}