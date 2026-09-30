package com.example.domain.providers

import com.example.data.model.OfferEntity

data class TrackingSession(
    val trackingId: String,
    val offerId: String,
    val partnerRedirectUrl: String,
    val expiresAt: Long = System.currentTimeMillis() + (24 * 3600 * 1000)
)

data class PartnerConversionWebhookPayload(
    val conversionId: String,
    val trackingId: String,
    val offerId: String,
    val partnerRevenue: Double,
    val status: String, // "APPROVED", "PENDING"
    val signature: String
)

interface OfferProvider {
    val providerName: String
    suspend fun getAvailableOffers(): List<OfferEntity>
    suspend fun createTrackingSession(offerId: String, userId: String): TrackingSession
    suspend fun simulatePartnerConversion(trackingId: String, offerId: String, userId: String): PartnerConversionWebhookPayload
}
