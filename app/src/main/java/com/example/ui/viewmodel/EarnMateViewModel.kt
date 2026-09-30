package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.EarnMateDatabase
import com.example.data.model.PayoutMethod
import com.example.data.model.UserProfileEntity
import com.example.data.repository.EarnMateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EarnMateViewModel(application: Application) : AndroidViewModel(application) {

    private val database = EarnMateDatabase.getDatabase(application)
    private val repository = EarnMateRepository(database.earnMateDao())

    private val _uiState = MutableStateFlow(EarnMateUiState(isLoading = true))
    val uiState: StateFlow<EarnMateUiState> = _uiState.asStateFlow()

    private val defaultUserId = "usr_rahul_sharma_01"

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded(defaultUserId)
            val user = repository.getUser(defaultUserId)
            _uiState.update { it.copy(currentUser = user, isLoading = false) }
            observeData(defaultUserId)
        }
    }

    private fun observeData(userId: String) {
        combine(
            repository.getWalletFlow(userId),
            repository.getTransactionsFlow(userId),
            repository.getSurveysFlow(),
            repository.getOffersFlow(),
            repository.getWithdrawalsFlow(userId),
            repository.getNotificationsFlow(userId),
            repository.getDailyBonusFlow(userId),
            repository.getSupportTicketsFlow(userId)
        ) { values ->
            @Suppress("UNCHECKED_CAST")
            val wallet = values[0] as? com.example.data.model.WalletAccountEntity
            @Suppress("UNCHECKED_CAST")
            val transactions = values[1] as? List<com.example.data.model.WalletTransactionEntity> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val surveys = values[2] as? List<com.example.data.model.SurveyEntity> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val offers = values[3] as? List<com.example.data.model.OfferEntity> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val withdrawals = values[4] as? List<com.example.data.model.WithdrawalEntity> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val notifications = values[5] as? List<com.example.data.model.NotificationItemEntity> ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val dailyBonus = values[6] as? com.example.data.model.DailyBonusStreakEntity
            @Suppress("UNCHECKED_CAST")
            val supportTickets = values[7] as? List<com.example.data.model.SupportTicketEntity> ?: emptyList()

            _uiState.update { state ->
                state.copy(
                    wallet = wallet,
                    transactions = transactions,
                    surveys = surveys,
                    offers = offers,
                    withdrawals = withdrawals,
                    notifications = notifications,
                    dailyBonus = dailyBonus,
                    supportTickets = supportTickets
                )
            }
        }.launchIn(viewModelScope)

        repository.getProfileFlow(userId).onEach { profile ->
            _uiState.update { it.copy(userProfile = profile) }
        }.launchIn(viewModelScope)

        // Observe Admin streams
        repository.getAllUsersFlow().onEach { users ->
            _uiState.update { it.copy(allUsers = users) }
        }.launchIn(viewModelScope)

        repository.getAllConversionsFlow().onEach { conversions ->
            _uiState.update { it.copy(allConversions = conversions) }
        }.launchIn(viewModelScope)

        repository.getAllWithdrawalsFlow().onEach { withdrawals ->
            _uiState.update { it.copy(allWithdrawals = withdrawals) }
        }.launchIn(viewModelScope)

        repository.getAllTransactionsFlow().onEach { txs ->
            _uiState.update { it.copy(allTransactions = txs) }
        }.launchIn(viewModelScope)

        repository.getFraudEventsFlow().onEach { events ->
            _uiState.update { it.copy(fraudEvents = events) }
        }.launchIn(viewModelScope)

        repository.getAppSettingsFlow().onEach { settings ->
            _uiState.update { it.copy(appSettings = settings) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            val activities = repository.getRewardActivities()
            _uiState.update { it.copy(rewardActivities = activities) }
        }
    }

    // --- Navigation ---
    fun navigateTo(screen: Screen) {
        _uiState.update { state ->
            state.copy(
                currentScreen = screen,
                screenStack = state.screenStack + screen
            )
        }
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.screenStack
        if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            val previousScreen = newStack.last()
            _uiState.update { it.copy(currentScreen = previousScreen, screenStack = newStack) }
            return true
        } else if (_uiState.value.currentScreen != Screen.Home) {
            _uiState.update { it.copy(currentScreen = Screen.Home, currentTab = BottomTab.HOME, screenStack = listOf(Screen.Home)) }
            return true
        }
        return false
    }

    fun selectTab(tab: BottomTab) {
        val targetScreen = when (tab) {
            BottomTab.HOME -> Screen.Home
            BottomTab.EARN -> Screen.Surveys
            BottomTab.WALLET -> Screen.Wallet
            BottomTab.REFER -> Screen.ReferAndEarn
            BottomTab.PROFILE -> Screen.Profile
        }
        _uiState.update {
            it.copy(
                currentTab = tab,
                currentScreen = targetScreen,
                screenStack = listOf(targetScreen)
            )
        }
    }

    // --- Authentication ---
    fun signInWithGoogle(name: String = "Rahul Sharma", email: String = "rahul.sharma@example.com") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = repository.getOrCreateUser(email = email, name = name)
            _uiState.update {
                it.copy(
                    currentUser = user,
                    isLoading = false,
                    currentScreen = Screen.Home,
                    currentTab = BottomTab.HOME,
                    screenStack = listOf(Screen.Home),
                    userMessage = "Welcome back, ${user.name}!"
                )
            }
            observeData(user.id)
        }
    }

    fun signInWithEmail(email: String, name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = repository.getOrCreateUser(email = email, name = name)
            _uiState.update {
                it.copy(
                    currentUser = user,
                    isLoading = false,
                    currentScreen = Screen.Home,
                    currentTab = BottomTab.HOME,
                    screenStack = listOf(Screen.Home),
                    userMessage = "Signed in successfully!"
                )
            }
            observeData(user.id)
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                currentScreen = Screen.Login,
                screenStack = listOf(Screen.Login),
                userMessage = "Logged out successfully"
            )
        }
    }

    // --- Survey Flow ---
    fun startSurvey(surveyId: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = repository.startSurvey(surveyId, user.id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentScreen = Screen.SurveyActive(session, System.currentTimeMillis())
                )
            }
        }
    }

    fun submitSurvey(
        sessionId: String,
        surveyId: String,
        answers: Map<String, String>,
        startTimeMs: Long
    ) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.submitSurvey(sessionId, surveyId, user.id, answers, startTimeMs)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                navigateTo(Screen.Wallet)
                _uiState.update { it.copy(userMessage = "Survey completed! Reward credited to your wallet.") }
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Failed to verify survey completion") }
            }
        }
    }

    // --- Offer Flow ---
    fun startOffer(offerId: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = repository.startOffer(offerId, user.id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    activeTrackingSession = session
                )
            }
            navigateTo(Screen.OfferDetail(offerId))
        }
    }

    fun simulatePartnerConversionWebhook(conversionId: String, offerId: String, partnerRevenue: Double) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.handlePartnerConversionWebhook(
                conversionId = conversionId,
                offerId = offerId,
                userId = user.id,
                partnerRevenue = partnerRevenue
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userMessage = result.getOrNull() ?: result.exceptionOrNull()?.message
                )
            }
        }
    }

    // --- Reward Activity ---
    fun completeRewardActivity(activityId: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.completeRewardActivity(activityId, user.id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userMessage = result.message
                )
            }
        }
    }

    // --- Withdrawals ---
    fun requestWithdrawal(amount: Double, method: PayoutMethod, details: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.requestWithdrawal(user.id, amount, method, details)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { withdrawal ->
                navigateTo(Screen.WithdrawalConfirmation(withdrawal))
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Withdrawal request failed") }
            }
        }
    }

    // --- Daily Bonus ---
    fun claimDailyBonus() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.claimDailyBonus(user.id)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess { coins ->
                _uiState.update { it.copy(userMessage = "Claimed ₹${String.format(java.util.Locale.US, "%.2f", coins)} bonus!") }
            }.onFailure { err ->
                _uiState.update { it.copy(userMessage = err.message ?: "Could not claim daily bonus") }
            }
        }
    }

    // --- Referral ---
    fun applyReferralCode(code: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val result = repository.applyReferralCode(user.id, code)
            _uiState.update {
                it.copy(userMessage = result.getOrNull() ?: result.exceptionOrNull()?.message)
            }
        }
    }

    // --- Support ---
    fun submitSupportTicket(category: String, subject: String, message: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.submitSupportTicket(user.id, category, subject, message)
            _uiState.update { it.copy(userMessage = "Support ticket submitted. Our team will review within 24 hours.") }
            navigateBack()
        }
    }

    // --- Profile & Privacy ---
    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            _uiState.update { it.copy(userMessage = "Profile updated successfully.") }
            navigateBack()
        }
    }

    fun requestAccountDeletion() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.requestAccountDeletion(user.id)
            _uiState.update { it.copy(userMessage = "Account data anonymized per legal retention standards.") }
            navigateTo(Screen.Login)
        }
    }

    fun markNotificationsRead() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id)
        }
    }

    // --- Admin Dashboard Mode ---
    fun toggleAdminMode() {
        _uiState.update {
            val newMode = !it.isAdminMode
            it.copy(
                isAdminMode = newMode,
                currentScreen = if (newMode) Screen.AdminDashboard else Screen.Home
            )
        }
    }

    fun approveWithdrawal(withdrawalId: String) {
        viewModelScope.launch {
            repository.approveWithdrawal(withdrawalId, "Approved via Admin Dashboard")
            _uiState.update { it.copy(userMessage = "Withdrawal $withdrawalId approved and processed.") }
        }
    }

    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.updateAppSetting(key, value)
            _uiState.update { it.copy(userMessage = "Setting $key updated.") }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
