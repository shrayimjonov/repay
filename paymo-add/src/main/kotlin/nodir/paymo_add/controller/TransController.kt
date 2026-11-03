package nodir.paymo_add.controller

import nodir.paymo_add.feign.FeignService
import nodir.paymo_add.service.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transaction")
class TransController(
    private val transactionService: TransactionService,
    private val feignService: FeignService
) {

    @PreAuthorize("hasRole('PAYMO_VIEW')")
    @GetMapping
    fun takeAllTransactions(@RequestParam("loan") loan: String): ResponseEntity<Any> {
        return ResponseEntity.ok().body(transactionService.takeAllTransactions(loan))
    }

    @PreAuthorize("hasRole('PAYMO_VIEW')")
    @GetMapping("/branch")
    fun allBranchesList(): ResponseEntity<Any> {
        val map981: Map<String, String> = mapOf("code" to "00981", "branch" to "Xadra")
        val map1046: Map<String, String> = mapOf("code" to "01046", "branch" to "Chilonzor")
        val map1069: Map<String, String> = mapOf("code" to "01069", "branch" to "Yakkasaroy")
        val map1072: Map<String, String> = mapOf("code" to "01072", "branch" to "Mirzo-Ulugbek")
        val map1086: Map<String, String> = mapOf("code" to "01086", "branch" to "Uchtepa")
        val map1121: Map<String, String> = mapOf("code" to "01121", "branch" to "Olmazor")
        val map1122: Map<String, String> = mapOf("code" to "01122", "branch" to "Yunusobod")
        val branchesList: MutableList<Any> = mutableListOf(map981, map1046, map1069, map1072, map1086, map1121, map1122)
        return ResponseEntity.ok(branchesList)
    }

    @PreAuthorize("hasRole('PAYMO_HUMO_HISTORY')")
    @GetMapping("/history/{external_id}")
    fun getHumoHistory(@PathVariable("external_id") external_id: String): ResponseEntity<Any> {
        return ResponseEntity.ok(feignService.findPaymentHistory(external_id))
    }

    @PreAuthorize("hasRole('PAYMO_PAY_HISTORY')")
    @GetMapping("/payHistory/{loanId}")
    fun getPayHistory(@PathVariable("loanId") loan_id: String): ResponseEntity<Any>{
        return ResponseEntity.ok(transactionService.findPayHistory(loan_id))
    }

}