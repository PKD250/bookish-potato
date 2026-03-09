package com.togai.app.sms

import com.togai.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SmsParserTest {

    private lateinit var parser: SmsParser

    @Before
    fun setup() {
        parser = SmsParser()
    }

    @Test
    fun `parse SBI debit SMS`() {
        val sms = "Dear Customer, Rs.2,500.00 has been debited from your A/c XXXXXXX1234 on 15-01-2025 by UPI ref no 503412345678. If not done by you, call 1800111109. -SBI"
        val result = parser.parse(sms, "VM-SBIINB")

        assertNotNull(result)
        assertEquals(2500.0, result!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.type)
        assertEquals("1234", result.accountNumber)
        assertEquals("SBI", result.bankName)
        assertEquals("503412345678", result.referenceId)
    }

    @Test
    fun `parse HDFC credit SMS`() {
        val sms = "HDFC Bank: Rs 45,000.00 credited to a/c XX4567 on 01-02-25 by NEFT-REF123456789. Avl bal: Rs 1,23,456.78"
        val result = parser.parse(sms, "VM-HDFCBK")

        assertNotNull(result)
        assertEquals(45000.0, result!!.amount, 0.01)
        assertEquals(TransactionType.CREDIT, result.type)
        assertEquals("4567", result.accountNumber)
        assertEquals("HDFC", result.bankName)
    }

    @Test
    fun `parse ICICI debit with merchant`() {
        val sms = "Your ICICI Bank Acct XX9876 is debited with INR 899.00 on 28-Jan-25. Info: AMAZON PAY INDIA. Avl Bal INR 15,234.56"
        val result = parser.parse(sms, "VM-ICICIB")

        assertNotNull(result)
        assertEquals(899.0, result!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.type)
        assertEquals("9876", result.accountNumber)
        assertEquals("ICICI", result.bankName)
    }

    @Test
    fun `parse Axis card spend`() {
        val sms = "Rs.250.00 spent on Axis Bank Card ending 5678 at SWIGGY on 05-02-25. Avl Bal: Rs.8,765.43"
        val result = parser.parse(sms, "VM-AXISBK")

        assertNotNull(result)
        assertEquals(250.0, result!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.type)
        assertEquals("5678", result.accountNumber)
        assertEquals("Axis", result.bankName)
    }

    @Test
    fun `filter out OTP message`() {
        val sms = "Your OTP for transaction is 123456. Do not share with anyone. Rs.500 will be debited."
        val result = parser.parse(sms, "VM-HDFCBK")
        assertNull(result)
    }

    @Test
    fun `filter out promotional message`() {
        val sms = "Apply now for pre-approved personal loan of Rs.5,00,000. Call 18001234567."
        val result = parser.parse(sms, "VM-HDFCBK")
        assertNull(result)
    }

    @Test
    fun `extract amount with Indian numbering`() {
        val amount = parser.extractAmount("Rs 1,23,456.78 debited")
        assertEquals(123456.78, amount!!, 0.01)
    }

    @Test
    fun `detect bank from sender`() {
        assertEquals("SBI", parser.detectBank("VM-SBIINB"))
        assertEquals("HDFC", parser.detectBank("AD-HDFCBK"))
        assertEquals("ICICI", parser.detectBank("VM-ICICIB"))
        assertEquals("Axis", parser.detectBank("VM-AXISBK"))
        assertEquals("Kotak", parser.detectBank("BZ-KOTAKB"))
        assertNull(parser.detectBank("VM-RANDOM"))
    }

    @Test
    fun `parse PNB credit`() {
        val sms = "Dear Customer, INR 35000 has been Credited to your A/c no. XX7890 on 01/02/2025. Your a/c bal is INR 42,500.00-PNB"
        val result = parser.parse(sms, "VM-PNBSMS")

        assertNotNull(result)
        assertEquals(35000.0, result!!.amount, 0.01)
        assertEquals(TransactionType.CREDIT, result.type)
        assertEquals("7890", result.accountNumber)
        assertEquals("PNB", result.bankName)
    }
}
