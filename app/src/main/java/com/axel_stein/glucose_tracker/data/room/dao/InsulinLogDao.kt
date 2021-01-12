package com.axel_stein.glucose_tracker.data.room.dao

import com.axel_stein.glucose_tracker.data.model.InsulinLog
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.MutableDateTime

class InsulinLogDao {
    fun insert(item: InsulinLog): Completable {
        return Completable.fromAction {}
    }

    fun insert(items: List<InsulinLog>) {}

    fun update(item: InsulinLog): Completable {
        return Completable.fromAction {}
    }

    fun delete(item: InsulinLog): Completable {
        return Completable.fromAction {}
    }

    fun deleteAll() {}

    fun deleteById(id: Long): Completable {
        return Completable.fromAction {}
    }

    fun get(id: Long): Single<InsulinLog> {
        return Single.fromCallable {
            InsulinLog(
            2, 4.5f, 3,
                MutableDateTime().apply {
                    year = 2020
                    monthOfYear = 11
                    dayOfMonth = 22
                }.toDateTime()
            )
        }
    }
}