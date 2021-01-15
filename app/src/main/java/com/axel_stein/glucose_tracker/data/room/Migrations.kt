@file:Suppress("FunctionName")

package com.axel_stein.glucose_tracker.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun migration_1_2() = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS insulin_list (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                type INTEGER NOT NULL DEFAULT 0,
                active INTEGER NOT NULL DEFAULT 1
            )
            """
        )

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS insulin_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                insulin_id INTEGER NOT NULL, 
                units REAL NOT NULL, 
                measured INTEGER NOT NULL,
                date_time TEXT NOT NULL,
                FOREIGN KEY(insulin_id) REFERENCES insulin_list(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """
        )

        database.execSQL(""" 
            CREATE INDEX IF NOT EXISTS index_insulin_log_insulin_id ON insulin_log(insulin_id)
            """
        )

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS medication_list (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                dosage_form INTEGER NOT NULL DEFAULT 0,
                dosage REAL NOT NULL DEFAULT 0,
                dosage_unit INTEGER NOT NULL DEFAULT -1,
                active INTEGER NOT NULL DEFAULT 1
            )
            """
        )

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS medication_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                medication_id INTEGER NOT NULL, 
                amount REAL NOT NULL, 
                measured INTEGER NOT NULL, 
                date_time TEXT NOT NULL, 
                FOREIGN KEY(medication_id) REFERENCES medication_list(id) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
            """
        )

        database.execSQL(""" 
            CREATE INDEX IF NOT EXISTS index_medication_log_medication_id ON medication_log(medication_id)
            """
        )

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS weight_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                kg REAL NOT NULL DEFAULT 0,
                pounds REAL NOT NULL DEFAULT 0,
                date_time TEXT NOT NULL
            )
            """
        )

        database.execSQL("drop table food_list")
        database.execSQL("drop table food_log")
    }
}