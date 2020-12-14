package com.example.glucose_tracker

import com.example.glucose_tracker.data.model.LogItem
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val list = mutableListOf(
                LogItem(1, 0, null, null, 0, null, null, DateTime("2020-12-09T20:00:30.740+02:00")),
                LogItem(2, 0, null, null, 0, null, null, DateTime("2020-12-09T07:20:30.740+02:00")),
                LogItem(3, 0, null, null, 0, null, null, DateTime("2020-12-09T13:00:30.740+02:00")),
                LogItem(4, 0, null, null, 0, null, null, DateTime("2020-12-11T13:10:30.740+02:00")),
                LogItem(5, 0, null, null, 0, null, null, DateTime("2020-12-11T08:20:30.740+02:00")),
        )
        list.shuffle()
        list.sortByDescending { it.dateTime.toLocalDate() }

        println("before")
        list.forEach { println(it.dateTime.toString("yyyy-MM-dd HH:mm")) }
        println("------")

        /*
        list.sortByDescending { it.dateTime.toLocalDate() }
        println("sort by date")
        list.forEach {
            print(it.id)
            print(" ")
            println(it.dateTime.toString("yyyy-MM-dd HH:mm"))
        }
        println("------")
        */

        list.sortWith(object : Comparator<LogItem> {
            override fun compare(a: LogItem?, b: LogItem?): Int {
                val d1 = a?.dateTime?.toLocalDate()
                val d2 = b?.dateTime?.toLocalDate()

                val compareDates = d1?.compareTo(d2)
                if (compareDates == 0) {
                    val t1 = a.dateTime.toLocalTime()
                    val t2 = b?.dateTime?.toLocalTime()
                    return t1?.compareTo(t2) ?: 0
                }
                return 0
            }
        })

        list.forEach {
            print(it.id)
            print(" ")
            println(it.dateTime.toString("yyyy-MM-dd HH:mm"))
        }

        assertEquals(4, 2 + 2)
    }
}