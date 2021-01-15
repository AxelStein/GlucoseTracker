package com.axel_stein.glucose_tracker.data.room.dao

import com.axel_stein.glucose_tracker.data.model.WeightLog
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.MutableDateTime

class WeightLogDao {
    fun insert(item: WeightLog): Completable {
        return Completable.fromAction {}
    }

    fun insert(items: List<WeightLog>) {}

    fun update(item: WeightLog): Completable {
        return Completable.fromAction {}
    }

    fun delete(item: WeightLog): Completable {
        return Completable.fromAction {}
    }

    fun deleteAll() {}

    fun deleteById(id: Long): Completable {
        return Completable.fromAction {}
    }

    fun get(id: Long): Single<WeightLog> {
        return Single.fromCallable {
            WeightLog(45f, 55f,
                MutableDateTime().apply {
                    year = 2020
                    monthOfYear = 11
                    dayOfMonth = 22
                }.toDateTime()
            )
        }
    }
}