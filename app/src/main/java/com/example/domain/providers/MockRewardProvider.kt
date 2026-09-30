package com.example.domain.providers

import java.util.UUID

class MockRewardProvider : RewardActivityProvider {

    override val providerName: String = "EngagePlus Permitted Rewards (Demo)"

    private val activities = listOf(
        RewardActivityItem(
            id = "act_fin_trivia_01",
            title = "Financial Literacy Quick Quiz",
            description = "Answer 3 basic questions about mutual funds, inflation, and compound interest to earn bonus reward.",
            rewardAmount = 5.0,
            category = "Daily Trivia",
            durationSeconds = 60
        ),
        RewardActivityItem(
            id = "act_eco_discovery_02",
            title = "Green Energy Brands Spotlight",
            description = "Explore eco-friendly and sustainable consumer products from verified Indian partner brands.",
            rewardAmount = 8.0,
            category = "Product Discovery",
            durationSeconds = 90
        ),
        RewardActivityItem(
            id = "act_cyber_quiz_03",
            title = "Safe Internet & UPI Security Check",
            description = "Quick interactive checklist to verify that you never share UPI PIN or OTP with unknown callers.",
            rewardAmount = 6.0,
            category = "Knowledge Challenge",
            durationSeconds = 45
        )
    )

    override suspend fun getAvailableActivities(): List<RewardActivityItem> {
        return activities
    }

    override suspend fun completeActivity(activityId: String, userId: String): RewardActivityResult {
        val item = activities.find { it.id == activityId } ?: activities.first()
        val refId = "act_ref_${UUID.randomUUID().toString().take(8)}"
        return RewardActivityResult(
            isSuccess = true,
            earnedReward = item.rewardAmount,
            activityReferenceId = refId,
            message = "Completed '${item.title}' successfully! Reward credited."
        )
    }
}
