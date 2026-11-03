package nodir.paymo_add.service

import com.google.gson.Gson
import nodir.paymo_add.entity.Transaction
import nodir.paymo_add.exception.EntityNotFound
import nodir.paymo_add.model.CardRequest
import nodir.paymo_add.model.CardResponse
import nodir.paymo_add.model.enum.PaymentType
import nodir.paymo_add.repository.PayHistoryRepository
import nodir.paymo_add.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

@Service
class TransactionServiceImpl(
    private val repository: TransactionRepository,
    private val svgateService: SvgateService,
    private val payRepository: PayHistoryRepository
) : TransactionService {

    override fun takeAllTransactions(loanId: String): List<Transaction> {

        val transaction = repository.findTransactionByStoreAccount(loanId)


        return transaction.onEach {
            it.uzcard_response_list = Gson().fromJson(it.uzcard_response, CardResponse::class.java)
            //     it.external_id = Pattern.compile("\\\"ext\\\":").matcher(it.uzcard_request).group(1)

            it.external_id = Gson()
                .fromJson(it.uzcard_request, CardRequest::class.java)
                .getParams().getTran().getExt()

            it.terminal_id = Gson()
                .fromJson(it.uzcard_request, CardRequest::class.java)
                .getParams().getTran().getTerminalId()

            when (it.action_details) {
                "1" -> it.action_details = PaymentType.AUTO_PAYMENT.toString()
                "2" -> it.action_details = PaymentType.MANUALLY.toString()
            }

            it.init_amount = separatorSpace(it.init_amount).toString()


//         if(it.terminal_id.startsWith("25")){
//             for (code in svgateService.findHumoErrors()) {
//                 if (it.response_code == code.codeNum) {
//                     it.response_code = code.codeMessage
//                 }
//             }
//         }


        }


    }

    override fun findPayHistory(loanId: String): List<Any> {
//        return Stream.concat(
//            payRepository.findPaymentHistory(loanId).stream(),
//            payRepository.findPaymentHistory2(loanId).stream()
//        )
//            .collect(Collectors.toList())
        return payRepository.findPaymentHistory(loanId)
    }

    fun separatorSpace(init_amount: String): String? {
        val amount = init_amount.toFloat() / 100
        val formatter = DecimalFormat("#,###.00")
        return formatter.format(amount).replace(",".toRegex(), " ")
    }


    override fun findTransactionById(id: Long): Transaction {
        val transaction = repository.findById(id)
        if (!transaction.isPresent) {
            throw EntityNotFound("Entity with number $id not found")
        }
        return repository.findById(id).get()
    }

    override fun updateTransactionStatusToReverse(id: Long, userInfo: String) {
        val transaction = findTransactionById(id)
        transaction.status = "REVERSE"
        transaction.reverse_reason = userInfo
        transaction.reverse_time = LocalDateTime.now()
        repository.save(transaction)
    }


}


