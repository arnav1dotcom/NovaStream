package com.novastream.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onStartCapture: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "NovaStream",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text("Device")
                Text("OPD2403")

                Spacer(modifier = Modifier.height(10.dp))

                Text("Status")
                Text("Idle")

                Spacer(modifier = Modifier.height(10.dp))

                Text("Resolution")
                Text("1348 x 953")

                Spacer(modifier = Modifier.height(10.dp))

                Text("FPS")
                Text("120")

                Spacer(modifier = Modifier.height(10.dp))

                Text("Bitrate")
                Text("40 Mbps")

                Spacer(modifier = Modifier.height(10.dp))

                Text("Encoder")
                Text("H.264")

            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartCapture
        ) {

            Text("Start Capture")

        }

    }

}