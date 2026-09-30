package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.EarnMateDao
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.DailyBonusStreakEntity
import com.example.data.model.FraudEventEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.OfferClickEntity
import com.example.data.model.OfferConversionEntity
import com.example.data.model.OfferEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.SurveyAttemptEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WalletAccountEntity
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WithdrawalEntity

@Database(
    entities = [
        UserEntity::class,
        UserProfileEntity::class,
        WalletAccountEntity::class,
        WalletTransactionEntity::class,
        SurveyEntity::class,
        SurveyAttemptEntity::class,
        OfferEntity::class,
        OfferClickEntity::class,
        OfferConversionEntity::class,
        WithdrawalEntity::class,
        DailyBonusStreakEntity::class,
        NotificationItemEntity::class,
        SupportTicketEntity::class,
        FraudEventEntity::class,
        AppSettingEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EarnMateDatabase : RoomDatabase() {

    abstract fun earnMateDao(): EarnMateDao

    companion object {
        @Volatile
        private var INSTANCE: EarnMateDatabase? = null

        fun getDatabase(context: Context): EarnMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EarnMateDatabase::class.java,
                    "earnmate_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
