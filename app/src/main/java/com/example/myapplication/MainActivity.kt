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


    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) = runBlocking{
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loading()

        GlobalScope.launch(Dispatchers.IO) {
            val a = fetchData()   //fetch json data and return a dataclass
            updateData(a)         //use the dataclass to populate the objects
        }.join() //wait for the job to finish

        showMainScreen()
    }



    private fun String.getRawDataCoroutine(): String { //get raw json data
        var json: String
        Log.d(TAG, ".getRawDataCoroutine called")
        try {
            json = URL(this).readText(Charsets.UTF_8)
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

    private fun fetchData(): DataClass { //fetch json data and return a dataclass
        val json: String = url.getRawDataCoroutine()
        return getJsonDataCoroutine(json)
    }

    private fun updateData(topic: DataClass) { //use the dataclass to populate the objects

        /* Populating extracted data into our views */
        ("Updated at: " + SimpleDateFormat(
            "$dateFormat $timeFormat",
            Locale.ENGLISH
        ).format(Date(topic.dt * 1000))).also { updated_at.text = it }
        "${topic.main.temp}°C".also { temp.text = it }

        "${topic.name}, ${topic.sys.country}".also { address.text = it }

        status.text = topic.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase( //this is done to capitalize first letter
                Locale.getDefault()
            ) else it.toString()
        }
        ("Min Temp: " + topic.main.temp_min.toString() + "°C").also { temp_min.text = it }
        ("Max Temp: " + topic.main.temp_max.toString() + "°C").also { temp_max.text = it }
        sunrise.text =
            SimpleDateFormat(timeFormat, Locale.ENGLISH).format(Date(topic.sys.sunrise * 1000))
        sunset.text =
            SimpleDateFormat(timeFormat, Locale.ENGLISH).format(Date(topic.sys.sunset * 1000))
        wind.text = topic.wind.speed.toString()
        pressure.text = topic.main.pressure.toString()
        humidity.text = topic.main.humidity.toString()

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
