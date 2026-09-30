package com.example.domain.providers

data class RewardActivityItem(
    val id: String,
    val title: String,
    val description: String,
    val rewardAmount: Double,
    val category: String, // "Daily Trivia", "Product Discovery", "Knowledge Challenge"
    val durationSeconds: Int,
    val provider: String = "Partner Activity Network"
)

data class RewardActivityResult(
    val isSuccess: Boolean,
    val earnedReward: Double,
    val activityReferenceId: String,
    val message: String
)

interface RewardActivityProvider {
    val providerName: String
    suspend fun getAvailableActivities(): List<RewardActivityItem>
    suspend fun completeActivity(activityId: String, userId: String): RewardActivityResult
}
