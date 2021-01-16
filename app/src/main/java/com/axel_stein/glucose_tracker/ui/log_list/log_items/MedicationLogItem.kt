package com.axel_stein.glucose_tracker.ui.log_list.log_items

import android.content.Context
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.model.MedicationLogEmbedded
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.utils.formatIfInt
import com.axel_stein.glucose_tracker.utils.formatTime

class MedicationLogItem(private val item: MedicationLogEmbedded) : LogItem {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var time: String
    private lateinit var timeDescription: String

    override fun format(context: Context, appSettings: AppSettings, appResources: AppResources) {
        title = item.medication.title
        description = "${item.log.amount.formatIfInt()} ${appResources.dosageFormsArray[item.medication.dosageForm]}"
        time = formatTime(context, item.log.dateTime)
        timeDescription = appResources.measuredArray[item.log.measured]
    }

    override fun id() = item.log.id
    override fun type() = ItemType.MEDICATION
    override fun icon() = R.drawable.icon_medication
    override fun title() = title
    override fun description() = description
    override fun time() = time
    override fun timeDescription() = timeDescription
    override fun dateTime() = item.log.dateTime
}