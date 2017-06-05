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
    private lateinit var ledGpioGreen: Gpio
    private lateinit var ledGpioBlue: Gpio
    private lateinit var ledGpioRed: Gpio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewRefreshHandler = ViewRefreshHandler()

        val service = PeripheralManagerService()

        try {
            ledGpioGreen = service.openGpio(getGPIOforLED(RGB.GREEN))
            ledGpioBlue = service.openGpio(getGPIOforLED(RGB.BLUE))
            ledGpioRed = service.openGpio(getGPIOforLED(RGB.RED))

            ledGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            ledGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            ledGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

            viewRefreshHandler.executePerSecond(TimerRunnable(this@MainActivity, Bundle.EMPTY))

        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }

    private inner class TimerRunnable(view: Context, args: Bundle) : ViewRefreshHandler.ViewRunnable<Context>(view, args) {


        private var mLedState = false

        override fun run(view: Context, args: Bundle) {
            mLedState = !mLedState
            ledGpioGreen.value = mLedState
            ledGpioBlue.value = mLedState
            ledGpioRed.value = mLedState
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            ledGpioRed.close()
            ledGpioBlue.close()
            ledGpioGreen.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }
}
