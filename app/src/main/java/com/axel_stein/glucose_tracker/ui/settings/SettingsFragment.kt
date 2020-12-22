package com.axel_stein.glucose_tracker.ui.settings

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.FileProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.backup.BackupHelper
import com.axel_stein.glucose_tracker.utils.readStrFromFileUri
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import java.io.File


class SettingsFragment : PreferenceFragmentCompat() {
    private val backupHelper = BackupHelper()
    private val codePickFile = 1

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exportBackup = preferenceManager.findPreference<Preference>("export_backup")
        exportBackup?.setOnPreferenceClickListener {
            backupHelper.createBackup().observeOn(mainThread()).subscribe(object : SingleObserver<File> {
                override fun onSubscribe(d: Disposable) {}

                override fun onSuccess(file: File) {
                    val uri = getUriForFile(file)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/zip"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        flags = FLAG_GRANT_WRITE_URI_PERMISSION or
                                FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(intent, null))
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), R.string.error_export_file, LENGTH_SHORT).show()
                }
            })
            true
        }

        val importBackup = preferenceManager.findPreference<Preference>("import_backup")
        importBackup?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                flags = FLAG_GRANT_WRITE_URI_PERMISSION or
                        FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, codePickFile)
            true
        }
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
                requireContext(),
                "com.axel_stein.glucose_tracker.fileprovider",
                file
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codePickFile) {
            readStrFromFileUri(requireContext().contentResolver, data?.data)
                .flatMapCompletable { backupHelper.importBackup(it) }
                .observeOn(mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onComplete() {
                        Toast.makeText(requireContext(), R.string.msg_import_completed, LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), R.string.error_import_file, LENGTH_SHORT).show()
                    }
                })
        }
    }
}