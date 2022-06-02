package cr.ac.gpsservice

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cr.ac.gpsservice.Db.LocationDatabase
import cr.ac.gpsservice.Entity.Location
import cr.ac.gpsservice.Service.GpsService
import cr.ac.gpsservice.databinding.ActivityMapsBinding

private lateinit var mMap: GoogleMap
private lateinit var locationDatabase: LocationDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    var mLocationRequest // para configurar la frecuencia de actualización del gps
            : LocationRequest? = null
    private var mLocationCallback // para indicar qué hace la app con cada actualización del gps
            : LocationCallback? = null

    private var mFusedLocationClient //proveedor de los servicios de localización de google
            : FusedLocationProviderClient? = null



    private lateinit var binding: ActivityMapsBinding
    private val SOLICITAR_GPS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationDatabase = LocationDatabase.getInstance(this)
        validaPermisos()
        getLocation()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        iniciaServicio()
        recuperarPuntos()

    }

    /**
     * obetener los untos almacenados en la bd y los muestra en el mapa
     */
    fun recuperarPuntos() {

        var lista : List<Location>  = locationDatabase.locationDAO.query()

        for(loc in lista) {
            // Add a marker in Sydney and move the camera
            val sydney = LatLng(loc.latitude, loc.longitude)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        }

    }

    /**
     * hace un filtro del broadcast/ accion de GPS (cr.ac.gpsservice.GPS_EVENT)
     * e inicia el servicio (StartService) GpsService
     */
    fun iniciaServicio() {
        val filter = IntentFilter()
        filter.addAction(GpsService.GPS)
        val rev = ProgressReceiver()
        registerReceiver(rev, filter)
        startService(Intent(this, GpsService::class.java))

    }

    /**
     * valida si la app tiene permisos de ACCESS_FINE_LOCATION Y ACCESS_COARSE_LOCATION
     * si no tiene permisos los solicita al usuario (requestPermissions)
     */
    fun validaPermisos() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //No tengo permisos, voy a solicitarlos al usuario
            // cuando llamo a este método se llama a @onRequestPermissionsResult
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                SOLICITAR_GPS
            )
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            SOLICITAR_GPS -> {
                if (grantResults.isEmpty()
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    //si dio permisos
                    System.exit(1)

                }
            }
        }
    }
    class ProgressReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action == GpsService.GPS){
                val localizacion : Location =
                    intent.getSerializableExtra("localizacion") as Location

                val punto = LatLng(localizacion.latitude, localizacion.longitude)
                mMap.addMarker(MarkerOptions().position(punto).title("marker in aaaa"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(punto))
            }

        }

    }
}

