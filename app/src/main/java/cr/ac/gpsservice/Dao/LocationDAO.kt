package cr.ac.gpsservice.Dao

import androidx.room.*
import cr.ac.gpsservice.Entity.Location


@Dao
interface LocationDAO {


    @Insert
    fun insert (location: Location)


    @Query ("select * from location_table")
    fun query(): List<Location>


}