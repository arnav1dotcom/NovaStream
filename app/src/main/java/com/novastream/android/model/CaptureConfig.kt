/*
 * NovaStream
 * Version 0.4
 *
 * File:
 * CaptureConfig.kt
 */

package com.novastream.android.model

data class CaptureConfig(

    var width: Int = 1920,

    var height: Int = 1080,

    var fps: Int = 120,

    var bitrate: Int = 40_000_000,

    var codec: VideoCodec = VideoCodec.H264,

    var enableAudio: Boolean = true,

    var connectionType: ConnectionType = ConnectionType.USB

)