package com.example.domain.engine

data class RewardBreakdown(
    val partnerRevenue: Double,
    val platformMarginAmount: Double,
    val marginPercentage: Double,
    val paymentGatewayFees: Double,
    val userReward: Double,
    val netPlatformContribution: Double
)

class RewardEngine(
    private val defaultMarginPercentage: Double = 10.0,
    private val estimatedGatewayFeeRate: Double = 0.02 // 2%
) {
    /**
     * Calculates user reward and platform accounting strictly on the backend.
     * partnerRevenue: gross revenue received/reported by the affiliate or survey partner
     * Target: 10% platform gross margin
     * User reward = partnerRevenue - platformMargin - applicableCosts
     */
    fun calculateReward(
        partnerRevenue: Double,
        customMarginPct: Double? = null
    ): RewardBreakdown {
        val marginPct = customMarginPct ?: defaultMarginPercentage
        val platformGrossMargin = partnerRevenue * (marginPct / 100.0)
        val estimatedFees = partnerRevenue * estimatedGatewayFeeRate
        val userReward = (partnerRevenue - platformGrossMargin).coerceAtLeast(0.0)
        val netContribution = (platformGrossMargin - estimatedFees).coerceAtLeast(0.0)

        return RewardBreakdown(
            partnerRevenue = partnerRevenue,
            platformMarginAmount = platformGrossMargin,
            marginPercentage = marginPct,
            paymentGatewayFees = estimatedFees,
            userReward = userReward,
            netPlatformContribution = netContribution
        )
    }
}
