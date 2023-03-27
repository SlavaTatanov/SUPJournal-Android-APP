package com.st.supjournal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.st.supjournal.databinding.ActivityTrainingWithGpsBinding
import com.st.supjournal.gpx.TrkPt
import java.time.LocalDateTime

class TrainingWithGPS : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingWithGpsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    val gpxData = mutableListOf<TrkPt>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingWithGpsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkLocationPermission()

        binding.gpxBtnStop.setOnClickListener { stopTraining() }
        binding.gpxBtnStart.setOnClickListener { lastLoc() }
    }

    /**
     * Проверяет наличие разрешения на запись GPS и если их нет, запрашивает
     */
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 101)
        }
    }

    /**
     * Функция которая начинает записывать GPX трек
     */
    private  fun lastLoc() {
        // Запрашиваем разрешение на запись
        checkLocationPermission()
        // Получаем location Manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object: LocationListener {
            override fun onLocationChanged(location: Location) {
                location.let {
                    var timeInPoint = LocalDateTime.now()
                    gpxData.add(TrkPt(it.latitude, it.longitude, timeInPoint))
                }
            }
        }

        locationManager
            .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1.0f,
                locationListener)

        binding.gpxBtnStop.visibility = View.VISIBLE
    }

    /**
     * Останавливает запись GPX трека
     */
    private fun stopTraining () {
        locationManager.removeUpdates(locationListener)
        println(gpxData.toString())
        binding.gpxBtnStop.visibility = View.INVISIBLE
    }

}