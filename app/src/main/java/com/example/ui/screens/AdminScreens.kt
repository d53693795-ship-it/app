package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingEntity
import com.example.data.model.FraudEventEntity
import com.example.data.model.OfferConversionEntity
import com.example.data.model.UserEntity
import com.example.data.model.WithdrawalEntity
import com.example.ui.theme.BackgroundSlate
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    users: List<UserEntity>,
    conversions: List<OfferConversionEntity>,
    withdrawals: List<WithdrawalEntity>,
    fraudEvents: List<FraudEventEntity>,
    settings: List<AppSettingEntity>,
    onApproveWithdrawal: (String) -> Unit,
    onUpdateSetting: (String, String) -> Unit,
    onExitAdmin: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Profit & KPIs", "Conversions", "Withdrawals", "Fraud & Risk", "Settings")

    // Financial accounting metrics (Baseline + real conversions)
    val basePartnerRevenue = 100000.0
    val dynamicPartnerRevenue = conversions.sumOf { it.partnerRevenue }
    val totalPartnerRevenue = basePartnerRevenue + dynamicPartnerRevenue

    val baseUserRewards = 90000.0
    val dynamicUserRewards = conversions.sumOf { it.userReward }
    val totalUserRewards = baseUserRewards + dynamicUserRewards

    val grossMargin = totalPartnerRevenue * 0.10 // 10%
    val estimatedGatewayFees = totalPartnerRevenue * 0.02 // 2%
    val netContribution = grossMargin - estimatedGatewayFees

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSlate)
            .padding(16.dp)
            .testTag("admin_dashboard_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExitAdmin) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text("Admin Control Hub", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("EarnMate Management & Profit Analytics", fontSize = 11.sp, color = TextSecondary)
                }
            }

            Surface(shape = RoundedCornerShape(8.dp), color = PrimaryIndigoLight) {
                Text(
                    "Admin Active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PrimaryIndigo,
            edgePadding = 0.dp,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> AdminProfitOverview(
                totalUsers = users.size + 148,
                activeUsers = 89,
                totalPartnerRevenue = totalPartnerRevenue,
                totalUserRewards = totalUserRewards,
                grossMargin = grossMargin,
                estimatedFees = estimatedGatewayFees,
                netContribution = netContribution,
                pendingWithdrawalsCount = withdrawals.count { it.status == "PENDING_APPROVAL" || it.status == "PROCESSING" },
                fraudAlertsCount = fraudEvents.size
            )
            1 -> AdminConversionsTab(conversions = conversions)
            2 -> AdminWithdrawalsTab(withdrawals = withdrawals, onApprove = onApproveWithdrawal)
            3 -> AdminFraudTab(fraudEvents = fraudEvents)
            4 -> AdminSettingsTab(settings = settings, onSave = onUpdateSetting)
        }
    }
}

