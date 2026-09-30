package com.example.domain.providers

import com.example.data.model.PayoutMethod
import java.util.UUID

class MockPayoutProvider : PayoutProvider {

    override val providerName: String = "PayoutGateway Mock India (UPI / IMPS)"

    override suspend fun validatePayoutAccount(method: PayoutMethod, details: String): PayoutValidationResult {
        val trimmed = details.trim()
        return when (method) {
            PayoutMethod.UPI -> {
                if (trimmed.contains("@") && trimmed.length >= 5) {
                    PayoutValidationResult(true, trimmed, "Valid UPI Virtual Payment Address.")
                } else {
                    PayoutValidationResult(false, trimmed, "Invalid UPI ID. Format should be username@bank (e.g. rahul@okaxis).")
                }
            }
            PayoutMethod.BANK -> {
                val parts = trimmed.split("|")
                if (parts.size >= 2 && parts[0].length >= 8 && parts[1].length >= 4) {
                    PayoutValidationResult(true, trimmed, "Bank account & IFSC format valid.")
                } else {
                    PayoutValidationResult(false, trimmed, "Please provide Account Number & IFSC code.")
                }
            }
        }
    }

    override suspend fun initiatePayout(
        withdrawalId: String,
        amount: Double,
        method: PayoutMethod,
        details: String
    ): PayoutExecutionResult {
        val referenceId = "IMPS_${System.currentTimeMillis()}_${(1000..9999).random()}"
        val payoutId = "po_${UUID.randomUUID().toString().take(10)}"

        return PayoutExecutionResult(
            isSuccess = true,
            payoutId = payoutId,
            status = "PROCESSING",
            referenceId = referenceId,
            message = "Payout initiated with partner bank. Reference: $referenceId."
        )
    }
}
