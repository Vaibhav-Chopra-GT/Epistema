package com.example.epistema.network

import com.example.epistema.data.ParseResponse
import com.example.epistema.data.WikiSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String,
        @Query("format") format: String,
        @Query("list") list: String,
        @Query("srsearch") query: String,
        @Query("srlimit") limit: Int
    ): WikiSearchResponse

    // by pageid (existing)
    @GET("w/api.php")
    suspend fun parseArticleById(
        @Query("action") action: String = "parse",
        @Query("pageid") pageid: String,
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "text"
    ): ParseResponse

    // **new**: by page TITLE
    @GET("w/api.php")
    suspend fun parseArticleByTitle(
        @Query("action") action: String = "parse",
        @Query("page") page: String,
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "text"
    ): ParseResponse
}
