package uz.nodir.paymentframe.service.business.persistent.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uz.nodir.paymentframe.model.payment.request.PaymentCreateRequestDTO
import uz.nodir.paymentframe.model.payment.response.PaymentBaseResponseDTO
import uz.nodir.paymentframe.model.payment.response.PaymentCreateResponseDTO
import uz.nodir.paymentframe.redis.entity.Payment
import uz.nodir.paymentframe.service.business.persistent.PaymentService
import uz.nodir.paymentframe.service.core.dao.PaymentDAO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import uz.repay.common.model.enums.ResponseStatus
import uz.repay.common.model.enums.ResultState
import java.util.*


@Service
class PaymentServiceImpl(
    private val paymentDAO: PaymentDAO
) : PaymentService {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override fun createSession(requestDTO: PaymentCreateRequestDTO): ResultData<PaymentCreateResponseDTO> {
        log.info("Received command to create payment session with request dto: $requestDTO")

        return Payment.from(requestDTO)
            .let { payment ->
                paymentDAO.save(payment)
                ResultData(PaymentCreateResponseDTO("/pay-frame/api/v1/internal/widget/payment/${payment.id}"))
            }.also {
                log.info("Finished command to create payment session with response dto: $it")
            }
    }


    override fun findByToken(token: UUID): ResultData<PaymentBaseResponseDTO> {
        log.info("Received command to find payment by token: $token")

        val result = paymentDAO.findById(token)?.let {
            val responseDTO = PaymentBaseResponseDTO.from(it)
            ResultData(responseDTO).also {
                log.info("Finished command to find payment by token with response dto: $it")
            }
        } ?: run {
            val message = "Payment session with token: $token not found!"
            log.error("Method finished. Cause: $message")
            ResultData(ResultState.EntityNotFound, message)
        }

        return result
    }

    override fun markAsPaid(token: UUID): ResultData<CoreError> {
        log.info("Received command to mark payment session by token: $token")

        return paymentDAO.findById(token)?.let { payment ->
            payment.paid = true
            paymentDAO.save(payment)

            ResultData(CoreError(0, ResponseStatus.SUCCESS.name))
                .also {
                    log.info("Finished command to mark payment session with response dto: $it")
                }
        } ?: run {
            val message = "Payment session with token: $token not found!"
            log.error("Method finished. Cause: $message")
            ResultData(ResultState.EntityNotFound, message)
        }
    }

}