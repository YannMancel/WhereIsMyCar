package com.mancel.yann.whereismycar.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mancel.yann.whereismycar.models.POI

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.databases
 *
 * An abstract [RoomDatabase] subclass.
 */
@Database(
    entities = [POI::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs ----------------------------------------------------------------------------------------

    abstract fun poiDAO(): PoiDAO

    // METHODS -------------------------------------------------------------------------------------

    companion object {

        private const val DATABASE_NAME = "WhereIsMyCar_Database"

        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}