package com.example.epistema.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: SavedArticle)

    @Query("SELECT * FROM saved_articles ORDER BY id DESC")
    fun getAll(): Flow<List<SavedArticle>>

    @Query("DELETE FROM saved_articles")
    suspend fun clearAllData()

    @Query("SELECT * FROM saved_articles WHERE id = :id")
    suspend fun getArticleById(id: Int): SavedArticle

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun deleteById(id: Int)
}
