package com.lihan.jiburiaiagent.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lihan.jiburiaiagent.explore.data.MovieDao
import com.lihan.jiburiaiagent.explore.data.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class JiburiDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
