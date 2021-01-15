package com.axel_stein.glucose_tracker.data.room

import android.content.Context
import android.util.SparseArray
import androidx.core.util.set
import androidx.room.InvalidationTracker
import com.axel_stein.glucose_tracker.data.model.*
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.log_list.log_items.*
import com.axel_stein.glucose_tracker.utils.formatDate
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDate

class LogRepository(private val ctx: Context, private val db: AppDatabase, private val dao: LogDao) {
    private val tables = arrayOf(
        "a1c_log",
        "glucose_log",
        "insulin_log",
        "medication_log",
        "note_log",
        "weight_log"
    )
    private val settings = AppSettings(ctx)
    private val resources = AppResources(ctx, settings)

    data class LogListResult(
        val list: List<LogItem>,
        val headers: SparseArray<String>
    )

    fun getRecentLogs() = createFlowable { dao.getRecentLogs() }

    fun getLogsByYearMonth(yearMonth: String) = createFlowable { dao.getLogsByYearMonth(yearMonth) }

    fun getA1cLogs() = createFlowable { dao.getA1cLogs() }

    fun getWeightLogs() = createFlowable { dao.getWeightLogs() }

    fun getYears() = dao.getYears()

    fun getMonths(year: String) = dao.getMonths(year)

    private fun createFlowable(getData: () -> List<Any>): Flowable<LogListResult> {
        return Flowable.create({ emitter ->
            val worker = Schedulers.io().createWorker()

            val emitCurrentList = {
                worker.schedule {
                    val objects = getData().map {
                        when (it) {
                            is GlucoseLog -> GlucoseLogItem(it)
                            is NoteLog -> NoteLogItem(it)
                            is A1cLog -> A1cLogItem(it)
                            is InsulinLogEmbedded -> InsulinLogItem(it)
                            is MedicationLogEmbedded -> MedicationLogItem(it)
                            is WeightLog -> WeightLogItem(it)
                            else -> TODO()
                        }
                    }
                    val list = sort(format(objects).toMutableList())
                    emitter.onNext(LogListResult(list, createHeaders(list)))
                }
            }

            val observer = object : InvalidationTracker.Observer(tables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    emitCurrentList()
                }
            }
            db.invalidationTracker.addObserver(observer)
            emitter.setCancellable { db.invalidationTracker.removeObserver(observer) }
            emitCurrentList()
        }, BackpressureStrategy.LATEST)
    }

    private fun format(items: List<LogItem>) =
        items.onEach {
            it.format(ctx, settings, resources)
        }

    private fun sort(items: MutableList<LogItem>): List<LogItem> {
        items.sortByDescending { it.dateTime() }
        items.sortWith { a, b ->
            val d1 = a?.dateTime()?.toLocalDate()
            val d2 = b?.dateTime()?.toLocalDate()

            val compareDates = d1?.compareTo(d2)
            if (compareDates == 0) {
                val t1 = a.dateTime().toLocalTime()
                val t2 = b?.dateTime()?.toLocalTime()
                t1.compareTo(t2)
            } else {
                0
            }
        }
        return items
    }

    private fun createHeaders(items: List<LogItem>): SparseArray<String> {
        val headers = SparseArray<String>()
        var date: LocalDate? = null
        items.forEachIndexed { index, item ->
            val itemDate = item.dateTime().toLocalDate()
            if (date == null || date != itemDate) {
                headers[index] = formatDate(ctx, item.dateTime())
                date = itemDate
            }
        }
        return headers
    }
}