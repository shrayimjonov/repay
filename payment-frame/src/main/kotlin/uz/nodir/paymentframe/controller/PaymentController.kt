package uz.nodir.paymentframe.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import uz.nodir.paymentframe.model.payment.request.PaymentCreateRequestDTO
import uz.nodir.paymentframe.model.payment.response.PaymentCreateResponseDTO
import uz.nodir.paymentframe.service.business.persistent.impl.PaymentServiceImpl
import uz.repay.common.model.dto.response.core.ResultData
import java.util.*



@Controller
@RequestMapping("/api/v1/internal")
class PaymentController(
    private val paymentService: PaymentServiceImpl,
) {

    @GetMapping("/widget/payment/{token}")
    fun view(@PathVariable token: UUID, model: Model, response: HttpServletResponse): String {
        val session = paymentService.findByToken(token)
        response.setHeader("Content-Security-Policy", "frame-ancestors *")
        model.addAttribute("paymentSession", session)
        return "payment"
    }

    @PostMapping("/widget/payment/{token}/pay")
    fun pay(@PathVariable token: UUID, redirectAttributes: RedirectAttributes): String {
        paymentService.markAsPaid(token)
        redirectAttributes.addFlashAttribute("message", "✅ Платёж завершён")
        return "redirect:/pay-frame/api/v1/internal/widget/payment/$token"
    }

    @GetMapping("/new")
    fun showForm(): String {
        return "create-payment"
    }

//    @PostMapping("/new")
//    @ResponseBody
//    fun create(@RequestBody @Valid requestDTO: PaymentCreateRequestDTO) =
//        paymentService.createSession(requestDTO)

    @PostMapping("/new")
    @ResponseBody
    fun create(
        @RequestBody @Valid requestDTO: PaymentCreateRequestDTO,
        model: Model
    ): ResultData<PaymentCreateResponseDTO> {
        return paymentService.createSession(requestDTO)
    }

}