package com.example.epistema.data

import com.google.gson.annotations.SerializedName

// --- existing search models ---
data class WikiSearchResponse(val query: Query)
data class Query(val search: List<WikiSearchResult>)

data class WikiSearchResult(
    val title: String,
    val snippet: String,
    @SerializedName("pageid") val pageId: Int
)

// --- new parse response models ---
data class ParseResponse(val parse: Parse)
data class Parse(val title: String, val text: Text)
data class Text(
    @field:SerializedName("*")
    val html: String
)

data class GeoArticle(
    val pageid: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val dist: Double,
    val description: String? // Add this field to hold the article description
)



// --- unify into WikiArticle ---
data class WikiArticle(
    val pageId: Int,
    val title: String,
    val content: String,  // now full HTML from parse
    val url: String? = null
)
data class GeoSearchResponse(val query: GeoQuery)
data class GeoQuery(val geosearch: List<GeoArticle>)

