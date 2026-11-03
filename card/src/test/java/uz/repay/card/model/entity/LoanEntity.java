package uz.repay.card.model.entity;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Language;


@Slf4j
public class LoanEntity {

    public static final Loan loan = new Loan();

    static {
        loan.setId(85600L);
        loan.setBranch(Branch._01121);
        loan.setPinfl("30000000000000");
        loan.setTin("609800230");
        loan.setBirthday("1996-08-29 00:00:00");
        loan.setExtId("01121158076");
        loan.setPayerName("ESHMATOV TOSHMAT TESHABOYEVICH");
        loan.setSurname("ESHMATOV");
        loan.setFirstName("TOSHMAT");
        loan.setPatronymic("TESHABOYEVICH");
        loan.setIsDeleted(false);
        loan.setPassportSerial("AD");
        loan.setPassportNumber("1311158");
        loan.setPayerPhone("998901234567");
        loan.setIdNibbd("60272385");
        loan.setCodeGender("1");
        loan.setBirthPlace("TOSHKENT");
        loan.setPassportDateRegistration("2022-04-29 00:00:00");
        loan.setPassportDateExpiration("2032-04-28 00:00:00");
        loan.setIsConvicted(false);
        //  loan.setLoanCards();
        loan.setLanguage(Language.EN);
        //  loan.setGuarantors();

        log.info("Prepared fake loan data: {}", loan);
    }
}
