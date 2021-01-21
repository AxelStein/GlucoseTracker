package com.axel_stein.glucose_tracker.data.room.repository

import android.content.Context
import android.util.SparseArray
import androidx.core.util.set
import androidx.room.InvalidationTracker
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.dao.LogDao
import com.axel_stein.glucose_tracker.data.room.model.*
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.list.log_list.log_items.*
import com.axel_stein.glucose_tracker.utils.formatDate
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDate

class LogRepository(private val ctx: Context, private val db: AppDatabase, private val dao: LogDao) {
    private val tables = arrayOf(
        "a1c_log",
        "glucose_log",
        "insulin_log",
        "medication_log",
        "note_log",
        "weight_log",
        "ap_log",
        "pulse_log"
    )
    private val settings = AppSettings(ctx)
    private val resources = AppResources(ctx)

    data class LogListResult(
        val list: List<LogItem>,
        val headers: SparseArray<String>
    )

    fun getRecentLogs() = createFlowable { dao.getRecentLogs() }

    fun getLogsByYearMonth(yearMonth: String) = createFlowable { dao.getLogsByYearMonth(yearMonth) }

    fun getA1cLogs() = createFlowable { dao.getA1cLogs() }

    fun getWeightLogs() = createFlowable { dao.getWeightLogs() }

    fun getPulseLogs() = createFlowable { dao.getPulseLogs() }

    fun getApLogs() = createFlowable { dao.getApLogs() }

    fun getYears() = dao.getYears()

    fun getMonths(year: String) = dao.getMonths(year)

    fun observeUpdates(): Flowable<Boolean> {
        return Flowable.create({ emitter ->
            val observer = object : InvalidationTracker.Observer(tables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    emitter.onNext(true)
                }
            }
            db.invalidationTracker.addObserver(observer)
            emitter.setCancellable {
                db.invalidationTracker.removeObserver(observer)
            }
        }, BackpressureStrategy.LATEST)
    }

    private fun createFlowable(getData: () -> List<Any>): Flowable<LogListResult> {
        return Flowable.create({ emitter ->
            val worker = Schedulers.io().createWorker()
            val emitData = {
                worker.schedule {
                    try {
                        val objects = getData().map {
                            when (it) {
                                is GlucoseLog -> GlucoseLogItem(it)
                                is NoteLog -> NoteLogItem(it)
                                is A1cLog -> A1cLogItem(it)
                                is InsulinLogEmbedded -> InsulinLogItem(it)
                                is MedicationLogEmbedded -> MedicationLogItem(it)
                                is WeightLog -> WeightLogItem(it)
                                is ApLog -> ApLogItem(it)
                                is PulseLog -> PulseLogItem(it)
                                else -> TODO()
                            }
                        }
                        val list = sort(format(objects).toMutableList())
                        emitter.onNext(LogListResult(list, createHeaders(list)))
                    } catch (e: Exception) {
                        emitter.tryOnError(e)
                    }
                }
            }

            val disposables = CompositeDisposable()
            disposables.add(
                settings.observeGlucoseUnits()
                    .subscribe {
                        emitData()
                    }
            )
            val observer = object : InvalidationTracker.Observer(tables) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    emitData()
                }
            }
            db.invalidationTracker.addObserver(observer)
            emitter.setCancellable {
                db.invalidationTracker.removeObserver(observer)
                disposables.clear()
            }
            emitData()
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