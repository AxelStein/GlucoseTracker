package com.axel_stein.glucose_tracker.data.pdf

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.view.LayoutInflater
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import java.io.File
import javax.inject.Inject

class PdfHelper {
    private lateinit var context: Context
    private lateinit var settings: AppSettings
    private lateinit var resources: AppResources
    private lateinit var glucoseDao: GlucoseLogDao

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun setContext(context: Context) {
        this.context = context
    }

    @Inject
    fun setResources(settings: AppSettings, resources: AppResources) {
        this.settings = settings
        this.resources = resources
    }

    @Inject
    fun setGlucoseDao(dao: GlucoseLogDao) {
        glucoseDao = dao
    }

    fun create(): Single<File> = Single.fromCallable {
        createImpl()
    }.subscribeOn(io())

    private fun createImpl(): File {
        val width = 595
        val height = 842

        // Creating a PdfWriter
        val file = File(resources.appDir(), "report.pdf")
        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)

        val view = LayoutInflater.from(context).inflate(R.layout.layout_report, null)
        val widthSpec = makeMeasureSpec(width, EXACTLY)
        val heightSpec = makeMeasureSpec(height, EXACTLY)
        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, width, height)

        page.canvas.save()
        page.canvas.translate(0f, 0f)
        view.draw(page.canvas)
        page.canvas.restore()

        document.finishPage(page)

        document.writeTo(file.outputStream())
        document.close()
        return file
    }
}