package com.axel_stein.glucose_tracker.data.room.dao

import com.axel_stein.glucose_tracker.data.model.Insulin
import com.axel_stein.glucose_tracker.data.model.Medication
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class MedicationDao {
    private var id = 2L

    private val items: MutableList<Medication> = mutableListOf(
        Medication("Glucophage", 500f, 0).apply { id = 1 },
    )

    fun insert(item: Medication): Completable {
        return Completable.fromAction {
            item.id = id++
            items.add(item)
        }
    }

    fun insert(items: List<Medication>) {
        this.items.addAll(items)
    }

    fun update(item: Medication): Completable {
        return Completable.fromAction {
            items.find { it.id == item.id }?.apply {
                title = item.title
                amount = item.amount
                dosageUnits = item.dosageUnits
            }
        }
    }

    fun delete(item: Insulin): Completable {
        return Completable.fromAction {
            items.removeAll { it.id == item.id }
        }
    }

    fun deleteAll() {
        items.clear()
    }

    fun deleteById(id: Long): Completable {
        return Completable.fromAction {
            items.removeAll { it.id == id }
        }
    }

    fun get(id: Long): Single<Medication> {
        return Single.fromCallable {
            items.find { it.id == id }
        }
    }

    fun observeItems(): Flowable<List<Medication>> {
        return Flowable.fromCallable { items }
    }

    fun getItems() = Single.fromCallable { items }
}