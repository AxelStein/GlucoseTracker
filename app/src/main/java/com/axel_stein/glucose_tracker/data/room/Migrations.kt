@file:Suppress("FunctionName")

package com.axel_stein.glucose_tracker.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun migration_1_2() = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE insulin_list (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                type INTEGER DEFAULT 0 NOT NULL,
                active INTEGER DEFAULT 1 NOT NULL
            )
            """
        )

        database.execSQL(""" 
            CREATE TABLE insulin_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                insulin_id INTEGER NOT NULL REFERENCES insulin_list(id) ON DELETE CASCADE,
                units REAL NOT NULL,
                measured INTEGER NOT NULL,
                date_time TEXT NOT NULL
            )
            """
        )

        database.execSQL(""" 
            CREATE INDEX index_insulin_log_insulin_id ON insulin_log(insulin_id)
            """
        )

        database.execSQL("""
            CREATE TABLE medication_list (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                dosage_form INTEGER DEFAULT 0 NOT NULL,
                dosage REAL DEFAULT 0 NOT NULL,
                dosage_unit INTEGER DEFAULT -1 NOT NULL,
                active INTEGER DEFAULT 1 NOT NULL
            )
            """
        )

        database.execSQL("""
            CREATE TABLE medication_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                medication_id INTEGER NOT NULL REFERENCES medication_list(id) ON DELETE CASCADE,
                amount REAL NOT NULL,
                measured INTEGER NOT NULL,
                date_time TEXT NOT NULL
            )
            """
        )

        database.execSQL(""" 
            CREATE INDEX index_medication_log_medication_id ON medication_log(medication_id)
            """
        )

        database.execSQL("""
            CREATE TABLE weight_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                kg REAL DEFAULT 0 NOT NULL,
                pounds REAL DEFAULT 0 NOT NULL,
                date_time TEXT NOT NULL
            )
            """
        )
    }
}