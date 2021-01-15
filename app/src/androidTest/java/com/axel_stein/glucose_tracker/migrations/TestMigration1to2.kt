package com.axel_stein.glucose_tracker.migrations

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.axel_stein.glucose_tracker.data.room.AppDatabase
import com.axel_stein.glucose_tracker.data.room.migration_1_2
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TestMigration1to2 {
    @JvmField
    @Rule
    val testHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db1 = testHelper.createDatabase(context.packageName, 1)
        val db2 = testHelper.runMigrationsAndValidate(context.packageName, 2, true, migration_1_2())
        assertTrue(db2.isDatabaseIntegrityOk)
    }
}