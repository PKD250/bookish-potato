package com.togai.app.domain.usecase.sms

import com.togai.app.domain.model.SmsParsedData
import com.togai.app.sms.SmsParser
import javax.inject.Inject

class ParseSmsUseCase @Inject constructor(
    private val smsParser: SmsParser
) {
    operator fun invoke(smsBody: String, sender: String): SmsParsedData? {
        return smsParser.parse(smsBody, sender)
    }
}
