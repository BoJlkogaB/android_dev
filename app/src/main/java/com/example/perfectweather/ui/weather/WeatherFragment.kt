package com.example.perfectweather.ui.weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.perfectweather.BuildConfig
import com.example.perfectweather.MainActivity
import com.example.perfectweather.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_weather.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Parcelize
data class Weather (
    var weather: String,
    var weatherDescription: String,
    var temperature: String,
    var humidity: String,
    var pressure: String,
    var windspeed: String,
    var clouds: String,
    var region: String
): Parcelable

class WeatherFragment : Fragment() {

    private lateinit var weatherViewModel: WeatherViewModel

    var okHttpClient: OkHttpClient = OkHttpClient()
    val APP_PREFERENCES = "WeatherApp"
    val APP_PREFERENCES_CurrentCity = ""
    lateinit var weatherParams: Weather

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        weatherViewModel =
            ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_weather, container, false)

        val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val URL = "http://api.openweathermap.org/data/2.5/weather?q=" + mSettings?.getString(
            APP_PREFERENCES_CurrentCity,
            ""
        ) + "&units=metric&lang=ru&APPID="+BuildConfig.API_KEY;
        APIData(URL, root)

        return root
    }

    private fun APIData(URL: String, root: View) {
        val request: Request = Request.Builder().url(URL).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call?, response: Response?) {
                val json = response?.body()?.string()

                if ((JSONObject(json).get("name")).toString() != "")  {
                    var weather =
                        JSONObject(JSONObject(json).getJSONArray("weather")[0].toString()).getString(
                            "main"
                        )
                    var weatherDescription =
                        JSONObject(JSONObject(json).getJSONArray("weather")[0].toString()).getString(
                            "description"
                        )
                    var temperature =
                        JSONObject(JSONObject(json).getString("main")).getString("temp").split(".")[0]
                    var humidity = JSONObject(JSONObject(json).getString("main")).getString("humidity")
                    var pressure = JSONObject(JSONObject(json).getString("main")).getString("pressure")
                    var windspeed = JSONObject(JSONObject(json).getString("wind")).getString("speed")
                    var clouds = JSONObject(JSONObject(json).getString("clouds")).getString("all")
                    var region = JSONObject(json).getString("name")

                    weatherParams = Weather(weather, weatherDescription, temperature, humidity, pressure, windspeed, clouds, region)

                    getActivity()?.runOnUiThread {
                        Region_field.text = weatherParams.region
                        Temperature_field.text = weatherParams.temperature + "°"
                        WeatherDescription_field.text = weatherParams.weatherDescription
                        Humidity_field.text = "Влажность: " + weatherParams.humidity + "%"
                        Pressure_field.text = "Давление: "+weatherParams.pressure + " мм рт. ст."
                        WindSpeed_field.text = "Скорость ветра: "+weatherParams.windspeed + "м/с"
                        Clouds_field.text = "Облачность: "+weatherParams.clouds + "%"

                        when(weather) {
                            "Thunderstorm" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.thunder).into(imageView1)
                                SendNotify("Тор опять куёт что-то?", R.drawable.thunder)
                            }
                            "Drizzle" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.rainy).into(imageView1)
                                SendNotify("Хочется плакать...", R.drawable.rainy)
                            }
                            "Rain" ->  {
                                Glide.with(this@WeatherFragment).load(R.drawable.rainy).into(imageView1)
                                SendNotify("Кажется дождик начинается", R.drawable.rainy)
                            }
                            "Snow" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.snowy).into(imageView1)
                                SendNotify("Ура! Идём строить снеговика?", R.drawable.snowy)
                            }
                            "Clouds" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.cloudy).into(imageView1)
                                SendNotify("Ты тоже на небе видешь проплывающего котика?", R.drawable.cloudy)
                            }
                            "Clear" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.sunny).into(imageView1)
                                SendNotify("...городу две тысячи лет, Прожитых под светом Звезды по имени Солнце", R.drawable.sunny)
                            }
                            "Fog" -> {
                                Glide.with(this@WeatherFragment).load(R.drawable.silent).into(imageView1)
                                SendNotify("Опять настройки тумана выкрутили на максимум?", R.drawable.silent)
                            }
                        }
                    }

                } else Toast.makeText(
                    activity,
                    getString(R.string.connection_error_parse),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SendNotify(Text: String, Pic: Int) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel("PerfectChannel", "Напоминание", importance)
        val notification: Notification = Notification.Builder(requireActivity().getApplicationContext())
            .setContentTitle(weatherParams.temperature)
            .setContentText(Text)
            .setLargeIcon(
                BitmapFactory.decodeResource(getResources(),
                Pic))
            .setSmallIcon(R.drawable.ic_cloud_black_24dp)
            .setChannelId("PerfectChannel")
            .build()

        val mNotificationManager =
            NotificationManagerCompat.from(requireActivity().getApplicationContext())
        mNotificationManager.createNotificationChannel(mChannel)
        mNotificationManager.notify(101, notification)
    }
}