package com.axel_stein.glucose_tracker.data.room.dao

import com.axel_stein.glucose_tracker.data.model.MedicationLog
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.MutableDateTime

class MedicationLogDao {
    fun insert(item: MedicationLog): Completable {
        return Completable.fromAction {}
    }

    fun insert(items: List<MedicationLog>) {}

    fun update(item: MedicationLog): Completable {
        return Completable.fromAction {}
    }

    fun delete(item: MedicationLog): Completable {
        return Completable.fromAction {}
    }

    fun deleteAll() {}

    fun deleteById(id: Long): Completable {
        return Completable.fromAction {}
    }

    fun get(id: Long): Single<MedicationLog> {
        return Single.fromCallable {
            MedicationLog(
                1L, 1f, 3,
                MutableDateTime().apply {
                    year = 2020
                    monthOfYear = 11
                    dayOfMonth = 22
                }.toDateTime()
            )
        }
    }
}