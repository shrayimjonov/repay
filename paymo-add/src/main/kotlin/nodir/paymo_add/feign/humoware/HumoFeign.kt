package nodir.paymo_add.feign.humoware

import nodir.paymo_add.feign.Request.*
import nodir.paymo_add.feign.humoware.Response.HistoryResp
import nodir.paymo_add.feign.humoware.Response.ReturnActiveResp
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(value = "humoware", url = "\${humo.humoware}")
interface HumoFeign {

    @PostMapping("/payment/cancelById")
    fun returnActivePayment(@RequestBody req: HumoReverse)

    @PostMapping("/payment/returnById")
    fun reverseFinishedPayment(@RequestBody req: HumoReturnFinished)

    @PostMapping("/payment/byId")
    fun getPaymentById(@RequestBody req: HumoReverse): ReturnActiveResp

    @PostMapping("/payment/byExternal")
    fun getPaymentByExternalId(@RequestBody req: HumoExternalReq): ReturnActiveResp

    @PostMapping("/info/story")
    fun getCardHistory(@RequestBody req: HumoHistoryRequestV2): HistoryResp


}