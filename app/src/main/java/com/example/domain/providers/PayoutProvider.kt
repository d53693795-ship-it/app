package com.example.domain.providers

import com.example.data.model.PayoutMethod

data class PayoutValidationResult(
    val isValid: Boolean,
    val cleanDetails: String,
    val message: String
)

data class PayoutExecutionResult(
    val isSuccess: Boolean,
    val payoutId: String,
    val status: String, // "PROCESSING", "COMPLETED", "FAILED"
    val referenceId: String,
    val message: String
)

interface PayoutProvider {
    val providerName: String
    suspend fun validatePayoutAccount(method: PayoutMethod, details: String): PayoutValidationResult
    suspend fun initiatePayout(withdrawalId: String, amount: Double, method: PayoutMethod, details: String): PayoutExecutionResult
}
