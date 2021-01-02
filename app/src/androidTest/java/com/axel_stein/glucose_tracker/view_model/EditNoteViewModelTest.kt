package com.axel_stein.glucose_tracker.view_model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.RxImmediateSchedulerRule
import com.axel_stein.glucose_tracker.data.model.NoteLog
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.NoteLogDao
import com.axel_stein.glucose_tracker.ui.edit_note.EditNoteViewModel
import org.joda.time.DateTime
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditNoteViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: NoteLogDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.noteLogDao()
    }

    @After
    fun closeDb() {
        dao.deleteAll()
        db.close()
    }

    @Test
    fun testSave() {
        val vm = createViewModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.setNote("Test")
        vm.save()

        Assert.assertFalse(vm.errorNoteEmptyObserver().value ?: false)
        Assert.assertNull(vm.errorSaveObserver().value)
        Assert.assertTrue(vm.actionFinishObserver().value ?: false)
    }

    @Test
    fun testSave_noteEmpty() {
        val vm = createViewModel()
        vm.setDate(2021, 1, 1)
        vm.setTime(20, 0)
        vm.save()

        Assert.assertTrue(vm.errorNoteEmptyObserver().value ?: false)
        Assert.assertNull(vm.actionFinishObserver().value)
    }

    @Test
    fun testLoad() {
        dao.insert(createLog("2021", "01", "10")).subscribe()

        val items = dao.get()
        Assert.assertFalse(items.isEmpty())

        val vm = createViewModel(items[0].id)
        Assert.assertEquals("com.axel_stein.glucose_tracker", vm.getNote())
        Assert.assertEquals(2021, vm.getCurrentDateTime().toLocalDate().year)
        Assert.assertEquals(1, vm.getCurrentDateTime().toLocalDate().monthOfYear)
        Assert.assertEquals(10, vm.getCurrentDateTime().toLocalDate().dayOfMonth)
    }

    @Test
    fun testDelete() {
        dao.insert(createLog()).subscribe()

        val log = dao.get()[0]
        val vm = createViewModel(log.id)
        vm.delete()

        Assert.assertTrue(vm.actionFinishObserver().value ?: false)
        Assert.assertTrue(dao.get().isEmpty())
    }

    private fun createViewModel(id: Long = 0L): EditNoteViewModel {
        return EditNoteViewModel(id = id, dao = dao)
    }

    private fun createLog(
        year: String = "2021",
        month: String = "01",
        day: String = "01",
        hours: String = "12",
        minutes: String = "00"
    ): NoteLog {
        return NoteLog("com.axel_stein.glucose_tracker",
            DateTime("$year-$month-${day}T$hours:$minutes:00.000+02:00")
        )
    }
}