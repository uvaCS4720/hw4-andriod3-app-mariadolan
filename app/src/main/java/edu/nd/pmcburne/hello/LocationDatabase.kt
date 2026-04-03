package edu.nd.pmcburne.hello

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationObject::class, LocationTag::class], version = 1)
    abstract class LocationDatabase : RoomDatabase() {
        abstract fun LocationDao(): LocationDao
        companion object {
            @Volatile
            private var instance: LocationDatabase? = null

            fun getInstance(context: Context): LocationDatabase {
                return instance ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(
                        context.applicationContext,
                        LocationDatabase::class.java,
                        "location_database"
                    ).build().also { instance = it }
                }
            }
        }
    }
