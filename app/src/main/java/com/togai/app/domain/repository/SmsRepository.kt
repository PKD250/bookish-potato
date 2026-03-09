package com.togai.app.domain.repository

import com.togai.app.domain.model.SmsParsedData
import kotlinx.coroutines.flow.Flow

data class ImportProgress(
    val total: Int,
    val processed: Int,
    val parsed: Int,
    val lastParsed: SmsParsedData? = null
)

interface SmsRepository {
    fun importHistoricalSms(sinceTimestamp: Long): Flow<ImportProgress>
}
