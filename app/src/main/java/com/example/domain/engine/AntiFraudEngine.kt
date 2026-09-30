package com.example.domain.engine

import com.example.data.model.FraudEventEntity
import com.example.data.model.RiskLevel

data class FraudEvaluation(
    val riskLevel: RiskLevel,
    val riskScore: Int,
    val reason: String,
    val fraudEvent: FraudEventEntity? = null
)

class AntiFraudEngine {

    /**
     * Evaluates survey completion speed to detect automated bot responses.
     */
    fun evaluateSurveyCompletion(
        userId: String,
        elapsedSeconds: Long,
        estimatedMinutes: Int
    ): FraudEvaluation {
        val minimumExpectedSeconds = (estimatedMinutes * 60) * 0.15 // at least 15% of estimated time
        return if (elapsedSeconds < minimumExpectedSeconds) {
            val score = 65
            val reason = "Survey completed unnaturally fast ($elapsedSeconds sec vs $estimatedMinutes min est)."
            FraudEvaluation(
                riskLevel = RiskLevel.MEDIUM_RISK,
                riskScore = score,
                reason = reason,
                fraudEvent = FraudEventEntity(
                    userId = userId,
                    signalType = "RAPID_SURVEY_COMPLETION",
                    riskScore = score,
                    riskLevel = RiskLevel.MEDIUM_RISK,
                    reason = reason
                )
            )
        } else {
            FraudEvaluation(
                riskLevel = RiskLevel.LOW_RISK,
                riskScore = 5,
                reason = "Normal survey completion duration."
            )
        }
    }

    /**
     * Evaluates withdrawal requests based on velocity, amount, and account age.
     */
    fun evaluateWithdrawalRisk(
        userId: String,
        amount: Double,
        availableBalance: Double,
        lifetimeEarned: Double,
        recentWithdrawalsCount24h: Int
    ): FraudEvaluation {
        var score = 10
        val reasons = mutableListOf<String>()

        if (recentWithdrawalsCount24h >= 3) {
            score += 40
            reasons.add("High withdrawal velocity: $recentWithdrawalsCount24h requests in last 24h.")
        }

        if (amount > 1000.0) {
            score += 25
            reasons.add("High withdrawal amount: ₹$amount.")
        }

        if (amount > availableBalance) {
            score = 100
            reasons.add("Requested amount exceeds available balance.")
        }

        val riskLevel = when {
            score >= 70 -> RiskLevel.HIGH_RISK
            score >= 40 -> RiskLevel.MEDIUM_RISK
            else -> RiskLevel.LOW_RISK
        }

        val finalReason = if (reasons.isEmpty()) "Standard verified withdrawal" else reasons.joinToString("; ")

        val event = if (riskLevel != RiskLevel.LOW_RISK) {
            FraudEventEntity(
                userId = userId,
                signalType = "WITHDRAWAL_RISK_FLAG",
                riskScore = score,
                riskLevel = riskLevel,
                reason = finalReason
            )
        } else null

        return FraudEvaluation(
            riskLevel = riskLevel,
            riskScore = score,
            reason = finalReason,
            fraudEvent = event
        )
    }
}
