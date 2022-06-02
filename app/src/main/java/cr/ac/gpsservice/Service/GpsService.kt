package cr.ac.gpsservice.Service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.Looper
import com.google.android.gms.location.*
import cr.ac.gpsservice.Db.LocationDatabase
import cr.ac.gpsservice.Entity.Location


class GpsService : IntentService("GpsService") {

    lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationDatabase: LocationDatabase

    companion object{
        val GPS = "cr.ac.gpsservice.GPS"
    }

    override fun onHandleIntent(intent: Intent?) {
        locationDatabase = LocationDatabase.getInstance(this)
        getLocation()
    }

    @SuppressLint("MissingPermission")
    /**
    * inicializa atributos locationCallback y fusedLocationClient
     * coloca un intervalo de 10000 y una prioridad de high accuracy
     * recibe gps mediante un onLocationResult
     * y envia un broadcast con una instancia de Location y la aacion GPS (cr.ac.gpsser)
     * ademas guarda la localizacion en la BD
     */
    fun getLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback(){
            override fun onLocationResult( locationResult : LocationResult) {
                if (locationResult == null){
                    return
                }
                for (location in locationResult.locations){

                    val bcIntent = Intent()
                    val localizacion = Location (null, location.latitude, location.longitude)
                    bcIntent.action = GpsService.GPS
                    bcIntent.putExtra("localizacion", localizacion)


                    sendBroadcast(bcIntent)

                    locationDatabase.locationDAO.insert(Location(null, localizacion.latitude, localizacion.longitude))


                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
        Looper.loop()
    }

}