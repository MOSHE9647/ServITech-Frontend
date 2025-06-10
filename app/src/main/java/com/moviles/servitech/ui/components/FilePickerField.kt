package com.moviles.servitech.ui.components

import android.net.Uri
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.getFileNameAndExtension
import github.mahdiasd.composefilepicker.screens.PickerDialog
import github.mahdiasd.composefilepicker.utils.PickerConfig
import github.mahdiasd.composefilepicker.utils.PickerResult
import github.mahdiasd.composefilepicker.utils.PickerType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePickerField(
    initialImage: String?,
    isEditing: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    isEnabled: Boolean = true,
    onValueChange: (String) -> Unit = { },
    onImageChange: (List<PickerResult>) -> Unit
) {
    // State to manage the image selection
    var selectedImage by remember { mutableStateOf(initialImage) }

    // State to manage the visibility of the PickerDialog
    var showPicker by remember { mutableStateOf(false) }

    // State to store the selected files
    var selectedFiles by remember { mutableStateOf(persistentListOf<PickerResult>()) }
    var selectedTypes by remember { mutableStateOf(persistentListOf<PickerType>()) }

    // Picker Configuration
    val pickerConfig = PickerConfig(maxSelection = 1)

    // Picker Types to show
    val pickerTypes = listOf(PickerType.ImageOnly)

    // Context
    val context = LocalContext.current

    pickerTypes.forEach { type ->
        CustomInputField(
            label = "Image",
            value = selectedImage ?: "",
            placeholder = "Select an Image (.jpg, .jpeg, .png)",
            onValueChange = { onValueChange(it) },
            trailingIcon = {
                Icon(
                    if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = "Select the image"
                )
            },
            isError = isError,
            supportingText = {
                ErrorText(
                    errorMessage ?: stringResource(R.string.email_invalid_error),
                )
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            selectedTypes = persistentListOf(type)
                            showPicker = true
                        }
                    }
                },
            enabled = isEnabled
        )
    }

    // Show the PickerDialog when showPicker is true
    if (showPicker && selectedTypes.isNotEmpty()) {
        PickerDialog(
            types = selectedTypes.toPersistentList(),
            pickerConfig = pickerConfig,
            onDismiss = { showPicker = false },
            selected = { files ->
                selectedFiles = files.toPersistentList()

                val fileName = getFileNameAndExtension(
                    selectedFiles.firstOrNull()?.uri ?: Uri.EMPTY, context.contentResolver
                )
                selectedImage = fileName

                onImageChange(selectedFiles)
                showPicker = false
            }
        )
    }
}