# EarnMate: Complete Tasks. Earn Rewards.

A mobile-first rewards application built with Kotlin, Jetpack Compose, Room Database, and Material Design 3.

EarnMate enables users to participate in legitimate third-party market research surveys, complete affiliate and partner offers, track earnings with an immutable double-entry style ledger, and withdraw funds securely through UPI and Bank Accounts.

---

## Key Highlights

- **Target Business Model**: 10% gross platform margin on qualifying partner revenue (`userReward = partnerRevenue - 10% platformMargin - remittanceCosts`). Never advertises or promises guaranteed earnings.
- **Ledger Security**: Clear visual separation of **Pending** rewards (awaiting partner conversion verification) and **Approved** rewards.
- **Provider Abstraction Architecture**: Pluggable interfaces for `SurveyProvider`, `OfferProvider`, `PayoutProvider`, and `RewardActivityProvider`. Cleanly switch from `DEMO_MODE=true` to live networks without rewriting the client.
- **Anti-Fraud Framework**: Multi-factor signal evaluation (completion velocity checks, withdrawal frequency, duplicate conversion prevention via unique constraint idempotency).
- **Compliance & Privacy**: Minimal personal data collection (no phone OTP required for registration). Zero-permission Google Sign-In and email login.

---

## Screens Included (28 User Screens + Admin Dashboard)

1. **Splash Screen**: Branded hero entry with tagline.
2. **Onboarding**: 3-step value proposition (Surveys, Offers, Transparent Ledger).
3. **Login**: Continue with Google or Email.
4. **Google Sign-In**: Account selection sheet.
5. **Email Authentication**: Sign-in / registration with credential validation.
6. **Complete Profile**: Demographics for survey matching (Age range, Gender, Occupation, Region).
7. **Home Dashboard**:
   - Greeting & Day streak badge
   - Balance Card: Available Balance, Pending Balance, Lifetime Earned, Today's Earnings
   - Quick Category pills (Surveys, Offers, Rewards, Refer)
   - Daily Bonus Banner
   - Top Surveys For You
   - New Partner Offers
   - Recent Earnings with status pills
   - "How EarnMate Works" educational guide
8. **Survey Marketplace**: Filterable by category (Fintech, Tech, Lifestyle, Travel) and difficulty.
9. **Survey Active Flow**: Question-by-question flow, progress bar, timer, and server-side verification.
10. **Offers Marketplace**: Categorized offers (Apps, Shopping, Games, Services) with reward amounts.
11. **Offer Details**: Requirement checklist, tracking token preview, and partner webhook simulation.
12. **Reward Activities**: Permitted non-intrusive knowledge tasks and daily trivia.
13. **Wallet**: Available balance, pending balance, lifetime totals, withdraw action.
14. **Transaction Ledger**: Complete immutable audit trail with filter tabs (All, Approved, Pending, Withdrawals).
15. **Withdraw**: Method selection (UPI ID vs Bank Account), quick amount pills (₹100, ₹200, ₹500), minimum ₹100 validation.
16. **Withdrawal Confirmation**: Breakdown of reserved funds, destination account, and risk status.
17. **Withdrawal History**: Past requests with live status (`PENDING_APPROVAL`, `PROCESSING`, `COMPLETED`).
18. **Refer & Earn**: Unique referral code (`EARNMATE123`), one-tap clipboard copy, and 4-step qualification rules.
19. **Daily Bonus**: 7-day streak calendar with server-time validation.
20. **Notifications**: Task alerts, reward approvals, and payout updates with unread indicators.
21. **Profile**: Demographics profile, avatar, quick links.
22. **Edit Profile**: Update region, field of work, and default UPI ID.
23. **Settings**: Notifications toggle, biometric lock switch, policy disclosures.
24. **Privacy Policy**: Transparent disclosures on partner tokens, ledger data, and retention.
25. **Terms & Conditions**: Rules against bots/VPN abuse, non-guaranteed income clause, payout thresholds.
26. **Help & Support**: FAQ accordion answering common questions.
27. **Support Ticket**: Ticket submission by category and previous ticket status tracker.
28. **Account Deletion**: User personal data anonymization with financial record preservation for audits.
29. **Admin Control Hub**:
    - **Profit Dashboard**: Partner Revenue vs User Rewards (90%) vs Gross Margin (10%) vs Net Platform Contribution.
    - **Conversions Ledger**: Real-time webhook logs.
    - **Withdrawals Manager**: Manual review & one-tap approval for high-risk payouts.
    - **Fraud & Risk Monitor**: Flags rapid survey completions and suspicious velocities.
    - **Settings Configuration**: Adjust minimum withdrawal, platform margin %, and referral rewards.

---

## Architecture

```
com.example/
├── MainActivity.kt               # Edge-to-edge entry point, BackHandler & Screen Routing
├── data/
│   ├── model/Entities.kt         # Room Entities (Users, Wallet, Transactions, Surveys, Offers, etc.)
│   ├── dao/EarnMateDao.kt        # Reactive DAO with Flow<T> queries and transactions
│   ├── database/EarnMateDatabase.kt # Abstract Room Database holder
│   └── repository/EarnMateRepository.kt # Central repository orchestrating data, idempotency & ledger
├── domain/
│   ├── engine/
│   │   ├── RewardEngine.kt       # 10% platform gross margin calculation formula
│   │   └── AntiFraudEngine.kt    # Velocity limits, speed checks & risk scoring
│   └── providers/
│       ├── SurveyProvider.kt & MockSurveyProvider.kt
│       ├── OfferProvider.kt & MockOfferProvider.kt
│       ├── PayoutProvider.kt & MockPayoutProvider.kt
│       └── RewardActivityProvider.kt & MockRewardProvider.kt
└── ui/
    ├── theme/ (Color.kt, Theme.kt, Type.kt) # Material 3 Indigo/Green/Amber palette
    ├── components/CommonComponents.kt       # TopBar, BottomBar, BalanceCard, Cards, Badges
    ├── screens/
    │   ├── AuthScreens.kt
    │   ├── HomeScreen.kt
    │   ├── EarnScreens.kt
    │   ├── WalletScreens.kt
    │   ├── ReferralAndBonusScreens.kt
    │   ├── ProfileAndSupportScreens.kt
    │   └── AdminScreens.kt
    └── viewmodel/
        ├── EarnMateState.kt      # Screen states & UI models
        └── EarnMateViewModel.kt  # StateFlow management & user actions
```

---

## Adding Real Providers (Production Ready)

To plug in a real network:
1. Implement the corresponding interface:
   - For Surveys: Implement `SurveyProvider` (e.g. `BitLabsSurveyProvider`, `PollfishProvider`).
   - For Offers: Implement `OfferProvider` (e.g. `AdGateOfferProvider`, `ToroxOfferProvider`).
   - For Payouts: Implement `PayoutProvider` (e.g. `RazorpayXProvider`, `CashfreePayoutProvider`).
2. Pass the real implementation to `EarnMateRepository` when `DEMO_MODE=false`.
3. Inbound webhooks from partner networks post to `/api/webhooks/conversion` and call `handlePartnerConversionWebhook(conversionId, offerId, userId, partnerRevenue)`. The unique constraint on `conversionId` guarantees strict idempotency.
