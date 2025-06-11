package com.moviles.servitech.view

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.convertMillisInDate
import com.moviles.servitech.common.Utils.getFileNameAndExtension
import com.moviles.servitech.common.Utils.rememberSessionManager
import com.moviles.servitech.common.Utils.uriToMultipart
import com.moviles.servitech.model.Image
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.enums.RepairStatus
import com.moviles.servitech.ui.components.CustomButton
import com.moviles.servitech.ui.components.CustomCard
import com.moviles.servitech.ui.components.CustomDropdown
import com.moviles.servitech.ui.components.CustomInputField
import com.moviles.servitech.ui.components.DatePickerField
import com.moviles.servitech.ui.components.ErrorText
import com.moviles.servitech.ui.components.FilePickerField
import com.moviles.servitech.ui.components.HandleServerError
import com.moviles.servitech.ui.components.HandleViewModelState
import com.moviles.servitech.ui.components.LoadingIndicator
import com.moviles.servitech.ui.components.PhoneNumberField
import com.moviles.servitech.viewmodel.RepairRequestViewModel
import com.moviles.servitech.viewmodel.auth.LogoutState
import com.moviles.servitech.viewmodel.auth.LogoutViewModel
import github.mahdiasd.composefilepicker.utils.PickerResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import okhttp3.MultipartBody

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: LogoutViewModel = hiltViewModel(),
    repReqViewModel: RepairRequestViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = { }
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = rememberSessionManager(context)

    val logoutState by viewModel.logoutState.observeAsState()

    val user by sessionManager.user.collectAsState(initial = null)
    val token by sessionManager.token.collectAsState(initial = "")
    val expiresAt by sessionManager.expiresAt.collectAsState(initial = 0L)

    val viewModelState by repReqViewModel.viewModelState.observeAsState()
    val repairRequests by repReqViewModel.repairRequests.collectAsState(initial = emptyList())
    val repairRequest by repReqViewModel.repairRequest.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 23.dp, bottom = 23.dp)
        ) {
            Column {
                Text(
                    text = "Bienvenido a ServITech",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Token: $token")
                Text(text = "Expires At: ${convertMillisInDate(expiresAt ?: 0L, context)}")

                Spacer(modifier = Modifier.height(8.dp))

                if (user != null) {
                    Text(text = "Usuario:")
                    Text(text = "ID: ${user?.id}")
                    Text(text = "Rol: ${user?.role}")
                    Text(text = "Nombre: ${user?.name}")
                    Text(text = "Email: ${user?.email}")
                    Text(text = "Teléfono: ${user?.phone}")
                } else {
                    Text("No se encontró información del usuario.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (token!!.isNotEmpty()) viewModel.logout(token!!)
                        else Toast.makeText(context, "No Token", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Cerrar sesión")
                }

                Button(
                    onClick = { repReqViewModel.getAllRepairRequests() }
                ) { Text("Obtener solicitudes de reparación") }

                Button(
                    onClick = {
                        val receiptNumber = when (repairRequests.isEmpty()) {
                            false -> repairRequests.first().receiptNumber
                            true -> ""
                        }
                        repReqViewModel.getRepairRequestByReceiptNumberOrID(receiptNumber)
                    }
                ) { Text("Obtener solicitud de reparación") }

                CreateUpdateRepairRequestForm(
                    repairRequest = repairRequest,
                    viewModel = repReqViewModel,
                    isLoading = false,
                    onSubmit = { repairRequest ->
                        val isUpdate: Boolean = when {
                            !repairRequest.receiptNumber.isNullOrEmpty() -> true
                            repairRequest.id != null -> true
                            else -> false
                        }
                        when (isUpdate) {
                            true -> repReqViewModel.updateRepairRequest(repairRequest)
                            false -> repReqViewModel.createRepairRequest(repairRequest)
                        }
                    },
                    onDelete = {
                        repReqViewModel.deleteUpdateRequest(it)
                    }
                )
            }
        }

        HandleViewModelState(
            state = viewModelState,
            logTag = "CreateUpdateRepairRequestForm"
        )

        when (val state = logoutState) {
            is LogoutState.Loading -> {
                // Animated loading indicator
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    withBlurBackground = true,
                    isVisible = true
                )
            }

            is LogoutState.Success -> {
                Toast.makeText(
                    context,
                    stringResource(R.string.success_logout),
                    Toast.LENGTH_LONG
                ).show()
                navigateToLogin()
            }

            is LogoutState.Error -> {
                HandleServerError("HomeScreen", state.message)
            }

            else -> { /* No-op */
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateUpdateRepairRequestForm(
    repairRequest: RepairRequest? = null,
    viewModel: RepairRequestViewModel,
    isLoading: Boolean = false,
    onSubmit: (RepairRequest) -> Unit = { },
    onDelete: (RepairRequest) -> Unit
) {
    // Observe the state from the ViewModel of the RepairRequest input form
    val customerName: String by viewModel.customerName.observeAsState(
        initial = repairRequest?.customerName ?: "Isaac Herrera"
    )
    val customerNameError: Boolean by viewModel.customerNameError.observeAsState(initial = false)
    val customerNameErrorMsg: String? by viewModel.customerNameErrorMsg.observeAsState(initial = null)

    val customerPhone: String by viewModel.customerPhone.observeAsState(
        initial = repairRequest?.customerPhone ?: "+50664212950"
    )
    val customerPhoneError: Boolean by viewModel.customerPhoneError.observeAsState(initial = false)
    val customerPhoneErrorMsg: String? by viewModel.customerPhoneErrorMsg.observeAsState(initial = null)

    val customerEmail: String by viewModel.customerEmail.observeAsState(
        initial = repairRequest?.customerEmail ?: "isaacmhp2001@gmail.com"
    )
    val customerEmailError: Boolean by viewModel.customerEmailError.observeAsState(initial = false)
    val customerEmailErrorMsg: String? by viewModel.customerEmailErrorMsg.observeAsState(initial = null)

    val articleName: String by viewModel.articleName.observeAsState(
        initial = repairRequest?.articleName ?: "Portátil Asus VivoBook 15"
    )
    val articleNameError: Boolean by viewModel.articleNameError.observeAsState(initial = false)
    val articleNameErrorMsg: String? by viewModel.articleNameErrorMsg.observeAsState(initial = null)

    val articleType: String by viewModel.articleType.observeAsState(
        initial = repairRequest?.articleType ?: "Laptop"
    )
    val articleTypeError: Boolean by viewModel.articleTypeError.observeAsState(initial = false)
    val articleTypeErrorMsg: String? by viewModel.articleTypeErrorMsg.observeAsState(initial = null)

    val articleBrand: String by viewModel.articleBrand.observeAsState(
        initial = repairRequest?.articleBrand ?: "Asus"
    )
    val articleBrandError: Boolean by viewModel.articleBrandError.observeAsState(initial = false)
    val articleBrandErrorMsg: String? by viewModel.articleBrandErrorMsg.observeAsState(initial = null)

    val articleModel: String by viewModel.articleModel.observeAsState(
        initial = repairRequest?.articleModel ?: "VivoBook 15"
    )
    val articleModelError: Boolean by viewModel.articleModelError.observeAsState(initial = false)
    val articleModelErrorMsg: String? by viewModel.articleModelErrorMsg.observeAsState(initial = null)

    val articleSerial: String by viewModel.articleSerial.observeAsState(
        initial = repairRequest?.articleSerialNumber ?: ""
    )
    val articleSerialError: Boolean by viewModel.articleSerialError.observeAsState(initial = false)
    val articleSerialErrorMsg: String? by viewModel.articleSerialErrorMsg.observeAsState(initial = null)

    val articleAccesories: String by viewModel.articleAccesories.observeAsState(
        initial = repairRequest?.articleAccesories ?: ""
    )
    val articleAccesoriesError: Boolean by viewModel.articleAccesoriesError.observeAsState(initial = false)
    val articleAccesoriesErrorMsg: String? by viewModel.articleAccesoriesErrorMsg.observeAsState(
        initial = null
    )

    val articleProblem: String by viewModel.articleProblem.observeAsState(
        initial = repairRequest?.articleProblem ?: "N/A"
    )
    val articleProblemError: Boolean by viewModel.articleProblemError.observeAsState(initial = false)
    val articleProblemErrorMsg: String? by viewModel.articleProblemErrorMsg.observeAsState(initial = null)

    val repairStatus: String by viewModel.repairStatus.observeAsState(
        initial = repairRequest?.repairStatus ?: RepairStatus.PENDING.value
    )
    val repairStatusError: Boolean by viewModel.repairStatusError.observeAsState(initial = false)
    val repairStatusErrorMsg: String? by viewModel.repairStatusErrorMsg.observeAsState(initial = null)

    val repairDetails: String by viewModel.repairDetails.observeAsState(
        initial = repairRequest?.repairDetails ?: ""
    )
    val repairDetailsError: Boolean by viewModel.repairDetailsError.observeAsState(initial = false)
    val repairDetailsErrorMsg: String? by viewModel.repairDetailsErrorMsg.observeAsState(initial = null)

    val repairPrice: Double by viewModel.repairPrice.observeAsState(
        initial = repairRequest?.repairPrice ?: 0.0
    )
    val repairPriceError: Boolean by viewModel.repairPriceError.observeAsState(initial = false)
    val repairPriceErrorMsg: String? by viewModel.repairPriceErrorMsg.observeAsState(initial = null)

    val receivedAt: String by viewModel.receivedAt.observeAsState(
        initial = repairRequest?.receivedAt ?: ""
    )
    val receivedAtError: Boolean by viewModel.receivedAtError.observeAsState(initial = false)
    val receivedAtErrorMsg: String? by viewModel.receivedAtErrorMsg.observeAsState(initial = null)

    val repairedAt: String? by viewModel.repairedAt.observeAsState(
        initial = repairRequest?.repairedAt ?: ""
    )
    val repairedAtError: Boolean by viewModel.repairedAtError.observeAsState(initial = false)
    val repairedAtErrorMsg: String? by viewModel.repairedAtErrorMsg.observeAsState(initial = null)

    val imagesList: List<Image> by viewModel.imagesList.observeAsState(
        initial = repairRequest?.images ?: emptyList<Image>()
    )
    val imagesListError: Boolean by viewModel.imagesListError.observeAsState(initial = false)
    val imagesListErrorMsg: String? by viewModel.imagesListErrorMsg.observeAsState(initial = null)

    val imagesNames: String by viewModel.imagesNames.observeAsState(
        initial = repairRequest?.images?.joinToString(
            separator = "; "
        ) { it.title ?: "" } ?: ""
    )
    val imagesNamesError: Boolean by viewModel.imagesNamesError.observeAsState(initial = false)
    val imagesNamesErrorMsg: String? by viewModel.imagesNamesErrorMsg.observeAsState(initial = null)

    // State to manage the selected images and other data
    var selectedImages by remember { mutableStateOf(persistentListOf<PickerResult>()) }
    val repairRequestSendEnable: Boolean by viewModel.repairRequestSendEnable.observeAsState(initial = false)

    val focusManager = LocalFocusManager.current
    val context: Context = LocalContext.current

    // Form submission logic
    CustomCard {
        CustomInputField(
            label = stringResource(R.string.customer_name),
            placeholder = stringResource(R.string.name_hint),
            value = customerName,
            onValueChange = { viewModel.onCustomerNameChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = customerNameError,
            enabled = !isLoading,
            supportingText = {
                if (customerNameError) ErrorText(
                    customerNameErrorMsg ?: stringResource(R.string.name_empty_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PhoneNumberField(
            label = stringResource(R.string.customer_phone),
            placeholder = stringResource(R.string.phone_hint),
            phoneValue = customerPhone,
            onPhoneChanged = { viewModel.onCustomerPhoneChanged(it) },
            isError = customerPhoneError,
            enabled = !isLoading,
            supportingText = {
                if (customerPhoneError) ErrorText(
                    customerPhoneErrorMsg ?: stringResource(R.string.phone_empty_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.email_hint),
            value = customerEmail,
            onValueChange = { viewModel.onCustomerEmailChanged(it) },
            keyboardType = KeyboardType.Email,
            isError = customerEmailError,
            enabled = !isLoading,
            supportingText = {
                if (customerEmailError) ErrorText(
                    customerEmailErrorMsg ?: stringResource(R.string.email_invalid_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_name),
            placeholder = stringResource(R.string.article_name_hint),
            value = articleName,
            onValueChange = { viewModel.onArticleNameChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleNameError,
            enabled = !isLoading,
            supportingText = {
                if (articleNameError) ErrorText(
                    articleNameErrorMsg ?: stringResource(R.string.article_name_required),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_type),
            placeholder = stringResource(R.string.article_type_hint),
            value = articleType,
            onValueChange = { viewModel.onArticleTypeChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleTypeError,
            enabled = !isLoading,
            supportingText = {
                if (articleTypeError) ErrorText(
                    articleTypeErrorMsg ?: stringResource(R.string.article_type_required),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_brand),
            placeholder = stringResource(R.string.article_brand_hint),
            value = articleBrand,
            onValueChange = { viewModel.onArticleBrandChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleBrandError,
            enabled = !isLoading,
            supportingText = {
                if (articleBrandError) ErrorText(
                    articleBrandErrorMsg ?: stringResource(R.string.article_brand_required),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_model),
            placeholder = stringResource(R.string.article_model_hint),
            value = articleModel,
            onValueChange = { viewModel.onArticleModelChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleModelError,
            enabled = !isLoading,
            supportingText = {
                if (articleModelError) ErrorText(
                    articleModelErrorMsg ?: stringResource(R.string.article_model_required),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_serial),
            placeholder = stringResource(R.string.article_serial_hint),
            value = articleSerial,
            onValueChange = { viewModel.onArticleSerialChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleSerialError,
            enabled = !isLoading,
            supportingText = {
                if (articleSerialError) ErrorText(
                    articleSerialErrorMsg ?: stringResource(R.string.unknown_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_accesories),
            placeholder = stringResource(R.string.article_accesories_hint),
            value = articleAccesories,
            onValueChange = { viewModel.onArticleAccesoriesChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleAccesoriesError,
            enabled = !isLoading,
            supportingText = {
                if (articleAccesoriesError) ErrorText(
                    articleAccesoriesErrorMsg ?: stringResource(R.string.unknown_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.article_problem),
            placeholder = stringResource(R.string.article_problem_hint),
            value = articleProblem,
            onValueChange = { viewModel.onArticleProblemChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = articleProblemError,
            enabled = !isLoading,
            supportingText = {
                if (articleProblemError) ErrorText(
                    articleProblemErrorMsg ?: stringResource(R.string.article_problem_required),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomDropdown(
            label = stringResource(R.string.repair_status),
            value = RepairStatus.valueOf(repairStatus.uppercase()).label(context),
            placeholder = stringResource(R.string.repair_status_hint),
            selected = repairStatus,
            options = RepairStatus.entries.map { it.label(context) },
            isEnabled = !isLoading,
            isError = repairStatusError,
            errorMessage = repairStatusErrorMsg ?: stringResource(R.string.repair_status_required),
            onSelectedChange = {
                val status = RepairStatus.fromLabel(context, it)
                viewModel.onRepairStatusChanged(status.toApiString())
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.repair_details),
            placeholder = stringResource(R.string.repair_details_hint),
            value = repairDetails,
            onValueChange = { viewModel.onRepairDetailsChanged(it) },
            keyboardType = KeyboardType.Text,
            isError = repairDetailsError,
            enabled = !isLoading,
            supportingText = {
                if (repairDetailsError) ErrorText(
                    repairDetailsErrorMsg ?: stringResource(R.string.unknown_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CustomInputField(
            label = stringResource(R.string.repair_price),
            placeholder = stringResource(R.string.repair_price_hint),
            value = repairPrice.toString(),
            onValueChange = { viewModel.onRepairPriceChanged(it.toDouble()) },
            keyboardType = KeyboardType.Text,
            isError = repairPriceError,
            enabled = !isLoading,
            supportingText = {
                if (repairPriceError) ErrorText(
                    repairPriceErrorMsg ?: stringResource(R.string.unknown_error),
                )
            },
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DatePickerField(
            context = context,
            label = stringResource(R.string.received_at),
            placeholder = stringResource(R.string.received_at_hint),
            initialDate = receivedAt,
            isError = receivedAtError,
            errorMessage = receivedAtErrorMsg ?: stringResource(R.string.received_at_required),
            isEnabled = !isLoading,
            onDateChange = { viewModel.onReceivedAtChanged(it.orEmpty()) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        DatePickerField(
            context = context,
            label = stringResource(R.string.repaired_at),
            placeholder = stringResource(R.string.repaired_at_hint),
            initialDate = repairedAt,
            isError = repairedAtError,
            useDefaultDate = false,
            errorMessage = repairedAtErrorMsg ?: stringResource(R.string.repaired_at_required),
            isEnabled = !isLoading,
            onDateChange = { viewModel.onRepairedAtChanged(it.orEmpty()) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilePickerField(
            initialImage = imagesNames,
            maxSelection = 5,
            isEditing = repairRequest != null,
            isError = imagesNamesError || imagesListError,
            errorMessage = when {
                imagesNamesError -> imagesNamesErrorMsg
                    ?: context.getString(R.string.image_empty_error)

                imagesListError -> imagesListErrorMsg
                    ?: context.getString(R.string.image_empty_error)

                else -> null
            },
            isEnabled = !isLoading,
            onValueChange = { viewModel.onImagesNamesChanged(it) },
            onImageChange = {
                selectedImages = it as PersistentList<PickerResult>
                val list: MutableList<Image> = mutableListOf()

                for (image in selectedImages) {
                    val imageUri: Uri = image.uri
                    val imagePath: String? = imageUri.path
                    val imageTitle: String =
                        getFileNameAndExtension(imageUri, context.contentResolver)
                    val imageFile: MultipartBody.Part? = uriToMultipart(context, imageUri)

                    list.add(
                        Image(
                            path = imagePath ?: "",
                            title = imageTitle,
                            file = imageFile
                        )
                    )
                }
                viewModel.onImagesListChanged(list)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Column {
                CustomButton(
                    text = stringResource(R.string.request_repair),
                    enabled = !isLoading,
//            enabled = repairRequestSendEnable && !isLoading,
                    onClick = {
                        val repairRequest = RepairRequest(
                            id = null,
                            receiptNumber = null,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            customerEmail = customerEmail,
                            articleName = articleName,
                            articleType = articleType,
                            articleBrand = articleBrand,
                            articleModel = articleModel,
                            articleSerialNumber = articleSerial,
                            articleAccesories = articleAccesories,
                            articleProblem = articleProblem,
                            repairStatus = repairStatus,
                            repairDetails = repairDetails,
                            repairPrice = repairPrice,
                            receivedAt = receivedAt,
                            repairedAt = repairedAt,
                            images = imagesList
                        )
                        onSubmit(repairRequest)
                    }
                )

                CustomButton(
                    text = "Update Repair Request",
                    enabled = !isLoading,
                    onClick = {
                        val repairRequest = RepairRequest(
                            id = repairRequest?.id ?: 1,
                            receiptNumber = repairRequest?.receiptNumber ?: "RR-000000000001",
                            customerName = customerName,
                            customerPhone = customerPhone,
                            customerEmail = customerEmail,
                            articleName = articleName,
                            articleType = articleType,
                            articleBrand = articleBrand,
                            articleModel = articleModel,
                            articleSerialNumber = articleSerial,
                            articleAccesories = articleAccesories,
                            articleProblem = articleProblem,
                            repairStatus = repairStatus,
                            repairDetails = repairDetails,
                            repairPrice = repairPrice,
                            receivedAt = receivedAt,
                            repairedAt = repairedAt,
                            images = imagesList
                        )
                        onSubmit(repairRequest)
                    }
                )

                CustomButton(
                    text = "Delete Repair Request",
                    enabled = !isLoading,
                    onClick = {
                        val request = RepairRequest(
                            id = 2,
                            receiptNumber = "RR-000000000002",
                            customerName = customerName,
                            customerPhone = customerPhone,
                            customerEmail = customerEmail,
                            articleName = articleName,
                            articleType = articleType,
                            articleBrand = articleBrand,
                            articleModel = articleModel,
                            articleSerialNumber = articleSerial,
                            articleAccesories = articleAccesories,
                            articleProblem = articleProblem,
                            repairStatus = repairStatus,
                            repairDetails = repairDetails,
                            repairPrice = repairPrice,
                            receivedAt = receivedAt,
                            repairedAt = repairedAt,
                            images = imagesList
                        )
                        onDelete(request)
                    }
                )
            }
        }
    }
}