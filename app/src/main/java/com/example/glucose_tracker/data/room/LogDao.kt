package com.example.glucose_tracker.data.room

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.example.glucose_tracker.data.model.LogItem

@Dao
interface LogDao {
    @Query("select id, 0 as item_type, value_mmol, value_mg, measured, null as note, null as foods, date, time from glucose_log union " +
            "select id, 1 as item_type, null as value_mmol, null as value_mg, null as measured, note, null as foods, date, time from note_log union " +
            "select food_log.id, 2 as item_type, null as value_mmol, null as value_mg, null as measured, null as note, group_concat(title, \"; \"), date, time from food_log " +
            "join food_list on food_log.food_id = food_list.id group by date, time order by date desc, time")
    fun getItems(): DataSource.Factory<Int, LogItem>
}