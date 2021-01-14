package com.axel_stein.glucose_tracker.utils

import android.content.ContentResolver
import android.net.Uri
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import java.io.*

fun readStrFromFileUri(cr: ContentResolver, uri: Uri?): Single<String> {
    return Single.fromCallable {
        if (uri == null) {
            throw FileNotFoundException()
        }
        val inputStream = cr.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val result = reader.readText()
        reader.close()
        result
    }.subscribeOn(io())
}