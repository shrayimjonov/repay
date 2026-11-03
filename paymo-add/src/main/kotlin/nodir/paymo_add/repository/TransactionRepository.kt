package nodir.paymo_add.repository

import nodir.paymo_add.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {

    @Query(value = "select * from transaction where store_account=:loan_id order by instime desc", nativeQuery = true)
    fun findTransactionByStoreAccount(loan_id: String): List<Transaction>


}