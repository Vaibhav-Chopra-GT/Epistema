package com.example.epistema.data
import androidx.room.Query

import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_table ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentHistory(): List<HistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntry)

    @Query("DELETE FROM history_table WHERE pageId NOT IN (SELECT pageId FROM history_table ORDER BY timestamp DESC LIMIT 10)")
    suspend fun trimToLatestTen()
}
