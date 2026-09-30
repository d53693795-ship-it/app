package com.example.domain.providers

import com.example.data.model.SurveyEntity
import java.util.UUID

class MockSurveyProvider : SurveyProvider {

    override val providerName: String = "ResearchPulse Labs (Demo)"

    private val demoSurveys = listOf(
        SurveyEntity(
            id = "srv_consumer_trends_01",
            title = "Digital Payments & UPI Usage Survey",
            description = "Share your daily online spending and payment habits to help Indian fintechs improve security and ease.",
            rewardAmount = 45.0, // 90% of 50
            partnerGrossRevenue = 50.0,
            estimatedMinutes = 8,
            difficulty = "Easy",
            rating = 4.8,
            provider = "ResearchPulse",
            category = "Fintech",
            attemptsCount = 1420
        ),
        SurveyEntity(
            id = "srv_tech_devices_02",
            title = "Smartphone & 5G Connectivity Study",
            description = "Help telecom researchers understand 5G device usage, video streaming, and mobile gaming performance.",
            rewardAmount = 72.0, // 90% of 80
            partnerGrossRevenue = 80.0,
            estimatedMinutes = 12,
            difficulty = "Medium",
            rating = 4.9,
            provider = "InnoStats",
            category = "Technology",
            attemptsCount = 985
        ),
        SurveyEntity(
            id = "srv_lifestyle_food_03",
            title = "Food Delivery & Quick Commerce Habits",
            description = "Tell us how frequently you order groceries or meals online and what features matter most to you.",
            rewardAmount = 27.0, // 90% of 30
            partnerGrossRevenue = 30.0,
            estimatedMinutes = 5,
            difficulty = "Easy",
            rating = 4.7,
            provider = "MarketMinds",
            category = "Lifestyle",
            attemptsCount = 2310
        ),
        SurveyEntity(
            id = "srv_travel_commute_04",
            title = "Daily Commute & Ride-Hailing Feedback",
            description = "Evaluate public transit, cab aggregators, and metro connectivity in your metropolitan region.",
            rewardAmount = 90.0, // 90% of 100
            partnerGrossRevenue = 100.0,
            estimatedMinutes = 15,
            difficulty = "In-depth",
            rating = 4.9,
            provider = "UrbanMobility",
            category = "Travel",
            attemptsCount = 670
        )
    )

    override suspend fun getAvailableSurveys(): List<SurveyEntity> {
        return demoSurveys
    }

    override suspend fun startSurvey(surveyId: String, userId: String): SurveySession {
        val survey = demoSurveys.find { it.id == surveyId } ?: demoSurveys.first()
        val questions = listOf(
            SurveyQuestion(
                id = "q1",
                questionText = "How frequently do you make online purchases or payments each week?",
                options = listOf("Daily", "3-5 times a week", "1-2 times a week", "Rarely")
            ),
            SurveyQuestion(
                id = "q2",
                questionText = "Which feature is most important to you when choosing an app?",
                options = listOf("Speed & Reliability", "Cashback / Rewards", "Clean User Interface", "Customer Support")
            ),
            SurveyQuestion(
                id = "q3",
                questionText = "Which mobile operating system do you primarily use?",
                options = listOf("Android 14 / 15", "Android 12 / 13", "iOS", "Other")
            ),
            SurveyQuestion(
                id = "q4",
                questionText = "Would you recommend rewarding apps with transparent double-entry ledgers to friends?",
                options = listOf("Definitely yes", "Probably yes", "Neutral", "Unlikely")
            )
        )

        return SurveySession(
            sessionId = "sess_${UUID.randomUUID().toString().take(8)}",
            surveyId = survey.id,
            surveyTitle = survey.title,
            questions = questions,
            estimatedMinutes = survey.estimatedMinutes
        )
    }

    override suspend fun submitSurvey(
        sessionId: String,
        surveyId: String,
        userId: String,
        answers: Map<String, String>
    ): SurveyCompletionResult {
        val survey = demoSurveys.find { it.id == surveyId } ?: demoSurveys.first()
        val conversionId = "cnv_srv_${UUID.randomUUID().toString().take(10)}"

        // Partner reports 10% platform gross margin, user receives 90%
        val partnerGross = survey.partnerGrossRevenue
        val userReward = partnerGross * 0.90

        return SurveyCompletionResult(
            isSuccess = true,
            partnerGrossRevenue = partnerGross,
            calculatedUserReward = userReward,
            partnerConversionId = conversionId,
            message = "Survey successfully verified by ${survey.provider}."
        )
    }
}
