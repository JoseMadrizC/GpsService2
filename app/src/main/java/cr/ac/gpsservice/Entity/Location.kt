package cr.ac.gpsservice.Entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "location_table")
data  class Location (
    @PrimaryKey(autoGenerate = true)
    val locationId: Long?,
    val latitude: Double,
    val longitude: Double

    ) : Serializable{

    }