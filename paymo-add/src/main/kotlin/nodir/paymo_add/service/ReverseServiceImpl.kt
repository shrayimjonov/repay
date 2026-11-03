package nodir.paymo_add.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import nodir.paymo_add.config.Keycloak.Model.TokenRequest
import nodir.paymo_add.config.Keycloak.Service.LoginService
import nodir.paymo_add.exception.ParamNotFound
import nodir.paymo_add.exception.TransactionFinished
import nodir.paymo_add.feign.FeignService
import nodir.paymo_add.feign.Request.HumoReturnFinished
import nodir.paymo_add.feign.Request.HumoReverse
import nodir.paymo_add.feign.Request.SvgateReverse
import nodir.paymo_add.feign.Request.SvgateReverseUtrnno
import nodir.paymo_add.feign.humoware.Response.ReturnActiveResp
import nodir.paymo_add.model.CardRequest
import nodir.paymo_add.model.CardResponse
import nodir.paymo_add.model.CardResponseGenesis
import nodir.paymo_add.model.CardTransResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest


@Service
class ReverseServiceImpl(
    private val feignService: FeignService,
    private val transactionService: TransactionService,
    @Value("\${svgate.login}")
    private val svgateLogin: String,
    @Value("\${genesis.login}")
    private val genesisLogin: String,
    private val loginService: LoginService,
    @Value("\${svgate.terminals}")
    private val svgateTerminals: String,
    @Value("\${genesis.terminals}")
    private val genesisTerminals: String
) :
    ReverseService {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun reverseTransaction(id: Long, servlet: ServletRequest) {

        val transaction = transactionService.findTransactionById(id)
        log.info("transaction: {}", transaction)

        if (transaction.status == "SUCCESS") {
            throw TransactionFinished(id.toString(), transaction.status)
        }

        if (transaction.status == "REVERSE") {
            throw TransactionFinished(id.toString(), transaction.status)
        }


        if (transaction.uzcard_response == null) {

            val terminalId = Gson()
                .fromJson(transaction.uzcard_request, CardRequest::class.java)
                .getParams().getTran().getTerminalId()

            val externalId = Gson()
                .fromJson(transaction.uzcard_request, CardRequest::class.java)
                .getParams().getTran().getExt()



            if (terminalId.startsWith("25")) {
                val findPaymentByExt = feignService.humowareGetPaymentByExt(externalId);

                if (findPaymentByExt.getStatus().equals("finished") || findPaymentByExt.getStatus().equals("active")) {
                    val paymentStatus = feignService.humowareGetPaymentByExt(externalId)
                    reverseOrReturn(paymentStatus, findPaymentByExt.getPaymentID())

                } else {
                    throw TransactionFinished(id.toString(), findPaymentByExt.getStatus())
                }


            } else {

                val mapper = ObjectMapper()
                if (terminalId.matches(genesisTerminals.toRegex())) {

                    val findPaymentByExt: CardResponseGenesis = mapper.convertValue(

                            feignService.genesisConnect(
                                SvgateReverse(
                                    id = "123", jsonrpc = "2.0",
                                    method = "autopay.trans.ext", params = SvgateReverse.Params(extId = externalId)
                                )
                            )
                        ,
                        object : TypeReference<CardResponseGenesis>() {}
                    )!!

                    if (findPaymentByExt.getResult().stream().anyMatch { z -> z.getStatus().equals("ROK") }) {
                        throw TransactionFinished(id.toString(), "ROK")
                    }

                    rokUzcard("GENESIS", externalId)

                } else if (terminalId.matches(svgateTerminals.toRegex())) {
                    val findPaymentByExt: CardTransResponse = mapper.convertValue(
                        feignService.svgateConnect(
                            SvgateReverse(
                                id = "123",
                                jsonrpc = "2.0",
                                method = "trans.ext",
                                params = SvgateReverse.Params(extId = externalId)
                            )
                        ),
                        object : TypeReference<CardTransResponse>() {}
                    )!!


                    if (findPaymentByExt.getResult().stream().anyMatch { z -> z.getStatus().equals("ROK") }) {
                        throw TransactionFinished(id.toString(), "ROK")
                    }

                    rokUzcard("SVGATE", findPaymentByExt.result[findPaymentByExt.getResult().size-1].getRefNum())
                } else {
                    throw ParamNotFound("Can't check request. This terminal not Found in code! RS.kt 124 line")
                }

            }
//
//            throw TransactionFinished(
//                id.toString(),
//                "not taken from card. Rejected by code from reverseService 76 line "
//            )
        } else {

            val uzcardResponse: CardResponse = Gson().fromJson(transaction.uzcard_response, CardResponse::class.java)
            log.info("card response: {}", uzcardResponse)


            if (uzcardResponse.getError() != null) {
                throw TransactionFinished(id.toString(), uzcardResponse.getError().message)
            }


            if (uzcardResponse.getResult().getStatus().equals("ERR")) {
                throw TransactionFinished(
                    id.toString(),
                    "not taken from card. Rejected by code from reverseService 76 line "
                )
            }

            when (uzcardResponse.getResult()?.getUsername()) {
                svgateLogin -> rokUzcard("SVGATE", uzcardResponse.getResult().getRefNum())
                genesisLogin -> rokUzcard("GENESIS", uzcardResponse.getResult().getExt())

                else -> {
                    val payment = feignService.humowareGetPayment(
                        HumoReverse(login = "wsdavr", paymentId = uzcardResponse.getResult().getId(), subSystem = "PAYMO")
                    )
                    if (payment != null) {
                        reverseOrReturn(payment, uzcardResponse.getResult().getId())
                    }
                }
            }

        }



        transactionService.updateTransactionStatusToReverse(id, findUsername(servlet) + " " + servlet.remoteAddr)
    }


    fun reverseOrReturn(payment: ReturnActiveResp, paymentId: String) {

        if (payment.getStatus().equals("finished")) {
            feignService.humowareReverseFinished(
                HumoReturnFinished(login = "wsdavr", paymentId = paymentId,subSystem = "PAYMO", merchantId = payment.merchant_id, terminalId = payment.terminal_id )
            )
        } else if (payment.getStatus().equals("active")) {
            feignService.humowareReturnActive(
                HumoReverse(login = "wsdavr", paymentId = paymentId,subSystem = "PAYMO")
            )
        }
    }

    fun rokUzcard(cardProcessing: String, utrnnoExternal: String) {
        val mapper = ObjectMapper()
        if (cardProcessing == "SVGATE") {

            val cardTransResponse: CardTransResponse = mapper.convertValue(
                feignService.svgateConnect(
                    SvgateReverseUtrnno(
                        id = "123", jsonrpc = "2.0", method = "trans.reverse",
                        params = SvgateReverseUtrnno.Params(tranId = utrnnoExternal)
                    )

                ),
                object : TypeReference<CardTransResponse>() {}
            )!!


            if (cardTransResponse.getError() != null) {
                throw TransactionFinished(cardTransResponse.getError().message, cardTransResponse.getError().code)
            }

        } else {
            val cardTransResponse: CardResponseGenesis = mapper.convertValue(
                feignService.genesisConnect(
                    SvgateReverse(
                        id = "123", jsonrpc = "2.0", method = "autopay.trans.reversal",
                        params = SvgateReverse.Params(extId = utrnnoExternal)
                    )
                ),
                object : TypeReference<CardResponseGenesis>() {}
            )!!

            if (cardTransResponse.getError() != null) {
                throw TransactionFinished(cardTransResponse.getError().message, cardTransResponse.getError().code)
            }
        }
    }

    fun findUsername(servlet: ServletRequest): String {
        val httpServletRequest = servlet as HttpServletRequest
        val tokenRequest = TokenRequest()
        tokenRequest.token = httpServletRequest.getHeader("Authorization")
            .substring(httpServletRequest.getHeader("Authorization").indexOf(" ") + 1)
        return Objects.requireNonNull(loginService.userInfo(tokenRequest).body)?.userName!!
    }


}