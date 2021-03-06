package com.joel.geolocalizacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.joel.geolocalizacion.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Activo el boton para ampliar el mapa
        mMap.uiSettings.isZoomControlsEnabled = true
        //A??ade una marca en vigo
        myCityMarker()

        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        enableLocation()
    }

    private fun myCityMarker() {
        //Definimos la posici??n de la marca
        val myCity = LatLng(42.23282, -8.72264)
        //A??adimos una marca con una posici??n y un nombre
        mMap.addMarker(MarkerOptions().position(myCity).title("Vigo"))
        //Movemos la camara a la marca con una animacion
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(myCity, 18f),
            4000,
            null
        )
    }

    /**
     * M??todo que comprueba que el permiso este activado
     */
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //SupressLint es una interfaz que indica que se deben ignoar las advertencias especificadas

    /**
     * M??todo para activar la localizaci??n propia
     */
    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        //Si el mapa no est?? inicializado, no inicies este metodo
        if (!::mMap.isInitialized) return
        if (isLocationPermissionGranted()) {
            //Se han aceptado los permisos, permitimos la localizaci??n
            mMap.isMyLocationEnabled = true
        } else {
            //No se han aceptado los permisos, los pedimos a traves de un request
            requestLocationPermission()
        }
    }

    /**
     * M??todo para pedir al usuario que active los permisos
     */
    private fun requestLocationPermission() {
        //Comprobamos si el usuario ha rechazado los permisos requeridos
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Tiene que aceptar los permisos para acceder a su localizaci??n", Toast.LENGTH_SHORT).show()

        } else {
            //Comprobamos a traves del REQUEST_CODE_LOCATION si el usuario ha aceptado los permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    /**
     * M??todo que captura la respuesta del usuario
     */
    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //Comparamos el RequestCode con el REQUEST_CODE_LOCATION definido
        when (requestCode) {
            //Si los permisos estan aceptados, deja acceder a la localizaci??n propia
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localizaci??n ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * M??todo para comprobar si los permisos siguen activos cuando el usuario se va de la aplicaci??n
     */
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::mMap.isInitialized) return
        if (!isLocationPermissionGranted()) {
            !mMap.isMyLocationEnabled
            Toast.makeText(this, "Tiene que aceptar los permisos para acceder a su localizaci??n", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * M??todo para ubicar al usuario cuando pulse el bot??n OnMyLocation
     */
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Tu localizaci??n", Toast.LENGTH_SHORT).show()
        return false
    }

    /**
     * M??todo que muestra la latitud y longitud de nuestra ubicaci??n
     */
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Est??s en ${p0.latitude},${p0.longitude} ", Toast.LENGTH_SHORT).show()
    }
}