package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
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
    private lateinit var dao: GlucoseLogDao
    private lateinit var appSettings: AppSettings
    private lateinit var appResources: AppResources

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.glucoseLogDao()
        appSettings = AppSettings(context)
        appResources = AppResources(context, appSettings)
    }

    @After
    fun closeDb() {
        dao.deleteAll()
        db.close()
    }

    @Test
    fun testSave() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = createVieModel()
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
    fun testSave_glucoseInvalid() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("3,5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyObserver().value ?: false)
        assertNull(vm.errorSaveObserver().value)
        assertTrue(vm.actionFinishObserver().value ?: false)
    }

    @Test
    fun testSave_glucoseNegative() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("-3,5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyObserver().value ?: false)
        assertNull(vm.errorSaveObserver().value)
        assertTrue(vm.actionFinishObserver().value ?: false)
    }

    @Test
    fun testSave_glucoseEmpty() {
        appSettings.setGlucoseUnits("mmol_l")

        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setMeasured(0)
        vm.save()

        assertTrue(vm.errorGlucoseEmptyObserver().value ?: false)
        assertNull(vm.actionFinishObserver().value)
    }

    @Test
    fun testSave_measured() {
        val vm = createVieModel()

        vm.setMeasured(-1)
        assertEquals(0, vm.getMeasured())

        vm.setMeasured(0)
        assertEquals(0, vm.getMeasured())

        vm.setMeasured(12)
        assertEquals(6, vm.getMeasured())
    }

    @Test
    fun testLoad() {
        appSettings.setGlucoseUnits("mmol_l")

        assertTrue(dao.get().isEmpty())
        dao.insert(createLog(2, "2021", "01", "10")).subscribe()

        val items = dao.get()
        assertFalse(items.isEmpty())

        val vm = createVieModel(items[0].id)
        assertNull(vm.errorLoadingObserver().value)
        assertEquals("5.0", vm.getGlucoseValue())
        assertEquals(2021, vm.getCurrentDateTime().toLocalDate().year)
        assertEquals(1, vm.getCurrentDateTime().toLocalDate().monthOfYear)
        assertEquals(10, vm.getCurrentDateTime().toLocalDate().dayOfMonth)
        assertEquals(2, vm.getMeasured())
    }

    @Test
    fun testLoad_error() {
        appSettings.setGlucoseUnits("mmol_l")

        dao.insert(createLog(2, "2021", "01", "10", "15", "30")).subscribe()
        val vm = createVieModel(2L)
        assertNotNull(vm.errorLoadingObserver().value)
        assertTrue(vm.errorLoadingObserver().value ?: false)
    }

    @Test
    fun testDelete() {
        appSettings.setGlucoseUnits("mmol_l")

        dao.insert(createLog()).subscribe()

        val log = dao.get()[0]
        val vm = createVieModel(log.id)
        vm.delete()

        assertTrue(vm.actionFinishObserver().value ?: false)
        assertTrue(dao.get().isEmpty())
    }

    private fun createVieModel(id: Long = 0L): EditGlucoseViewModel {
        return EditGlucoseViewModel(
            id = id,
            dao = dao,
            appSettings = appSettings,
            appResources = appResources
        )
    }

    private fun createLog(
        measured: Int = 0,
        year: String = "2021",
        month: String = "01",
        day: String = "01",
        hours: String = "12",
        minutes: String = "00"
    ): GlucoseLog {
        return GlucoseLog(5f, 90, measured,
            DateTime("$year-$month-${day}T$hours:$minutes:00.000+02:00")
        )
    }
}