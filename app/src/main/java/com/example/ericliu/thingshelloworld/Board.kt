package com.example.ericliu.thingshelloworld

/**
 * Created by ericliu on 5/6/17.
 */

enum class RGB { RED, GREEN, BLUE }

fun getGPIOforLED(color: RGB): String {
    return when (color) {
        RGB.GREEN -> "IO13"
        RGB.BLUE -> "IO8"
        RGB.RED -> "IO12"
    }
}