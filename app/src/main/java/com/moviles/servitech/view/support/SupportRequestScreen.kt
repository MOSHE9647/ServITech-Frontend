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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    // Observe ViewModel states
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

    // Handle success
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            // Show success message
            android.widget.Toast.makeText(
                context,
                "Support request sent successfully",
                android.widget.Toast.LENGTH_LONG
            ).show()

            // Reset form
            viewModel.clearSuccess()
            viewModel.resetForm()

            // Navigate back or execute success callback
            onSuccess()
        }
    }

    // Handle errors
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
                title = { Text("Support Request") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Title and description
            Text(
                text = "Technical Support Request",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Complete the form to request technical assistance. An administrator will contact you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Form
            CustomCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date field
                    DatePickerField(
                        context = context,
                        label = "Request date",
                        placeholder = "Select date",
                        initialDate = date,
                        isError = dateError != null,
                        errorMessage = dateError,
                        isEnabled = !isLoading,
                        onDateChange = { viewModel.updateDate(it.orEmpty()) }
                    )

                    // Location field
                    CustomInputField(
                        label = "Location",
                        placeholder = "Ex: San José, Costa Rica",
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

                    // Detail field
                    OutlinedTextField(
                        value = detail,
                        onValueChange = { viewModel.updateDetail(it) },
                        label = { Text("Request detail") },
                        placeholder = { Text("Describe your problem or request in detail...") },
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

            // Submit button
            CustomButton(
                text = if (isLoading) "Sending..." else "Send Support Request",
                onClick = { viewModel.submitSupportRequest() },
                enabled = isFormValid && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Loading indicator
            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    isVisible = true
                )
            }
        }
    }
}