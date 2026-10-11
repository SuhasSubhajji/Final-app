package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ThreatItemEntity::class,
        SecurityAlertEntity::class,
        GlossaryTermEntity::class,
        DynamicRoadmapTopicEntity::class,
        AttackScenarioEntity::class,
        ThreatHistoryEntity::class,
        TtpProgressEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_6_7 = androidx.room.migration.Migration(6, 7) { database ->
            val cursor = database.query("PRAGMA table_info(threat_items)")
            var hasCisaKev = false
            var hasEpssScore = false
            
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex != -1) {
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex)
                    if (name == "cisaKev") hasCisaKev = true
                    if (name == "epssScore") hasEpssScore = true
                }
            }
            cursor.close()

            if (!hasCisaKev) {
                database.execSQL("ALTER TABLE threat_items ADD COLUMN cisaKev INTEGER NOT NULL DEFAULT 0")
            }
            if (!hasEpssScore) {
                database.execSQL("ALTER TABLE threat_items ADD COLUMN epssScore REAL")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cybershield.db"
                ).addMigrations(MIGRATION_6_7).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
