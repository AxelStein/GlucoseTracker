package com.axel_stein.glucose_tracker.ui

import android.annotation.TargetApi
import android.app.Application
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.dagger.AppComponent
import com.axel_stein.glucose_tracker.data.dagger.AppModule
import com.axel_stein.glucose_tracker.data.dagger.DaggerAppComponent
import com.axel_stein.glucose_tracker.ui.edit.edit_glucose.EditGlucoseActivity
import com.axel_stein.glucose_tracker.ui.edit.edit_insulin_log.EditInsulinLogActivity
import com.axel_stein.glucose_tracker.ui.edit.edit_medication_log.EditMedicationLogActivity
import com.axel_stein.glucose_tracker.ui.edit.edit_note.EditNoteActivity

class App: Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        if (Build.VERSION.SDK_INT >= 25) { createShortcuts() }
    }

    @TargetApi(25)
    private fun createShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        if (shortcutManager.dynamicShortcuts.isEmpty()) {
            shortcutManager.dynamicShortcuts = listOf(
                createShortcut(
                    EditGlucoseActivity::class.java, "add_glucose",
                    R.string.fab_menu_blood_sugar,
                    R.drawable.icon_glucose
                ),
                createShortcut(
                    EditNoteActivity::class.java, "add_note",
                    R.string.fab_menu_note,
                    R.drawable.icon_note
                ),
                createShortcut(
                    EditInsulinLogActivity::class.java, "add_insulin_log",
                    R.string.fab_menu_insulin,
                    R.drawable.icon_insulin
                ),
                createShortcut(
                    EditMedicationLogActivity::class.java, "add_medication_log",
                    R.string.fab_menu_medication,
                    R.drawable.icon_medication
                ),
            )
        }
    }

    @TargetApi(25)
    private fun <T> createShortcut(cls: Class<T>, id: String, labelId: Int, iconId: Int): ShortcutInfo {
        val intent = Intent(applicationContext, cls)
            .apply {
                action = Intent.ACTION_VIEW
                putExtra("id", 0L)
            }
        return ShortcutInfo.Builder(this, id)
            .setIntent(intent)
            .setShortLabel(getString(labelId))
            .setIcon(Icon.createWithResource(this, iconId))
            .build()

    }
}