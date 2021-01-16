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
import com.axel_stein.glucose_tracker.ui.edit_glucose.EditGlucoseViewModelImpl
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
        appSettings.setGlucoseUnits("mmol_l")

        appResources = AppResources(context, appSettings)
    }

    @After
    fun closeDb() {
        dao.deleteAll()
        db.close()
    }

    @Test
    fun testSave() {
        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("3.5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyLiveData().value ?: false)
        assertNull(vm.errorSaveLiveData().value)
        assertTrue(vm.actionFinishLiveData().value ?: false)
    }

    @Test
    fun testSave_glucoseInvalid() {
        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("3,5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyLiveData().value ?: false)
        assertNull(vm.errorSaveLiveData().value)
        assertTrue(vm.actionFinishLiveData().value ?: false)
    }

    @Test
    fun testSave_glucoseNegative() {
        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setGlucose("-3,5")
        vm.setMeasured(0)
        vm.save()

        assertFalse(vm.errorGlucoseEmptyLiveData().value ?: false)
        assertNull(vm.errorSaveLiveData().value)
        assertTrue(vm.actionFinishLiveData().value ?: false)
    }

    @Test
    fun testSave_glucoseEmpty() {
        val vm = createVieModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setMeasured(0)
        vm.save()

        assertTrue(vm.errorGlucoseEmptyLiveData().value ?: false)
        assertNull(vm.actionFinishLiveData().value)
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
        assertTrue(dao.getAll().isEmpty())
        dao.insert(createLog(2, "2021", "01", "10")).subscribe()

        val items = dao.getAll()
        assertFalse(items.isEmpty())

        val vm = createVieModel(items[0].id)
        assertNull(vm.errorLoadingLiveData().value)
        assertEquals("5.0", vm.getGlucoseValue())
        assertEquals(2021, vm.getCurrentDateTime().toLocalDate().year)
        assertEquals(1, vm.getCurrentDateTime().toLocalDate().monthOfYear)
        assertEquals(10, vm.getCurrentDateTime().toLocalDate().dayOfMonth)
        assertEquals(2, vm.getMeasured())
    }

    @Test
    fun testLoad_error() {
        dao.insert(createLog(2, "2021", "01", "10", "15", "30")).subscribe()
        val vm = createVieModel(2L)  // incorrect id
        assertNotNull(vm.errorLoadingLiveData().value)
        assertTrue(vm.errorLoadingLiveData().value ?: false)
    }

    @Test
    fun testDelete() {
        dao.insert(createLog()).subscribe()

        val log = dao.getAll()[0]
        val vm = createVieModel(log.id)
        vm.delete()

        assertTrue(vm.actionFinishLiveData().value ?: false)
        assertTrue(dao.getAll().isEmpty())
    }

    private fun createVieModel(id: Long = 0L): EditGlucoseViewModelImpl {
        return EditGlucoseViewModelImpl(id).apply {
            setDao(dao)
            setAppResources(appResources)
            setAppSettings(appSettings)
            loadData()
        }
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