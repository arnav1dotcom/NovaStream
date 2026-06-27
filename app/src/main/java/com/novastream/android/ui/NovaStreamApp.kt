package com.novastream.android.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.novastream.android.theme.NovaStreamTheme

@Composable
fun NovaStreamApp(
    onStartCapture: () -> Unit
) {

    NovaStreamTheme {

        Surface(
            color = MaterialTheme.colorScheme.background
        ) {

            DashboardScreen(
                onStartCapture = onStartCapture
            )

        }

    }

}