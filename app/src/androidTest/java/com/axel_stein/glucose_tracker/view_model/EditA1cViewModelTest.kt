package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.model.A1cLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.A1cLogDao
import com.axel_stein.glucose_tracker.ui.edit_a1c.EditA1cViewModel
import org.joda.time.DateTime
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditA1cViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: A1cLogDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.a1cDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testSave() {
        val vm = createViewModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setValue("3.2")
        vm.save()

        assertFalse(vm.errorValueEmptyObserver().value ?: false)
        assertNull(vm.errorSaveObserver().value)
        assertTrue(vm.actionFinishObserver().value ?: false)
    }

    @Test
    fun testSave_noteEmpty() {
        val vm = createViewModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.save()

        assertTrue(vm.errorValueEmptyObserver().value ?: false)
        assertNull(vm.actionFinishObserver().value)
    }

    @Test
    fun testLoad() {
        dao.insert(createLog("2021", "01", "10", "15", "30")).subscribe()

        val items = dao.get()
        Assert.assertFalse(items.isEmpty())

        val vm = createViewModel(items[0].id)
        assertEquals("5.2", vm.getValue())
        assertEquals(2021, vm.getCurrentDateTime().toLocalDate().year)
        assertEquals(1, vm.getCurrentDateTime().toLocalDate().monthOfYear)
        assertEquals(10, vm.getCurrentDateTime().toLocalDate().dayOfMonth)
        assertEquals(15, vm.getCurrentDateTime().toLocalTime().hourOfDay)
        assertEquals(30, vm.getCurrentDateTime().toLocalTime().minuteOfHour)
    }

    @Test
    fun testDelete() {
        dao.insert(createLog()).subscribe()

        val log = dao.get()[0]
        val vm = createViewModel(log.id)
        vm.delete()

        assertTrue(vm.actionFinishObserver().value ?: false)
        assertTrue(dao.get().isEmpty())
    }

    private fun createViewModel(id: Long = 0L): EditA1cViewModel {
        return EditA1cViewModel(id = id, dao = dao)
    }

    private fun createLog(
        year: String = "2021",
        month: String = "01",
        day: String = "01",
        hours: String = "12",
        minutes: String = "00"
    ): A1cLog {
        return A1cLog(5.2f,
            DateTime("$year-$month-${day}T$hours:$minutes:00.000+02:00")
        )
    }
}