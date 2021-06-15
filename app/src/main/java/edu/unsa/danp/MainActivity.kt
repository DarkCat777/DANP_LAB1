package edu.unsa.danp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var accelerometerSensor: Sensor
    private lateinit var magnetometerSensor: Sensor
    private val accelerometer = Sensor.TYPE_ACCELEROMETER
    private val magnetometer = Sensor.TYPE_MAGNETIC_FIELD
    private lateinit var sensorManager: SensorManager
    private lateinit var textViewAngle: TextView
    private lateinit var mAccelerometer: FloatArray
    private lateinit var mMagnetometer: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewAngle = findViewById(R.id.text)
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.accelerometerSensor = sensorManager.getDefaultSensor(accelerometer)!!
        this.magnetometerSensor = sensorManager.getDefaultSensor(magnetometer)!!
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (this::mAccelerometer.isInitialized and this::mMagnetometer.isInitialized) {
            val R = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, null, mAccelerometer, mMagnetometer)
            if (success) {
                val orientation = FloatArray(3) // [Z, X, Y]
                SensorManager.getOrientation(R, orientation)
                if (::textViewAngle.isInitialized) {
                    textViewAngle.text =
                        orientation.toList().map { (Math.toDegrees(it.toDouble()) * 100).roundToInt() / 100.0 }.toString()
                }
                //Log.i("ORIENTATION: ", orientation.toList().map { Math.toDegrees(it.toDouble()) }.toString())
            }
        }
        when (event!!.sensor.type) {
            accelerometer -> {
                mAccelerometer = event.values.clone()
            }
            magnetometer -> {
                mMagnetometer = event.values.clone()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        this.accelerometerSensor.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        this.magnetometerSensor.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        this.sensorManager.unregisterListener(this)
    }
}