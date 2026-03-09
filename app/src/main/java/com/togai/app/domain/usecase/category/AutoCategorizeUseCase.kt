package com.togai.app.domain.usecase.category

import com.togai.app.domain.model.SmsParsedData
import com.togai.app.sms.TransactionCategorizer
import javax.inject.Inject

class AutoCategorizeUseCase @Inject constructor(
    private val categorizer: TransactionCategorizer
) {
    suspend operator fun invoke(parsed: SmsParsedData): Long? {
        return categorizer.categorize(parsed.merchant, parsed.rawSms, parsed.type)
    }
}
