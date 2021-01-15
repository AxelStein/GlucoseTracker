package com.axel_stein.glucose_tracker.ui.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.InsulinLogEmbedded
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.formatTime

class InsulinLogItem(private val item: InsulinLogEmbedded) : LogItem {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var time: String
    private lateinit var timeDescription: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        title = item.insulin.title
        description = "${item.log.units} ${appResources.unitsSuffix}"
        time = formatTime(context, item.log.dateTime)
        timeDescription = appResources.measuredArray[item.log.measured]
    }

    override fun id() = item.log.id
    override fun type() = ItemType.INSULIN
    override fun icon() = R.drawable.icon_insulin
    override fun title() = title
    override fun description() = description
    override fun time() = time
    override fun timeDescription() = timeDescription
    override fun dateTime() = item.log.dateTime
}