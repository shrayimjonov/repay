//package uz.repay.autodebit.service.business.implV3
//
//import kotlinx.coroutines.*
//import lombok.extern.slf4j.Slf4j
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//import uz.repay.autodebit.model.core.request.AutoPaymentSwitcherDTO
//import uz.repay.autodebit.model.core.response.AutoPayResultV2
//import uz.repay.autodebit.model.property.WorkerProperty
//import uz.repay.autodebit.service.business.AutoPaymentServiceV3
//import uz.repay.autodebit.service.business.implV2.DebitStarterServiceV3
//import uz.repay.autodebit.service.web.CardFeignService
//import uz.repay.autodebit.service.web.LoanFeignService
//import uz.repay.autodebit.service.web.PaymentFeignService
//import uz.repay.autodebit.service.core.LoanResultService
//import uz.repay.autodebit.utils.DebtorsUtils
//import uz.repay.common.model.enums.Branch
//import java.util.*
//import java.util.concurrent.CopyOnWriteArrayList
//
///**
// */
//@Service
//@Slf4j
//class AutoPaymentServiceV3Impl @Autowired constructor(
//    private val workerProperty: WorkerProperty,
//    private val loanFeignService: LoanFeignService,
//    private val workingBranches: CopyOnWriteArrayList<Branch>,
//    private val cardFeignService: CardFeignService,
//    private val paymentFeignService: PaymentFeignService,
//    private val loanResultService: LoanResultService
//) : AutoPaymentServiceV3 {
//
//    companion object {
//        @Volatile
//        var isActive: Boolean = false
//        private val log: Logger = LoggerFactory.getLogger(this::class.java)
//    }
//
//    /**
//     * Основной метод старта авто-платежей, разбивающий список должников по филиалам и порциям.
//     */
//    private suspend fun startAutoPayment() {
//        log.info("Starting autoPayment service v3...")
//
//        if (!isActive) {
//            log.error("Auto payment is disabled. Please turn on firstly!")
//            return
//        }
//
//        // Получаем список должников
//        val debtLoansResponse = withContext(Dispatchers.IO) { loanFeignService.getAllDebtLoans2() }
//        if (!debtLoansResponse.isSuccess) {
//            log.error("Debtors list fetching has error: ${debtLoansResponse.error.message}")
//            return
//        }
//        val debtors = debtLoansResponse.result
//        log.info("Total debtors count: ${debtors.size}")
//
//        // Разбиваем список по филиалам
//        val separatedByBranchList = DebtorsUtils.separateListV3ForEachBranch(debtors)
//        log.info("Sub Lists by branch count: ${separatedByBranchList.size}")
//
//        // Для каждого филиала запускаем корутину (если для данного филиала ещё не запущен воркер)
//        coroutineScope {
//            separatedByBranchList.forEach { (branchObj, branchDebtors) ->
//                async {
//                    val branch = branchObj.code
//                    // Проверяем, что для данного филиала ещё не запущен процесс
//                    if (workingBranches.any { it.code == branch }) {
//                        log.error("Please wait. There is already a worker in process for branch: $branch")
//                        return@async
//                    }
//                    log.info("Worker not found and will be created for: $branch")
//                    workingBranches.add(branchObj)
//
//                    // Разбиваем список должников для данного филиала на подпорции
//                    val subLists = DebtorsUtils.separateList(branchDebtors, workerProperty.subListSize)
//                    log.info("Separated list inside branch count: ${subLists.size}")
//
//                    // Для каждой подпорции запускаем процесс дебетования через корутину
//                    subLists.forEach { subList ->
//                        async {
//                            val autoPayResult = AutoPayResultV2(branch)
//                            log.info("Start session: {} for branch: {}", autoPayResult.sessionID, branch)
//                            // Каждый воркер получает свой уникальный id
//                            val workerId: UUID = UUID.randomUUID()
//                            val debitService = DebitStarterServiceV3(
//                                branch = branch,
//                                minimumDebitAmount = workerProperty.minimumDebitAmount,
//                                isActive = isActive,
//                                debtorsList = subList,
//                                cardFeignService = cardFeignService,
//                                loanFeignService = loanFeignService,
//                                paymentFeignService = paymentFeignService,
//                                workerId = workerId,
//                                loanResultService = loanResultService,
//                                result = autoPayResult,
//                                isInsolvent = false
//                            )
//                            // Запуск дебетования в suspend‑функции
//                            debitService.runSuspend()
//                        }.join() // ждём завершения данной подпорции
//                        log.info("Finished processing one sub list for branch: {}", branch)
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Переключатель работы авто-платежей.
//     */
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun switchAP(dto: AutoPaymentSwitcherDTO) {
//        when {
//            dto.enable && !isActive -> {
//                log.info("Auto Debit v2 starting...")
//                isActive = true
//                GlobalScope.launch {
//                    startAutoPayment()
//                }
//            }
//            dto.enable && isActive -> log.info("Auto payment already working")
//            !dto.enable && !isActive -> log.info("Auto payment already stopped")
//            !dto.enable && isActive -> {
//                log.info("Auto payment stopping")
//                // Здесь можно добавить логику остановки, например, установив isActive = false
//            }
//            else -> log.info("Unexpected error please contact with coder :)")
//        }
//    }
//}
