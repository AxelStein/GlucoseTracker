package com.axel_stein.glucose_tracker.ui.list.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.model.NoteLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.formatTime

class NoteLogItem(private val log: NoteLog) : LogItem {
    private lateinit var time: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        time = formatTime(context, log.dateTime)
    }

    override fun id() = log.id
    override fun type() = ItemType.NOTE
    override fun icon() = R.drawable.icon_note
    override fun title() = log.note
    override fun description(): String? = null
    override fun time() = time
    override fun timeDescription(): String? = null
    override fun dateTime() = log.dateTime
}