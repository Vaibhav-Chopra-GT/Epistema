package com.example.epistema.data

data class SummaryResponse(
    val title: String,
    val extract: String,
    val thumbnail: Thumbnail?
)

data class Thumbnail(
    val source: String
)
