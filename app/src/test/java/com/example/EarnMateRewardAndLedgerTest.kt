package com.example

import com.example.data.model.PayoutMethod
import com.example.data.model.RiskLevel
import com.example.domain.engine.AntiFraudEngine
import com.example.domain.engine.RewardEngine
import com.example.domain.providers.MockOfferProvider
import com.example.domain.providers.MockPayoutProvider
import com.example.domain.providers.MockRewardProvider
import com.example.domain.providers.MockSurveyProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EarnMateRewardAndLedgerTest {

    private val rewardEngine = RewardEngine(defaultMarginPercentage = 10.0, estimatedGatewayFeeRate = 0.02)
    private val antiFraudEngine = AntiFraudEngine()
    private val surveyProvider = MockSurveyProvider()
    private val offerProvider = MockOfferProvider()
    private val payoutProvider = MockPayoutProvider()
    private val rewardProvider = MockRewardProvider()

    @Test
    fun testTarget10PercentPlatformGrossMarginCalculation() {
        val partnerRevenue = 100.0
        val breakdown = rewardEngine.calculateReward(partnerRevenue)

        // Target Model Check:
        // Partner pays ₹100
        // Expected: Platform gross margin = ₹10 (10%)
        // User reward = ₹90
        assertEquals(100.0, breakdown.partnerRevenue, 0.001)
        assertEquals(10.0, breakdown.platformMarginAmount, 0.001)
        assertEquals(90.0, breakdown.userReward, 0.001)
        assertEquals(2.0, breakdown.paymentGatewayFees, 0.001) // 2%
        assertEquals(8.0, breakdown.netPlatformContribution, 0.001) // 10 - 2 = 8
    }

    @Test
    fun testCustomMarginCalculation() {
        val partnerRevenue = 200.0
        val breakdown = rewardEngine.calculateReward(partnerRevenue, customMarginPct = 15.0)

        assertEquals(200.0, breakdown.partnerRevenue, 0.001)
        assertEquals(30.0, breakdown.platformMarginAmount, 0.001)
        assertEquals(170.0, breakdown.userReward, 0.001)
    }

    @Test
    fun testSurveyCompletionSpeedFraudDetection() {
        val userId = "test_user_01"
        // Survey estimated at 10 minutes (600 seconds)
        // User completes in 10 seconds -> Bot / Script flag
        val rapidResult = antiFraudEngine.evaluateSurveyCompletion(userId, elapsedSeconds = 10L, estimatedMinutes = 10)
        assertEquals(RiskLevel.MEDIUM_RISK, rapidResult.riskLevel)
        assertTrue(rapidResult.riskScore >= 60)
        assertNotNull(rapidResult.fraudEvent)

        // Normal completion in 300 seconds (5 mins)
        val normalResult = antiFraudEngine.evaluateSurveyCompletion(userId, elapsedSeconds = 300L, estimatedMinutes = 10)
        assertEquals(RiskLevel.LOW_RISK, normalResult.riskLevel)
        assertEquals(null, normalResult.fraudEvent)
    }

    @Test
    fun testWithdrawalRiskEvaluation() {
        val userId = "test_user_02"

        // Exceeds available balance -> High Risk
        val overdrawResult = antiFraudEngine.evaluateWithdrawalRisk(
            userId = userId,
            amount = 500.0,
            availableBalance = 100.0,
            lifetimeEarned = 1000.0,
            recentWithdrawalsCount24h = 0
        )
        assertEquals(RiskLevel.HIGH_RISK, overdrawResult.riskLevel)

        // Normal valid withdrawal
        val normalResult = antiFraudEngine.evaluateWithdrawalRisk(
            userId = userId,
            amount = 100.0,
            availableBalance = 250.0,
            lifetimeEarned = 1000.0,
            recentWithdrawalsCount24h = 0
        )
        assertEquals(RiskLevel.LOW_RISK, normalResult.riskLevel)
    }

    @Test
    fun testUpiPayoutValidation() = runBlocking {
        val validUpi = payoutProvider.validatePayoutAccount(PayoutMethod.UPI, "rahul@okhdfcbank")
        assertTrue(validUpi.isValid)

        val invalidUpi = payoutProvider.validatePayoutAccount(PayoutMethod.UPI, "invalid_no_at")
        assertFalse(invalidUpi.isValid)
    }

    @Test
    fun testMockSurveyAndOfferProvidersLoad() = runBlocking {
        val surveys = surveyProvider.getAvailableSurveys()
        assertTrue(surveys.isNotEmpty())
        assertTrue(surveys.any { it.rewardAmount > 0 })

        val offers = offerProvider.getAvailableOffers()
        assertTrue(offers.isNotEmpty())
        assertTrue(offers.any { it.category == "Apps" })

        val session = offerProvider.createTrackingSession(offers.first().id, "usr_123")
        assertTrue(session.trackingId.isNotBlank())
        assertTrue(session.partnerRedirectUrl.contains(session.trackingId))
    }

    @Test
    fun testMockRewardActivitiesProvider() = runBlocking {
        val activities = rewardProvider.getAvailableActivities()
        assertTrue(activities.isNotEmpty())

        val result = rewardProvider.completeActivity(activities.first().id, "usr_123")
        assertTrue(result.isSuccess)
        assertTrue(result.earnedReward > 0)
    }
}
