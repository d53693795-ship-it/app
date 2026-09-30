package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyBonusStreakEntity
import com.example.data.model.OfferEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.UserEntity
import com.example.data.model.WalletAccountEntity
import com.example.data.model.WalletTransactionEntity
import com.example.ui.components.BalanceCard
import com.example.ui.components.OfferCard
import com.example.ui.components.SurveyCard
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoDark
import com.example.ui.theme.PrimaryIndigoLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberDark
import com.example.ui.theme.WarningAmberLight

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    wallet: WalletAccountEntity?,
    surveys: List<SurveyEntity>,
    offers: List<OfferEntity>,
    recentTransactions: List<WalletTransactionEntity>,
    dailyBonus: DailyBonusStreakEntity?,
    onWithdrawClick: () -> Unit,
    onEarnClick: () -> Unit,
    onSurveysClick: () -> Unit,
    onOffersClick: () -> Unit,
    onRewardsClick: () -> Unit,
    onReferClick: () -> Unit,
    onStartSurvey: (String) -> Unit,
    onStartOffer: (String) -> Unit,
    onClaimBonus: () -> Unit,
    onViewAllTransactions: () -> Unit
) {
    val userName = currentUser?.name?.split(" ")?.firstOrNull() ?: "Rahul"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .padding(horizontal = 16.dp)
            .testTag("home_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // User Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, $userName 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ready to earn verified rewards today?",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Streak pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarningAmberLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = null,
                            tint = WarningAmberDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Day ${dailyBonus?.streakDay ?: 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = WarningAmberDark
                        )
                    }
                }
            }
        }

        // Main Balance Card
        item {
            BalanceCard(
                wallet = wallet,
                onWithdrawClick = onWithdrawClick,
                onEarnClick = onEarnClick
            )
        }

        // Quick Category Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryPill(
                    title = "Surveys",
                    icon = Icons.Default.Assignment,
                    color = PrimaryIndigo,
                    onClick = onSurveysClick
                )
                CategoryPill(
                    title = "Offers",
                    icon = Icons.Default.Paid,
                    color = SuccessGreenDark,
                    onClick = onOffersClick
                )
                CategoryPill(
                    title = "Rewards",
                    icon = Icons.Default.Star,
                    color = Color(0xFFD97706),
                    onClick = onRewardsClick
                )
                CategoryPill(
                    title = "Refer",
                    icon = Icons.Default.CardGiftcard,
                    color = Color(0xFF7C3AED),
                    onClick = onReferClick
                )
            }
        }

        // Daily Bonus Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .testTag("daily_bonus_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(WarningAmberLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎁", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Attendance Bonus",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Streak Day ${dailyBonus?.streakDay ?: 1} • Earn up to ₹50 on Day 7",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onClaimBonus,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Top Surveys For You Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Surveys For You",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = onSurveysClick) {
                    Text("View All", color = PrimaryIndigo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // Top Surveys Items (take 2)
        items(surveys.take(2)) { survey ->
            SurveyCard(
                survey = survey,
                onStartClick = { onStartSurvey(survey.id) }
            )
        }

        // New Offers Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Partner Offers",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = onOffersClick) {
                    Text("View All", color = PrimaryIndigo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // New Offers Items (take 2)
        items(offers.take(2)) { offer ->
            OfferCard(
                offer = offer,
                onStartClick = { onStartOffer(offer.id) }
            )
        }

        // Your Recent Earnings Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Recent Earnings",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                TextButton(onClick = onViewAllTransactions) {
                    Text("Ledger", color = PrimaryIndigo, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // Recent Earnings Items (take 3)
        items(recentTransactions.take(3)) { tx ->
            TransactionItemRow(transaction = tx)
        }

        // How EarnMate Works Educational Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigoLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "How EarnMate Works",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryIndigoDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HowItWorksStep(step = "1", text = "Choose a survey or affiliate offer")
                    HowItWorksStep(step = "2", text = "Complete requirements on the partner's site/app")
                    HowItWorksStep(step = "3", text = "Partner verifies completion & server calculates your 90% reward")
                    HowItWorksStep(step = "4", text = "Withdraw earnings to UPI or Bank (Min ₹100)")

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Platform gross margin target: 10% on qualifying partner revenue. Rewards vary by task availability; no income is guaranteed.",
                        fontSize = 11.sp,
                        color = PrimaryIndigoDark.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryPill(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun HowItWorksStep(step: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(PrimaryIndigo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = TextPrimary)
    }
}
