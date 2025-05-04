package com.example.epistema.network

import com.example.epistema.data.ParseResponse
import com.example.epistema.data.WikiSearchResponse
import com.example.epistema.data.GeoSearchResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {

    // Search articles by keyword
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("list") list: String,
        @Query("srsearch") query: String,
        @Query("srlimit") limit: Int
    ): WikiSearchResponse

    // Parse full article by page ID
    @GET("w/api.php")
    suspend fun parseArticleById(
        @Query("action") action: String = "parse",
        @Query("pageid") pageid: String,
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "text"
    ): ParseResponse

    // Parse full article by title
    @GET("w/api.php")
    suspend fun parseArticleByTitle(
        @Query("action") action: String = "parse",
        @Query("page") page: String,
        @Query("format") format: String = "json"
    ): ParseResponse

    // ✅ Get nearby articles using GPS coordinates
    @GET("w/api.php")
    suspend fun getNearbyArticles(
        @Query("action") action: String = "query",
        @Query("list") list: String = "geosearch",
        @Query("gscoord") coord: String, // "latitude|longitude"
        @Query("gsradius") radius: Int = 10000, // meters
        @Query("gslimit") limit: Int = 10,
        @Query("format") format: String = "json"
    ): GeoSearchResponse
}

data class ParseResponse(
    val parse: Parse
)

data class Parse(
    val title: String,
    val pageid: Int,
    val text: TextContent
)

data class TextContent(
    @SerializedName("*")
    val html: String
)
