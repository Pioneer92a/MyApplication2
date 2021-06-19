package com.example.myapplication

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.myapplication.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainActivity"

    class MainActivity : AppCompatActivity(), GetRawData.GetRawDataInterface, GetJsonData.GetJsonDataInterface {
    private val city = "islamabad,pk"
    private val api = "0e2d77d481bcfa957f3841c0875961a6"
    private val url =
        "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getRawData = GetRawData(this)
        getRawData.execute(url)
    }

    override fun loading() {
        /* Showing the ProgressBar, Making the main design GONE */
        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE
    }

    override fun onDownloadComplete(result: String?) {
        Log.d(TAG, "onDownloadComplete called")
        val getJsonData = GetJsonData(this)
        getJsonData.execute(result)
    }

    override fun showError() {
        /* Make the main design gone, show error message */
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
    }

    override fun onDataAvailable(data: Data) {
        Log.d(TAG, "onDataAvailable called")
        /* Populating extracted data into our views */
        findViewById<TextView>(R.id.address).text = data.address
        findViewById<TextView>(R.id.updated_at).text = data.updatedAtText
        findViewById<TextView>(R.id.status).text = data.weatherDescription.capitalize()
        findViewById<TextView>(R.id.temp).text = data.temp
        findViewById<TextView>(R.id.temp_min).text = data.tempMin
        findViewById<TextView>(R.id.temp_max).text = data.tempMax
        findViewById<TextView>(R.id.sunrise).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(data.sunrise * 1000))
        findViewById<TextView>(R.id.sunset).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(data.sunset * 1000))
        findViewById<TextView>(R.id.wind).text = data.windSpeed
        findViewById<TextView>(R.id.pressure).text = data.pressure
        findViewById<TextView>(R.id.humidity).text = data.humidity

        showMainScreen()
    }

    private fun showMainScreen() {
        /* Make the main design visible */
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE
    }

}