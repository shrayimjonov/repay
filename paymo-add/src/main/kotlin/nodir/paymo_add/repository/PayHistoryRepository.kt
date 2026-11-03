package nodir.paymo_add.repository

import nodir.paymo_add.entity.PayHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PayHistoryRepository: JpaRepository<PayHistory, String>  {
    @Query(
        value = "select ua.id,ua.action, ua.date_created,l.external_id,(SELECT card_Pan from payment_core.card c WHERE c.id = ua.card_id) AS pan,u.username,u.name,u.last_name, ua.remote_address FROM marketplace.user_activity ua\n" +
                "  INNER JOIN marketplace.user u ON ua.user_id = u.id\n" +
                "  inner JOIN marketplace.loan l ON ua.loan_id = l.id\n" +
                "WHERE l.external_id=:loan_id \n" +
                "  order BY ua.id DESC", nativeQuery = true
    )
    fun findPaymentHistory(loan_id: String): List<PayHistory>


    @Query(
        value = "select ua.id,ua.action, ua.date_created,l.external_id,(SELECT card_Pan from payment_core.card c WHERE c.id = ua.card_id) AS pan,u.username,u.name,u.last_name, ua.remote_address FROM marketplace.user_activity_arh ua\n" +
                "  INNER JOIN marketplace.user u ON ua.user_id = u.id\n" +
                "  inner JOIN marketplace.loan l ON ua.loan_id = l.id\n" +
                "WHERE l.external_id=:loan_id \n" +
                "  order BY ua.id DESC", nativeQuery = true
    )
    fun findPaymentHistory2(loan_id: String): List<PayHistory>
}