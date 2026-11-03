package nodir.paymo_add.service

import nodir.paymo_add.entity.Transaction

interface TransactionService {
    fun takeAllTransactions(loanId: String):List<Transaction>

    fun findTransactionById(id: Long) : Transaction

    fun updateTransactionStatusToReverse(id: Long, userInfo: String)

    fun findPayHistory(loanId: String): List<Any>


}