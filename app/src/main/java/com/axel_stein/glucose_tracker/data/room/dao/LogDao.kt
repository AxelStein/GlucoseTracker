package com.axel_stein.glucose_tracker.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.axel_stein.glucose_tracker.data.room.model.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LogDao {
    @Query("select * from glucose_log where date_time > date('now', '-3 month')")
    fun getRecentGlucoseLogs(): List<GlucoseLog>

    @Query("select * from note_log where date_time > date('now', '-3 month')")
    fun getRecentNotes(): List<NoteLog>

    @Query("select * from a1c_log where date_time > date('now', '-3 month')")
    fun getRecentA1cLogs(): List<A1cLog>

    @Transaction
    @Query("select * from insulin_log where date_time > date('now', '-3 month')")
    fun getRecentInsulinLogs(): List<InsulinLogEmbedded>

    @Transaction
    @Query("select * from medication_log where date_time > date('now', '-3 month')")
    fun getRecentMedicationLogs(): List<MedicationLogEmbedded>

    @Query("select * from weight_log where date_time > date('now', '-3 month')")
    fun getRecentWeightLogs(): List<WeightLog>

    @Query("select * from ap_log where date_time > date('now', '-3 month')")
    fun getRecentApLogs(): List<ApLog>

    @Query("select * from pulse_log where date_time > date('now', '-3 month')")
    fun getRecentPulseLogs(): List<PulseLog>

    @Transaction
    fun getRecentLogs(): List<Any> {
        val list = ArrayList<Any>()
        list.addAll(getRecentGlucoseLogs())
        list.addAll(getRecentNotes())
        list.addAll(getRecentA1cLogs())
        list.addAll(getRecentInsulinLogs())
        list.addAll(getRecentMedicationLogs())
        list.addAll(getRecentWeightLogs())
        list.addAll(getRecentApLogs())
        list.addAll(getRecentPulseLogs())
        return list
    }

    @Query("select * from a1c_log")
    fun getA1cLogs(): List<A1cLog>

    @Query("select * from weight_log")
    fun getWeightLogs(): List<WeightLog>

    @Query("select * from ap_log")
    fun getApLogs(): List<ApLog>

    @Query("select * from pulse_log")
    fun getPulseLogs(): List<PulseLog>

    @Query("select * from glucose_log where substr(date_time, 1, 7) = :yearMonth")
    fun getGlucoseLogsByYearMonth(yearMonth: String): List<GlucoseLog>

    @Query("select * from note_log where substr(date_time, 1, 7) = :yearMonth")
    fun getNotesByYearMonth(yearMonth: String): List<NoteLog>

    @Query("select * from a1c_log where substr(date_time, 1, 7) = :yearMonth")
    fun getA1cLogsByYearMonth(yearMonth: String): List<A1cLog>

    @Transaction
    @Query("select * from insulin_log where substr(date_time, 1, 7) = :yearMonth")
    fun getInsulinLogsByYearMonth(yearMonth: String): List<InsulinLogEmbedded>

    @Transaction
    @Query("select * from medication_log where substr(date_time, 1, 7) = :yearMonth")
    fun getMedicationLogsByYearMonth(yearMonth: String): List<MedicationLogEmbedded>

    @Query("select * from weight_log where substr(date_time, 1, 7) = :yearMonth")
    fun getWeightLogsByYearMonth(yearMonth: String): List<WeightLog>

    @Query("select * from ap_log where substr(date_time, 1, 7) = :yearMonth")
    fun getApLogsByYearMonth(yearMonth: String): List<ApLog>

    @Query("select * from pulse_log where substr(date_time, 1, 7) = :yearMonth")
    fun getPulseLogsByYearMonth(yearMonth: String): List<PulseLog>

    @Transaction
    fun getLogsByYearMonth(yearMonth: String): List<Any> {
        val list = ArrayList<Any>()
        list.addAll(getGlucoseLogsByYearMonth(yearMonth))
        list.addAll(getNotesByYearMonth(yearMonth))
        list.addAll(getA1cLogsByYearMonth(yearMonth))
        list.addAll(getInsulinLogsByYearMonth(yearMonth))
        list.addAll(getMedicationLogsByYearMonth(yearMonth))
        list.addAll(getWeightLogsByYearMonth(yearMonth))
        list.addAll(getApLogsByYearMonth(yearMonth))
        list.addAll(getPulseLogsByYearMonth(yearMonth))
        return list
    }

    @Query("""
        select substr(date_time, 6, 2) as month from glucose_log
        where substr(date_time, 1, 4) = :year union
        
        select substr(date_time, 6, 2) as month from a1c_log 
        where substr(date_time, 1, 4) = :year union
        
        select substr(date_time, 6, 2) as month from note_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from insulin_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from medication_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from weight_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from ap_log
        where substr(date_time, 1, 4) = :year union

        select substr(date_time, 6, 2) as month from pulse_log
        where substr(date_time, 1, 4) = :year
        
        group by month order by month desc
    """)
    fun getMonths(year: String): Single<List<String>>

    @Query("""
        select substr(date_time, 1, 4) as year from glucose_log union
        select substr(date_time, 1, 4) as year from a1c_log union
        select substr(date_time, 1, 4) as year from note_log union
        select substr(date_time, 1, 4) as year from insulin_log union
        select substr(date_time, 1, 4) as year from medication_log union
        select substr(date_time, 1, 4) as year from weight_log union
        select substr(date_time, 1, 4) as year from ap_log union
        select substr(date_time, 1, 4) as year from pulse_log
        group by year order by year desc
    """)
    fun getYears(): Flowable<List<String>>
}