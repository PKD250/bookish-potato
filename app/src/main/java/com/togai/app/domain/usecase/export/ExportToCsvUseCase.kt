package com.togai.app.domain.usecase.export

import com.togai.app.domain.model.Transaction
import com.togai.app.util.CsvExporter
import android.net.Uri
import javax.inject.Inject

class ExportToCsvUseCase @Inject constructor(
    private val csvExporter: CsvExporter
) {
    suspend operator fun invoke(transactions: List<Transaction>): Uri {
        return csvExporter.exportToCsv(transactions)
    }
}
