package nodir.paymo_add.controller

import nodir.paymo_add.service.NciService
import nodir.paymo_add.service.RestService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/nci")
class NciController(
    private val nciService: NciService,
    private val restService: RestService
) {

    @PreAuthorize("hasRole('PAYMO_NCI_VIEW')")
    @GetMapping
    fun findTransactionsFromNci(@RequestParam("loan") loan: String): ResponseEntity<Any> {
        return ResponseEntity.ok(nciService.takeTransactionAccount(loan))
    }

    @PreAuthorize("hasRole('PAYMO_NCI_VIEW')")
    @GetMapping("/bank")
    fun bankDay(): ResponseEntity<Any> {
        return ResponseEntity.ok(restService.bankDay())
    }


}