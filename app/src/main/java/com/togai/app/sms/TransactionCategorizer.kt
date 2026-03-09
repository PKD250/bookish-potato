package com.togai.app.sms

import com.togai.app.data.local.dao.CategoryDao
import com.togai.app.domain.model.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionCategorizer @Inject constructor(
    private val categoryDao: CategoryDao
) {
    private val categoryKeywords = mapOf(
        "Food & Dining" to listOf(
            "swiggy", "zomato", "dominos", "mcdonalds", "kfc", "pizza",
            "restaurant", "cafe", "food", "burger", "starbucks", "chaayos",
            "barbeque", "subway", "haldiram", "biryani", "dining",
            "pizzahut", "dunkin", "baskin", "ice cream", "bakery"
        ),
        "Transport" to listOf(
            "uber", "ola", "rapido", "metro", "irctc", "railway", "redbus",
            "makemytrip", "goibibo", "petrol", "diesel", "fuel", "parking",
            "fastag", "toll", "cab", "auto", "rickshaw", "cleartrip",
            "bpcl", "hpcl", "iocl"
        ),
        "Shopping" to listOf(
            "amazon", "flipkart", "myntra", "ajio", "meesho", "nykaa",
            "croma", "reliance digital", "tatacliq", "snapdeal",
            "lenskart", "decathlon", "pepperfry"
        ),
        "Groceries" to listOf(
            "bigbasket", "blinkit", "zepto", "instamart", "jiomart",
            "dmart", "grocery", "supermarket", "kirana", "grofers",
            "dunzo", "country delight"
        ),
        "Bills & Utilities" to listOf(
            "electricity", "electric", "bescom", "tatapower", "adani",
            "gas bill", "water bill", "broadband", "wifi",
            "dth", "tata play", "dish tv", "insurance", "lic", "maintenance"
        ),
        "Entertainment" to listOf(
            "netflix", "hotstar", "prime video", "spotify", "youtube",
            "bookmyshow", "pvr", "inox", "gaming", "playstation", "steam"
        ),
        "Health" to listOf(
            "pharmacy", "medical", "hospital", "doctor", "apollo",
            "1mg", "pharmeasy", "netmeds", "diagnostic", "lab", "clinic", "gym"
        ),
        "Education" to listOf(
            "school", "college", "university", "tuition", "course",
            "udemy", "coursera", "unacademy", "byju", "exam"
        ),
        "ATM Withdrawal" to listOf(
            "atm", "cash withdrawal", "cash wdl", "atm-wdl", "atm wdl"
        ),
        "UPI Transfer" to listOf(
            "upi", "phonepe", "gpay", "google pay", "paytm", "bhim", "cred"
        ),
        "EMI / Loan" to listOf(
            "emi", "loan", "installment", "bajaj finserv", "hdfc ltd",
            "home loan", "car loan", "personal loan"
        ),
        "Rent" to listOf("rent", "nobroker", "housing", "flat", "landlord"),
        "Recharge" to listOf("recharge", "prepaid", "postpaid", "mobile recharge"),
        "Investment" to listOf(
            "zerodha", "groww", "upstox", "kuvera", "mutual fund", "sip", "nps"
        ),
        "Salary" to listOf("salary", "neft-salary", "payroll", "wages", "stipend"),
        "Refund" to listOf("refund", "reversed", "reversal", "return", "chargeback"),
        "Cashback" to listOf("cashback", "cash back", "reward"),
        "Interest" to listOf("interest credited", "int.credit", "interest cr"),
    )

    suspend fun categorize(merchant: String, smsBody: String, type: TransactionType): Long? {
        val searchText = "$merchant $smsBody".lowercase()

        var bestMatch: String? = null
        var bestScore = 0

        for ((categoryName, keywords) in categoryKeywords) {
            val score = keywords.count { searchText.contains(it) }
            if (score > bestScore) {
                bestScore = score
                bestMatch = categoryName
            }
        }

        if (bestMatch == null) {
            bestMatch = if (type == TransactionType.CREDIT) "Other Income" else "Other Expense"
        }

        return categoryDao.getCategoryByName(bestMatch)?.id
    }
}
