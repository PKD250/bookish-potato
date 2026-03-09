package com.togai.app.sms

object BankPatterns {

    val DEBIT_KEYWORDS = listOf(
        "debited", "deducted", "spent", "paid", "withdrawn",
        "purchase", "bought", "payment", "sent", "used at", "charged",
        "debit", "dr"
    )

    val CREDIT_KEYWORDS = listOf(
        "credited", "received", "deposited", "refund",
        "cashback", "reversed", "credit", "cr"
    )

    val DEBIT_REGEX = Regex(
        """(?i)\b(debit(?:ed)?|deducted|spent|paid|withdraw(?:n|al)?|purchase[d]?|payment|sent|used\s+at|charged)\b"""
    )

    val CREDIT_REGEX = Regex(
        """(?i)\b(credit(?:ed)?|received|deposit(?:ed)?|refund(?:ed)?|cashback|reversed)\b"""
    )

    val AMOUNT_PATTERNS = listOf(
        Regex("""(?i)(?:rs\.?|inr\.?|rupees)\s*([0-9]{1,3}(?:,?[0-9]{2,3})*(?:\.[0-9]{1,2})?)"""),
        Regex("""(?i)inr\s+([0-9]{1,3}(?:,?[0-9]{2,3})*(?:\.[0-9]{1,2})?)"""),
        Regex("""([0-9]{1,3}(?:,?[0-9]{2,3})*(?:\.[0-9]{1,2})?)\s*(?:debited|credited|deducted)""", RegexOption.IGNORE_CASE),
    )

    val ACCOUNT_PATTERNS = listOf(
        Regex("""(?i)a/?c\s*(?:no\.?\s*)?(?:ending\s*)?[xX*]*(\d{3,6})"""),
        Regex("""(?i)account\s*(?:no\.?\s*)?(?:ending\s*)?[xX*]*(\d{3,6})"""),
        Regex("""(?i)acct?\s*[xX*]*(\d{3,6})"""),
        Regex("""(?i)card\s*(?:ending\s*)?(?:no\.?\s*)?[xX*]*(\d{3,6})"""),
    )

    val BALANCE_PATTERNS = listOf(
        Regex("""(?i)(?:avl\.?\s*bal(?:ance)?|available\s*bal(?:ance)?|a/?c\s*bal|bal(?:ance)?)\s*[:=]?\s*(?:is\s*)?(?:rs\.?|inr\.?)\s*([0-9]{1,3}(?:,?[0-9]{2,3})*(?:\.[0-9]{1,2})?)"""),
        Regex("""(?i)bal\s*[-:]?\s*(?:rs\.?|inr\.?)?\s*([0-9]{1,3}(?:,?[0-9]{2,3})*(?:\.[0-9]{1,2})?)"""),
    )

    val DATE_PATTERNS = listOf(
        Regex("""(\d{2})[/-](\d{2})[/-](\d{2,4})"""),
        Regex("""(\d{1,2})[/\s-](Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[/\s-](\d{2,4})""", RegexOption.IGNORE_CASE),
        Regex("""(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+(\d{1,2}),?\s+(\d{4})""", RegexOption.IGNORE_CASE),
        Regex("""(\d{2})(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\d{2,4})""", RegexOption.IGNORE_CASE),
    )

    val MONTH_MAP = mapOf(
        "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4,
        "may" to 5, "jun" to 6, "jul" to 7, "aug" to 8,
        "sep" to 9, "oct" to 10, "nov" to 11, "dec" to 12
    )

    val MERCHANT_PATTERNS = listOf(
        Regex("""(?i)(?:at|to|towards|for)\s+([A-Za-z0-9\s&.'-]+?)(?:\s+on|\s+ref|\s+avl|\s+bal|\s*\.|\s*-|$)"""),
        Regex("""(?i)(?:vpa|upi)\s*[:=]?\s*([a-zA-Z0-9._-]+@[a-zA-Z]+)"""),
        Regex("""(?i)info\s*:\s*(.+?)(?:\s+avl|\s+bal|\.|$)"""),
    )

    val UPI_REF_PATTERN = Regex("""(?i)(?:ref\s*(?:no\.?\s*)?|upi\s*ref\s*|txn\s*(?:id|no)?\s*[:=]?\s*)(\d{8,14})""")

    val BANK_SENDER_MAP = mapOf(
        "SBIINB" to "SBI", "SBIPSG" to "SBI", "SBISMS" to "SBI",
        "ATMSBI" to "SBI", "SBIBNK" to "SBI",
        "HDFCBK" to "HDFC", "HDFCBN" to "HDFC",
        "ICICIB" to "ICICI", "ICICIT" to "ICICI",
        "AXISBK" to "Axis", "AXISBN" to "Axis",
        "KOTAKB" to "Kotak", "KOTKBK" to "Kotak",
        "PNBSMS" to "PNB", "PUNJNB" to "PNB",
        "BOIIND" to "BOI",
        "BOBSMS" to "BOB", "BABORB" to "BOB",
        "CANBNK" to "Canara", "CNRBNK" to "Canara",
        "YESBNK" to "Yes Bank",
        "INDBNK" to "Indian Bank",
        "UCOBNK" to "UCO Bank",
        "IABORB" to "IOB",
        "CENTBK" to "Central Bank",
        "IDFCFB" to "IDFC First", "IDFCBK" to "IDFC First",
        "FEDBNK" to "Federal Bank",
        "PAYTMB" to "Paytm",
        "JIOPYB" to "Jio Pay",
        "UNIONB" to "Union Bank",
        "SCBNIN" to "Standard Chartered",
        "CITIBK" to "Citi",
        "INDUSB" to "IndusInd",
        "RBLBNK" to "RBL",
    )

    val NON_TRANSACTIONAL_KEYWORDS = listOf(
        "otp", "one time password", "verification code",
        "login", "password", "pin", "welcome",
        "activate", "register", "download",
        "offer", "cashback offer", "reward points",
        "apply now", "pre-approved", "limit increased",
        "emi conversion", "loan offer"
    )

    // Payment method detection patterns
    val UPI_PATTERNS = Regex("""(?i)\b(UPI|VPA)\b|@\w+\.\w+|UPI\s*Ref""")
    val CREDIT_CARD_PATTERNS = Regex("""(?i)(?:Credit\s*Card|CC)\s+(?:ending|no\.?|[xX*]+)\s*(\d{4})""")
    val DEBIT_ACCOUNT_PATTERNS = Regex("""(?i)(?:Savings|Current|A/c|Acct?)\s+(?:(?:no\.?|ending|[xX*]+)\s*)?(\d{4,6})""")
    val DUE_DATE_PATTERNS = Regex("""(?i)(?:Payment\s+Due\s+Date|Due\s+Date|Bill\s+Due(?:\s+On)?)[:\s]+(\d{1,2}[-/\s][A-Za-z0-9\-/\s]{2,15})""")
}
