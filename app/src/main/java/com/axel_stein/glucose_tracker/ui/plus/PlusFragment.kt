package com.axel_stein.glucose_tracker.ui.plus

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.pdf.PdfHelper
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenA1cList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenApList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenInsulinList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenMedicationList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenPulseList
import com.axel_stein.glucose_tracker.ui.plus.PlusFragmentDirections.Companion.actionOpenWeightList
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import java.io.File

class PlusFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.plus_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val insulinPref = preferenceManager.findPreference<Preference>("insulin_list")
        insulinPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenInsulinList())
            true
        }

        val medicationsPref = preferenceManager.findPreference<Preference>("medications")
        medicationsPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenMedicationList())
            true
        }

        val a1cListPref = preferenceManager.findPreference<Preference>("a1c_list")
        a1cListPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenA1cList())
            true
        }

        val weightListPref = preferenceManager.findPreference<Preference>("weight_list")
        weightListPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenWeightList())
            true
        }

        val apListPref = preferenceManager.findPreference<Preference>("ap_list")
        apListPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenApList())
            true
        }

        val pulseListPref = preferenceManager.findPreference<Preference>("pulse_list")
        pulseListPref?.setOnPreferenceClickListener {
            findNavController().navigate(actionOpenPulseList())
            true
        }

        val reportPdf = preferenceManager.findPreference<Preference>("report_export_pdf")
        reportPdf?.setOnPreferenceClickListener {
            PdfHelper().create()
                .map { file ->
                    Intent(Intent.ACTION_SEND).apply {
                        type = "*/*"
                        putExtra(Intent.EXTRA_STREAM, getUriForFile(file))
                        flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                }
                .observeOn(mainThread())
                .subscribe({
                    startActivity(Intent.createChooser(it, null))
                }, {
                    it.printStackTrace()
                })
            true
        }
    }

    private fun getUriForFile(file: File) = FileProvider.getUriForFile(
        requireContext(),
        "com.axel_stein.glucose_tracker.fileprovider",
        file
    )
}