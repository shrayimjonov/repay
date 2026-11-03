package uz.nodir.paymentframe.service.business.persistent

import uz.nodir.paymentframe.model.payment.request.PaymentCreateRequestDTO
import uz.nodir.paymentframe.model.payment.response.PaymentBaseResponseDTO
import uz.nodir.paymentframe.model.payment.response.PaymentCreateResponseDTO
import uz.repay.common.model.dto.response.core.CoreError
import uz.repay.common.model.dto.response.core.ResultData
import java.util.*

interface PaymentService {

    fun createSession(requestDTO: PaymentCreateRequestDTO): ResultData<PaymentCreateResponseDTO>

    fun findByToken(token: UUID): ResultData<PaymentBaseResponseDTO>

    fun markAsPaid(token: UUID):ResultData<CoreError>
}