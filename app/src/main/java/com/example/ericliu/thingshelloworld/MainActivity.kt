package com.example.ericliu.thingshelloworld

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val ledGpioGreen = service.openGpio("BCM6")
 * ledGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * ledGpioGreen.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private lateinit var viewRefreshHandler: ViewRefreshHandler

    private val gpioList: MutableList<Gpio> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewRefreshHandler = ViewRefreshHandler()

        val service = PeripheralManagerService()

        try {

            RGB.values().forEach { color ->
                var gpio = service.openGpio(getGPIOforLED(color))
                gpioList.add(gpio)
                gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            }

            viewRefreshHandler.executePeriodically(TimerRunnable(this@MainActivity, Bundle.EMPTY), 200)

        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    private inner class TimerRunnable(view: Context, args: Bundle) : ViewRefreshHandler.ViewRunnable<Context>(view, args) {


        private var index: Int = 0

        override fun run(view: Context, args: Bundle) {
            gpioList.get(index).value = !gpioList.get(index).value
            index = (++ index)% gpioList.size
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            gpioList.forEach { gpio ->
                gpio.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }
}
