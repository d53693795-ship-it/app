package com.example.ui.viewmodel

import com.example.data.model.AppSettingEntity
import com.example.data.model.DailyBonusStreakEntity
import com.example.data.model.FraudEventEntity
import com.example.data.model.NotificationItemEntity
import com.example.data.model.OfferConversionEntity
import com.example.data.model.OfferEntity
import com.example.data.model.SupportTicketEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WalletAccountEntity
import com.example.data.model.WalletTransactionEntity
import com.example.data.model.WithdrawalEntity
import com.example.domain.providers.RewardActivityItem
import com.example.domain.providers.SurveySession
import com.example.domain.providers.TrackingSession

sealed interface Screen {
    data object Splash : Screen
    data object Onboarding : Screen
    data object Login : Screen
    data object GoogleSignIn : Screen
    data object EmailAuth : Screen
    data object CompleteProfile : Screen
    data object Home : Screen
    data object Surveys : Screen
    data class SurveyDetail(val surveyId: String) : Screen
    data class SurveyActive(val session: SurveySession, val startTimeMs: Long) : Screen
    data object Offers : Screen
    data class OfferDetail(val offerId: String) : Screen
    data object RewardActivities : Screen
    data object Wallet : Screen
    data object Transactions : Screen
    data object Withdraw : Screen
    data class WithdrawalConfirmation(val withdrawal: WithdrawalEntity) : Screen
    data object WithdrawalHistory : Screen
    data object ReferAndEarn : Screen
    data object DailyBonus : Screen
    data object Notifications : Screen
    data object Profile : Screen
    data object EditProfile : Screen
    data object Settings : Screen
    data object PrivacyPolicy : Screen
    data object TermsConditions : Screen
    data object HelpSupport : Screen
    data object SupportTicket : Screen
    data object AccountDeletion : Screen
    data object AdminDashboard : Screen
}

enum class BottomTab {
    HOME,
    EARN,
    WALLET,
    REFER,
    PROFILE
}

data class EarnMateUiState(
    val currentUser: UserEntity? = null,
    val userProfile: UserProfileEntity? = null,
    val wallet: WalletAccountEntity? = null,
    val currentScreen: Screen = Screen.Login,
    val screenStack: List<Screen> = listOf(Screen.Login),
    val currentTab: BottomTab = BottomTab.HOME,
    val transactions: List<WalletTransactionEntity> = emptyList(),
    val surveys: List<SurveyEntity> = emptyList(),
    val offers: List<OfferEntity> = emptyList(),
    val rewardActivities: List<RewardActivityItem> = emptyList(),
    val withdrawals: List<WithdrawalEntity> = emptyList(),
    val notifications: List<NotificationItemEntity> = emptyList(),
    val dailyBonus: DailyBonusStreakEntity? = null,
    val supportTickets: List<SupportTicketEntity> = emptyList(),
    // Admin state
    val allUsers: List<UserEntity> = emptyList(),
    val allConversions: List<OfferConversionEntity> = emptyList(),
    val allWithdrawals: List<WithdrawalEntity> = emptyList(),
    val allTransactions: List<WalletTransactionEntity> = emptyList(),
    val fraudEvents: List<FraudEventEntity> = emptyList(),
    val appSettings: List<AppSettingEntity> = emptyList(),
    val isAdminMode: Boolean = false,
    val activeTrackingSession: TrackingSession? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
