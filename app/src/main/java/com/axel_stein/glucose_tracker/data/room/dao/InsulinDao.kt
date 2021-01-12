package com.axel_stein.glucose_tracker.data.room.dao

import com.axel_stein.glucose_tracker.data.model.Insulin
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class InsulinDao {
    private var id = 2L

    private val items: MutableList<Insulin> = mutableListOf(
        Insulin("Humalog").apply { id = 1 },
    )

    fun insert(item: Insulin): Completable {
        return Completable.fromAction {
            item.id = id++
            items.add(item)
        }
    }

    fun insert(items: List<Insulin>) {
        this.items.addAll(items)
    }

    fun update(item: Insulin): Completable {
        return Completable.fromAction {
            items.find { it.id == item.id }?.apply {
                title = item.title
                type = item.type
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

    fun get(id: Long): Single<Insulin> {
        return Single.fromCallable {
            items.find { it.id == id }
        }
    }

    fun get(): Flowable<List<Insulin>> {
        return Flowable.fromCallable { items }
    }

    fun getItems() = Single.fromCallable { items }
}