package uz.nodir.processing.endpoint

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uz.nodir.processing.client.HuMoMiddlewareWebClient
import uz.nodir.processing.client.MasterGateWebClient
import uz.nodir.processing.client.SVGateWebClient
import uz.nodir.processing.client.VisaMiddlewareWebClient
import uz.nodir.processing.service.business.main.LoanCardService

@RestController
@RequestMapping("/api/v1/card")
class TestEndpoint(
    private val huMoMiddlewareWebClient: HuMoMiddlewareWebClient,
    private val svGateWebClient: SVGateWebClient,
    private val visaMiddlewareWebClient: VisaMiddlewareWebClient,
    private val masterGateWebClient: MasterGateWebClient,
    private val loanCardService: LoanCardService
) {

    @GetMapping("/info")
    fun testCardInfo(@RequestParam pan: String) = huMoMiddlewareWebClient.cardInfo(pan)

    @GetMapping("/card")
    fun testCard(@RequestParam panList: List<String>) = svGateWebClient.balanceGet(panList)

    @GetMapping("/cardsGet")
    fun testCard2(@RequestParam panList: List<String>) = svGateWebClient.cardsGet(panList)


    @GetMapping("/cardsInfo")
    fun testCard3(@RequestParam panList: List<String>) = visaMiddlewareWebClient.cardsInfo(panList)


    @GetMapping("/getBalance")
    fun testCard4(@RequestParam token: String) = masterGateWebClient.balance(token)

    @GetMapping("/cardBalance")
    fun test5(@RequestParam id: Long) = loanCardService.getLoanCards(id)
}