package uz.nodir.paymentframe.redis.repository

import org.springframework.data.repository.CrudRepository
import uz.nodir.paymentframe.redis.entity.Payment
import java.util.UUID



interface PaymentRepository: CrudRepository<Payment, UUID> {
}