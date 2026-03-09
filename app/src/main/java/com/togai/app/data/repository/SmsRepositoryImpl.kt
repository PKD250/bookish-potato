package com.togai.app.data.repository

import android.content.Context
import android.provider.Telephony
import com.togai.app.domain.repository.ImportProgress
import com.togai.app.domain.repository.SmsRepository
import com.togai.app.sms.SmsParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsParser: SmsParser
) : SmsRepository {

    override fun importHistoricalSms(sinceTimestamp: Long): Flow<ImportProgress> = flow {
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(
                Telephony.Sms.BODY,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE
            ),
            "${Telephony.Sms.DATE} >= ?",
            arrayOf(sinceTimestamp.toString()),
            "${Telephony.Sms.DATE} DESC"
        ) ?: return@flow

        val total = cursor.count
        var processed = 0
        var parsed = 0

        cursor.use {
            while (it.moveToNext()) {
                val body = it.getString(0) ?: continue
                val address = it.getString(1) ?: continue

                val result = smsParser.parse(body, address)
                if (result != null) {
                    parsed++
                }

                processed++
                if (processed % 10 == 0 || processed == total) {
                    emit(ImportProgress(total, processed, parsed, result))
                }
            }
        }

        emit(ImportProgress(total, processed, parsed, null))
    }.flowOn(Dispatchers.IO)
}
