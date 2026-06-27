package com.novastream.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.novastream.android.service.CaptureService
import com.novastream.android.ui.NovaStreamApp

class MainActivity : ComponentActivity() {

    private val screenCaptureLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode != RESULT_OK) {
                return@registerForActivityResult
            }

            val data = result.data ?: return@registerForActivityResult

            val serviceIntent = Intent(
                this,
                CaptureService::class.java
            ).apply {

                action = CaptureService.ACTION_START

                putExtra(
                    CaptureService.EXTRA_RESULT_CODE,
                    result.resultCode
                )

                putExtra(
                    CaptureService.EXTRA_DATA,
                    data
                )
            }

            startForegroundService(serviceIntent)

        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            NovaStreamApp(

                onStartCapture = {

                    val projectionManager =
                        getSystemService(
                            android.media.projection.MediaProjectionManager::class.java
                        )

                    screenCaptureLauncher.launch(
                        projectionManager.createScreenCaptureIntent()
                    )

                }

            )

        }

    }

}