package com.axel_stein.glucose_tracker.data.google_drive

import android.content.Context
import androidx.fragment.app.Fragment
import com.axel_stein.glucose_tracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount
import com.google.android.gms.common.Scopes.EMAIL
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes.DRIVE_APPDATA
import com.google.api.services.drive.DriveScopes.DRIVE_FILE
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import java.io.IOException
import java.io.InputStream

class GoogleDriveService(private val context: Context) {
    private var service: Drive? = null

    fun hasPermissions(): Boolean {
        return GoogleSignIn.hasPermissions(
            getLastSignedInAccount(context),
            Scope(EMAIL),
            Scope(DRIVE_APPDATA),
            Scope(DRIVE_FILE)
        )
    }

    fun requestPermissions(fragment: Fragment, requestCode: Int) {
        GoogleSignIn.requestPermissions(fragment, requestCode,
            getLastSignedInAccount(context),
            Scope(EMAIL),
            Scope(DRIVE_APPDATA),
            Scope(DRIVE_FILE)
        )
    }

    private fun setupDriveService(): Boolean {
        if (service == null) {
            val account = getLastSignedInAccount(context)
            if (account != null) {
                val credential = GoogleAccountCredential.usingOAuth2(context,
                    listOf(DRIVE_APPDATA, DRIVE_FILE)
                )
                credential.selectedAccount = account.account
                service = Drive.Builder(NetHttpTransport(), GsonFactory(), credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build()
            }
        }
        return true
    }

    fun uploadFile(fileName: String, file: java.io.File): Completable {
        return Completable.fromAction { _uploadFile(fileName, file) }.subscribeOn(io())
    }

    fun _uploadFile(fileName: String, file: java.io.File) {
        val metadata = File().setName(fileName)
        val contentStream = ByteArrayContent.fromString("text/plain", file.readText())

        if (setupDriveService()) {
            val id = getFileId(fileName)
            if (id.isNullOrEmpty()) {
                metadata.parents = listOf("appDataFolder")
                service?.files()?.create(metadata, contentStream)?.setFields("id")?.execute()
            } else {
                service?.files()?.update(id, metadata, contentStream)?.execute()
            }
        }
    }

    fun downloadFile(fileName: String): Single<String> {
        return Single.fromCallable {
            val id = getFileId(fileName)
            var result = ""
            if (!id.isNullOrEmpty() && setupDriveService()) {
                val stream: InputStream? = service?.files()?.get(id)?.executeMediaAsInputStream()
                result = stream?.bufferedReader()?.readText() ?: ""
            }
            result
        }.subscribeOn(io())
    }

    fun getLastSyncTime(fileName: String): Single<Long> {
        return Single.fromCallable { _getLastSyncTime(fileName) }.subscribeOn(io())
    }
    
    fun _getLastSyncTime(fileName: String): Long {
        val id = getFileId(fileName)
        if (!id.isNullOrEmpty() && setupDriveService()) {
            try {
                val f: File? = service?.files()?.get(id)?.setFields("modifiedTime")?.execute()
                return f?.modifiedTime?.value ?: -1L
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return -1
    }

    private fun getFileId(fileName: String): String? {
        try {
            if (setupDriveService()) {
                val result: FileList? = service?.files()
                    ?.list()
                    ?.setSpaces("appDataFolder")
                    ?.setQ(String.format("name = '%s'", fileName))
                    ?.execute()
                val files: List<File>? = result?.files
                if (files != null && files.isNotEmpty()) {
                    return files[0].id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}