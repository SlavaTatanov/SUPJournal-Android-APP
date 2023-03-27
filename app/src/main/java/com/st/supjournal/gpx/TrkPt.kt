package com.st.supjournal.gpx

import java.time.LocalDateTime


data class TrkPt (
    val lat: Double,
    val lon: Double,
    val time: LocalDateTime
        )