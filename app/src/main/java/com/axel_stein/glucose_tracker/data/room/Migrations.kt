@file:Suppress("FunctionName")

package com.axel_stein.glucose_tracker.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

fun migration_1_2() = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            create table insulin_list (
                id integer primary key autoincrement,
                title text,
                type integer default 0,
                active integer default 1
            )
            """
        )

        database.execSQL(""" 
            create table insulin_log (
                id integer primary key autoincrement,
                insulin_id integer,
                units integer,
                measured integer,
                date_time text
            )
            """
        )

        database.execSQL("""
            create table medication_list (
                id integer primary key autoincrement,
                title text not null,
                dosage_form integer default 0,
                dosage integer default 0,
                dosage_unit integer default -1,
                active integer default 1
            )
            """
        )

        database.execSQL("""
            create table medication_log (
                id integer primary key autoincrement,
                medication_id integer,
                amount integer,
                measured integer,
                date_time text
            )
            """
        )

        database.execSQL("""
            create table weight_log (
                id integer primary key autoincrement,
                kg integer default 0,
                pounds integer default 0,
                date_time text
            )
            """
        )
    }
}