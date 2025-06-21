package com.moviles.servitech.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.res.stringResource
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.moviles.servitech.R
import com.moviles.servitech.common.Utils.formatLocalDateForDisplay
import com.moviles.servitech.common.Utils.getDisplayLanguage
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(
    context: Context,
    label: String,
    placeholder: String,
    initialDate: String?,
    useDefaultDate: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    isEnabled: Boolean = true,
    onDateChange: (String?) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }
    var selectedDate by remember {
        when {
            initialDate.isNullOrEmpty() -> {
                if (useDefaultDate) {
                    mutableStateOf(LocalDate.now())
                } else {
                    mutableStateOf(null)
                }
            }

            else -> mutableStateOf(LocalDate.parse(initialDate))
        }
    }
    if (useDefaultDate) {
        onDateChange(selectedDate?.toString())
    }
    val calendarState = rememberUseCaseState(
        visible = true,
        onDismissRequest = { showModal = false }
    )

    CustomInputField(
        label = label,
        value = selectedDate?.let {
            formatLocalDateForDisplay(selectedDate.toString(), getDisplayLanguage(context))
        } ?: "",
        onValueChange = { },
        placeholder = placeholder,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(R.string.select_date)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                        calendarState.show()
                    }
                }
            },
        isError = isError,
        readOnly = true,
        supportingText = {
            if (isError) {
                ErrorText(
                    errorMessage ?: stringResource(R.string.unknown_error),
                )
            }
        },
        enabled = isEnabled
    )

    if (showModal) {
        DatePickerModal(
            initialDate = selectedDate,
            calendarState = calendarState,
            onDateSelected = { newDate ->
                selectedDate = newDate?.let { LocalDate.parse(it) }
                onDateChange(selectedDate.toString())
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: LocalDate?,
    calendarState: UseCaseState,
    onDateSelected: (String?) -> Unit
) {
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Date(
            selectedDate = initialDate
        ) { date ->
            onDateSelected(date.toString())
        }
    )
}