package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface EarnMateDao {

    // --- User & Profile ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getProfileFlow(userId: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    // --- Wallet Account ---
    @Query("SELECT * FROM wallet_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getWalletAccount(userId: String): WalletAccountEntity?

    @Query("SELECT * FROM wallet_accounts WHERE userId = :userId LIMIT 1")
    fun getWalletAccountFlow(userId: String): Flow<WalletAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletAccount(wallet: WalletAccountEntity)

    @Update
    suspend fun updateWalletAccount(wallet: WalletAccountEntity)

    // --- Wallet Transactions ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)

    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: String): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getAllTransactionsFlow(): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): WalletTransactionEntity?

    @Update
    suspend fun updateTransaction(transaction: WalletTransactionEntity)

    // --- Surveys ---
    @Query("SELECT * FROM surveys WHERE isActive = 1")
    fun getSurveysFlow(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE id = :id LIMIT 1")
    suspend fun getSurveyById(id: String): SurveyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveys(surveys: List<SurveyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveyAttempt(attempt: SurveyAttemptEntity)

    @Query("SELECT * FROM survey_attempts WHERE userId = :userId ORDER BY startedAt DESC")
    fun getSurveyAttemptsFlow(userId: String): Flow<List<SurveyAttemptEntity>>

    // --- Offers ---
    @Query("SELECT * FROM offers WHERE isActive = 1")
    fun getOffersFlow(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE id = :id LIMIT 1")
    suspend fun getOfferById(id: String): OfferEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfferClick(click: OfferClickEntity)

    @Query("SELECT * FROM offer_clicks WHERE trackingSessionId = :sessionId LIMIT 1")
    suspend fun getClickBySession(sessionId: String): OfferClickEntity?

    // --- Conversions & Idempotency ---
    @Query("SELECT * FROM offer_conversions WHERE conversionId = :conversionId LIMIT 1")
    suspend fun getConversionById(conversionId: String): OfferConversionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertConversion(conversion: OfferConversionEntity): Long

    @Query("SELECT * FROM offer_conversions ORDER BY createdAt DESC")
    fun getAllConversionsFlow(): Flow<List<OfferConversionEntity>>

    // --- Withdrawals ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getWithdrawalsFlow(userId: String): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY requestedAt DESC")
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalById(id: String): WithdrawalEntity?

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)

    // --- Daily Bonus ---
    @Query("SELECT * FROM daily_bonuses WHERE userId = :userId LIMIT 1")
    suspend fun getDailyBonus(userId: String): DailyBonusStreakEntity?

    @Query("SELECT * FROM daily_bonuses WHERE userId = :userId LIMIT 1")
    fun getDailyBonusFlow(userId: String): Flow<DailyBonusStreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyBonus(bonus: DailyBonusStreakEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsFlow(userId: String): Flow<List<NotificationItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItemEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSupportTicketsFlow(userId: String): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllSupportTicketsFlow(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateSupportTicket(ticket: SupportTicketEntity)

    // --- Fraud & Risk ---
    @Query("SELECT * FROM fraud_events ORDER BY timestamp DESC")
    fun getFraudEventsFlow(): Flow<List<FraudEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFraudEvent(event: FraudEventEntity)

    @Update
    suspend fun updateFraudEvent(event: FraudEventEntity)

    // --- App Settings ---
    @Query("SELECT * FROM app_settings")
    fun getAppSettingsFlow(): Flow<List<AppSettingEntity>>

    @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSettingEntity)

    // --- Admin Audit Logs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity)

    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogsFlow(): Flow<List<AdminAuditLogEntity>>

    // --- Anonymize / Delete user data ---
    @Query("UPDATE users SET name = 'Deleted User', email = 'anonymized@earnmate.internal', avatarUrl = '', isSuspended = 1 WHERE id = :userId")
    suspend fun anonymizeUser(userId: String)
}
