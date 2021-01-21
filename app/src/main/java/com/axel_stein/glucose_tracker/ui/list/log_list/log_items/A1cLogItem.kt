package com.axel_stein.glucose_tracker.ui.list.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.A1cLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.list.log_list.log_items.ItemType.A1C
import com.axel_stein.glucose_tracker.utils.formatTime

class A1cLogItem(private val log: A1cLog) : LogItem {
    private lateinit var title: String
    private lateinit var time: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        title = "${log.value}%"
        time = formatTime(context, log.dateTime)
    }

    override fun id(): Long = log.id
    override fun type() = A1C
    override fun icon() = R.drawable.icon_a1c
    override fun title() = title
    override fun description(): String? = null
    override fun time() = time
    override fun timeDescription(): String? = null
    override fun dateTime() = log.dateTime
}