package com.example.myapplication

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "GetJsonData"

class GetJsonData(private val listener: GetJsonDataInterface) :
    AsyncTask<String, Void, Data>() { //result is ArrayList<Photo>
//this class is created to do Parsing of JSON data

    interface GetJsonDataInterface {
        fun onDataAvailable(data: Data)
        fun showError()
    }

    override fun doInBackground(vararg params: String?): Data {
        Log.d(TAG, "doInBackground called")
        val data = Data()  //used for holding JSON data
        try {
            /* Extracting JSON returns from the API */
            val jsonObj = JSONObject(params[0])
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val updatedAt: Long = jsonObj.getLong("dt")

            //assign values to the data object
            data.updatedAtText = "Updated at: " + SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH
            ).format(Date(updatedAt * 1000))
            data.temp = main.getString("temp") + "°C"
            data.tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            data.tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            data.pressure = main.getString("pressure")
            data.humidity = main.getString("humidity")
            data.sunrise = sys.getLong("sunrise")
            data.sunset = sys.getLong("sunset")
            data.windSpeed = wind.getString("speed")
            data.weatherDescription = weather.getString("description")
            data.address =
                jsonObj.getString("name") + ", " + sys.getString("country")

        } catch (e: Exception) {
            e.printStackTrace()
            listener.showError()
        }
        return data
    }

    override fun onPostExecute(result: Data) {
        super.onPostExecute(result)
        listener.onDataAvailable(result)
    }
}