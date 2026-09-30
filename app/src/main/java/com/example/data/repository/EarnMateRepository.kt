package com.example.data.repository

import com.example.data.dao.EarnMateDao
import com.example.data.model.AdminAuditLogEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.DailyBonusStreakEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.OfferClickEntity
import com.example.data.model.OfferConversionEntity
import com.example.data.model.OfferEntity
import com.example.data.model.PayoutMethod
import com.example.data.model.RiskLevel
import com.example.data.model.SupportTicketEntity
import com.example.data.model.SurveyAttemptEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WalletAccountEntity
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WithdrawalEntity
import com.example.domain.engine.AntiFraudEngine
import com.example.domain.engine.RewardEngine
import com.example.domain.providers.MockOfferProvider
import com.example.domain.providers.MockPayoutProvider
import com.example.domain.providers.MockRewardProvider
import com.example.domain.providers.MockSurveyProvider
import com.example.domain.providers.OfferProvider
import com.example.domain.providers.PayoutProvider
import com.example.domain.providers.RewardActivityProvider
import com.example.domain.providers.RewardActivityResult
import com.example.domain.providers.SurveyCompletionResult
import com.example.domain.providers.SurveyProvider
import com.example.domain.providers.SurveySession
import com.example.domain.providers.TrackingSession
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EarnMateRepository(
    private val dao: EarnMateDao,
    private val surveyProvider: SurveyProvider = MockSurveyProvider(),
    private val offerProvider: OfferProvider = MockOfferProvider(),
    private val payoutProvider: PayoutProvider = MockPayoutProvider(),
    private val rewardProvider: RewardActivityProvider = MockRewardProvider(),
    private val rewardEngine: RewardEngine = RewardEngine(defaultMarginPercentage = 10.0),
    private val antiFraudEngine: AntiFraudEngine = AntiFraudEngine()
) {

    // --- Flows for UI ---
    fun getWalletFlow(userId: String): Flow<WalletAccountEntity?> = dao.getWalletAccountFlow(userId)
    fun getTransactionsFlow(userId: String): Flow<List<WalletTransactionEntity>> = dao.getTransactionsFlow(userId)
    fun getSurveysFlow(): Flow<List<SurveyEntity>> = dao.getSurveysFlow()
    fun getOffersFlow(): Flow<List<OfferEntity>> = dao.getOffersFlow()
    fun getWithdrawalsFlow(userId: String): Flow<List<WithdrawalEntity>> = dao.getWithdrawalsFlow(userId)
    fun getNotificationsFlow(userId: String): Flow<List<NotificationItemEntity>> = dao.getNotificationsFlow(userId)
    fun getDailyBonusFlow(userId: String): Flow<DailyBonusStreakEntity?> = dao.getDailyBonusFlow(userId)
    fun getSupportTicketsFlow(userId: String): Flow<List<SupportTicketEntity>> = dao.getSupportTicketsFlow(userId)
    fun getProfileFlow(userId: String): Flow<UserProfileEntity?> = dao.getProfileFlow(userId)

    // --- Admin Flows ---
    fun getAllUsersFlow(): Flow<List<UserEntity>> = dao.getAllUsersFlow()
    fun getAllConversionsFlow(): Flow<List<OfferConversionEntity>> = dao.getAllConversionsFlow()
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalEntity>> = dao.getAllWithdrawalsFlow()
    fun getAllTransactionsFlow(): Flow<List<WalletTransactionEntity>> = dao.getAllTransactionsFlow()
    fun getFraudEventsFlow() = dao.getFraudEventsFlow()
    fun getAppSettingsFlow(): Flow<List<AppSettingEntity>> = dao.getAppSettingsFlow()
    fun getAuditLogsFlow() = dao.getAuditLogsFlow()

    // --- Initialization & Seeding ---
    suspend fun seedInitialDataIfNeeded(defaultUserId: String = "usr_rahul_sharma_01") {
        val existingUser = dao.getUserById(defaultUserId)
        if (existingUser == null) {
            val user = UserEntity(
                id = defaultUserId,
                email = "rahul.sharma@example.com",
                name = "Rahul Sharma",
                referralCode = "EARNMATE123",
                isProfileComplete = true
            )
            dao.insertUser(user)

            val profile = UserProfileEntity(
                userId = defaultUserId,
                ageRange = "25-34",
                gender = "Male",
                occupation = "Software Engineer",
                education = "Graduate Degree",
                city = "Bengaluru",
                upiId = "rahul@okhdfcbank"
            )
            dao.insertProfile(profile)

            val wallet = WalletAccountEntity(
                userId = defaultUserId,
                availableBalance = 125.50,
                pendingBalance = 40.00,
                lifetimeEarned = 1250.00,
                lifetimeWithdrawn = 1000.00,
                todayEarned = 25.00
            )
            dao.insertWalletAccount(wallet)

            // Seed initial sample ledger transactions
            val initialTransactions = listOf(
                WalletTransactionEntity(
                    id = "tx_seed_01",
                    userId = defaultUserId,
                    amount = 50.0,
                    type = TransactionType.EARNING_APPROVED,
                    status = TransactionStatus.APPROVED,
                    source = "ResearchPulse Survey",
                    notes = "Digital Payments Survey Completed",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                WalletTransactionEntity(
                    id = "tx_seed_02",
                    userId = defaultUserId,
                    amount = 40.0,
                    type = TransactionType.EARNING_PENDING,
                    status = TransactionStatus.PENDING,
                    source = "BlinkFast Offer",
                    notes = "Awaiting partner grocery delivery verification",
                    timestamp = System.currentTimeMillis() - 3600000L * 4
                ),
                WalletTransactionEntity(
                    id = "tx_seed_03",
                    userId = defaultUserId,
                    amount = 100.0,
                    type = TransactionType.WITHDRAWAL_COMPLETED,
                    status = TransactionStatus.COMPLETED,
                    source = "UPI Payout",
                    notes = "Transferred to rahul@okhdfcbank (Ref: IMPS_894372)",
                    timestamp = System.currentTimeMillis() - 86400000L * 5
                ),
                WalletTransactionEntity(
                    id = "tx_seed_04",
                    userId = defaultUserId,
                    amount = 10.0,
                    type = TransactionType.DAILY_BONUS,
                    status = TransactionStatus.APPROVED,
                    source = "Day 2 Login Streak",
                    notes = "Daily attendance reward",
                    timestamp = System.currentTimeMillis() - 86400000L * 1
                ),
                WalletTransactionEntity(
                    id = "tx_seed_05",
                    userId = defaultUserId,
                    amount = 15.0,
                    type = TransactionType.REFERRAL_BONUS,
                    status = TransactionStatus.APPROVED,
                    source = "Referral Reward",
                    notes = "Friend Vikas completed qualifying survey",
                    timestamp = System.currentTimeMillis() - 86400000L * 3
                )
            )
            initialTransactions.forEach { dao.insertTransaction(it) }

            // Seed daily bonus streak
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(System.currentTimeMillis() - 86400000L))
            dao.insertDailyBonus(
                DailyBonusStreakEntity(
                    userId = defaultUserId,
                    streakDay = 2,
                    lastClaimedDate = todayStr,
                    totalCoinsEarned = 15.0
                )
            )

            // Seed initial notifications
            dao.insertNotification(
                NotificationItemEntity(
                    userId = defaultUserId,
                    title = "Reward Approved! +₹50.00",
                    message = "Your survey for ResearchPulse was approved and ₹50.00 has been credited to your available balance.",
                    type = "REWARD_APPROVED",
                    timestamp = System.currentTimeMillis() - 7200000L
                )
            )
            dao.insertNotification(
                NotificationItemEntity(
                    userId = defaultUserId,
                    title = "New High-Paying Survey Available",
                    message = "Smartphone & 5G Connectivity Study is now live. Earn up to ₹72.00.",
                    type = "SURVEY",
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )

            // Seed default settings
            dao.insertSetting(AppSettingEntity("min_withdrawal", "100.0"))
            dao.insertSetting(AppSettingEntity("platform_margin_pct", "10.0"))
            dao.insertSetting(AppSettingEntity("referral_reward", "10.0"))
            dao.insertSetting(AppSettingEntity("maintenance_mode", "false"))
        }

        // Seed Surveys and Offers if empty
        val surveys = surveyProvider.getAvailableSurveys()
        dao.insertSurveys(surveys)

        val offers = offerProvider.getAvailableOffers()
        dao.insertOffers(offers)
    }

    // --- Authentication ---
    suspend fun getOrCreateUser(email: String, name: String, avatarUrl: String = ""): UserEntity {
        val existing = dao.getUserByEmail(email)
        if (existing != null) return existing

        val newId = "usr_${UUID.randomUUID().toString().take(8)}"
        val refCode = "MATE${(1000..9999).random()}"
        val user = UserEntity(
            id = newId,
            email = email,
            name = name,
            avatarUrl = avatarUrl,
            referralCode = refCode
        )
        dao.insertUser(user)
        dao.insertProfile(UserProfileEntity(userId = newId))
        dao.insertWalletAccount(WalletAccountEntity(userId = newId))
        dao.insertDailyBonus(DailyBonusStreakEntity(userId = newId, streakDay = 1))
        return user
    }

    suspend fun getUser(userId: String): UserEntity? = dao.getUserById(userId)

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.insertProfile(profile)
    }

    // --- Survey Lifecycle ---
    suspend fun startSurvey(surveyId: String, userId: String): SurveySession {
        dao.insertSurveyAttempt(
            SurveyAttemptEntity(
                surveyId = surveyId,
                userId = userId,
                status = "STARTED",
                startedAt = System.currentTimeMillis()
            )
        )
        return surveyProvider.startSurvey(surveyId, userId)
    }

    suspend fun submitSurvey(
        sessionId: String,
        surveyId: String,
        userId: String,
        answers: Map<String, String>,
        startTimeMs: Long
    ): Result<SurveyCompletionResult> {
        val survey = dao.getSurveyById(surveyId) ?: return Result.failure(Exception("Survey not found"))
        val elapsedSec = (System.currentTimeMillis() - startTimeMs) / 1000

        // Fraud check on completion velocity
        val fraudEval = antiFraudEngine.evaluateSurveyCompletion(userId, elapsedSec, survey.estimatedMinutes)
        if (fraudEval.fraudEvent != null) {
            dao.insertFraudEvent(fraudEval.fraudEvent)
        }

        val result = surveyProvider.submitSurvey(sessionId, surveyId, userId, answers)
        if (!result.isSuccess) {
            return Result.failure(Exception(result.message))
        }

        // Backend calculates reward: 10% platform gross margin, 90% user reward
        val breakdown = rewardEngine.calculateReward(result.partnerGrossRevenue)

        // Credit to Wallet ledger
        creditReward(
            userId = userId,
            partnerRevenue = breakdown.partnerRevenue,
            userReward = breakdown.userReward,
            platformMargin = breakdown.platformMarginAmount,
            source = "Survey: ${survey.title}",
            referenceId = survey.id,
            conversionId = result.partnerConversionId,
            autoApprove = true
        )

        dao.insertNotification(
            NotificationItemEntity(
                userId = userId,
                title = "Survey Reward Approved! +₹${String.format(Locale.US, "%.2f", breakdown.userReward)}",
                message = "Your response for '${survey.title}' was verified. ₹${String.format(Locale.US, "%.2f", breakdown.userReward)} credited to your wallet.",
                type = "REWARD_APPROVED"
            )
        )

        return Result.success(result)
    }

    // --- Offer Lifecycle & Webhooks ---
    suspend fun startOffer(offerId: String, userId: String): TrackingSession {
        val session = offerProvider.createTrackingSession(offerId, userId)
        dao.insertOfferClick(
            OfferClickEntity(
                offerId = offerId,
                userId = userId,
                trackingSessionId = session.trackingId
            )
        )
        return session
    }

    /**
     * Webhook conversion verification with strict Idempotency.
     * Duplicate conversions are safely ignored without double-crediting.
     */
    suspend fun handlePartnerConversionWebhook(
        conversionId: String,
        offerId: String,
        userId: String,
        partnerRevenue: Double
    ): Result<String> {
        // Idempotency check:
        val existing = dao.getConversionById(conversionId)
        if (existing != null) {
            return Result.success("Conversion $conversionId already processed. Idempotent acknowledgment returned.")
        }

        val offer = dao.getOfferById(offerId) ?: return Result.failure(Exception("Offer not found"))
        val breakdown = rewardEngine.calculateReward(partnerRevenue)

        val conversion = OfferConversionEntity(
            conversionId = conversionId,
            offerId = offerId,
            userId = userId,
            partnerRevenue = breakdown.partnerRevenue,
            platformMargin = breakdown.platformMarginAmount,
            userReward = breakdown.userReward,
            status = "APPROVED"
        )
        val insertedRow = dao.insertConversion(conversion)
        if (insertedRow <= 0) {
            return Result.success("Duplicate conversion ignored.")
        }

        // Ledger credit
        creditReward(
            userId = userId,
            partnerRevenue = breakdown.partnerRevenue,
            userReward = breakdown.userReward,
            platformMargin = breakdown.platformMarginAmount,
            source = "Offer: ${offer.title}",
            referenceId = offer.id,
            conversionId = conversionId,
            autoApprove = true
        )

        dao.insertNotification(
            NotificationItemEntity(
                userId = userId,
                title = "Offer Reward Credited! +₹${String.format(Locale.US, "%.2f", breakdown.userReward)}",
                message = "Conversion for '${offer.title}' verified by partner. Enjoy your reward!",
                type = "REWARD_APPROVED"
            )
        )

        return Result.success("Conversion verified and credited successfully.")
    }

    // --- Permitted Reward Activity ---
    suspend fun getRewardActivities() = rewardProvider.getAvailableActivities()

    suspend fun completeRewardActivity(activityId: String, userId: String): RewardActivityResult {
        val result = rewardProvider.completeActivity(activityId, userId)
        if (result.isSuccess) {
            val wallet = dao.getWalletAccount(userId) ?: WalletAccountEntity(userId = userId)
            val updated = wallet.copy(
                availableBalance = wallet.availableBalance + result.earnedReward,
                lifetimeEarned = wallet.lifetimeEarned + result.earnedReward,
                todayEarned = wallet.todayEarned + result.earnedReward,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateWalletAccount(updated)

            dao.insertTransaction(
                WalletTransactionEntity(
                    userId = userId,
                    amount = result.earnedReward,
                    type = TransactionType.REWARD_ACTIVITY,
                    status = TransactionStatus.APPROVED,
                    source = "Reward Activity",
                    referenceId = result.activityReferenceId,
                    notes = result.message
                )
            )

            dao.insertNotification(
                NotificationItemEntity(
                    userId = userId,
                    title = "Activity Reward Earned! +₹${String.format(Locale.US, "%.2f", result.earnedReward)}",
                    message = result.message,
                    type = "BONUS"
                )
            )
        }
        return result
    }

    // --- Centralized Ledger Credit ---
    private suspend fun creditReward(
        userId: String,
        partnerRevenue: Double,
        userReward: Double,
        platformMargin: Double,
        source: String,
        referenceId: String,
        conversionId: String,
        autoApprove: Boolean
    ) {
        val wallet = dao.getWalletAccount(userId) ?: WalletAccountEntity(userId = userId)

        if (autoApprove) {
            val updatedWallet = wallet.copy(
                availableBalance = wallet.availableBalance + userReward,
                lifetimeEarned = wallet.lifetimeEarned + userReward,
                todayEarned = wallet.todayEarned + userReward,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateWalletAccount(updatedWallet)

            dao.insertTransaction(
                WalletTransactionEntity(
                    userId = userId,
                    amount = userReward,
                    type = TransactionType.EARNING_APPROVED,
                    status = TransactionStatus.APPROVED,
                    source = source,
                    referenceId = referenceId,
                    partnerConversionId = conversionId,
                    notes = "Partner gross ₹${String.format(Locale.US, "%.2f", partnerRevenue)} (Platform margin: ₹${String.format(Locale.US, "%.2f", platformMargin)})"
                )
            )
        } else {
            val updatedWallet = wallet.copy(
                pendingBalance = wallet.pendingBalance + userReward,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateWalletAccount(updatedWallet)

            dao.insertTransaction(
                WalletTransactionEntity(
                    userId = userId,
                    amount = userReward,
                    type = TransactionType.EARNING_PENDING,
                    status = TransactionStatus.PENDING,
                    source = source,
                    referenceId = referenceId,
                    partnerConversionId = conversionId,
                    notes = "Pending partner completion verification"
                )
            )
        }
    }

    // --- Withdrawals ---
    suspend fun requestWithdrawal(
        userId: String,
        amount: Double,
        method: PayoutMethod,
        details: String
    ): Result<WithdrawalEntity> {
        val wallet = dao.getWalletAccount(userId) ?: return Result.failure(Exception("Wallet account not found"))
        val minSetting = dao.getSettingValue("min_withdrawal")?.toDoubleOrNull() ?: 100.0

        if (amount < minSetting) {
            return Result.failure(Exception("Minimum withdrawal amount is ₹${String.format(Locale.US, "%.2f", minSetting)}."))
        }

        if (wallet.availableBalance < amount) {
            return Result.failure(Exception("Insufficient available balance. You have ₹${String.format(Locale.US, "%.2f", wallet.availableBalance)}."))
        }

        // Validate payout account format
        val validation = payoutProvider.validatePayoutAccount(method, details)
        if (!validation.isValid) {
            return Result.failure(Exception(validation.message))
        }

        // Risk evaluation
        val fraudEval = antiFraudEngine.evaluateWithdrawalRisk(
            userId = userId,
            amount = amount,
            availableBalance = wallet.availableBalance,
            lifetimeEarned = wallet.lifetimeEarned,
            recentWithdrawalsCount24h = 0
        )
        if (fraudEval.fraudEvent != null) {
            dao.insertFraudEvent(fraudEval.fraudEvent)
        }

        val withdrawalId = "wd_${UUID.randomUUID().toString().take(10)}"
        val initialStatus = if (fraudEval.riskLevel == RiskLevel.HIGH_RISK) "PENDING_APPROVAL" else "PROCESSING"

        // Deduct from available balance (reserve funds)
        val updatedWallet = wallet.copy(
            availableBalance = wallet.availableBalance - amount,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateWalletAccount(updatedWallet)

        val withdrawal = WithdrawalEntity(
            id = withdrawalId,
            userId = userId,
            amount = amount,
            method = method,
            payoutDetails = validation.cleanDetails,
            status = initialStatus,
            riskLevel = fraudEval.riskLevel,
            riskReason = fraudEval.reason
        )
        dao.insertWithdrawal(withdrawal)

        dao.insertTransaction(
            WalletTransactionEntity(
                userId = userId,
                amount = amount,
                type = TransactionType.WITHDRAWAL_REQUESTED,
                status = TransactionStatus.PENDING,
                source = "${method.name} Withdrawal",
                referenceId = withdrawalId,
                notes = "Reserved for payout to ${validation.cleanDetails}. Risk: ${fraudEval.riskLevel}"
            )
        )

        // If low/medium risk, dispatch to payout provider
        if (fraudEval.riskLevel != RiskLevel.HIGH_RISK) {
            val execution = payoutProvider.initiatePayout(withdrawalId, amount, method, validation.cleanDetails)
            if (execution.isSuccess) {
                val completedWithdrawal = withdrawal.copy(
                    status = "COMPLETED",
                    processedAt = System.currentTimeMillis()
                )
                dao.updateWithdrawal(completedWithdrawal)

                // Update lifetime withdrawn
                val finalWallet = dao.getWalletAccount(userId) ?: updatedWallet
                dao.updateWalletAccount(
                    finalWallet.copy(
                        lifetimeWithdrawn = finalWallet.lifetimeWithdrawn + amount
                    )
                )

                dao.insertTransaction(
                    WalletTransactionEntity(
                        userId = userId,
                        amount = amount,
                        type = TransactionType.WITHDRAWAL_COMPLETED,
                        status = TransactionStatus.COMPLETED,
                        source = "${method.name} Payout",
                        referenceId = execution.referenceId,
                        notes = "Completed. Ref: ${execution.referenceId}"
                    )
                )

                dao.insertNotification(
                    NotificationItemEntity(
                        userId = userId,
                        title = "Withdrawal Completed! ₹${String.format(Locale.US, "%.2f", amount)}",
                        message = "₹${String.format(Locale.US, "%.2f", amount)} transferred to ${validation.cleanDetails}. Ref: ${execution.referenceId}",
                        type = "WITHDRAWAL"
                    )
                )
            }
        } else {
            dao.insertNotification(
                NotificationItemEntity(
                    userId = userId,
                    title = "Withdrawal Under Review",
                    message = "Your withdrawal of ₹${String.format(Locale.US, "%.2f", amount)} is held for manual security review. It will be processed shortly.",
                    type = "WITHDRAWAL"
                )
            )
        }

        return Result.success(withdrawal)
    }

    // --- Daily Bonus Claim with Server Time Simulation ---
    suspend fun claimDailyBonus(userId: String): Result<Double> {
        val bonus = dao.getDailyBonus(userId) ?: DailyBonusStreakEntity(userId = userId)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        if (bonus.lastClaimedDate == todayStr) {
            return Result.failure(Exception("You have already claimed today's bonus! Check back tomorrow."))
        }

        val streakReward = when (bonus.streakDay) {
            1 -> 5.0
            2 -> 10.0
            3 -> 15.0
            4 -> 20.0
            5 -> 25.0
            6 -> 30.0
            7 -> 50.0
            else -> 5.0
        }

        val nextStreakDay = if (bonus.streakDay >= 7) 1 else bonus.streakDay + 1
        val updatedBonus = bonus.copy(
            streakDay = nextStreakDay,
            lastClaimedDate = todayStr,
            totalCoinsEarned = bonus.totalCoinsEarned + streakReward
        )
        dao.insertDailyBonus(updatedBonus)

        // Update Wallet
        val wallet = dao.getWalletAccount(userId) ?: WalletAccountEntity(userId = userId)
        dao.updateWalletAccount(
            wallet.copy(
                availableBalance = wallet.availableBalance + streakReward,
                lifetimeEarned = wallet.lifetimeEarned + streakReward,
                todayEarned = wallet.todayEarned + streakReward,
                updatedAt = System.currentTimeMillis()
            )
        )

        dao.insertTransaction(
            WalletTransactionEntity(
                userId = userId,
                amount = streakReward,
                type = TransactionType.DAILY_BONUS,
                status = TransactionStatus.APPROVED,
                source = "Day ${bonus.streakDay} Daily Bonus",
                notes = "Daily login streak reward"
            )
        )

        dao.insertNotification(
            NotificationItemEntity(
                userId = userId,
                title = "Daily Bonus Claimed! +₹${String.format(Locale.US, "%.2f", streakReward)}",
                message = "Day ${bonus.streakDay} streak bonus added to your balance. Keep your streak going!",
                type = "BONUS"
            )
        )

        return Result.success(streakReward)
    }

    // --- Referral System ---
    suspend fun applyReferralCode(userId: String, code: String): Result<String> {
        val trimmed = code.trim().uppercase()
        if (trimmed == "EARNMATE123" || trimmed.startsWith("EARN") || trimmed.startsWith("MATE")) {
            val user = dao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
            dao.updateUser(user.copy(referredByCode = trimmed))
            return Result.success("Referral code applied! Bonus unlocks when you complete your first verified survey.")
        }
        return Result.failure(Exception("Invalid referral code. Please check and try again."))
    }

    // --- Support Tickets ---
    suspend fun submitSupportTicket(
        userId: String,
        category: String,
        subject: String,
        message: String
    ): SupportTicketEntity {
        val ticket = SupportTicketEntity(
            userId = userId,
            category = category,
            subject = subject,
            message = message,
            status = "Open"
        )
        dao.insertSupportTicket(ticket)
        return ticket
    }

    // --- Account Deletion & Anonymization ---
    suspend fun requestAccountDeletion(userId: String) {
        // Anonymize user records per privacy & legal requirements while retaining immutable financial transactions
        dao.anonymizeUser(userId)
        dao.insertAuditLog(
            AdminAuditLogEntity(
                action = "ACCOUNT_DELETION_ANONYMIZED",
                targetType = "USER",
                targetId = userId,
                details = "User personal details anonymized per data privacy policy. Financial ledger records preserved for audit."
            )
        )
    }

    // --- Admin Operations ---
    suspend fun approveWithdrawal(withdrawalId: String, adminNotes: String = "") {
        val withdrawal = dao.getWithdrawalById(withdrawalId) ?: return
        dao.updateWithdrawal(
            withdrawal.copy(
                status = "COMPLETED",
                processedAt = System.currentTimeMillis(),
                riskReason = "$adminNotes (Approved by Admin)"
            )
        )
        val wallet = dao.getWalletAccount(withdrawal.userId)
        if (wallet != null) {
            dao.updateWalletAccount(
                wallet.copy(
                    lifetimeWithdrawn = wallet.lifetimeWithdrawn + withdrawal.amount
                )
            )
        }
        dao.insertTransaction(
            WalletTransactionEntity(
                userId = withdrawal.userId,
                amount = withdrawal.amount,
                type = TransactionType.WITHDRAWAL_COMPLETED,
                status = TransactionStatus.COMPLETED,
                source = "${withdrawal.method} Payout",
                referenceId = withdrawalId,
                notes = "Approved by Admin. $adminNotes"
            )
        )
        dao.insertAuditLog(
            AdminAuditLogEntity(
                action = "WITHDRAWAL_APPROVED",
                targetType = "WITHDRAWAL",
                targetId = withdrawalId,
                details = "Approved withdrawal ₹${withdrawal.amount} for user ${withdrawal.userId}"
            )
        )
    }

    suspend fun updateAppSetting(key: String, value: String) {
        dao.insertSetting(AppSettingEntity(key, value))
        dao.insertAuditLog(
            AdminAuditLogEntity(
                action = "SETTING_CHANGED",
                targetType = "APP_SETTINGS",
                targetId = key,
                details = "Updated setting $key to $value"
            )
        )
    }

    suspend fun markAllNotificationsRead(userId: String) {
        dao.markAllNotificationsRead(userId)
    }
}