@Composable
fun AdminProfitOverview(
    totalUsers: Int,
    activeUsers: Int,
    totalPartnerRevenue: Double,
    totalUserRewards: Double,
    grossMargin: Double,
    estimatedFees: Double,
    netContribution: Double,
    pendingWithdrawalsCount: Int,
    fraudAlertsCount: Int
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Target Profit Model Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigoDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PLATFORM PROFIT & MARGIN DASHBOARD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC7D2FE),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Partner Revenue", fontSize = 11.sp, color = Color(0xFFE0E7FF))
                            Text("₹${String.format(Locale.US, "%,.2f", totalPartnerRevenue)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("User Rewards (90%)", fontSize = 11.sp, color = Color(0xFFE0E7FF))
                            Text("₹${String.format(Locale.US, "%,.2f", totalUserRewards)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreenLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gross Margin vs Net Contribution
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Gross Margin (10%)", fontSize = 10.sp, color = Color(0xFFC7D2FE))
                            Text("₹${String.format(Locale.US, "%,.2f", grossMargin)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            Text("Target Gross: 10.0%", fontSize = 10.sp, color = Color(0xFFE0E7FF))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Net Platform Contribution", fontSize = 10.sp, color = Color(0xFFC7D2FE))
                            Text("₹${String.format(Locale.US, "%,.2f", netContribution)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SuccessGreenLight)
                            Text("After ~2% remittance costs", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: Target model represents 10% gross margin on qualifying partner revenue. It is not advertised as guaranteed net profit.",
                        fontSize = 10.sp,
                        color = Color(0xFFA5B4FC),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // KPI Badges Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    title = "Total Users",
                    value = "$totalUsers",
                    icon = Icons.Default.People,
                    color = PrimaryIndigo,
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    title = "Active Today",
                    value = "$activeUsers",
                    icon = Icons.Default.TrendingUp,
                    color = SuccessGreenDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminKpiCard(
                    title = "Pending Payouts",
                    value = "$pendingWithdrawalsCount",
                    icon = Icons.Default.Paid,
                    color = WarningAmberDark,
                    modifier = Modifier.weight(1f)
                )
                AdminKpiCard(
                    title = "Fraud Alerts",
                    value = "$fraudAlertsCount",
                    icon = Icons.Default.Security,
                    color = DangerRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AdminKpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text(title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AdminConversionsTab(conversions: List<OfferConversionEntity>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (conversions.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No conversions recorded yet. Simulate an offer to test.", color = TextSecondary)
                }
            }
        } else {
            items(conversions) { cnv ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ID: ${cnv.conversionId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                            Surface(shape = RoundedCornerShape(6.dp), color = SuccessGreenLight) {
                                Text(cnv.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreenDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Partner: ₹${String.format(Locale.US, "%.2f", cnv.partnerRevenue)}", fontSize = 12.sp, color = TextSecondary)
                            Text("Margin: ₹${String.format(Locale.US, "%.2f", cnv.platformMargin)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            Text("User: ₹${String.format(Locale.US, "%.2f", cnv.userReward)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreenDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalsTab(
    withdrawals: List<WithdrawalEntity>,
    onApprove: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (withdrawals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No withdrawal requests found.", color = TextSecondary)
                }
            }
        } else {
            items(withdrawals) { wd ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "₹${String.format(Locale.US, "%.2f", wd.amount)} via ${wd.method}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (wd.status == "COMPLETED") SuccessGreenLight else WarningAmberLight
                            ) {
                                Text(
                                    wd.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wd.status == "COMPLETED") SuccessGreenDark else WarningAmberDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Destination: ${wd.payoutDetails}", fontSize = 12.sp, color = TextSecondary)
                        Text("Risk: ${wd.riskLevel.name} (${wd.riskReason})", fontSize = 11.sp, color = TextTertiary)

                        if (wd.status != "COMPLETED") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onApprove(wd.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Approve & Mark Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFraudTab(fraudEvents: List<FraudEventEntity>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (fraudEvents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No fraud events flagged. System running smoothly.", color = TextSecondary)
                }
            }
        } else {
            items(fraudEvents) { ev ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DangerRedLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ev.signalType, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DangerRed)
                            Text("Score: ${ev.riskScore}/100", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DangerRed)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ev.reason, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(
    settings: List<AppSettingEntity>,
    onSave: (String, String) -> Unit
) {
    var minWithdrawal by remember { mutableStateOf("100.0") }
    var marginPct by remember { mutableStateOf("10.0") }
    var referralReward by remember { mutableStateOf("10.0") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Platform Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = minWithdrawal,
                    onValueChange = { minWithdrawal = it },
                    label = { Text("Minimum Withdrawal (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = marginPct,
                    onValueChange = { marginPct = it },
                    label = { Text("Platform Gross Margin (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = referralReward,
                    onValueChange = { referralReward = it },
                    label = { Text("Referral Bonus per Qualifying User (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSave("min_withdrawal", minWithdrawal)
                        onSave("platform_margin_pct", marginPct)
                        onSave("referral_reward", referralReward)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save App Configuration")
                }
            }
        }
    }
}
