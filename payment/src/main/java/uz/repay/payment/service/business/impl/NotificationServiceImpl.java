package uz.repay.payment.service.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uz.repay.payment.model.dto.fasto.sms.request.SMSDebitPayRequest;
import uz.repay.payment.service.business.NotificationService;
import uz.repay.payment.service.web.SMSFastoService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SMSFastoService smsFastoService;

    @Override
    @Async
    public void sendMessageAfterDebit(String phone, String fullName,
                                      String loanExtId, Long amount,
                                      String cardPan) {
        log.info("Send sms to: {} for loan: {}", phone, loanExtId);

        if (phone == null || phone.isEmpty()) {
            log.error("Phone is empty");
            return;
        }

        var newPhone = phone
                .replaceAll("-", "")
                .replaceAll("\\+", "")
                .replaceAll(" ", "");

        var request = new SMSDebitPayRequest(
                newPhone,
                fullName,
                amount,
                "ru",
                loanExtId,
                cardPan.substring(10)
        );

        var response = smsFastoService.sendDebitMessage(request);
        log.info("SMS response is: {}", response);
    }
}
