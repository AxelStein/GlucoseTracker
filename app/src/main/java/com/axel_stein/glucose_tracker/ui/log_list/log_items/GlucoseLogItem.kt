package com.axel_stein.glucose_tracker.ui.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.GlucoseLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.log_list.log_items.ItemType.GLUCOSE
import com.axel_stein.glucose_tracker.utils.formatTime

class GlucoseLogItem(private val log: GlucoseLog): LogItem {
    private lateinit var title: String
    private lateinit var time: String
    private lateinit var timeDescription: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        title = if (appSettings.useMmolAsGlucoseUnits()) {
            "${log.valueMmol} ${appResources.mmolSuffix}"
        } else {
            "${log.valueMg} ${appResources.mgSuffix}"
        }
        time = formatTime(context, log.dateTime)
        timeDescription = appResources.measuredArray[log.measured]
    }

    override fun id() = log.id
    override fun type() = GLUCOSE
    override fun icon() = R.drawable.icon_glucose
    override fun title() = title
    override fun description(): String? = null
    override fun time() = time
    override fun timeDescription() = timeDescription
    override fun dateTime() = log.dateTime
}