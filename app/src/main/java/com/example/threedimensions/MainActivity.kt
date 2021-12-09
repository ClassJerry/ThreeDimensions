package com.example.threedimensions

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.imageResource
import kotlin.math.PI

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : ComponentActivity() {

    companion object {
        private const val SCALE = 1.3f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Display() }
    }

    @Composable
    private fun Display() {
        val imageBack = ImageBitmap.imageResource(R.drawable.back)
        val imageMid = ImageBitmap.imageResource(R.drawable.mid)
        val imageFore = ImageBitmap.imageResource(R.drawable.fore)

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Canvas(Modifier.fillMaxSize().scale(SCALE)) {
            translate(-offsetX, -offsetY) { drawImage(imageBack) }
            drawImage(imageMid)
            translate(offsetX, offsetY) { drawImage(imageFore) }
        }

        val manager = getSystemService(SensorManager::class.java)
        val listener = object : SensorEventListener {

            private val SAMPLING_PERIOD = 0.02f
            private val MAX_RADIAN = (PI / 6).toFloat()
            private val MAX_OFFSET = 100

            private var radianX = 0f
            private var radianY = 0f

            override fun onSensorChanged(event: SensorEvent) {
                radianX += event.values[0] * SAMPLING_PERIOD
                radianY += event.values[1] * SAMPLING_PERIOD

                radianX = radianX.coerceIn(-MAX_RADIAN, MAX_RADIAN)
                radianY = radianY.coerceIn(-MAX_RADIAN, MAX_RADIAN)

                offsetX = radianY * MAX_OFFSET / MAX_RADIAN
                offsetY = radianX * MAX_OFFSET / MAX_RADIAN
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }
        manager.registerListener(
            listener,
            manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_GAME
        )
    }
}