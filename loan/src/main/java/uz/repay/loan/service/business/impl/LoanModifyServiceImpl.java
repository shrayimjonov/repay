package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.loan.model.dto.nciGateway.response.NCILoanInfoRepayResponse;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.service.business.LoanModifyService;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.web.NCIGatewayClientService;


@Service
@Slf4j
@RequiredArgsConstructor
public class LoanModifyServiceImpl implements LoanModifyService {
    private final NCIGatewayClientService nciGatewayClientService;
    private final LoanDAO loanDAO;

    @Override
    public ResultData<CoreError> add(@NotNull LoanSimpleRequestDTO requestDTO) {
        log.info("Received command to \"add loan\" with request dto: {}", requestDTO);

        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        return createLoan(requestDTO);
    }

    @Override
    public ResultData<CoreError> addWithoutBranchValidation(@NotNull LoanSimpleRequestDTO requestDTO) {
        log.info("Received command to \"add loan without branch validation in role\" with request dto : {}", requestDTO);

        return createLoan(requestDTO);
    }


    private ResultData<CoreError> createLoan(LoanSimpleRequestDTO requestDTO) {
        var loanOptional = loanDAO.findByExtIdV2(requestDTO.getLoanExtId());

        if (loanOptional.isPresent()) {
            var message = "Loan %s already has in database".formatted(requestDTO.getLoanExtId());
            log.error("Finished command. Cause: {}", message);
            return new ResultData<>(ResultState.InvalidRequest, message);
        }

        var loanResponse = nciGatewayClientService.getLoanInfoRepayByLoanExtId(requestDTO.getLoanExtId());

        if (!loanResponse.getStatus()) {
            log.error("Finished command. Cause: {}", loanResponse.getError().getMessage());
            return new ResultData<>(ResultState.InternalServerError, loanResponse.getError().getMessage());
        }

        if (!loanResponse.getData().getIsSuccess()) {
            log.error("Finished command. Cause: {}", loanResponse.getData().getError().getMessage());
            return new ResultData<>(ResultState.ThirdPartyError, loanResponse.getData().getError().getMessage());
        }

        var loanResult = loanResponse.getData().getResult();

        var validateResult = validateLoanInfoFields(loanResult);

        if (!validateResult.getStatus()) {
            log.error("Finished command. Cause: {}", validateResult.getError().getMessage());
            return new ResultData<>(ResultState.NotProcessed, validateResult.getError().getMessage());
        }

        var preparedEntity = new Loan(
                requestDTO.getLoanExtId(),
                loanResult
        );

        loanDAO.save(preparedEntity);

        var response = ResultData.ok(
                new CoreError(
                        0,
                        ResponseStatus.SUCCESS.name()
                )
        );

        log.info("Finished command to add loan with response dto: {}", response);
        return response;
    }

    public SimpleResponse<NCILoanInfoRepayResponse> validateLoanInfoFields(NCILoanInfoRepayResponse response) {

        if (response.getPersonCode() == null || response.getPersonCode().isEmpty())
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "PINFL can not be null");

        if (response.getFirstName() == null || response.getFirstName().isEmpty())
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "First name can not be null");

        if (response.getSurname() == null || response.getSurname().isEmpty())
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "Surname can not be null");

        if (response.getPatronymic() == null || response.getPatronymic().isEmpty())
            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "Patronymic can not be null");

        /*
        YATT clients hasn't ID_NIBBD field
         */
//        if (response.getIdNibbd() == null || response.getIdNibbd().isEmpty())
//            return new SimpleResponse<>(ResultState.DataNotFound.getCode(), "NIBBD id can not be null");

        return new SimpleResponse<>(response);
    }
}
