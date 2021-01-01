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
        glucoseDao.insert(createGlucoseLog("2020", "12")).subscribe()

        logDao.getYears().test()
            .assertValue { it.size == 1 }
            .assertValue { it[0] == "2020" }

        glucoseDao.insert(createGlucoseLog("2021", "01")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "11")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "09")).subscribe()

        logDao.getYears().test()
            .assertValue { it.size == 2 }
            .assertValue { it == listOf("2021", "2020") }

        glucoseDao.insert(createGlucoseLog("2019", "06")).subscribe()

        logDao.getYears().test()
            .assertValue { it.size == 3 }
            .assertValue { it == listOf("2021", "2020", "2019") }
    }

    @Test
    fun testGetMonths() {
        glucoseDao.insert(createGlucoseLog("2020", "12")).subscribe()

        logDao.getMonths("2020").test().assertValue { it.size == 1 }

        glucoseDao.insert(createGlucoseLog("2021", "01")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "11")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "09")).subscribe()

        logDao.getMonths("2020").test().assertValue { it.size == 3 }
        logDao.getMonths("2021").test().assertValue { it.size == 1 }

        // test no data
        logDao.getMonths("2019").test().assertValue { it.isEmpty() }

        // test empty year
        logDao.getMonths("").test().assertValue { it.isEmpty() }
    }

    @Test
    fun testGetItems() {
        glucoseDao.insert(createGlucoseLog("2020", "12")).subscribe()
        glucoseDao.insert(createGlucoseLog("2021", "01")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "12")).subscribe()
        glucoseDao.insert(createGlucoseLog("2020", "11")).subscribe()
        glucoseDao.insert(createGlucoseLog("2019", "09")).subscribe()

        logDao.getItems("2020-12").test().assertValue { it.size == 2 }
        logDao.getItems("2020-11").test().assertValue { it.size == 1 }
        logDao.getItems("2021-01").test().assertValue { it.size == 1 }

        // test empty yearMonth
        logDao.getItems("").test().assertValue { it.isEmpty() }

        // test without month
        logDao.getItems("2020").test().assertValue { it.isEmpty() }

        // test no data
        logDao.getItems("2021-02").test().assertValue { it.isEmpty() }

        // test incorrect format
        logDao.getItems("2021-01-03").test().assertValue { it.isEmpty() }
    }

    private fun createGlucoseLog(year: String, month: String): GlucoseLog {
        return GlucoseLog(4f, 4, 0,
            DateTime("$year-$month-01T16:39:17.183+02:00")
        )
    }
}