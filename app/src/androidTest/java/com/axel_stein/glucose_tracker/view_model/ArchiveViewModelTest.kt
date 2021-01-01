package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
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

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        dao = db.logDao()

        glucoseDao = db.glucoseLogDao()

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
        val vm = ArchiveViewModel(dao = dao)
        assertEquals("", vm.getCurrentYear())
        assertEquals(-1, vm.getSelectedYear())

        assertEquals(-1, vm.getCurrentMonth())
        assertEquals(-1, vm.getSelectedMonth())
    }

    @Test
    fun testCurrentYear() {
        glucoseDao.insert(createLog(year = "2012")).subscribe()
        glucoseDao.insert(createLog(year = "2009")).subscribe()

        val vm = ArchiveViewModel(dao = dao)
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(2, vm.yearsData().value?.size)
    }

    @Test
    fun testSelectedYear() {
        glucoseDao.insert(createLog(year = "2012", month = "01")).subscribe()
        glucoseDao.insert(createLog(year = "2012", month = "02")).subscribe()
        glucoseDao.insert(createLog(year = "2009", month = "03")).subscribe()
        glucoseDao.insert(createLog(year = "2009", month = "04")).subscribe()
        glucoseDao.insert(createLog(year = "2005", month = "05")).subscribe()
        glucoseDao.insert(createLog(year = "2005", month = "06")).subscribe()

        val vm = ArchiveViewModel(dao = dao)
        assertEquals(2, vm.getCurrentMonth())

        vm.setCurrentYear(1)
        assertEquals("2009", vm.getCurrentYear())
        assertEquals(1, vm.getSelectedYear())

        assertEquals(4, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
    }

    @Test
    fun testDeleteYear() {
        glucoseDao.insert(createLog(year = "2012", month = "01")).subscribe()
        glucoseDao.insert(createLog(year = "2009", month = "03")).subscribe()
        glucoseDao.insert(createLog(year = "2005", month = "05")).subscribe()
        assertEquals(3, glucoseDao.get().size)

        val vm = ArchiveViewModel(dao = dao)
        vm.setCurrentYear(1)
        assertEquals("2009", vm.getCurrentYear())

        // delete year 2009
        glucoseDao.deleteById(2).subscribe()
        assertEquals(2, glucoseDao.get().size)

        assertEquals(1, vm.getSelectedYear())
        assertEquals("2005", vm.getCurrentYear())

        // delete year 2005
        glucoseDao.deleteById(3).subscribe()
        assertEquals(1, glucoseDao.get().size)

        assertEquals(0, vm.getSelectedYear())
        assertEquals("2012", vm.getCurrentYear())

        // delete year 2012
        glucoseDao.deleteById(1).subscribe()
        assertTrue(glucoseDao.get().isEmpty())

        assertEquals(-1, vm.getSelectedYear())
        assertEquals("", vm.getCurrentYear())
    }

    @Test
    fun testCurrentMonth() {
        glucoseDao.insert(createLog(year = "2021", month = "01")).subscribe()
        glucoseDao.insert(createLog(year = "2021", month = "02")).subscribe()
        glucoseDao.insert(createLog(year = "2020", month = "02")).subscribe()

        val vm = ArchiveViewModel(dao = dao)
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
        assertEquals(2, vm.monthsData().value?.size)
    }

    @Test
    fun testSelectedMonth() {
        glucoseDao.insert(createLog(year = "2012", month = "01")).subscribe()
        glucoseDao.insert(createLog(year = "2012", month = "02")).subscribe()

        val vm = ArchiveViewModel(dao = dao)
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())
        assertEquals(2, vm.monthsData().value?.size)

        vm.setCurrentMonth(1)
        assertEquals(1, vm.getCurrentMonth())
        assertEquals(1, vm.getSelectedMonth())
    }

    @Test
    fun testDeleteMonth() {
        glucoseDao.insert(createLog(year = "2012", month = "01")).subscribe()
        glucoseDao.insert(createLog(year = "2012", month = "02")).subscribe()
        glucoseDao.insert(createLog(year = "2009", month = "03")).subscribe()
        glucoseDao.insert(createLog(year = "2009", month = "04")).subscribe()
        glucoseDao.insert(createLog(year = "2005", month = "05")).subscribe()
        glucoseDao.insert(createLog(year = "2005", month = "06")).subscribe()

        val vm = ArchiveViewModel(dao = dao)

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
        glucoseDao.deleteById(3).subscribe()

        // expect year 2009 month 4
        assertEquals("2009", vm.getCurrentYear())
        assertEquals(4, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 4 month of 2009
        glucoseDao.deleteById(4).subscribe()

        // expect year 2005 month 6
        assertEquals("2005", vm.getCurrentYear())
        assertEquals(6, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 6 month of 2005
        glucoseDao.deleteById(6).subscribe()

        // expect year 2005 month 5
        assertEquals("2005", vm.getCurrentYear())
        assertEquals(5, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 5 month of 2005
        glucoseDao.deleteById(5).subscribe()

        // expect year 2012 month 2
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(2, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 2 month of 2012
        glucoseDao.deleteById(2).subscribe()

        // expect year 2012 month 1
        assertEquals("2012", vm.getCurrentYear())
        assertEquals(1, vm.getCurrentMonth())
        assertEquals(0, vm.getSelectedMonth())

        // delete 1 month of 2012
        glucoseDao.deleteById(1).subscribe()

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