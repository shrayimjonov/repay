package nodir.paymo_add.controller

import nodir.paymo_add.exception.ParamNotFound
import nodir.paymo_add.feign.FeignService
import nodir.paymo_add.feign.Request.HumoReturnFinished
import nodir.paymo_add.feign.Request.HumoReverse
import nodir.paymo_add.feign.Request.SvgateReverse
import nodir.paymo_add.model.ErrorDto
import nodir.paymo_add.service.ReverseService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.ServletRequest

@RestController
@RequestMapping("/reverse")
class ReverseController(
    private val reverseService: ReverseService,
    private val feignService: FeignService,
    @Value("\${svgate.terminals}")
    private val svgateTerminals: String,
    @Value("\${genesis.terminals}")
    private val genesisTerminals: String
) {

    @PreAuthorize("hasRole('PAYMO_REVERSE')")
    @PatchMapping("{id}")
    fun reverseByExternalId(@PathVariable("id") id: Long, servlet: ServletRequest): ResponseEntity<Any> {
        reverseService.reverseTransaction(id, servlet)
        val errorDto = ErrorDto()
        errorDto.message = "#$id transaction successfully updated"
        return ResponseEntity.ok(errorDto)
    }


    @PreAuthorize("hasRole('PAYMO_VIEW')")
    @GetMapping("/{terminal_id}/{external_id}")
    fun getPaymentStatus(
        @PathVariable("terminal_id") terminal_id: String,
        @PathVariable("external_id") external_id: String,
        servlet: ServletRequest
    ): ResponseEntity<Any> {

        return if (terminal_id.startsWith("25")) {
            ResponseEntity.ok(feignService.humowareGetPaymentByExt(external_id))
        } else {
            if (terminal_id.matches(genesisTerminals.toRegex())) {
                ResponseEntity.ok(
                    feignService.genesisConnect(
                        SvgateReverse(
                            id = "123", jsonrpc = "2.0",
                            method = "autopay.trans.ext", params = SvgateReverse.Params(extId = external_id)
                        )
                    )
                )
            } else if (terminal_id.matches(svgateTerminals.toRegex())) {
                ResponseEntity.ok(
                    feignService.svgateConnect(
                        SvgateReverse(
                            id = "123", jsonrpc = "2.0",
                            method = "trans.ext", params = SvgateReverse.Params(extId = external_id)
                        )
                    )
                )
            } else {
                throw ParamNotFound("Can't check request. This terminal not Found in code! RC.kt 69 line")
            }

        }
    }

    @PreAuthorize("hasRole('PAYMO_REVERSE')")
    @PatchMapping("/test")
    fun reverseByExternalId(servlet: ServletRequest): ResponseEntity<Any> {
        feignService.humowareReverseFinished(HumoReturnFinished(login = "wsdavr", paymentId = "1567772046",subSystem = "PAYMO", terminalId = "1567772046", merchantId = "01121000011214P"))

        return ResponseEntity.ok("")
    }


}