package com.axel_stein.glucose_tracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseViewModel
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditGlucoseViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var db: AppDatabase
    private lateinit var glucoseDao: GlucoseLogDao
    private lateinit var appSettings: AppSettings

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        glucoseDao = db.glucoseLogDao()
        appSettings = AppSettings(context)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testSave() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = EditGlucoseViewModel(dao = glucoseDao, appSettings = appSettings)
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("3.5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyObserver().value ?: false)
        assertNull(vm.errorSaveObserver().value)
        assertTrue(vm.actionFinishObserver().value ?: false)
    }

    @Test
    fun testSave_glucoseEmpty() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = EditGlucoseViewModel(dao = glucoseDao, appSettings = appSettings)
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setMeasured(0)
        vm.save()

        assertTrue(vm.errorGlucoseEmptyObserver().value ?: false)
        assertNull(vm.actionFinishObserver().value)
    }

    @Test
    fun testDelete() {
        appSettings.setGlucoseUnits("mmol_l")

        glucoseDao.insert(createGlucoseLog("2021", "01")).subscribe()

        val items = glucoseDao.get()
        assertFalse(items.isEmpty())

        val vm = EditGlucoseViewModel(id = items[0].id, dao = glucoseDao, appSettings = appSettings)
        assertEquals("5.0", vm.getGlucoseValue())
        vm.delete()

        assertTrue(vm.actionFinishObserver().value ?: false)
        assertTrue(glucoseDao.get().isEmpty())
    }

    private fun createGlucoseLog(year: String, month: String): GlucoseLog {
        return GlucoseLog(5f, 90, 0,
            DateTime("$year-$month-01T16:39:17.183+02:00")
        )
    }
}