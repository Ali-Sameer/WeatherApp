package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.room.util.query
import com.ApiInterface
import com.WeatherApp
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//23a90c8a45f6837de2ad415bbc68ca09
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy{
      ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("mumbai")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityname:String) {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityname,"23a90c8a45f6837de2ad415bbc68ca09","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
              val  responseBody = response.body()
                if(response.isSuccessful && responseBody !=null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text = "$temperature°C"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: $maxTemp°C"
                    binding.mintemp.text = "Min Temp: $minTemp°C"
                    binding.humidity.text =  "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.seallevel.text = "$sealLevel"
                    binding.condintion.text = condition
                    binding.day.text =day(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityname.text ="$cityname"


                   // Log.d("TAG","onResponse: $temperature")

                    changeImagesAccordingToWeatherCondition(condition)
                }
            }


            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })



    }

    private fun changeImagesAccordingToWeatherCondition(conditions: String) {
       when(conditions){




           "Clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }


           "Haze","Clouds" ->{
               binding.root.setBackgroundResource(R.drawable.cloud)
               binding.lottieAnimationView.setAnimation(R.raw.aaaa)
           }

           "Rain" ->{
               binding.root.setBackgroundResource(R.drawable.rain)
               binding.lottieAnimationView.setAnimation(R.raw.ranny)
           }

           "Snow" ->{
               binding.root.setBackgroundResource(R.drawable.snow)
               binding.lottieAnimationView.setAnimation(R.raw.snowing)
           }

           else->{

               binding.root.setBackgroundResource(R.drawable.sunny)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }


       }

        binding.lottieAnimationView.playAnimation()
    }


    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy",Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:MM",Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }



    fun day(timeStamp: Long): String{
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format((Date()))
    }
}