package com.moviles.servitech.view

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.RemoteImage
import com.moviles.servitech.common.Utils.getFileNameAndExtension
import com.moviles.servitech.common.Utils.getFilePathFromUri
import com.moviles.servitech.common.Utils.uriToMultipart
import com.moviles.servitech.model.Image
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.model.enums.RepairStatus
import com.moviles.servitech.ui.components.CustomButton
import com.moviles.servitech.ui.components.CustomCard
import com.moviles.servitech.ui.components.CustomDialog
import com.moviles.servitech.ui.components.CustomDropdown
import com.moviles.servitech.ui.components.CustomInputField
import com.moviles.servitech.ui.components.DatePickerField
import com.moviles.servitech.ui.components.ErrorText
import com.moviles.servitech.ui.components.FilePickerField
import com.moviles.servitech.ui.components.HandleViewModelState
import com.moviles.servitech.ui.components.PhoneNumberField
import com.moviles.servitech.viewmodel.RepairRequestViewModel
import com.moviles.servitech.viewmodel.utils.ViewModelState
import github.mahdiasd.composefilepicker.utils.PickerResult
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import okhttp3.MultipartBody

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RepairRequestScreen(
    viewModel: RepairRequestViewModel = hiltViewModel(),
    navigateBack: () -> Unit = { }
) {
    val viewModelState by viewModel.viewModelState.observeAsState()
    val repairRequests by viewModel.repairRequests.collectAsState(initial = emptyList())
    val isLoading = viewModelState is ViewModelState.Loading

    // State for managing the form and tabs
    var selectedRepairRequest by remember { mutableStateOf<RepairRequest?>(null) }
    var forceUpdate by remember { mutableStateOf(false) }
    var currentTab by remember { mutableIntStateOf(0) }
    var showForm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load all repair requests when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getAllRepairRequests()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Solicitudes de Reparación") },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.getAllRepairRequests()
                            forceUpdate = !forceUpdate
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recargar Solicitudes")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTab == 0 && !showForm) {
                    FloatingActionButton(onClick = {
                        selectedRepairRequest = null
                        showForm = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir solicitud")
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (!showForm) {
                    // Tabs for navigating between request types
                    TabRow(selectedTabIndex = currentTab) {
                        Tab(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0 },
                            text = { Text("Todas") }
                        )
                        Tab(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1 },
                            text = { Text("Pendientes") }
                        )
                        Tab(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2 },
                            text = { Text("Completadas") }
                        )
                    }

                    key(forceUpdate) {
                        // Repair requests list based on the selected tab
                        var repairRequestsList = repairRequests
                        val filteredRequests = when (currentTab) {
                            1 -> repairRequestsList.filter { it.repairStatus == RepairStatus.PENDING.value }
                            2 -> repairRequestsList.filter { it.repairStatus == RepairStatus.COMPLETED.value }
                            else -> repairRequestsList
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(filteredRequests) { request ->
                                RepairRequestItem(
                                    repairRequest = request,
                                    onEdit = {
                                        selectedRepairRequest = request
                                        showForm = true
                                    },
                                    onDelete = {
                                        viewModel.deleteUpdateRequest(request)
                                        Toast.makeText(
                                            context,
                                            "Solicitud eliminada correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        viewModel.getAllRepairRequests()
                                        showForm = false
                                        forceUpdate = !forceUpdate
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                } else {
                    // Create/update repair request form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedRepairRequest == null) "Nueva Solicitud" else "Editar Solicitud",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { showForm = false }) {
                                Icon(Icons.Default.Clear, contentDescription = "Cerrar")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        CreateUpdateRepairRequestForm(
                            repairRequest = selectedRepairRequest,
                            viewModel = viewModel,
                            isLoading = isLoading,
                            onSubmit = { request ->
                                Log.d("RepairRequestScreen", "Submitting request: $request")
                                if (selectedRepairRequest?.id == null) {
                                    viewModel.createRepairRequest(request)
                                } else {
                                    viewModel.updateRepairRequest(request)
                                }
                                // Go back to the list after submission
                                Toast.makeText(
                                    context,
                                    "Solicitud guardada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.getAllRepairRequests()
                                showForm = false
                                forceUpdate = !forceUpdate
                            }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent),
        ) {
            HandleViewModelState(
                state = viewModelState,
                logTag = "RepairRequestScreen",
            )
        }
    }
}

@Composable
fun RepairRequestItem(
    repairRequest: RepairRequest,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recibo: ${repairRequest.receiptNumber ?: "Pendiente"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cliente: ${repairRequest.customerName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Artículo: ${repairRequest.articleName}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Estado: ${repairRequest.repairStatus}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Problema: ${repairRequest.articleProblem}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Clear, contentDescription = "Eliminar")
                }

                if (showDeleteDialog) {
                    CustomDialog(
                        title = "Eliminar Solicitud",
                        message = "¿Estás seguro de que deseas eliminar esta solicitud?",
                        confirmButtonText = "Eliminar",
                        onConfirm = {
                            showDeleteDialog = false
                            onDelete()
                        },
                        cancelButtonText = "Cancelar",
                        onDismiss = { showDeleteDialog = false }
                    )
                }
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
    onSubmit: (RepairRequest) -> Unit = { }
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

    val repairPrice: Double? by viewModel.repairPrice.observeAsState(
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
    var selectedImagesUri by remember { mutableStateOf(listOf<Uri>()) }

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
            value = repairPrice?.let { if (it == 0.0) "" else it.toString() } ?: "",
            onValueChange = { viewModel.onRepairPriceChanged(it.toDoubleOrNull() ?: 0.0) },
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
                selectedImagesUri = selectedImages.map { image -> image.uri }
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

        key(selectedImagesUri) {
            val lazyRowState = rememberLazyListState()
            LazyRow(
                state = lazyRowState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp), // Padding horizontal para mostrar que hay más contenido
                horizontalArrangement = Arrangement.spacedBy(8.dp)  // Espacio entre elementos
            ) {
                if (imagesList.isNotEmpty()) {
                    items(imagesList) { image ->
                        val imagePath = image.path.replace("localhost", "10.0.2.2")
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            RemoteImage(imagePath)
                        }
                    }
                } else {
                    Log.d("RepairRequestScreen", "Selected images: $selectedImagesUri")
                    items(selectedImagesUri) { uri ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Log.d("RepairRequestScreen", "Image URI: $uri")
                            val imagePath = getFilePathFromUri(uri, context)
                                ?: uri.toString()
                            RemoteImage(imagePath.toString())
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row {
            Column {
                CustomButton(
                    text = repairRequest?.let {
                        stringResource(R.string.update_repair_request)
                    } ?: stringResource(R.string.request_repair),
                    enabled = !isLoading,
                    onClick = {
                        val repairRequest = RepairRequest(
                            id = repairRequest?.id,
                            receiptNumber = repairRequest?.receiptNumber,
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
                            repairPrice = repairPrice?.toDouble() ?: 0.0,
                            receivedAt = receivedAt,
                            repairedAt = repairedAt,
                            images = imagesList
                        )
                        onSubmit(repairRequest)
                    }
                )
            }
        }
    }
}