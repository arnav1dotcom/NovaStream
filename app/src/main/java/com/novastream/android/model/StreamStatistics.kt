package com.novastream.android.model

data class StreamStatistics(

    var fps: Int = 0,

    var bitrate: Long = 0,

    var encodedFrames: Long = 0,

    var droppedFrames: Long = 0,

    var averageEncodeTimeMs: Double = 0.0

)