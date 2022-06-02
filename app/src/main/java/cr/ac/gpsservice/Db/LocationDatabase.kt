package cr.ac.gpsservice.Db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cr.ac.gpsservice.Dao.LocationDAO
import cr.ac.gpsservice.Entity.Location


@Database(entities = [Location::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {

    abstract val locationDAO: LocationDAO

    companion object {


        private var INSTANCE: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocationDatabase::class.java,
                        "database"
                    )
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}