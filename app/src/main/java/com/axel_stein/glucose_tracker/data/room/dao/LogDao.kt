package com.axel_stein.glucose_tracker.data.room.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.axel_stein.glucose_tracker.data.model.LogItem

@Dao
interface LogDao {
    // "select *, food_log.id, 2 as item_type, null as value_mmol, null as value_mg, null as measured, null as note, group_concat(title, \"; \"), date_time from food_log " +
    // "join food_list on food_log.food_id = food_list.id group by food_log.id "

    @Query("select id, value_mmol, value_mg, measured, date_time, 0 as item_type, null as note, null as foods from glucose_log union select id, null as value_mmol, null as value_mg, null as measured, date_time, 1 as item_type, note, null as foods from note_log")
    fun getItems(): DataSource.Factory<Int, LogItem>
}