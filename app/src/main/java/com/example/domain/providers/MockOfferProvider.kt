package com.example.domain.providers

import com.example.data.model.OfferEntity
import java.util.UUID

class MockOfferProvider : OfferProvider {

    override val providerName: String = "AdAffiliate Global (Demo)"

    private val demoOffers = listOf(
        OfferEntity(
            id = "off_stream_prime_01",
            title = "Kite Trading & Stock Investing App",
            description = "Download the investment app, complete free KYC verification, and explore demat features.",
            category = "Apps",
            rewardAmount = 135.0, // 90% of 150
            partnerGrossRevenue = 150.0,
            requirements = "1. Install app via EarnMate link\n2. Sign up and submit mobile verification\n3. Reward credited after partner KYC check",
            estimatedTime = "10 mins",
            provider = "AdAffiliate",
            partnerUrl = "https://partner-network.demo/kite?subid=",
            badge = "High Reward"
        ),
        OfferEntity(
            id = "off_grocery_fast_02",
            title = "BlinkFast 10-Minute Grocery Delivery",
            description = "Order daily essentials or groceries worth ₹199 or more using your unique affiliate tracking link.",
            category = "Shopping",
            rewardAmount = 54.0, // 90% of 60
            partnerGrossRevenue = 60.0,
            requirements = "1. Tap 'Start Offer' and install or open store\n2. Add items worth ₹199+\n3. Complete delivery successfully",
            estimatedTime = "15 mins",
            provider = "ShopTrack",
            partnerUrl = "https://shoptrack.demo/blink?aff_id=",
            badge = "Popular"
        ),
        OfferEntity(
            id = "off_game_clash_03",
            title = "Kingdom Quest: Tower Defense RPG",
            description = "Install Kingdom Quest, play through introductory tutorial, and defeat the Chapter 2 Fortress Boss.",
            category = "Games",
            rewardAmount = 90.0, // 90% of 100
            partnerGrossRevenue = 100.0,
            requirements = "1. Download and install new user copy\n2. Reach Castle Level 5\n3. Defeat Boss 2-1 within 7 days",
            estimatedTime = "25 mins",
            provider = "PlayMatrix",
            partnerUrl = "https://playmatrix.demo/kingdom?ref=",
            badge = "Gaming"
        ),
        OfferEntity(
            id = "off_credit_score_04",
            title = "Experian Free Credit Score & Report",
            description = "Check your official bureau credit score for ₹0 and receive a free credit health analysis.",
            category = "Services",
            rewardAmount = 45.0, // 90% of 50
            partnerGrossRevenue = 50.0,
            requirements = "1. Enter basic identification details\n2. View updated credit score report\n3. No credit card required",
            estimatedTime = "5 mins",
            provider = "BureauDirect",
            partnerUrl = "https://bureaudirect.demo/check?sub=",
            badge = "Instant"
        ),
        OfferEntity(
            id = "off_cloud_storage_05",
            title = "SecureCloud 50GB Free Backup Trial",
            description = "Sign up for SecureCloud free trial account and sync your first photo or document safely.",
            category = "Services",
            rewardAmount = 36.0, // 90% of 40
            partnerGrossRevenue = 40.0,
            requirements = "1. Sign up with verified email\n2. Upload at least 1 file\n3. Reward arrives within 2 hours",
            estimatedTime = "7 mins",
            provider = "CloudPartners",
            partnerUrl = "https://cloudpartners.demo/trial?tag=",
            badge = "Quick"
        )
    )

    override suspend fun getAvailableOffers(): List<OfferEntity> {
        return demoOffers
    }

    override suspend fun createTrackingSession(offerId: String, userId: String): TrackingSession {
        val trackingId = "trk_${UUID.randomUUID().toString().take(12)}"
        val offer = demoOffers.find { it.id == offerId } ?: demoOffers.first()
        return TrackingSession(
            trackingId = trackingId,
            offerId = offerId,
            partnerRedirectUrl = "${offer.partnerUrl}$trackingId&uid=$userId"
        )
    }

    override suspend fun simulatePartnerConversion(
        trackingId: String,
        offerId: String,
        userId: String
    ): PartnerConversionWebhookPayload {
        val offer = demoOffers.find { it.id == offerId } ?: demoOffers.first()
        val conversionId = "cnv_off_${UUID.randomUUID().toString().take(10)}"

        return PartnerConversionWebhookPayload(
            conversionId = conversionId,
            trackingId = trackingId,
            offerId = offerId,
            partnerRevenue = offer.partnerGrossRevenue,
            status = "APPROVED",
            signature = "sig_sha256_${UUID.randomUUID().toString().take(16)}"
        )
    }
}
