package com.togai.app.domain.usecase.transaction

import com.togai.app.domain.model.DateRange
import com.togai.app.domain.model.Transaction
import com.togai.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import javax.inject.Inject

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(dateRange: DateRange): Flow<List<Transaction>> {
        val zone = ZoneId.systemDefault()
        val startMillis = dateRange.start.atZone(zone).toInstant().toEpochMilli()
        val endMillis = dateRange.end.atZone(zone).toInstant().toEpochMilli()
        return repository.getTransactionsByDateRange(startMillis, endMillis)
    }
}
