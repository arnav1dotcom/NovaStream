/*
 * NovaStream
 * Version 0.5
 *
 * File:
 * CaptureEngine.kt
 */

package com.novastream.android.capture

import android.media.projection.MediaProjection
import android.view.Surface
import com.novastream.android.model.CaptureConfig

class CaptureEngine(

    private val captureSession: CaptureSession,

    private val projection: MediaProjection,

    private val config: CaptureConfig

) {

    private var surface: Surface? = null

    fun attachSurface(

        encoderSurface: Surface

    ) {

        surface = encoderSurface

    }

    fun start() {

        surface?.let {

            captureSession.start(it)

        }

    }

    fun stop() {

        captureSession.stop()

    }

}