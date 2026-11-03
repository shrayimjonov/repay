package nodir.paymo_add.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import nodir.paymo_add.model.CardResponse
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "transaction")

data class Transaction(
    @Id

    val id: Long,
    //  var card_id: BigInteger,
    //   var details: String,
    var merchant_transaction_id: String? = null,
    var uzcard_id: String? = null,
    var init_amount: String,
    // var total_amount: BigInteger,
    var status: String,
    var store_account: String,
//    @Lob
//    @Column(length = 10000)
//    var billing_request: String,
//
//    @Lob
//    @Column(length = 10000)
//    var billing_response: String,
    @Lob
    @Column(length = 10000)
    @JsonIgnore
    var uzcard_request: String,
    @Transient
    var external_id: String? = null,
    @Lob
    @Column(length = 10000)
    @JsonIgnore
    var uzcard_response: String? = null,
    @Transient
    var uzcard_response_list: CardResponse? = null,
      var response_code: String,
    //  var uzcard_time: LocalDateTime,
    @Lob
    @Column(length = 10000)
    var reverse_reason: String,
//    @Lob
//    @Column(length = 10000)
//    var reverse_request: String,
//    @Lob
//    @Column(length = 10000)
//    var reverse_response: String,
    var reverse_time: LocalDateTime? = null,
    var terminal_id: String,
    //var action_id: Int,
    var action_details: String,
    var instime: String,
    // var updtime: LocalDateTime,
    // var humo_reco_status: Int,
    //  var uzcard_contract_id: String
)



