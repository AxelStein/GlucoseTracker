package com.axel_stein.glucose_tracker.ui.list.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import org.joda.time.DateTime

interface LogItem {
    fun format(context: Context, appSettings: AppSettings, appResources: AppResources)
    fun id(): Long
    fun type(): ItemType
    fun icon(): Int
    fun title(): String?
    fun description(): String?
    fun time(): String?
    fun timeDescription(): String?
    fun dateTime(): DateTime
}