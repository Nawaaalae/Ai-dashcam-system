package com.example.driveguard.data



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.driveguard.data.dao.TripDao
import com.example.driveguard.data.entities.IncidentEntity
import com.example.driveguard.data.entities.RoutePointEntity
import com.example.driveguard.data.entities.TripEntity

@Database(
    entities = [
        TripEntity::class,
        IncidentEntity::class,
        RoutePointEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DriveGuardDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: DriveGuardDatabase? = null

        fun getDatabase(context: Context): DriveGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriveGuardDatabase::class.java,
                    "driveguard_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}