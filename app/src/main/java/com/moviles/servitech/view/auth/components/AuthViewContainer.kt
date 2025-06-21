package com.moviles.servitech.view.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthViewContainer (
    modifier: Modifier = Modifier,
    authHandler: @Composable (BoxScope.() -> Unit) = {},
    content: @Composable (ColumnScope.() -> Unit) = {}
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Surface (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 23.dp, bottom = 23.dp)
        ) {
            Column {
                content()
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent),
        ) {
            authHandler()
        }
    }
}