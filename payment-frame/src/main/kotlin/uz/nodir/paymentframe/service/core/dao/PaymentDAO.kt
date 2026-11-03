package uz.nodir.paymentframe.service.core.dao

import uz.nodir.paymentframe.redis.entity.Payment
import java.util.*

interface PaymentDAO {

    fun save(payment: Payment): Payment

    fun findById(id: UUID): Payment?
}