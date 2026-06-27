package com.novastream.android.capture

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.Surface

class CaptureSession(
    private val context: Context,
    private val mediaProjection: MediaProjection
) {

    private var virtualDisplay: VirtualDisplay? = null

    private var width = 0
    private var height = 0
    private var density = 0

    init {

        val metrics = context.resources.displayMetrics

        width = metrics.widthPixels
        height = metrics.heightPixels
        density = metrics.densityDpi

    }

    fun start(surface: Surface) {

        stop()

        virtualDisplay = mediaProjection.createVirtualDisplay(

            "NovaStream",

            width,

            height,

            density,

            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,

            surface,

            null,

            null

        )

    }

    fun stop() {

        virtualDisplay?.release()

        virtualDisplay = null

    }

    fun getWidth(): Int {

        return width

    }

    fun getHeight(): Int {

        return height

    }

    fun getDensity(): Int {

        return density

    }

}