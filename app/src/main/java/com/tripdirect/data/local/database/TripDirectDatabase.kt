package com.tripdirect.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tripdirect.data.local.dao.EmergencyContactDao
import com.tripdirect.data.local.dao.ItineraryDao
import com.tripdirect.data.local.dao.ReviewDao
import com.tripdirect.data.local.dao.SavedPlaceDao
import com.tripdirect.data.local.dao.TripDao
import com.tripdirect.data.local.entity.EmergencyContactEntity
import com.tripdirect.data.local.entity.ItineraryEntity
import com.tripdirect.data.local.entity.ReviewEntity
import com.tripdirect.data.local.entity.SavedPlaceEntity
import com.tripdirect.data.local.entity.TripEntity

@Database(
    entities = [
        TripEntity::class,
        ItineraryEntity::class,
        SavedPlaceEntity::class,
        ReviewEntity::class,
        EmergencyContactEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class TripDirectDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun savedPlaceDao(): SavedPlaceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        @Volatile
        private var INSTANCE: TripDirectDatabase? = null

        fun getInstance(context: Context): TripDirectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripDirectDatabase::class.java,
                    "tripdirect_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
