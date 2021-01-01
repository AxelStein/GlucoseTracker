package com.axel_stein.glucose_tracker

import org.junit.Test
import java.text.DecimalFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        val df = DecimalFormat("#.#")
        println(df.format("3.415"))
    }
}