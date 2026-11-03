package nodir.paymo_add.feign

import lombok.RequiredArgsConstructor
import nodir.paymo_add.exception.ConnectionError
import nodir.paymo_add.exception.TransactionFinished
import nodir.paymo_add.feign.Genesis.GenesisFeign
import nodir.paymo_add.feign.Request.*
import nodir.paymo_add.feign.Svgate.SvGateFeign
import nodir.paymo_add.feign.humoware.HumoFeign
import nodir.paymo_add.feign.humoware.Response.HistoryResp
import nodir.paymo_add.feign.humoware.Response.ReturnActiveResp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
@RequiredArgsConstructor
class FeignService(
    private val svGateFeign: SvGateFeign,
    private val genesisFeign: GenesisFeign,
    private val humoFeign: HumoFeign
) : FeignImpl {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun <E> svgateConnect(value: E): Any? {
        log.info("svgate Connect request: {}", value)
        try {
            val svgateResponse = svGateFeign.svgateConnect(value)
            log.info("svgate Connect response: {}", svgateResponse)
            return svgateResponse
        } catch (ex: Exception) {
            throw ConnectionError("SVGATE UZCARD", ex.message)
        }
    }

    override fun <E> genesisConnect(value: E): Any? {
        try {
            log.info("genesis request: {}", value)
            val genesisResponse = genesisFeign.genesisConnect(value)
            log.info("genesis response: {}", genesisResponse)
            return genesisResponse;
        } catch (ex: Exception) {
            throw ConnectionError("GENESIS UZCARD", ex.message)
        }
    }

    fun humowareGetPayment(humoReverse: HumoReverse): ReturnActiveResp {
        try {
            log.info("humoware getPayment request: {}", humoReverse)
            val activeResp = humoFeign.getPaymentById(humoReverse)
            log.info("humoware getPayment response: {}", activeResp)
            return activeResp
        } catch (ex: Exception) {
            throw ConnectionError("127.0.0.1/humoware/humoGetPaymentByPaymentId", ex.message)
        }
    }

    fun humowareGetPaymentByExt(paymentRef: String): ReturnActiveResp {
        try {
            val external = HumoExternalReq(refNum = paymentRef, login = "wsdavr", subSystem = "PAYMO")
            log.info("humoware getPayment by ExternalId request: {}", external)
            val activeResp = humoFeign.getPaymentByExternalId(external)
            log.info("humoware getPayment response: {}", activeResp)
            return activeResp
        } catch (ex: Exception) {
            throw ConnectionError("127.0.0.1/humoware/humoGetPaymentByExternalId", ex.message)
        }
    }

    fun humoCardHistory(pan: String, authTime: String): HistoryResp {
        try {
            val request = HumoHistoryRequestV2(
                pan = pan,
                beginDate = takeDays(authTime),
                finishDate = addDays(authTime),
                subSystem = "PAYMO"
            )
            log.info("HumoGateway card history  request: {}", request)
            return humoFeign.getCardHistory(request)
        } catch (ex: Exception) {
            throw ConnectionError("Humo Gateway Card History method error: ", ex.message)
        }
    }

    fun humowareReturnActive(humoReverse: HumoReverse) {
        try {
            log.info("humoware returnActive request: {}", humoReverse)
            humoFeign.returnActivePayment(humoReverse)
        } catch (ex: Exception) {
            throw ConnectionError("127.0.0.1/humoware/humoReturnActivePayment", ex.message)
        }
    }

    fun humowareReverseFinished(humoReverse: HumoReturnFinished) {
        try {
            log.info("humoware reverseFinish request: {}", humoReverse)
            humoFeign.reverseFinishedPayment(humoReverse)
        } catch (ex: Exception) {
            throw ConnectionError("127.0.0.1/humoware/humoReturnFinishedPayment", ex.message)
        }
    }


    fun takeDays(authTime: String): String {
        return LocalDate.parse(
            authTime.substring(0, 4) + "-" + authTime.substring(4, 6) + "-" + authTime.substring(6, 8)
        ).minusDays(7).toString()
    }

    fun addDays(authTime: String): String {
        return LocalDate.parse(
            authTime.substring(0, 4) + "-" + authTime.substring(4, 6) + "-" + authTime.substring(6, 8)
        ).plusDays(7).toString()
    }

    fun findPaymentHistory(externalId: String): Any {
        val payment = humowareGetPaymentByExt(externalId)

        if (payment == null) {
            throw TransactionFinished(externalId, "not taken from card")
        }

        val history = humoCardHistory(payment.pan, payment.auth_time.substring(0, 8))
        if (history.details?.isNotEmpty() == true) {
            val isReversedOrNot: MutableList<Any> = ArrayList()
            history.details!!.onEach {
                if (it.refNumber.equals(payment.auth_ref_number))
                    isReversedOrNot.add(it)

                it.amountNet = separatorSpace(it.amountNet.toString())

                it.cTime = localDateTimeWithoutT(it.cTime.toString())
                it.recDate = localDateTimeWithoutT(it.recDate.toString())
                it.postDate = localDateTimeWithoutT(it.postDate.toString())
                it.tranDateTime = localDateTimeWithoutT(it.tranDateTime.toString())

                if (it.tranType.equals("205")) {
                    it.tranType = "Покупка/Платеж (P2P отправка)"
                } else if (it.tranType.equals("225")) {
                    it.tranType = "Возврат (реверсал) покупки (205)"
                }
            }
            return isReversedOrNot
        }
        return mapOf("message" to "Humo Processing doesn't send any information")
    }

    fun localDateTimeWithoutT(localDateTime: String): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val localDate = LocalDateTime.parse(localDateTime, formatter)
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDate)
    }

    fun separatorSpace(init_amount: String): String? {
        val amount = init_amount.toFloat() / 100
        val formatter = DecimalFormat("#,###.00")
        return formatter.format(amount).replace(",".toRegex(), " ")
    }

}