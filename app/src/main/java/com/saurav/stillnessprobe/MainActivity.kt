package com.saurav.stillnessprobe

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.slider.Slider
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var lineChart: LineChart
    private lateinit var slider: Slider
    private lateinit var display: TextView
    private lateinit var lineDataSet: LineDataSet
    private var timeElapsed = 0f
    private var scaleFactor = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        slider = findViewById(R.id.slider)
        display = findViewById(R.id.value)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        initLineChart()

        slider.addOnChangeListener { _, value, _ ->
            scaleFactor = value
        }


    }

    private fun initLineChart() {
        lineDataSet = LineDataSet(mutableListOf(), "Acceleration")
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(false)
        lineDataSet.lineWidth = 2f

        lineChart.data = LineData(lineDataSet)
        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        val acceleration = sqrt(
            (event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]).toDouble()
        ).toFloat()

        val vibe = (acceleration)*10000 * scaleFactor
        addEntryToChart(timeElapsed, vibe)
        timeElapsed += 0.1f
        display.text = vibe.toString()
    }

    private fun addEntryToChart(x: Float, y: Float) {
        lineDataSet.addEntry(Entry(x, y))
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.setVisibleXRangeMaximum(50f)
        lineChart.moveViewToX(lineDataSet.entryCount.toFloat())
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not needed for this example
    }
}
