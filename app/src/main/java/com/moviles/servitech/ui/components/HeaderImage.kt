package com.moviles.servitech.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moviles.servitech.R

@Composable
fun HeaderImage(
    modifier: Modifier = Modifier,
    imageID: Int = R.drawable.logo,
    imageDescription: String = "Logo",
    withBackground: Boolean = false
) {
    Box(
        modifier = if (withBackground) {
            modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
                .size(270.dp)
        } else {
            modifier.size(270.dp)
        },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = imageID),
            contentDescription = imageDescription,
            contentScale = ContentScale.Fit
        )
    }
}