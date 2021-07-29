package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private val TAG = MainActivity::class.java.name
private const val timeFormat = "hh:mm a"
private const val dateFormat = "dd/MM/yyyy"

class MainActivity : AppCompatActivity() {

    private val city = "islamabad,pk"
    private val api = "0e2d77d481bcfa957f3841c0875961a6"
    private val url =
        "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api"


    override fun onCreate(savedInstanceState: Bundle?) = runBlocking{
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loading()

            val job = GlobalScope.launch(Dispatchers.IO) {
                val a = fetchData()   //fetch json data and return a dataclass
                updateData(a)         //use the dataclass to populate the objects
            }.join() //wait for the job to finish

        showMainScreen()
    }



    private fun getRawDataCoroutine(str: String): String { //get raw json data
        var json: String
        Log.d(TAG, ".getRawDataCoroutine called")
        try {
            json = URL(str).readText(Charsets.UTF_8)
//            Log.d(TAG, response)
        } catch (e: Exception) {
            Log.e(TAG, ".getRawDataCoroutine: $e")
            json = ""
            showError()
        }
//        Log.d(TAG, ".getRawDataCoroutine finished --> response: $response")
        return json
    }

    private fun getJsonDataCoroutine(json: String): DataClass { //use gson library to return a proper dataclass
        Log.d(TAG, ".getJsonDataCoroutine called")
        lateinit var gson: DataClass
        try {
            gson =
                Gson().fromJson(
                    json,
                    DataClass::class.java
                )  //using Gson library to extract JSON data
//            Log.d(TAG, topic.toString())

        } catch (e: Exception) {
            Log.e(TAG, ".getJsonDataCoroutine: $e")
            showError()
        }
//        Log.d(TAG, ".getJsonDataCoroutine finished --> topic: $topic1")
        return gson
    }

    private suspend fun fetchData(): DataClass { //fetch json data and return a dataclass
        val json: String = getRawDataCoroutine(url)
        return getJsonDataCoroutine(json)
    }

    private suspend fun updateData(topic: DataClass) { //use the dataclass to populate the objects

        /* Populating extracted data into our views */
        updated_at.text = "Updated at: " + SimpleDateFormat(
            "$dateFormat $timeFormat",
            Locale.ENGLISH
        ).format(Date(topic.dt * 1000))
        temp.text = topic.main.temp.toString() + "°C"

        val tempMin = "Min Temp: " + topic.main.temp_min.toString() + "°C"
        val tempMax = "Max Temp: " + topic.main.temp_max.toString() + "°C"
        val pressure1 = topic.main.pressure.toString()
        val humidity1 = topic.main.humidity.toString()
        val sunrise1 = topic.sys.sunrise
        val sunset1 = topic.sys.sunset
        val windSpeed = topic.wind.speed.toString()
        address.text =
            topic.name + ", " + topic.sys.country

        status.text = topic.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase( //this is done to capitalize first letter
                Locale.getDefault()
            ) else it.toString()
        }
        temp_min.text = tempMin
        temp_max.text = tempMax
        sunrise.text =
            SimpleDateFormat(timeFormat, Locale.ENGLISH).format(Date(sunrise1 * 1000))
        sunset.text =
            SimpleDateFormat(timeFormat, Locale.ENGLISH).format(Date(sunset1 * 1000))
        wind.text = windSpeed
        pressure.text = pressure1
        humidity.text = humidity1

    }

    private fun loading() {
        /* Showing the ProgressBar, Making the main design GONE */
        loader.visibility = View.VISIBLE
        mainContainer.visibility = View.GONE
        errorText.visibility = View.GONE
    }

    private fun showError() {
        /* Make the main design gone, show error message */
        loader.visibility = View.GONE
        mainContainer.visibility = View.GONE
        errorText.visibility = View.VISIBLE
    }

    private fun showMainScreen() {
        /* Make the main design visible */
        loader.visibility = View.GONE
        mainContainer.visibility = View.VISIBLE
        errorText.visibility = View.GONE
//        Log.d(TAG, ".showMainScreen --> ${Thread.currentThread().name}")
    }
}
