package com.example.domain.providers

import com.example.data.model.SurveyEntity

data class SurveyQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>
)

data class SurveySession(
    val sessionId: String,
    val surveyId: String,
    val surveyTitle: String,
    val questions: List<SurveyQuestion>,
    val estimatedMinutes: Int,
    val startedAt: Long = System.currentTimeMillis()
)

data class SurveyCompletionResult(
    val isSuccess: Boolean,
    val partnerGrossRevenue: Double,
    val calculatedUserReward: Double,
    val partnerConversionId: String,
    val message: String
)

interface SurveyProvider {
    val providerName: String
    suspend fun getAvailableSurveys(): List<SurveyEntity>
    suspend fun startSurvey(surveyId: String, userId: String): SurveySession
    suspend fun submitSurvey(sessionId: String, surveyId: String, userId: String, answers: Map<String, String>): SurveyCompletionResult
}
