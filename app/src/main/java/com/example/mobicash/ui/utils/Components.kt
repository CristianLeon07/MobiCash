package com.example.mobicash.ui.utils

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Idle : UiState<Nothing>()
}

fun String.isDigitsOnly(): Boolean = all { it.isDigit() }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputBoxes(
    pinValue: String,
    onPinChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    pinLength: Int = 4
) {
    val focusRequesters = remember { List(pinLength) { FocusRequester() } }
    val colorScheme = MaterialTheme.colorScheme
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (0 until pinLength).forEach { index ->
            val digit = pinValue.getOrNull(index)?.toString() ?: ""

            OutlinedTextField(
                value = digit,
                onValueChange = { newDigit: String ->
                    // Lógica de manejo de entrada (omito por brevedad, pero mantenemos tu lógica)
                    if (newDigit.isDigitsOnly() && newDigit.length <= 1) {
                        val currentChars = pinValue.padEnd(pinLength, ' ').toMutableList()
                        var nextFocusIndex = index

                        if (newDigit.isNotEmpty()) {
                            currentChars[index] = newDigit[0]

                            if (index < pinLength - 1) {
                                nextFocusIndex = index + 1
                            } else {
                                keyboardController?.hide()
                            }
                        } else {
                            currentChars[index] = ' '
                            if (index > 0) nextFocusIndex = index - 1
                        }

                        val finalPin = currentChars.joinToString("").filter { it.isDigit() }
                        onPinChange(finalPin)

                        if (nextFocusIndex != index) {
                            focusRequesters.getOrNull(nextFocusIndex)?.requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .focusRequester(focusRequesters[index])
                    // **Controlamos el tamaño aquí para que sean pequeños**
                    .width(48.dp)
                    .height(52.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = isError,

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline.copy(alpha = 1f),
                    errorBorderColor = colorScheme.error,
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    cursorColor = Color.Transparent,

                    )
            )
        }
    }
}
