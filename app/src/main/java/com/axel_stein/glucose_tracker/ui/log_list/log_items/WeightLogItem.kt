package com.axel_stein.glucose_tracker.ui.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.WeightLog
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.formatTime

class WeightLogItem(private val log: WeightLog) : LogItem {
    private lateinit var title: String
    private lateinit var time: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        title = "${log.kg.formatIfInt()} ${appResources.kgSuffix}"
        time = formatTime(context, log.dateTime)
    }

    override fun id() = log.id
    override fun type() = ItemType.WEIGHT
    override fun icon() = R.drawable.icon_weight
    override fun title() = title
    override fun description(): String? = null
    override fun time() = time
    override fun timeDescription(): String? = null
    override fun dateTime() = log.dateTime
}