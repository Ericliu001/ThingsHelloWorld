package com.example.ericliu.thingshelloworld

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService

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
        ledGpioGreen = service.openGpio(getGPIOforLED(RGB.GREEN))
        ledGpioBlue = service.openGpio(getGPIOforLED(RGB.BLUE))
        ledGpioRed = service.openGpio(getGPIOforLED(RGB.RED))

        ledGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        ledGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        viewRefreshHandler.executePerSecond(TimerRunnable(this@MainActivity, Bundle.EMPTY))
    }

    private inner class TimerRunnable : ViewRefreshHandler.ViewRunnable<Context> {

        constructor(view: Context, args: Bundle) : super(view, args)


        private var mLedState = false

        override fun run(view: Context, args: Bundle) {
            mLedState = !mLedState
            ledGpioGreen.value = mLedState
            ledGpioBlue.value = mLedState
            ledGpioRed.value = mLedState
        }

    }
}
