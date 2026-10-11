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
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6 = androidx.room.migration.Migration(5, 6) { database ->
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS threat_items_new (
                    id TEXT PRIMARY KEY NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    fullContent TEXT NOT NULL,
                    source TEXT NOT NULL,
                    link TEXT NOT NULL,
                    pubDateMillis INTEGER NOT NULL,
                    severity TEXT NOT NULL,
                    category TEXT NOT NULL,
                    cveIdsJson TEXT NOT NULL,
                    tagsJson TEXT NOT NULL,
                    isBookmarked INTEGER NOT NULL,
                    isRead INTEGER NOT NULL,
                    aiSummary TEXT,
                    aiRemediationStepsJson TEXT,
                    aiRiskScore INTEGER,
                    aiMitigationCode TEXT,
                    cisaKev INTEGER NOT NULL DEFAULT 0,
                    epssScore REAL
                )
            """)
            database.execSQL("""
                INSERT INTO threat_items_new (
                    id, title, description, fullContent, source, link, pubDateMillis, severity, category, 
                    cveIdsJson, tagsJson, isBookmarked, isRead, aiSummary, aiRemediationStepsJson, aiRiskScore, aiMitigationCode, 
                    cisaKev, epssScore
                )
                SELECT 
                    id, title, description, fullContent, source, link, pubDateMillis, severity, category, 
                    cveIdsJson, tagsJson, isBookmarked, isRead, aiSummary, aiRemediationStepsJson, aiRiskScore, aiMitigationCode, 
                    0, NULL
                FROM threat_items
            """)
            database.execSQL("DROP TABLE threat_items")
            database.execSQL("ALTER TABLE threat_items_new RENAME TO threat_items")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_threat_items_pubDateMillis ON threat_items(pubDateMillis)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_threat_items_isBookmarked ON threat_items(isBookmarked)")
            database.execSQL("CREATE INDEX IF NOT EXISTS index_threat_items_source ON threat_items(source)")
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cybershield.db"
                ).addMigrations(MIGRATION_5_6).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
