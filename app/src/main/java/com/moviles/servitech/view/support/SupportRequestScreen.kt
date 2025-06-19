package com.moviles.servitech.view.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moviles.servitech.R
import com.moviles.servitech.ui.components.*
import com.moviles.servitech.viewmodel.SupportRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportRequestScreen(
    viewModel: SupportRequestViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current

    // Observar estados del ViewModel
    val date by viewModel.date.observeAsState("")
    val location by viewModel.location.observeAsState("")
    val detail by viewModel.detail.observeAsState("")

    val dateError by viewModel.dateError.observeAsState(null)
    val locationError by viewModel.locationError.observeAsState(null)
    val detailError by viewModel.detailError.observeAsState(null)

    val isLoading by viewModel.isLoading.observeAsState(false)
    val isSuccess by viewModel.isSuccess.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)
    val isFormValid by viewModel.isFormValid.observeAsState(false)

    // Manejar éxito
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            // Mostrar mensaje de éxito
            android.widget.Toast.makeText(
                context,
                "Solicitud de soporte enviada exitosamente",
                android.widget.Toast.LENGTH_LONG
            ).show()

            // Resetear el formulario
            viewModel.clearSuccess()
            viewModel.resetForm()

            // Navegar de vuelta o ejecutar callback de éxito
            onSuccess()
        }
    }

    // Manejar errores
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            android.widget.Toast.makeText(
                context,
                error,
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de Soporte") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título y descripción
            Text(
                text = "Solicitud de Soporte Técnico",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Complete el formulario para solicitar asistencia técnica. Un administrador se pondrá en contacto con usted.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Formulario
            CustomCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo de fecha
                    DatePickerField(
                        context = context,
                        label = "Fecha de la solicitud",
                        placeholder = "Seleccione la fecha",
                        initialDate = date,
                        isError = dateError != null,
                        errorMessage = dateError,
                        isEnabled = !isLoading,
                        onDateChange = { viewModel.updateDate(it.orEmpty()) }
                    )

                    // Campo de ubicación
                    CustomInputField(
                        label = "Ubicación",
                        placeholder = "Ej: San José, Costa Rica",
                        value = location,
                        onValueChange = { viewModel.updateLocation(it) },
                        keyboardType = KeyboardType.Text,
                        isError = locationError != null,
                        enabled = !isLoading,
                        supportingText = {
                            if (locationError != null) {
                                ErrorText(locationError!!)
                            }
                        },
                        imeAction = ImeAction.Next
                    )

                    // Campo de detalle
                    OutlinedTextField(
                        value = detail,
                        onValueChange = { viewModel.updateDetail(it) },
                        label = { Text("Detalle de la solicitud") },
                        placeholder = { Text("Describa detalladamente su problema o solicitud...") },
                        isError = detailError != null,
                        enabled = !isLoading,
                        supportingText = {
                            if (detailError != null) {
                                Text(detailError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Botón de envío
            CustomButton(
                text = if (isLoading) "Enviando..." else "Enviar Solicitud de Soporte",
                onClick = { viewModel.submitSupportRequest() },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Indicador de carga
            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    isVisible = true
                )
            }
        }
    }
}