package com.axel_stein.glucose_tracker

import junit.framework.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        val value = 24.9f
        val result = when {
            value < 16f -> 0
            value < 17f -> 1
            value < 18.5f -> 2
            value < 25f -> 3
            value < 30f -> 4
            value < 35f -> 5
            value < 40f -> 6
            else -> 7
        }
        println(result)
        assertTrue(4 == 2+2)
    }
}