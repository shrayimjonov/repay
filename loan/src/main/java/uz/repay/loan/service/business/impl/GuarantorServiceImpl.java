package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ProtocolType;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.model.enums.Sex;
import uz.repay.common.utils.CommonUtils;
import uz.repay.common.utils.DateUtils;
import uz.repay.loan.model.dto.core.guarantor.PassportData;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorAddRequestDTO;
import uz.repay.loan.model.dto.core.guarantor.request.GuarantorToggleRequestDTO;
import uz.repay.loan.model.entity.Guarantor;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.LoanGuarantor;
import uz.repay.loan.model.entity.pk.LoanGuarantorKey;
import uz.repay.loan.service.business.GuarantorService;
import uz.repay.loan.service.business.component.LogProtocolComponent;
import uz.repay.loan.service.core.GuarantorDAO;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.core.LoanGuarantorDAO;
import uz.repay.loan.service.web.ClientService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static uz.repay.common.model.enums.GCPDocument.IDMS_RECV_CITIZ_DOCUMENTS;
import static uz.repay.common.model.enums.GCPDocument.IDMS_RECV_MVD_IDCARD_CITIZEN;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuarantorServiceImpl implements GuarantorService {

    private final LoanDAO loanDAO;
    private final GuarantorDAO guarantorDAO;
    private final LoanGuarantorDAO loanGuarantorDAO;
    private final LogProtocolComponent logProtocolComponent;
    private final ClientService clientService;

    @Override
    public ResultData<CoreError> addGuarantorToLoan(@NotNull GuarantorAddRequestDTO requestDTO) {
        log.info("Received command to add guarantor to loan with request dto: {}", requestDTO);
        String personCode = requestDTO.getPersonCode();

        var fieldsValidationResult = fieldsValidation(requestDTO);

        if (!fieldsValidationResult.getStatus())
            return new ResultData<>(fieldsValidationResult.getError(), false);


        var birthDate = fieldsValidationResult.getData();

        var baseValidationResult = baseValidation(requestDTO);

        if (!baseValidationResult.getStatus())
            return new ResultData<>(baseValidationResult.getError(), false);

        var loan = baseValidationResult.getData();

        if (loan.getPinfl().equals(requestDTO.getPersonCode())) {
            var message = "Loan: %s and guarantor person code is the same!".formatted(loan.getExtId());
            return new ResultData<>(ResultState.BlockOperation, message);
        }

        var gcpValidationResult = gcpValidation(requestDTO, birthDate);

        if (!gcpValidationResult.getStatus()) {
            log.error(gcpValidationResult.getError().getMessage());
            return new ResultData<>(gcpValidationResult.getError(), false);
        }

        var guarantorEntity = gcpValidationResult.getData();

        var guarantorOptional = guarantorDAO.findByPersonCode(personCode);

        if (guarantorOptional.isEmpty()) {
            var guarantor = guarantorDAO.save(guarantorEntity);

            var lgEntity = new LoanGuarantor(
                    new LoanGuarantorKey(loan.getId(), guarantor.getId()),
                    loan,
                    guarantor
            );
            loanGuarantorDAO.save(lgEntity);
        } else {
            var guarantor = updateGuarantor(guarantorOptional.get(), guarantorEntity);
            guarantorDAO.save(guarantor);

            var lgEntity = new LoanGuarantor(
                    new LoanGuarantorKey(loan.getId(), guarantor.getId()),
                    loan,
                    guarantor
            );
            loanGuarantorDAO.save(lgEntity);

        }

        var resultData = ResultData.ok(
                new CoreError(0,
                        ResponseStatus.SUCCESS.name())
        );

        log.info("Finished command to add guarantor to loan with response dto: {}", resultData);
        return resultData;
    }

    private static Guarantor updateGuarantor(Guarantor guarantor, Guarantor guarantorEntity) {
        guarantor.setPassport(guarantorEntity.getPassport());
        guarantor.setFirstName(guarantorEntity.getFirstName());
        guarantor.setSurname(guarantorEntity.getSurname());
        guarantor.setPatronymic(guarantorEntity.getPatronymic());
        guarantor.setBirthday(guarantorEntity.getBirthday());
        guarantor.setPhone(guarantorEntity.getPhone());
        guarantor.setLanguage(guarantorEntity.getLanguage());
        guarantor.setGender(guarantorEntity.getGender());
        return guarantor;
    }


    private static SimpleResponse<LocalDate> fieldsValidation(GuarantorAddRequestDTO requestDTO) {
        var dateResult = DateUtils.strToLocalDate(requestDTO.getBirthDate(), "dd.MM.yyyy");

        if (!dateResult.getStatus()) {
            log.error(dateResult.getError().getMessage());
            return new SimpleResponse<>(dateResult.getError().getCode(), dateResult.getError().getMessage());
        }


        var phone = CommonUtils.phoneImprover(requestDTO.getPhone());
        requestDTO.setPhone(phone);


        return new SimpleResponse<>(
                dateResult.getData()
        );
    }

    private SimpleResponse<Loan> baseValidation(GuarantorAddRequestDTO requestDTO) {
        String loanExtId = requestDTO.getLoanExtId();
        String personCode = requestDTO.getPersonCode();

        var loanOptional = loanDAO.findByExtIdV2(loanExtId);

        if (loanOptional.isEmpty()) {
            var message = "Loan with external id: %s not found!".formatted(loanExtId);
            log.error(message);
            return new SimpleResponse<>(ResultState.EntityNotFound.getCode(), message);
        }

        var loan = loanOptional.get();


        Optional<LoanGuarantor> guarantorOptional = loan.getGuarantors()
                .stream()
                .filter(z -> z.getGuarantor().getPersonCode().equals(personCode))
                .findFirst();

        if (guarantorOptional.isPresent()) {
            var message = "Guarantor with person code: %s already added to loan: %s".formatted(personCode, loanExtId);
            log.error(message);
            return new SimpleResponse<>(ResultState.NotProcessed.getCode(), message);
        }

        return new SimpleResponse<>(
                loan
        );
    }

    public SimpleResponse<Guarantor> gcpValidation(GuarantorAddRequestDTO requestDTO, LocalDate birthDate) {
        var clientInfoResult = clientService.getClientInfo(requestDTO.getPersonCode(), birthDate);

        if (!clientInfoResult.getStatus())
            return new SimpleResponse<>(ResultState.InternalServerError.getCode(), clientInfoResult.getError().getMessage());

        if (!clientInfoResult.getData().getIsSuccess())
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), clientInfoResult.getData().getError().getMessage());


        var clientInfoResponse = clientInfoResult.getData().getResult();

        if (!clientInfoResponse.getResult().equals("1")) { // 1 -> SUCCESS
            var message = "Client info from GCP is FAILED with response: %s".formatted(clientInfoResponse);
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), message);
        }

        if (clientInfoResponse.getData().isEmpty()) {
            String message = "Client response is empty!";
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), message);
        }

        var client = clientInfoResponse.getData().getFirst();

        String firstname;
        String lastname;
        String patronymic;
        LocalDate birthday;
        String passportSerial;
        String passportNumber;
        LocalDate passportIssueDate;
        LocalDate passportExpiryDate;
        String passportIssuer;
        Sex gender;

        if (client.getSurnameLatin() != null && !client.getSurnameLatin().isEmpty())
            lastname = client.getSurnameLatin();
        else if (client.getSurnameEnglish() != null && !client.getSurnameEnglish().isEmpty())
            lastname = client.getSurnameEnglish();
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Surname is null");


        if (client.getNameLatin() != null && !client.getNameLatin().isEmpty())
            firstname = client.getNameLatin();
        else if (client.getNameEnglish() != null && !client.getNameEnglish().isEmpty())
            firstname = client.getNameEnglish();
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Firstname is null");


        if (client.getPatronymLatin() != null && !client.getPatronymLatin().isEmpty())
            patronymic = client.getPatronymLatin();
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Patronym is null");

        if (client.getBirthDate() != null)
            birthday = client.getBirthDate();
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Birthday is null");


        if (client.getDocuments().isEmpty())
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Document is empty!");

        var documentResult = client.getDocuments()
                .stream()
                .filter(z -> z.getType().equals(IDMS_RECV_CITIZ_DOCUMENTS) || z.getType().equals(IDMS_RECV_MVD_IDCARD_CITIZEN))
                .findFirst();

        if (documentResult.isEmpty())
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Internal passport data is empty!");

        var document = documentResult.get();

        if (document.getPassportData().length() != 9)
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Passport serial and number is less than 9 symbols. Passport data: %s".formatted(document.getPassportData()));

        passportSerial = document.getPassportData().substring(0, 2);
        passportNumber = document.getPassportData().substring(2);

        if (document.getDateBegin() == null || document.getDateEnd() == null)
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Passport begin or end date is null");

        passportIssueDate = document.getDateBegin();
        passportExpiryDate = document.getDateEnd();

        if (document.getDocGivenPlace() != null && !document.getDocGivenPlace().isEmpty())
            passportIssuer = document.getDocGivenPlace();
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Passport issuer is null");


        var preparedPassportData = new PassportData(
                passportSerial,
                passportNumber,
                passportIssueDate,
                passportExpiryDate,
                passportIssuer
        );

        log.info("Prepared passport data: {}", preparedPassportData);

        if (client.getSex() != null)
            gender = client.getSex() == 1 ? Sex.MALE : Sex.FEMALE;
        else
            return new SimpleResponse<>(ResultState.ThirdPartyError.getCode(), "Gender is null");

        var guarantor = new Guarantor(
                firstname,
                lastname,
                patronymic,
                birthday,
                preparedPassportData,
                requestDTO.getPhone(),
                requestDTO.getLanguage(),
                requestDTO.getPersonCode(),
                gender
        );

        log.info("Prepared guarantor: {}", guarantor);

        return new SimpleResponse<>(guarantor);
    }

    @Override
    public ResultData<CoreError> deleteOrApproveGuarantor(@NotNull GuarantorToggleRequestDTO requestDTO) {
        log.info("Received command to toggle loan guarantor with request: {}", requestDTO);
        var lgOptional = loanGuarantorDAO.findByLoanIdAndGuarantorId(requestDTO.getLoanId(), requestDTO.getGuarantorId());

        if (lgOptional.isEmpty()) {
            var message = "Loan with id: %d and guarantor with id: %d not found".formatted(requestDTO.getLoanId(), requestDTO.getGuarantorId());
            log.error(message);
            return new ResultData<>(ResultState.EntityNotFound.getCode(), message);
        }

        var lg = lgOptional.get();

        log.info("Loan guarantor result is: {}", lg);

        lg.setIsDeleted(!lg.getIsDeleted());
        loanGuarantorDAO.save(lg);
        String loanExtId = lg.getLoan().getExtId();

        logProtocolComponent.saveLog(
                loanExtId,
                ProtocolType.LOAN_GUARANTOR_TOGGLE,
                Map.of(
                        "loanExtId", loanExtId,
                        "guarantorId", requestDTO.getGuarantorId(),
                        "toggle", lg.getIsDeleted())
        );

        var result = new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to delete card from loan with response: {}", result);
        return result;
    }


}
