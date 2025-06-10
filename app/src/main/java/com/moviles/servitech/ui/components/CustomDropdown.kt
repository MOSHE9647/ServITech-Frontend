package com.moviles.servitech.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moviles.servitech.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    placeholder: String,
    selected: String,
    options: List<String>,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        CustomInputField(
            label = label,
            placeholder = placeholder,
            trailingIcon = {
                TrailingIcon(expanded = expanded)
            },
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = isEnabled,
            isError = isError,
            supportingText = {
                if (isError) ErrorText(
                    errorMessage ?: stringResource(R.string.unknown_error)
                )
            },
            modifier = Modifier
                .menuAnchor(
                    MenuAnchorType.PrimaryNotEditable, isEnabled
                )
                .padding(bottom = 5.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
