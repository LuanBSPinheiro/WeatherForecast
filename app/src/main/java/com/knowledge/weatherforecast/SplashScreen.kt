package com.knowledge.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.splash_screen.*

class SplashScreen: AppCompatActivity() {

    lateinit var mFusedLocation: FusedLocationProviderClient
    private var myRequestCode = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        iv_sun.startAnimation(topAnimation)
        tv_appname.startAnimation(bottomAnimation)

        val splashScreenTimeOut = 4000
        val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(homeIntent)
            finish()
        }, splashScreenTimeOut.toLong())
    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(CheckPermission()) {
            if(locationEnabled()) {
                mFusedLocation.lastLocation.addOnCompleteListener { task ->
                    var location: Location ?= task.result
                        if(location == null) {
                            NewLocation()
                        } else {
                            Log.i("Location", location.latitude.toString())
                        }
                }
            } else {
                Toast.makeText(this, "Favor, ative o serviço de localização", Toast.LENGTH_SHORT).show()
            }
        } else {
            RequestPermission()
        }
    }

    private fun NewLocation() {

    }

    private fun RequestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            myRequestCode)
    }

    private fun locationEnabled(): Boolean {
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun CheckPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == myRequestCode) {
            if(grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

}