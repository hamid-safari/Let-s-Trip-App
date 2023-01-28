package com.request.trip.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.request.trip.utils.G
import com.request.trip.utils.DB_NAME

@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(G.context, AppDatabase::class.java, DB_NAME)
                        //use prepopulate database for cities
                    .createFromAsset("city.db") //path: assets/city.db
                    .build()
            }
            return instance!!
        }

    }

    abstract fun cityDao(): CityDao
}
