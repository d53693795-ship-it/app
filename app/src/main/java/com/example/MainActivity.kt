package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.EarnMateBottomBar
import com.example.ui.components.EarnMateTopBar
import com.example.ui.screens.AccountDeletionScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CompleteProfileScreen
import com.example.ui.screens.DailyBonusScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.EmailAuthScreen
import com.example.ui.screens.GoogleSignInSheet
import com.example.ui.screens.HelpSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OfferDetailScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReferAndEarnScreen
import com.example.ui.screens.RewardActivitiesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SupportTicketScreen
import com.example.ui.screens.SurveyActiveScreen
import com.example.ui.screens.SurveysScreen
import com.example.ui.screens.TermsConditionsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.screens.WithdrawalConfirmationScreen
import com.example.ui.screens.WithdrawalHistoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.EarnMateViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EarnMateApp()
            }
        }
    }
}

@Composable
fun EarnMateApp(
    viewModel: EarnMateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display user messages via Snackbar
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearMessage()
        }
    }

    val currentScreen = uiState.currentScreen
    val isPrimaryTabScreen = currentScreen == Screen.Home ||
            currentScreen == Screen.Surveys ||
            currentScreen == Screen.Wallet ||
            currentScreen == Screen.ReferAndEarn ||
            currentScreen == Screen.Profile

    val isAuthScreen = currentScreen == Screen.Splash ||
            currentScreen == Screen.Onboarding ||
            currentScreen == Screen.Login ||
            currentScreen == Screen.EmailAuth ||
            currentScreen == Screen.CompleteProfile

    // Handle Hardware / Gesture Back Button
    BackHandler(enabled = !isPrimaryTabScreen && !isAuthScreen) {
        viewModel.navigateBack()
    }

    val topBarTitle = when (currentScreen) {
        Screen.Home -> "EarnMate"
        Screen.Surveys -> "Surveys"
        is Screen.SurveyDetail -> "Survey Info"
        is Screen.SurveyActive -> "Survey Session"
        Screen.Offers -> "Partner Offers"
        is Screen.OfferDetail -> "Offer Details"
        Screen.RewardActivities -> "Activities"
        Screen.Wallet -> "Wallet Ledger"
        Screen.Transactions -> "All Transactions"
        Screen.Withdraw -> "Withdraw"
        is Screen.WithdrawalConfirmation -> "Withdrawal Status"
        Screen.WithdrawalHistory -> "Payout History"
        Screen.ReferAndEarn -> "Refer & Earn"
        Screen.DailyBonus -> "Daily Bonus"
        Screen.Notifications -> "Notifications"
        Screen.Profile -> "Profile"
        Screen.EditProfile -> "Edit Profile"
        Screen.Settings -> "Settings"
        Screen.PrivacyPolicy -> "Privacy Policy"
        Screen.TermsConditions -> "Terms & Conditions"
        Screen.HelpSupport -> "Help & FAQ"
        Screen.SupportTicket -> "Support Ticket"
        Screen.AccountDeletion -> "Account Deletion"
        Screen.AdminDashboard -> "Admin Control"
        else -> "EarnMate"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (!isAuthScreen && currentScreen != Screen.AdminDashboard) {
                EarnMateTopBar(
                    title = topBarTitle,
                    canNavigateBack = !isPrimaryTabScreen,
                    onNavigateBack = { viewModel.navigateBack() },
                    unreadNotificationCount = uiState.notifications.count { !it.isRead },
                    onNotificationClick = { viewModel.navigateTo(Screen.Notifications) },
                    isAdminMode = uiState.isAdminMode,
                    onAdminToggle = { viewModel.toggleAdminMode() }
                )
            }
        },
        bottomBar = {
            if (isPrimaryTabScreen && !uiState.isAdminMode) {
                EarnMateBottomBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                Screen.Splash -> SplashScreen(
                    onContinue = { viewModel.navigateTo(Screen.Onboarding) }
                )

                Screen.Onboarding -> OnboardingScreen(
                    onFinish = { viewModel.navigateTo(Screen.Login) }
                )

                Screen.Login -> LoginScreen(
                    onGoogleSignInClick = { viewModel.navigateTo(Screen.GoogleSignIn) },
                    onEmailLoginClick = { viewModel.navigateTo(Screen.EmailAuth) },
                    onDemoLoginClick = { viewModel.signInWithGoogle() },
                    onViewOnboarding = { viewModel.navigateTo(Screen.Splash) }
                )

                Screen.GoogleSignIn -> GoogleSignInSheet(
                    onAccountSelected = { name, email ->
                        viewModel.signInWithGoogle(name = name, email = email)
                    },
                    onCancel = { viewModel.navigateBack() }
                )

                Screen.EmailAuth -> EmailAuthScreen(
                    onSuccess = { email, name ->
                        viewModel.signInWithEmail(email, name)
                    },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.CompleteProfile -> CompleteProfileScreen(
                    currentProfile = uiState.userProfile,
                    onSaveProfile = { profile ->
                        viewModel.updateProfile(profile)
                        viewModel.navigateTo(Screen.Home)
                    }
                )

                Screen.Home -> HomeScreen(
                    currentUser = uiState.currentUser,
                    wallet = uiState.wallet,
                    surveys = uiState.surveys,
                    offers = uiState.offers,
                    recentTransactions = uiState.transactions,
                    dailyBonus = uiState.dailyBonus,
                    onWithdrawClick = { viewModel.navigateTo(Screen.Withdraw) },
                    onEarnClick = { viewModel.selectTab(BottomTab.EARN) },
                    onSurveysClick = { viewModel.navigateTo(Screen.Surveys) },
                    onOffersClick = { viewModel.navigateTo(Screen.Offers) },
                    onRewardsClick = { viewModel.navigateTo(Screen.RewardActivities) },
                    onReferClick = { viewModel.selectTab(BottomTab.REFER) },
                    onStartSurvey = { id -> viewModel.startSurvey(id) },
                    onStartOffer = { id -> viewModel.startOffer(id) },
                    onClaimBonus = { viewModel.claimDailyBonus() },
                    onViewAllTransactions = { viewModel.navigateTo(Screen.Transactions) }
                )

                Screen.Surveys -> SurveysScreen(
                    surveys = uiState.surveys,
                    onStartSurvey = { id -> viewModel.startSurvey(id) },
                    onTabSelected = {}
                )

                is Screen.SurveyDetail -> {
                    // Navigate directly to active survey session if opened
                    LaunchedEffect(screen.surveyId) {
                        viewModel.startSurvey(screen.surveyId)
                    }
                }

                is Screen.SurveyActive -> SurveyActiveScreen(
                    session = screen.session,
                    startTimeMs = screen.startTimeMs,
                    onSubmitSurvey = { sessionId, surveyId, answers, startTime ->
                        viewModel.submitSurvey(sessionId, surveyId, answers, startTime)
                    },
                    onCancel = { viewModel.navigateBack() }
                )

                Screen.Offers -> OffersScreen(
                    offers = uiState.offers,
                    onStartOffer = { id -> viewModel.startOffer(id) }
                )

                is Screen.OfferDetail -> OfferDetailScreen(
                    offer = uiState.offers.find { it.id == screen.offerId },
                    session = uiState.activeTrackingSession,
                    onSimulateConversion = { convId, offId, rev ->
                        viewModel.simulatePartnerConversionWebhook(convId, offId, rev)
                    },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.RewardActivities -> RewardActivitiesScreen(
                    activities = uiState.rewardActivities,
                    onCompleteActivity = { id -> viewModel.completeRewardActivity(id) },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.Wallet -> WalletScreen(
                    wallet = uiState.wallet,
                    transactions = uiState.transactions,
                    onWithdrawClick = { viewModel.navigateTo(Screen.Withdraw) },
                    onViewTransactionsClick = { viewModel.navigateTo(Screen.Transactions) },
                    onViewWithdrawalHistory = { viewModel.navigateTo(Screen.WithdrawalHistory) }
                )

                Screen.Transactions -> TransactionsScreen(
                    transactions = uiState.transactions,
                    onBack = { viewModel.navigateBack() }
                )

                Screen.Withdraw -> WithdrawScreen(
                    wallet = uiState.wallet,
                    profile = uiState.userProfile,
                    onConfirmWithdrawal = { amt, method, details ->
                        viewModel.requestWithdrawal(amt, method, details)
                    },
                    onBack = { viewModel.navigateBack() }
                )

                is Screen.WithdrawalConfirmation -> WithdrawalConfirmationScreen(
                    withdrawal = screen.withdrawal,
                    onBackToWallet = {
                        viewModel.selectTab(BottomTab.WALLET)
                    },
                    onViewHistory = {
                        viewModel.navigateTo(Screen.WithdrawalHistory)
                    }
                )

                Screen.WithdrawalHistory -> WithdrawalHistoryScreen(
                    withdrawals = uiState.withdrawals,
                    onBack = { viewModel.navigateBack() }
                )

                Screen.ReferAndEarn -> ReferAndEarnScreen(
                    currentUser = uiState.currentUser,
                    onApplyCode = { code -> viewModel.applyReferralCode(code) }
                )

                Screen.DailyBonus -> DailyBonusScreen(
                    bonus = uiState.dailyBonus,
                    onClaimBonus = { viewModel.claimDailyBonus() },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.Notifications -> NotificationsScreen(
                    notifications = uiState.notifications,
                    onMarkAllRead = { viewModel.markNotificationsRead() },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.Profile -> ProfileScreen(
                    user = uiState.currentUser,
                    profile = uiState.userProfile,
                    onEditProfile = { viewModel.navigateTo(Screen.EditProfile) },
                    onNotifications = { viewModel.navigateTo(Screen.Notifications) },
                    onSettings = { viewModel.navigateTo(Screen.Settings) },
                    onHelpSupport = { viewModel.navigateTo(Screen.HelpSupport) },
                    onSupportTicket = { viewModel.navigateTo(Screen.SupportTicket) },
                    onPrivacyPolicy = { viewModel.navigateTo(Screen.PrivacyPolicy) },
                    onTerms = { viewModel.navigateTo(Screen.TermsConditions) },
                    onAccountDeletion = { viewModel.navigateTo(Screen.AccountDeletion) },
                    onToggleAdminMode = { viewModel.toggleAdminMode() },
                    onLogout = { viewModel.logout() }
                )

                Screen.EditProfile -> EditProfileScreen(
                    profile = uiState.userProfile,
                    onSave = { profile -> viewModel.updateProfile(profile) },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.Settings -> SettingsScreen(
                    onBack = { viewModel.navigateBack() }
                )

                Screen.PrivacyPolicy -> PrivacyPolicyScreen(
                    onBack = { viewModel.navigateBack() }
                )

                Screen.TermsConditions -> TermsConditionsScreen(
                    onBack = { viewModel.navigateBack() }
                )

                Screen.HelpSupport -> HelpSupportScreen(
                    onSubmitTicketClick = { viewModel.navigateTo(Screen.SupportTicket) },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.SupportTicket -> SupportTicketScreen(
                    existingTickets = uiState.supportTickets,
                    onSubmitTicket = { cat, sub, msg -> viewModel.submitSupportTicket(cat, sub, msg) },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.AccountDeletion -> AccountDeletionScreen(
                    onConfirmDelete = { viewModel.requestAccountDeletion() },
                    onBack = { viewModel.navigateBack() }
                )

                Screen.AdminDashboard -> AdminDashboardScreen(
                    users = uiState.allUsers,
                    conversions = uiState.allConversions,
                    withdrawals = uiState.allWithdrawals,
                    fraudEvents = uiState.fraudEvents,
                    settings = uiState.appSettings,
                    onApproveWithdrawal = { id -> viewModel.approveWithdrawal(id) },
                    onUpdateSetting = { k, v -> viewModel.updateSetting(k, v) },
                    onExitAdmin = { viewModel.toggleAdminMode() }
                )
            }
        }
    }
}
