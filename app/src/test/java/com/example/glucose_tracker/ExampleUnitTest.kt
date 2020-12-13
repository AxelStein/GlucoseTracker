package com.example.glucose_tracker

import org.joda.time.LocalDate
import org.joda.time.LocalTime
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
        /*
        var list = listOf(
                LogItem(1, 0, null, null, 0, null, null, "2020-12-09T20:00:30.740+02:00", ""),
                LogItem(2, 0, null, null, 0, null, null, "2020-12-09T07:20:30.740+02:00", ""),
                LogItem(2, 0, null, null, 0, null, null, "2020-12-09T13:00:30.740+02:00", ""),
                LogItem(3, 0, null, null, 0, null, null, "2020-12-11T13:10:30.740+02:00", ""),
                LogItem(4, 0, null, null, 0, null, null, "2020-12-11T08:20:30.740+02:00", ""),
        )
        list = list.shuffled()

        val comp = Comparator<LogItem> { a, b ->
            DateTime(a?.date).toLocalTime().compareTo(DateTime(b?.date).toLocalTime())
        }.thenComparing { a, b ->
            DateTime(b?.date).toLocalDate().compareTo(DateTime(a?.date).toLocalDate())
        }

        val sortedList = list.sortedWith(comp)
        sortedList.forEach {
            val dateTime = DateTime(it.date)
            val date = dateTime.toDate()
            val time = dateTime.toLocalTime()
            println("date=$date time=$time")
        }
        */
        val date = LocalDate("2020-12-12")
        val time = LocalTime("17:20")
        println(date.toString("yyyy-MM-dd.SSSZZ"))
        //var dateTime = DateTime().withDate(date).withTime(time)
        //println(dateTime)
        assertEquals(4, 2 + 2)
    }
}