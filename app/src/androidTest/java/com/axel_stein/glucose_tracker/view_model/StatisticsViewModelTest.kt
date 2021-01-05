package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.StatsDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.statistics.StatisticsViewModel
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: StatsDao
    private lateinit var glucoseDao: GlucoseLogDao
    private lateinit var a1cDao: A1cLogDao
    private lateinit var appSettings: AppSettings
    private lateinit var appResources: AppResources

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        dao = db.statsDao()
        glucoseDao = db.glucoseLogDao()
        a1cDao = db.a1cDao()

        appSettings = AppSettings(context)
        appResources = AppResources(context, appSettings)
    }

    @After
    fun closeDb() {
        glucoseDao.deleteAll()
        db.close()
    }

    @Test
    fun testNoData() {
        val vm = StatisticsViewModel(dao, glucoseDao, a1cDao, appSettings, appResources)
        assertNull(vm.statsLiveData().value)
        assertNull(vm.diabetesControlLiveData().value)
        assertFalse(vm.showErrorLiveData().value ?: true)
    }

    @Test
    fun testControlGood() {
        glucoseDao.insert(createLog(4f)).subscribe()

        val vm = StatisticsViewModel(dao, glucoseDao, a1cDao, appSettings, appResources)
        assertNotNull(vm.statsLiveData().value)
        assertNotNull(vm.diabetesControlLiveData().value)
        assertEquals(0, vm.diabetesControlLiveData().value)
        assertFalse(vm.showErrorLiveData().value ?: true)
    }

    @Test
    fun testControlAvg() {
        glucoseDao.insert(createLog(8f)).subscribe()

        val vm = StatisticsViewModel(dao, glucoseDao, a1cDao, appSettings, appResources)
        assertNotNull(vm.statsLiveData().value)
        assertNotNull(vm.diabetesControlLiveData().value)
        assertFalse(vm.showErrorLiveData().value ?: true)
    }

    @Test
    fun testControlBad() {
        glucoseDao.insert(createLog(10f)).subscribe()

        val vm = StatisticsViewModel(dao, glucoseDao, a1cDao, appSettings, appResources)
        assertNotNull(vm.statsLiveData().value)
        assertNotNull(vm.diabetesControlLiveData().value)
        assertFalse(vm.showErrorLiveData().value ?: true)
    }

    private fun createLog(mmol: Float = 4f): GlucoseLog {
        return GlucoseLog(mmol, 90, 0, DateTime())
    }
}