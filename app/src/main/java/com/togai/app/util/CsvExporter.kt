package com.togai.app.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.togai.app.domain.model.Transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun exportToCsv(
        transactions: List<Transaction>,
        fileName: String = "togai_export_${LocalDate.now()}.csv"
    ): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IOException("Failed to create file")

        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            writer.appendLine("Date,Type,Amount (INR),Category,Description,Account,Bank,Reference")

            transactions.forEach { txn ->
                val date = txn.transactionDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val desc = txn.description.replace("\"", "\"\"")
                val cat = (txn.categoryName ?: "Uncategorized").replace("\"", "\"\"")
                writer.appendLine(
                    "$date,${txn.type.name},${txn.amount},\"$cat\",\"$desc\",${txn.accountNumber ?: ""},${txn.bankName ?: ""},${txn.referenceId ?: ""}"
                )
            }
        }

        return uri
    }
}
