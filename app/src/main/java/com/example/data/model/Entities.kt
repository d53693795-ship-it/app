package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val name: String,
    val avatarUrl: String = "",
    val referralCode: String,
    val referredByCode: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSuspended: Boolean = false,
    val isProfileComplete: Boolean = true
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val ageRange: String = "25-34",
    val gender: String = "Not specified",
    val occupation: String = "Professional",
    val education: String = "Graduate",
    val city: String = "Bengaluru",
    val preferredCategory: String = "All",
    val upiId: String = "",
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val bankHolderName: String = ""
)

@Entity(tableName = "wallet_accounts")
data class WalletAccountEntity(
    @PrimaryKey val userId: String,
    val availableBalance: Double = 0.0,
    val pendingBalance: Double = 0.0,
    val lifetimeEarned: Double = 0.0,
    val lifetimeWithdrawn: Double = 0.0,
    val todayEarned: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    EARNING_PENDING,
    EARNING_APPROVED,
    EARNING_REVERSED,
    REFERRAL_BONUS,
    DAILY_BONUS,
    REWARD_ACTIVITY,
    WITHDRAWAL_REQUESTED,
    WITHDRAWAL_PROCESSING,
    WITHDRAWAL_COMPLETED,
    WITHDRAWAL_FAILED,
    WITHDRAWAL_REVERSED,
    ADMIN_ADJUSTMENT
}

enum class TransactionStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REVERSED,
    COMPLETED,
    FAILED
}

@Entity(
    tableName = "wallet_transactions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["timestamp"]),
        Index(value = ["partnerConversionId"], unique = false)
    ]
)
data class WalletTransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val amount: Double,
    val currency: String = "INR",
    val type: TransactionType,
    val status: TransactionStatus,
    val source: String,
    val referenceId: String = "",
    val partnerConversionId: String? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardAmount: Double,
    val estimatedMinutes: Int,
    val difficulty: String, // "Easy", "Medium", "In-depth"
    val rating: Double,
    val provider: String,
    val category: String,
    val partnerGrossRevenue: Double,
    val isActive: Boolean = true,
    val attemptsCount: Int = 0
)

@Entity(tableName = "survey_attempts")
data class SurveyAttemptEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val surveyId: String,
    val userId: String,
    val status: String, // "STARTED", "COMPLETED", "REWARDED", "DISQUALIFIED"
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val answersJson: String = "{}"
)

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "Apps", "Shopping", "Games", "Services", "Other"
    val rewardAmount: Double,
    val partnerGrossRevenue: Double,
    val requirements: String,
    val estimatedTime: String,
    val provider: String,
    val partnerUrl: String,
    val badge: String = "Hot",
    val isActive: Boolean = true
)

@Entity(tableName = "offer_clicks")
data class OfferClickEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val offerId: String,
    val userId: String,
    val trackingSessionId: String,
    val clickedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "offer_conversions",
    indices = [
        Index(value = ["conversionId"], unique = true),
        Index(value = ["userId"])
    ]
)
data class OfferConversionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversionId: String, // Idempotency key from partner webhook
    val offerId: String,
    val userId: String,
    val partnerRevenue: Double,
    val platformMargin: Double, // 10%
    val userReward: Double,     // 90%
    val status: String,         // "PENDING", "APPROVED", "REVERSED"
    val createdAt: Long = System.currentTimeMillis()
)

enum class PayoutMethod {
    UPI,
    BANK
}

enum class RiskLevel {
    LOW_RISK,
    MEDIUM_RISK,
    HIGH_RISK
}

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val amount: Double,
    val method: PayoutMethod,
    val payoutDetails: String, // UPI ID or Bank Account summary
    val status: String,        // "PENDING_APPROVAL", "PROCESSING", "COMPLETED", "REJECTED"
    val riskLevel: RiskLevel,
    val riskReason: String = "Automated risk evaluation",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val failureReason: String? = null
)

@Entity(tableName = "daily_bonuses")
data class DailyBonusStreakEntity(
    @PrimaryKey val userId: String,
    val streakDay: Int = 1,
    val lastClaimedDate: String = "",
    val totalCoinsEarned: Double = 0.0
)

@Entity(tableName = "notifications")
data class NotificationItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "SURVEY", "REWARD_PENDING", "REWARD_APPROVED", "WITHDRAWAL", "BONUS", "SYSTEM"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val category: String, // "Missing Reward", "Withdrawal Problem", "Account Problem", "Referral Problem", "Technical Problem", "Other"
    val subject: String,
    val message: String,
    val status: String = "Open", // "Open", "In Review", "Waiting for User", "Resolved", "Closed"
    val createdAt: Long = System.currentTimeMillis(),
    val adminReply: String? = null
)

@Entity(tableName = "fraud_events")
data class FraudEventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val signalType: String,
    val riskScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reviewStatus: String = "PENDING" // "PENDING", "RESOLVED", "IGNORED"
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val adminId: String = "admin@earnmate.internal",
    val action: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
