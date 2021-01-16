package com.axel_stein.glucose_tracker.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogDaoTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var glucoseDao: GlucoseLogDao
    private lateinit var logDao: LogDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        logDao = db.logDao()
        glucoseDao = db.glucoseLogDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testGetYears() {
        glucoseDao.insert(createGlucoseLog("2020", "12"))

        logDao.getYears().test()
            .assertValue { it.size == 1 }
            .assertValue { it[0] == "2020" }

        glucoseDao.insert(createGlucoseLog("2021", "01"))
        glucoseDao.insert(createGlucoseLog("2020", "11"))
        glucoseDao.insert(createGlucoseLog("2020", "09"))

        logDao.getYears().test()
            .assertValue { it.size == 2 }
            .assertValue { it == listOf("2021", "2020") }

        glucoseDao.insert(createGlucoseLog("2019", "06"))

        logDao.getYears().test()
            .assertValue { it.size == 3 }
            .assertValue { it == listOf("2021", "2020", "2019") }
    }

    @Test
    fun testGetMonths() {
        glucoseDao.insert(createGlucoseLog("2020", "12"))

        logDao.getMonths("2020").test().assertValue { it.size == 1 }

        glucoseDao.insert(createGlucoseLog("2021", "01"))
        glucoseDao.insert(createGlucoseLog("2020", "11"))
        glucoseDao.insert(createGlucoseLog("2020", "09"))

        logDao.getMonths("2020").test().assertValue { it.size == 3 }
        logDao.getMonths("2021").test().assertValue { it.size == 1 }

        // test no data
        logDao.getMonths("2019").test().assertValue { it.isEmpty() }

        // test empty year
        logDao.getMonths("").test().assertValue { it.isEmpty() }
    }

    @Test
    fun testGetItems() {
        glucoseDao.insert(createGlucoseLog("2020", "12"))
        glucoseDao.insert(createGlucoseLog("2021", "01"))
        glucoseDao.insert(createGlucoseLog("2020", "12"))
        glucoseDao.insert(createGlucoseLog("2020", "11"))
        glucoseDao.insert(createGlucoseLog("2019", "09"))

        assertEquals(2, logDao.getLogsByYearMonth("2020-12").size)
        assertEquals(1, logDao.getLogsByYearMonth("2020-11").size)
        assertEquals(1, logDao.getLogsByYearMonth("2020-01").size)

        // test empty yearMonth
        assertTrue(logDao.getLogsByYearMonth("").isEmpty())

        // test without month
        assertTrue(logDao.getLogsByYearMonth("2020").isEmpty())

        // test no data
        assertTrue(logDao.getLogsByYearMonth("2021-02").isEmpty())

        // test incorrect format
        assertTrue(logDao.getLogsByYearMonth("2021-02-03").isEmpty())
    }

    private fun createGlucoseLog(year: String, month: String): GlucoseLog {
        return GlucoseLog(4f, 4, 0,
            DateTime("$year-$month-01T16:39:17.183+02:00")
        )
    }
}