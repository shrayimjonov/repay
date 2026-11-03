package uz.repay.payment.service.business;

import uz.repay.common.model.dto.request.payment.PayRequestDTO;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.payment.model.dto.cardService.response.TerminalResponse;
import uz.repay.payment.model.dto.core.pay.response.result.DebitResult;
import uz.repay.payment.model.dto.humo.response.HuMoBasePaymentResponse;
import uz.repay.payment.model.entity.Bill;
import uz.repay.payment.model.entity.Transaction;

public interface DebitTransService {
    DebitResult pay(PayRequestDTO requestDTO, String extId, TerminalResponse ePosResponse,Integer method);

    SimpleResponse<HuMoBasePaymentResponse> huMoDebitConfirm(Bill debitBill, Transaction transaction);

}
