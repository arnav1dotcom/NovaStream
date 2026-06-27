package com.novastream.android.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager

class ProjectionManager(
    private val activity: Activity
) {

    private val manager =
        activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                as MediaProjectionManager

    private var projection: MediaProjection? = null

    fun createScreenCaptureIntent(): Intent {
        return manager.createScreenCaptureIntent()
    }

    fun onPermissionGranted(
        resultCode: Int,
        data: Intent
    ) {

        projection =
            manager.getMediaProjection(
                resultCode,
                data
            )

    }

    fun getProjection(): MediaProjection? {
        return projection
    }
}