package nodir.paymo_add.controller;

import lombok.RequiredArgsConstructor;
import nodir.paymo_add.exception.ParamNotFound;
import nodir.paymo_add.model.ErrorDto;
import nodir.paymo_add.service.LoanCardService;
import nodir.paymo_add.service.LoanService;
import nodir.paymo_add.service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {
    private final LoanService loanService;
    private final LoanCardService loanCardService;
    private final SmsService smsService;

    @PreAuthorize("hasRole('PAYMO_VIEW')")
    @GetMapping
    public ResponseEntity<?> findLoanInfo(@RequestParam("loanId") String loanId) {
        return new ResponseEntity<>(loanService.findLoanInfo(loanId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PAYMO_LOAN_CARD')")
    @GetMapping("/card")
    public ResponseEntity<?> findLoanCardInfo(@RequestParam(value = "loanId", required = false) String loanId,
                                              @RequestParam(value = "card", required = false) String card,
                                              @RequestParam(value = "phone", required = false) String phone
    ) {
        if (loanId != null) {
            return new ResponseEntity<>(loanCardService.findLoanCardByLoanId(loanId), HttpStatus.OK);
        } else if (card != null) {
            return new ResponseEntity<>(loanCardService.findLoanCardByCard(card), HttpStatus.OK);
        } else if (phone != null) {
            return new ResponseEntity<>(loanCardService.findLoanCardByPhone(phone), HttpStatus.OK);
        } else {
            throw new ParamNotFound("/loan/card necessary fields is empty");
        }
    }

    @PreAuthorize("hasRole('PAYMO_LOANCARD_UPDATE')")
    @PatchMapping("/update")
    public ResponseEntity<?> updateLoanUzcardContract(@RequestParam("id") Long id) {
        loanCardService.updateUzCardContract(id);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage("card status changed");
        return new ResponseEntity<>(errorDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PAYMO_SMS')")
    @GetMapping("/sms")
    public ResponseEntity<?> takeAllSms(@RequestParam("phone") String phone) {
        return new ResponseEntity<>(smsService.findAllSmsByPhone(phone), HttpStatus.OK);
    }

}
