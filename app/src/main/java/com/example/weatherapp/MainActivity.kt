package com.example.weatherapp

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.NumberFormatException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var clMain: RelativeLayout
    lateinit var background: GradientDrawable
    lateinit var background2: GradientDrawable

    lateinit var box1: LinearLayout
    lateinit var box2: LinearLayout
    lateinit var box3: LinearLayout
    lateinit var box4: LinearLayout
    lateinit var box5: LinearLayout
    lateinit var box6: LinearLayout


    lateinit var addressText: TextView
    lateinit var updatedAt: TextView
    lateinit var status: TextView
    lateinit var temp: TextView
    lateinit var lowText: TextView
    lateinit var highText: TextView
    lateinit var sunriseText: TextView
    lateinit var sunsetText: TextView
    lateinit var windText: TextView
    lateinit var humidityText: TextView
    lateinit var pressureText: TextView
    lateinit var feelsLikeText: TextView

    var id = 4140963
    var newId = 0
    var text = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //UI elements
        addressText = findViewById(R.id.addressText)
        updatedAt = findViewById(R.id.updatedAt)
        status = findViewById(R.id.status)
        temp = findViewById(R.id.temp)
        lowText = findViewById(R.id.lowText)
        highText = findViewById(R.id.highText)
        sunriseText = findViewById(R.id.sunriseText)
        sunsetText = findViewById(R.id.sunsetText)
        windText = findViewById(R.id.windText)
        humidityText = findViewById(R.id.humidityText)
        pressureText = findViewById(R.id.pressureText)
        feelsLikeText = findViewById(R.id.feelsLikeText)


        //UI linear boxes
        box1 = findViewById(R.id.box1)
        box2 = findViewById(R.id.box2)
        box3 = findViewById(R.id.box3)
        box4 = findViewById(R.id.box4)
        box5 = findViewById(R.id.box5)
        box6 = findViewById(R.id.box6)

        //menu
        clMain = findViewById(R.id.clMain)
        background = ResourcesCompat.getDrawable(
            clMain.resources,
            R.drawable.bg_gradient,
            null
        ) as GradientDrawable

        background2 = ResourcesCompat.getDrawable(
            clMain.resources,
            R.drawable.bg_gradient2,
            null
        ) as GradientDrawable

        background.mutate() // Mutate the drawable so changes don't affect every other drawable

        background2.mutate()

        requestAPI(id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.other -> {
                customAlert()
            }

            R.id.NY -> {
                //alert
                requestAPI(5128581)
            }
            R.id.mekkah -> {
                requestAPI(104515)
            }
            R.id.riyadh -> {
                requestAPI(108410)
            }
            R.id.jeddah -> {
                requestAPI(105343)
            }
            R.id.tabuk -> {
                requestAPI(101628)
            }
            R.id.LA -> {
                requestAPI(5368361)
            }
            R.id.boston -> {
                requestAPI(4930956)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //    for the other option
    private fun customAlert() {
        // first we create a variable to hold an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)
        // then we set up the input
        val input = EditText(this)
        input.setInputType(InputType.TYPE_CLASS_NUMBER)


        // here we set the message of our alert dialog
        dialogBuilder.setMessage("Enter your city ID code:")
        // positive button text and action
        dialogBuilder.setPositiveButton("Search", DialogInterface.OnClickListener {

                dialog, id ->
            //taking the new ID and call an API request

            try {
                text = input.text.toString()
                newId = text.toInt()
            } catch (e: NumberFormatException) {
                //check if input is white space
                if (text.trim() == "") {
                    Snackbar.make(clMain, "Empty city id was entered", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            requestAPI(newId)

        })

            // negative button text and action
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("CITY ID CODE")
        // add the Edit Text

        alert.setView(input)
        // show alert dialog
        alert.show()

    }

    fun requestAPI(id: Int) {
        CoroutineScope(IO).launch {
            val data = async {
                fetchData(id)
            }.await()

            if (data.isNotEmpty()) {
                populateRV(data)
            } else {
                Log.d("MAiN", "Unable to get data")
            }
        }
    }

    private fun fetchData(id: Int): String {
        var response = ""
        try {
            response =
                URL("https://api.openweathermap.org/data/2.5/weather?id=$id&units=metric&appid=8a316bae40ca552c86771c6d73150592").readText()
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE")
        }
        return response
    }

    private suspend fun populateRV(data: String) {
        withContext(Main) {

            val jsonObject = JSONObject(data) //all data including date
            val weather = jsonObject.getJSONArray("weather")
                .getJSONObject(0) // taking the description"Clear sky"
            val description = weather.getString("description") //getting the clear sky

            val sunrise = jsonObject.getJSONObject("sys").getLong("sunrise")
            val sunset = jsonObject.getJSONObject("sys").getLong("sunset")
            val country = jsonObject.getJSONObject("sys").getString("country")

            val pressure = jsonObject.getJSONObject("main").getString("pressure")
            val humidity = jsonObject.getJSONObject("main").getString("humidity")
            val tempreture = jsonObject.getJSONObject("main").getInt("temp")
            val feels_like = jsonObject.getJSONObject("main").getInt("feels_like")
            val temp_min = jsonObject.getJSONObject("main").getInt("temp_min")
            val temp_max = jsonObject.getJSONObject("main").getInt("temp_max")

            val wind = jsonObject.getJSONObject("wind").getDouble("speed")

            val date = jsonObject.getLong("dt")
            val city = jsonObject.getString("name")

            addressText.text = "$city, $country"
            updatedAt.text =
                "updated at: " + SimpleDateFormat("dd/mm/yyyy HH:MM").format(Date(date.toLong() * 1000))
            status.text = description
            temp.text = "${tempreture}°C"
            lowText.text = "Low: $temp_min°C"
            highText.text = "high: $temp_max°C"
            sunriseText.text = SimpleDateFormat("HH:MM").format(Date(sunrise * 1000))
            sunsetText.text = SimpleDateFormat("HH:MM").format(Date(sunset * 1000))
            windText.text = "$wind km/h"
            humidityText.text = "$humidity%"
            pressureText.text = pressure.toString()
            feelsLikeText.text = "$feels_like°C"


            if (tempreture < 20) {
                clMain.background = background
                box1.setBackgroundColor(Color.parseColor("#9ec2e2"))
                box2.setBackgroundColor(Color.parseColor("#9ec2e2"))
                box3.setBackgroundColor(Color.parseColor("#9ec2e2"))
                box4.setBackgroundColor(Color.parseColor("#9ec2e2"))
                box5.setBackgroundColor(Color.parseColor("#9ec2e2"))
                box6.setBackgroundColor(Color.parseColor("#9ec2e2"))
            } else {
                clMain.background = background2
                box1.setBackgroundColor(Color.parseColor("#fcf2c6"))
                box2.setBackgroundColor(Color.parseColor("#fcf2c6"))
                box3.setBackgroundColor(Color.parseColor("#fcf2c6"))
                box4.setBackgroundColor(Color.parseColor("#fcf2c6"))
                box5.setBackgroundColor(Color.parseColor("#fcf2c6"))
                box6.setBackgroundColor(Color.parseColor("#fcf2c6"))

            }
        }
    }
}