package com.example.myapplication

import android.os.AsyncTask
import android.util.Log
import java.net.URL

private const val TAG = "GetRawData"

class GetRawData(private val listener: GetRawDataInterface) : AsyncTask<String, Void, String>() {

    interface GetRawDataInterface {
        fun loading()
        fun onDownloadComplete(result: String?)
        fun showError()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        /* Showing the ProgressBar, Making the main design GONE */
        listener.loading()
    }

    override fun doInBackground(vararg params: String?): String? {
        Log.d(TAG, "doInBackground starts")
        var response: String?
        try {
            response = URL(params[0]).readText(Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            response = null
            listener.showError()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        listener.onDownloadComplete(result)
    }
}