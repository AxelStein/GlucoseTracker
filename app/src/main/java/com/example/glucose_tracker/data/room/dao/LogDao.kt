package com.example.glucose_tracker.data.room.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.example.glucose_tracker.data.model.LogItem

@Dao
interface LogDao {
    @Query("select *, 0 as item_type, null as note, null as foods from glucose_log union " +
            "select *, 1 as item_type, null as value_mmol, null as value_mg, null as measured, null as foods from note_log union " +
            "select food_log.id, 2 as item_type, null as value_mmol, null as value_mg, null as measured, null as note, group_concat(title, \"; \"), date_time from food_log " +
            "join food_list on food_log.food_id = food_list.id group by food_log.id ")
    fun getItems(): DataSource.Factory<Int, LogItem>
}