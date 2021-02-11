package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.room.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.repository.LogRepository
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.archive.ArchiveViewModel
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArchiveViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: LogDao
    private lateinit var glucoseDao: GlucoseLogDao
    private lateinit var appSettings: AppSettings
    private lateinit var appResources: AppResources
    private lateinit var logRepository: LogRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        dao = db.logDao()
        glucoseDao = db.glucoseLogDao()

        appSettings = AppSettings(context)
        appResources = AppResources(context)

        logRepository = LogRepository(context, db, dao)
    }

    @After
    fun closeDb() {
        glucoseDao.deleteAll()
        db.close()
    }

    @Test
    fun testNoData() {
        val vm = ArchiveViewModel(repository = logRepository)
        assertEquals("", vm.getCurrentYear())
        assertEquals(-1, vm.getSelectedYear())

        assertEquals(-1, vm.getCurrentMonth())
        assertEquals(-1, vm.getSelectedMonth())
    }

    @Test
    fun testCurrentYear() {
        glucoseDao.insert(createLog(year = "2012"))
        glucoseDao.insert(createLog(year = "2009"))

        val vm = ArchiveViewModel(repository = logRepository)
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(2, vm.yearsLiveData().value?.size)
    }

    @Test
    fun testSelectedYear() {
        glucoseDao.insert(createLog(year = "2012", month = "01"))
        glucoseDao.insert(createLog(year = "2012", month = "02"))
        glucoseDao.insert(createLog(year = "2009", month = "03"))
        glucoseDao.insert(createLog(year = "2009", month = "04"))
        glucoseDao.insert(createLog(year = "2005", month = "05"))
        glucoseDao.insert(createLog(year = "2005", month = "06"))

        val vm = ArchiveViewModel(repository = logRepository)
        assertEquals(2, vm.getCurrentMonth())

        vm.setCurrentYear(1)
        assertEquals("2009", vm.getCurrentYear())
        assertEquals(1, vm.getSelectedYear())

        assertEquals(4, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
    }

    @Test
    fun testDeleteYear() {
        glucoseDao.insert(createLog(year = "2012", month = "01"))
        glucoseDao.insert(createLog(year = "2009", month = "03"))
        glucoseDao.insert(createLog(year = "2005", month = "05"))
        assertEquals(3, glucoseDao.getAll().size)

        val vm = ArchiveViewModel(repository = logRepository)
        vm.setCurrentYear(1)
        assertEquals("2009", vm.getCurrentYear())

        // delete year 2009
        glucoseDao.deleteById(2)
        assertEquals(2, glucoseDao.getAll().size)

        assertEquals(1, vm.getSelectedYear())
        assertEquals("2005", vm.getCurrentYear())

        // delete year 2005
        glucoseDao.deleteById(3)
        assertEquals(1, glucoseDao.getAll().size)

        assertEquals(0, vm.getSelectedYear())
        assertEquals("2012", vm.getCurrentYear())

        // delete year 2012
        glucoseDao.deleteById(1)
        assertTrue(glucoseDao.getAll().isEmpty())

        assertEquals(-1, vm.getSelectedYear())
        assertEquals("", vm.getCurrentYear())
    }

    @Test
    fun testCurrentMonth() {
        glucoseDao.insert(createLog(year = "2021", month = "01"))
        glucoseDao.insert(createLog(year = "2021", month = "02"))
        glucoseDao.insert(createLog(year = "2020", month = "02"))

        val vm = ArchiveViewModel(repository = logRepository)
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
        assertEquals(2, vm.monthsLiveData().value?.size)
    }

    @Test
    fun testSelectedMonth() {
        glucoseDao.insert(createLog(year = "2012", month = "01"))
        glucoseDao.insert(createLog(year = "2012", month = "02"))

        val vm = ArchiveViewModel(repository = logRepository)
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
        assertEquals(2, vm.monthsLiveData().value?.size)

        vm.setCurrentMonth(1)
        assertEquals(1, vm.getCurrentMonth())
        assertEquals(1, vm.getSelectedMonth())
    }

    @Test
    fun testDeleteMonth() {
        glucoseDao.insert(createLog(year = "2012", month = "01"))
        glucoseDao.insert(createLog(year = "2012", month = "02"))
        glucoseDao.insert(createLog(year = "2009", month = "03"))
        glucoseDao.insert(createLog(year = "2009", month = "04"))
        glucoseDao.insert(createLog(year = "2005", month = "05"))
        glucoseDao.insert(createLog(year = "2005", month = "06"))

        val vm = ArchiveViewModel(repository = logRepository)

        // expect year 2012 month 2
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        vm.setCurrentYear(1)
        vm.setCurrentMonth(1)

        // expect year 2009 month 3
        assertEquals("2009", vm.getCurrentYear())
        assertEquals(3, vm.getCurrentMonth())
        assertEquals(1, vm.getSelectedMonth())

        // delete 3 month of 2009
        glucoseDao.deleteById(3)

        // expect year 2009 month 4
        assertEquals("2009", vm.getCurrentYear())
        assertEquals(4, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 4 month of 2009
        glucoseDao.deleteById(4)

        // expect year 2005 month 6
        assertEquals("2005", vm.getCurrentYear())
        assertEquals(6, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 6 month of 2005
        glucoseDao.deleteById(6)

        // expect year 2005 month 5
        assertEquals("2005", vm.getCurrentYear())
        assertEquals(5, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 5 month of 2005
        glucoseDao.deleteById(5)

        // expect year 2012 month 2
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 2 month of 2012
        glucoseDao.deleteById(2)

        // expect year 2012 month 1
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(1, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 1 month of 2012
        glucoseDao.deleteById(1)

        // expect no data
        assertEquals("", vm.getCurrentYear())
        assertEquals(-1, vm.getCurrentMonth())
        assertEquals(-1, vm.getSelectedMonth())
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