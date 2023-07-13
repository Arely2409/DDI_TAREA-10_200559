package edu.mx.ti.idgs.ddi.tarea07_200559.presentation

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import edu.mx.ti.idgs.ddi.tarea07_200559.R

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var clockTextView: TextView
    private lateinit var saludoTextView: TextView
    private lateinit var temperaturaTextView: TextView
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable

    companion object {
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
        private const val API_KEY = "9267c661bf2cd2cb12228829ef8f7522"
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockTextView = findViewById(R.id.clockTextView)
        saludoTextView = findViewById(R.id.saludar)
        temperaturaTextView = findViewById(R.id.temperaturaTextView)

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val saludo: String = when(hourOfDay) {
            in 6..11 -> "Buenos días profe"
            in 12..18 -> "Buenas tardes profe"
            else -> "Buenas noches profe"
        }
        saludoTextView.text = saludo

        handler = Handler()
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentTime = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = dateFormat.format(currentTime)
                clockTextView.text = formattedTime

                obtenerTemperatura()

                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun obtenerTemperatura() {

        val latitud = 28.5273273
        val longitud = 77.1265842


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(WeatherService::class.java)

        val call = apiService.getWeather(latitud, longitud, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val temperatura = weatherResponse?.main?.temp
                    temperaturaTextView.text = "$temperatura °C"
                } else {
                    Log.e("API_ERROR", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error en la solicitud: ${t.message}")
            }
        })
    }
}
