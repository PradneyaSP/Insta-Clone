package com.example.myinsta.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myinsta.model.Post
import com.example.myinsta.model.Reel

@Database(
    entities = [
        Post::class,
        Reel::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun reelDao(): ReelDao

    companion object {
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "myinsta_database"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = db
                db
            }
        }
    }
}

