package com.axel_stein.glucose_tracker.data.pdf

import android.content.Context
import com.axel_stein.glucose_tracker.data.room.dao.GlucoseLogDao
import com.axel_stein.glucose_tracker.data.settings.AppResources
import com.axel_stein.glucose_tracker.data.settings.AppSettings
import com.axel_stein.glucose_tracker.ui.App
import com.axel_stein.glucose_tracker.utils.formatDateTime
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import org.joda.time.DateTime
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
        // Creating a PdfWriter
        val file = File(resources.appDir(), "report.pdf")
        val dest = file.absolutePath
        val writer = PdfWriter(dest)

        // Creating a PdfDocument
        val pdf = PdfDocument(writer)

        // Creating a Document by passing PdfDocument object to its constructor
        val document = Document(pdf)

        val p = Paragraph("Report ${formatDateTime(context, DateTime())}")
        document.add(p)

        // Creating a table object
        val table = Table(3)

        val glucoseLogs = glucoseDao.getLastThreeMonths()
        glucoseLogs.forEach {
            val dateTime = formatDateTime(context, it.dateTime)
            val glucose = if (settings.useMmolAsGlucoseUnits())
                "${it.valueMmol.toString()} ${resources.mmolSuffix}"
                else "${it.valueMg.toString()} ${resources.mgSuffix}"
            val measured = resources.measuredArray[it.measured]

            table.addCell(paragraphCell(dateTime))
            table.addCell(paragraphCell(glucose))
            table.addCell(paragraphCell(measured))
        }
        document.add(table)

        // Closing the document
        document.close()
        return file
    }

    private fun paragraphCell(text: String) = Cell().add(Paragraph(text))
}