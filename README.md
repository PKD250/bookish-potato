<div align="center">

# 💰 Togai

### The local-first SMS expense tracker built for India

[![Build Debug APK](https://github.com/PKD250/bookish-potato/actions/workflows/build.yml/badge.svg)](https://github.com/PKD250/bookish-potato/actions/workflows/build.yml)
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.12-4285F4?logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

*Automatically reads your bank SMS messages, categorizes spending, and gives you a complete picture of your finances — no servers, no cloud, no subscriptions.*

</div>

---

## ✨ Features

### 📲 SMS Auto-Import
- Reads transaction SMS from **23 Indian banks** automatically
- Detects **UPI payments**, **credit card charges**, and **savings account debits**
- SHA-256 deduplication — never imports the same transaction twice
- Daily incremental sync — only fetches what's new since the last run

### 🏦 Multi-Account Architecture
| Account Type | Use Case |
|---|---|
| 💵 Savings | Bank accounts, salary accounts |
| 💳 Credit Card | Credit cards with limit & billing cycle tracking |
| 📈 Investment | Mutual funds, FDs, stock accounts |
| 💸 Cash | Physical cash tracking |

### 💳 Credit Card Intelligence
- Tracks **utilization %** per card for the current billing cycle
- Extracts **payment due dates** directly from bank SMS
- Color-coded utilization bar (green → amber → red at 50%/80%)
- Aggregate utilization across all cards

### 📊 Dashboard & Analytics
- Monthly income vs expense summary
- Today's spending at a glance
- Category-wise spending breakdown with charts (powered by Vico)
- Pending account assignment banner — alerts when transactions need review

### 🗂️ Smart Categorization
- Auto-categorizes transactions by merchant name and SMS content
- Categories: Food, Transport, Shopping, Entertainment, Bills, Healthcare, and more
- Manual override always available

### 📤 Export & Backup
- Export all transactions to **CSV** with one tap
- Fully local-first — your data never leaves your device

---

## 🏗️ Architecture

Togai follows **Clean Architecture** with strict separation of concerns across three layers:

```
┌─────────────────────────────────────────────────┐
│                  UI Layer                        │
│   Jetpack Compose · ViewModel · StateFlow        │
│   Screens: Dashboard, Transactions, Accounts,   │
│   Analytics, Credit Cards, Settings, Savings    │
├─────────────────────────────────────────────────┤
│                Domain Layer                      │
│   Use Cases · Repository Interfaces             │
│   Domain Models · Business Logic                │
├─────────────────────────────────────────────────┤
│                Data Layer                        │
│   Room Database · Repository Implementations    │
│   SMS ContentResolver · SharedPreferences       │
└─────────────────────────────────────────────────┘
```

### SMS Processing Pipeline

```
Incoming SMS / Historical Import
         │
         ▼
   SmsParser.parse()
    ├─ isTransactionalSms()     → filter OTPs, offers, etc.
    ├─ extractAmount()          → parse ₹ amount
    ├─ detectTransactionType()  → DEBIT or CREDIT
    ├─ extractPaymentMethod()   → UPI (0.9) · CC (1.0) · Savings (0.85)
    ├─ extractDueDate()         → credit card billing dates
    └─ extractMerchant()        → payee name
         │
         ▼
   AccountDao.findByAccountNumber()
    ├─ Match found  → link transaction to account
    └─ No match     → flag as pendingAccountAssignment
         │
         ▼
   TransactionCategorizer
         │
         ▼
   Room Database (togai_db)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose (BOM 2024.12.01) + Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.53.1 |
| Database | Room 2.6.1 |
| Async | Kotlin Coroutines 1.9.0 + Flow |
| Charts | Vico 2.0.0-beta.2 |
| Navigation | Navigation Compose 2.8.5 |
| Build | Gradle 8.11.1 + KSP 2.0.21-1.0.28 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 (Android 15) |

---

## 🏦 Supported Banks

Togai automatically recognises SMS from the following Indian banks and wallets:

<table>
<tr>
<td>

**Public Sector Banks**
- 🏛️ State Bank of India (SBI)
- 🏛️ Punjab National Bank (PNB)
- 🏛️ Bank of India (BOI)
- 🏛️ Bank of Baroda (BOB)
- 🏛️ Canara Bank
- 🏛️ Indian Bank
- 🏛️ UCO Bank
- 🏛️ Indian Overseas Bank (IOB)
- 🏛️ Central Bank of India
- 🏛️ Union Bank of India

</td>
<td>

**Private Sector Banks**
- 🏦 HDFC Bank
- 🏦 ICICI Bank
- 🏦 Axis Bank
- 🏦 Kotak Mahindra Bank
- 🏦 Yes Bank
- 🏦 IDFC First Bank
- 🏦 Federal Bank
- 🏦 IndusInd Bank
- 🏦 RBL Bank
- 🏦 Standard Chartered
- 🏦 Citibank

</td>
<td>

**Fintech & Wallets**
- 📱 Paytm
- 📱 Jio Pay

</td>
</tr>
</table>

> **Don't see your bank?** The parser also works with any SMS containing standard Indian banking keywords (debited/credited, INR/Rs, UPI Ref, etc.). Open an issue to request explicit support.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android device or emulator running API 26+
- **SMS Read permission** must be granted on the device

### Build locally

```bash
# Clone the repository
git clone https://github.com/PKD250/bookish-potato.git
cd bookish-potato

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

The debug APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Get APK from CI (no Android Studio needed)

1. Go to the [**Actions tab**](https://github.com/PKD250/bookish-potato/actions)
2. Click the latest **Build Debug APK** workflow run
3. Download the artifact named `togai-debug-N`

---

## 📁 Project Structure

```
app/src/main/java/com/togai/app/
│
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs (TransactionDao, AccountDao, ...)
│   │   ├── entity/       # Room entities
│   │   └── AppDatabase.kt
│   ├── preferences/      # SyncPreferencesManager
│   └── repository/       # Repository implementations
│
├── domain/
│   ├── model/            # Pure domain models (Transaction, Account, ...)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # One use case per file
│       ├── account/
│       ├── sms/
│       ├── transaction/
│       └── ...
│
├── sms/
│   ├── BankPatterns.kt         # All regex patterns
│   ├── SmsParser.kt            # Core SMS → SmsParsedData parser
│   ├── SmsBroadcastReceiver.kt # Real-time incoming SMS
│   └── TransactionCategorizer.kt
│
├── ui/
│   ├── navigation/       # AppNavigation, Screen sealed class
│   ├── screens/          # One folder per screen
│   │   ├── dashboard/
│   │   ├── transactions/
│   │   ├── accounts/
│   │   ├── creditcards/
│   │   ├── analytics/
│   │   ├── savings/
│   │   └── settings/
│   ├── common/           # Reusable Composables
│   └── theme/            # TogaiTheme, colors, typography
│
├── di/                   # Hilt modules (AppModule, RepositoryModule)
└── util/                 # DateUtils, CsvExporter, Extensions
```

---

## 🔒 Privacy

Togai is **100% local-first**:

- ✅ All data stays on your device
- ✅ No internet permission required for core functionality
- ✅ No analytics, no tracking, no ads
- ✅ No account registration
- ❌ No cloud sync (by design)

The only permissions requested are:
| Permission | Reason |
|---|---|
| `RECEIVE_SMS` | Capture new bank transactions in real time |
| `READ_SMS` | Import historical transactions on first sync |

---

## 🗺️ Roadmap

- [ ] Budgets & spending limits with alerts
- [ ] Savings goals progress tracking
- [ ] Monthly & yearly comparison charts
- [ ] Widget for quick spending summary
- [ ] Encrypted local backup / restore
- [ ] Signed release APK with Play Store listing

---

## 🤝 Contributing

Contributions are welcome! If you want to add support for a new bank, improve SMS parsing accuracy, or fix a bug:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/add-xyz-bank`)
3. Make your changes
4. Push and open a Pull Request

For new bank support, add the sender code → bank name mapping to `BankPatterns.BANK_SENDER_MAP` in `sms/BankPatterns.kt`.

---

## 📄 License

```
MIT License

Copyright (c) 2025 Togai Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<div align="center">

Built with ❤️ for India · Kotlin + Jetpack Compose · Local-first forever

</div>
