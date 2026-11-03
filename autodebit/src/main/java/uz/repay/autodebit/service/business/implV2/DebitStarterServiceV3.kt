//package uz.repay.autodebit.service.business.implV2
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import lombok.extern.slf4j.Slf4j
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO
//import uz.repay.autodebit.model.core.response.AutoPayResultV2
//import uz.repay.autodebit.model.payment.response.PayResponseDTO
//import uz.repay.autodebit.service.core.LoanResultService
//import uz.repay.autodebit.service.web.CardFeignService
//import uz.repay.autodebit.service.web.LoanFeignService
//import uz.repay.autodebit.service.web.PaymentFeignService
//import uz.repay.autodebit.utils.ErrorUtils
//import uz.repay.common.model.dto.request.payment.PayRequestDTO
//import uz.repay.common.model.dto.request.payment.params.NotificationParams
//import uz.repay.common.model.dto.response.core.ResultData
//import uz.repay.common.model.dto.response.core.SimpleResponse
//import uz.repay.common.model.dto.response.core.result.LoanCardsResult
//import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO
//import uz.repay.common.model.enums.AddType
//import uz.repay.common.model.enums.Language
//import uz.repay.common.model.enums.Processing
//import uz.repay.common.model.enums.ResultState
//import uz.repay.common.utils.CardUtils
//import uz.repay.common.utils.DateUtils
//import java.util.*
//import java.util.List;
//
//
///**
// *
// * Класс переписан на Kotlin с использованием корутин.
// * Добавлена логика метода loopCardsToDebit, который вызывается из основного цикла.
// */
//@Slf4j
//class DebitStarterServiceV3(
//    private val branch: String,
//    private val minimumDebitAmount: Long,
//    private var isActive: Boolean,
//    private val debtorsList: List<String>,
//    private val cardFeignService: CardFeignService,
//    private val loanFeignService: LoanFeignService,
//    private val paymentFeignService: PaymentFeignService,
//    private val workerId: UUID,
//    private val loanResultService: LoanResultService,
//    private val result: AutoPayResultV2,
//    private val isInsolvent: Boolean
//) {
//    companion object {
//        val log: Logger = LoggerFactory.getLogger(this::class.java)
//    }
//
//    /**
//     * Основной suspend‑метод, аналогичный run() в исходной реализации.
//     * В нем используется метка mainLoop для возможности выхода из цикла при аварийном завершении.
//     */
//    suspend fun runSuspend() = withContext(Dispatchers.IO) {
//        val uniqueIDForThread = UUID.randomUUID()
//        val startTime = DateUtils.getDateTimeNow()
//        log.info(
//            "Begin worker with session: {} for branch: {} with debtors size: {} started at: {}",
//            uniqueIDForThread, branch, debtorsList.size, startTime
//        )
//        log.info("Debtors list for branch {}: {}", branch, debtorsList)
//
//        mainLoop@ for (loan in debtorsList) {
//            if (!isActive) break
//
//            log.info("Begin auto payment for loan: {}", loan)
//
//            val loanCardsResponse = cardFeignService.getLoansCardsV2(
//                LoanCardsRequestDTO(listOf(loan))
//            )
//            if (loanCardsResponse.error != null) {
//                log.error("Loan card response {} – skipping loan {}", loanCardsResponse, loan)
//                handleError(result, loan, ResultState.WrongParams, loanCardsResponse.error.message)
//                continue
//            }
//
//            val loanCardsOptional = loanCardsResponse.result.firstOrNull { it.loanExtId == loan }
//            if (loanCardsOptional == null || loanCardsOptional.cards.isEmpty()) {
//                log.error("Loan: {} cards is empty", loan)
//                handleError(result, loan, ResultState.WrongParams, "Loan cards is empty")
//                continue
//            }
//            val loanCards = loanCardsOptional
//
//            // Удаляем карты, для которых отсутствуют параметры дебетования
//            loanCards.cards.removeIf { it.debitParams == null }
//            // Сортируем карты по приоритету плательщика (сначала карты заемщика)
//            loanCards.cards.sortBy { it.payer.priority }
//
//            val rate = visaCardsSeparateAndSetBalance(loanCards)
//            val totalAmountInCards = calculateTotalAmount(loanCards)
//            val loanResponse = loanFeignService.getLoanAdditionalInfo(loan)
//            if (loanResponse.error != null) {
//                log.error("Loan response has error: {}", loanResponse.error.message)
//                handleError(result, loan, ResultState.WrongParams, loanResponse.error.message)
//                continue
//            }
//            val loanResultData = loanResponse.result
//            var recommendedAmount = loanResultData.recommendedAmount
//
//            if (!isInsolvent) {
//                val transitBalance = loanResultData.transitBalance
//                if (transitBalance > 0) {
//                    log.info("Transit balance {} will be subtracted from recommended amount", transitBalance)
//                    recommendedAmount -= transitBalance
//                }
//                if (recommendedAmount <= 0) {
//                    log.warn("Payment exited because recommended amount is: {}", recommendedAmount)
//                    handleError(result, loan, ResultState.NotProcessed, "Payment exited: recommended amount is 0")
//                    removeDebtLoanWithoutDebt(loan)
//                    continue
//                }
//                if (totalAmountInCards < minimumDebitAmount && recommendedAmount > minimumDebitAmount) {
//                    val cardsBalanceToLog = StringBuilder().apply {
//                        loanCards.cards.forEach { card ->
//                            append("[${card.pan} ${card.amount}] ")
//                        }
//                    }
//                    val comment =
//                        "Debit amount: $totalAmountInCards less than minimum: $minimumDebitAmount. Details: $cardsBalanceToLog"
//                    log.warn(comment)
//                    handleError(result, loan, ResultState.NotProcessed, comment)
//                    continue
//                }
//            } else {
//                recommendedAmount = totalAmountInCards
//                if (recommendedAmount == 0L) {
//                    handleError(
//                        result,
//                        loan,
//                        ResultState.NotProcessed,
//                        "All cards amount is 0. Insolvent auto payment finished!"
//                    )
//                    continue
//                }
//            }
//
//            // Вызов метода loopCardsToDebit. Если он возвращает true,
//            // устанавливаем флаг emergencyExit и выходим из основного цикла.
//            if (loopCardsToDebit(loanResultData, recommendedAmount, loanCards, result, rate)) {
//                result.emergencyExit = true
//                break@mainLoop
//            }
//
//            log.info("Loan: {} payment process finished", loan)
//        }
//        log.info(
//            "End worker with session: {} for branch: {} finished at: {}",
//            uniqueIDForThread, branch, DateUtils.getDateTimeNow()
//        )
//    }
//
//    /**
//     * Метод, перебирающий карты и выполняющий операцию дебетования.
//     * Возвращает true, если обнаружена ситуация, при которой необходимо аварийно остановить авто-платеж.
//     */
//    private suspend fun loopCardsToDebit(
//        loanInfo: LoanAdditionalResponseDTO,
//        amount: Long,
//        loanCards: uz.repay.common.model.dto.response.core.LoansCardsResponseDTO<LoanCardsResult>,
//        result: AutoPayResultV2,
//        rate: Long?
//    ): Boolean = withContext(Dispatchers.IO) {
//        var recAmount = amount
//        val loanExtId = loanInfo.loanExtId
//
//        for (card in loanCards.cards) {
//            log.info("Recommended amount: {}", recAmount)
//            if (recAmount <= 0) break
//
//            val paidAmount: Long = when {
//                card.amount >= recAmount -> recAmount
//                card.amount != 0L && card.amount > minimumDebitAmount -> card.amount
//                else -> {
//                    log.error("Card: {} amount: {} and will not be debited", card.pan, card.amount)
//                    handleError(
//                        result, loanExtId, ResultState.NotProcessed,
//                        "Card: ${card.pan} amount: ${card.amount} and will not be debited"
//                    )
//                    continue
//                }
//            }
//
//            if (!CardUtils.isSumPC(card.processing) && (rate == null || rate == 0L)) {
//                val message = "Rate is null for debiting from Card: ${card.pan}"
//                log.error(message)
//                handleError(result, loanExtId, ResultState.NotProcessed, message)
//                continue
//            }
//
//            val originalSumAmount = paidAmount
//            var finalPaidAmount = paidAmount
//            if (!CardUtils.isSumPC(card.processing)) {
//                val newAmount = paidAmount.toDouble() / rate!!.toDouble() * 100.0
//                finalPaidAmount = newAmount.toLong()
//            }
//            log.info("Paid amount: {} from card: {}", finalPaidAmount, card.pan)
//            val request = payRequestBuilder(loanInfo, card, finalPaidAmount)
//            val payResponse = paymentFeignService.payV2(request)
//            log.info("Payment v2 response: {}", payResponse)
//            if (payResponse.error != null) {
//                checkCodeToBlockCard(card, payResponse)
//                handleError(
//                    result, loanExtId, ResultState.NotProcessed,
//                    "${payResponse.error.message} from card: ${card.pan}"
//                )
//                if (payResponse.error.code == ResultState.BlockOperation.code) {
//                    log.info("Stop autoPayment because payment is turned off")
//                    return@withContext true
//                }
//                continue
//            }
//            result.successList[loanExtId] = payResponse
//            recAmount -= originalSumAmount
//        }
//        return@withContext false
//    }
//
//    /**
//     * Собирает запрос на оплату.
//     */
//    private fun payRequestBuilder(
//        loanInfo: LoanAdditionalResponseDTO,
//        card: LoanCardsResult,
//        paidAmount: Long
//    ): PayRequestDTO {
//        val loanExtId: String = loanInfo.loanExtId
//        val loanDebtor: String = loanInfo.debtor
//        val loanId: Long = loanInfo.id
//        val phone: String = card.phone
//        val language: Language = loanInfo.language
//
//        val notificationParams = NotificationParams(
//            phone,
//            loanDebtor,
//            loanId,
//            language
//        )
//
//
//        val request = PayRequestDTO(
//            loanExtId,
//            paidAmount,
//            card.debitParams,
//            PayRequestDTO.PayParams(
//                card.pan,
//                card.holder,
//                card.payer
//            ),
//            AddType.AUTO,
//            notificationParams
//        )
//        log.info(
//            """
//            {
//                "loanExtId" : "$loanExtId",
//                "amount" : $paidAmount,
//                "debitParams" : {
//                    "type" : "${card.debitParams.type}",
//                    "card" : "${card.debitParams.card}",
//                    "processing" : "${card.debitParams.processing}"
//                },
//                "params" : {
//                    "cardPan" : "${card.pan}",
//                    "cardOwner" : "${card.holder}"
//                },
//                "addType" : "AUTO",
//                "notificationParams" : {
//                    "phone" : "$phone",
//                    "fullName" : "$loanDebtor",
//                    "loanId" : $loanId
//                }
//            }
//            """.trimIndent()
//        )
//        return request
//    }
//
//    /**
//     * Если необходимо, проверяет код ошибки и, при совпадении, деактивирует карту.
//     */
//    private suspend fun checkCodeToBlockCard(
//        card: LoanCardsResult,
//        payResponse: ResultData<PayResponseDTO>
//    ) = withContext(Dispatchers.IO) {
//        val signalCode = ErrorUtils.statusIsCardBlocked(card.processing, payResponse.error?.code)
//        if (signalCode) {
//            val response = cardFeignService.deactivateCard(card.id)
//            log.info("Deactivate card response: {}", response)
//        }
//    }
//
//    private suspend fun visaCardsSeparateAndSetBalance(
//        loanCards: uz.repay.common.model.dto.response.core.LoansCardsResponseDTO<LoanCardsResult>
//    ): Long? = withContext(Dispatchers.IO) {
//        if (loanCards.cards.any { it.processing == Processing.DAVR_VISA }) {
//            val response = handleVisaBalance(loanCards)
//            if (response.status) return@withContext response.data
//        }
//        return@withContext null
//    }
//
//    private suspend fun handleVisaBalance(
//        loanCards: uz.repay.common.model.dto.response.core.LoansCardsResponseDTO<LoanCardsResult>
//    ): SimpleResponse<Long> = withContext(Dispatchers.IO) {
//        val bankCourseResponse = paymentFeignService.getCourse()
//        if (!bankCourseResponse.isSuccess || bankCourseResponse.result.none { it.abbr == "USD" }) {
//            loanCards.cards.removeIf { it.processing == Processing.DAVR_VISA }
//            return@withContext SimpleResponse(-1, "Course response not fetched")
//        }
//        val rate = bankCourseResponse.result.first { it.abbr == "USD" }.purchase
//        if (rate == null || rate == 0L) {
//            log.error("Purchase rate is null")
//            loanCards.cards.removeIf { it.processing == Processing.DAVR_VISA }
//            return@withContext SimpleResponse(-2, "Rate is null")
//        }
//        log.info("Taken rate: {}", rate)
//        loanCards.cards.filter { it.processing == Processing.DAVR_VISA }
//            .forEach { it.amount = rate * it.amount / 100 }
//        return@withContext SimpleResponse(rate)
//    }
//
//    private suspend fun removeDebtLoanWithoutDebt(loanExtId: String) = withContext(Dispatchers.IO) {
//        log.info("Loan: {} recommended amount is 0 and will be removed from debt loans", loanExtId)
//        loanFeignService.removeDebtLoanByLoanExtId(loanExtId)
//    }
//
//    private suspend fun handleError(
//        result: AutoPayResultV2,
//        loanExtId: String,
//        resultState: ResultState,
//        errorMessage: String
//    ) = withContext(Dispatchers.IO) {
//        result.failedList[loanExtId] = ResultData(resultState, errorMessage)
//        loanResultService.save(loanExtId, errorMessage, result.sessionID, workerId)
//    }
//
//    private suspend fun calculateTotalAmount(
//        loanCards: uz.repay.common.model.dto.response.core.LoansCardsResponseDTO<LoanCardsResult>
//    ): Long = withContext(Dispatchers.IO) {
//        val totalAmount = loanCards.cards.sumOf { if (it.amount >= 0) it.amount else 0 }
//        log.info("In {} cards total sum: {}", loanCards.cards.size, totalAmount)
//        totalAmount
//    }
//}
