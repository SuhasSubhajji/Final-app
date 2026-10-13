package com.example.data.mitre.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TacticEntity::class,
        TechniqueEntity::class,
        TechniqueTacticCrossRef::class,
        ProcedureEntity::class,
        MitigationEntity::class,
        DetectionEntity::class,
        D3fendTacticEntity::class,
        D3fendTechniqueEntity::class,
        RedirectEntity::class,
        TechniqueFtsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MitreDatabase : RoomDatabase() {
    abstract fun mitreDao(): MitreDao

    companion object {
        @Volatile
        private var INSTANCE: MitreDatabase? = null

        fun getInstance(context: Context): MitreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MitreDatabase::class.java,
                    "mitre_attack_d3fend.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
