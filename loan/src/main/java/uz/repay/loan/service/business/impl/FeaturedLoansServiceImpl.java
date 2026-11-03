package uz.repay.loan.service.business.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;
import uz.repay.loan.model.entity.FeaturedLoans;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.embedded.LoanFeaturePK;
import uz.repay.loan.service.business.FeaturedLoansService;
import uz.repay.loan.service.core.FeaturedLoansDAO;
import uz.repay.loan.service.core.LoanDAO;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeaturedLoansServiceImpl implements FeaturedLoansService {
    private final FeaturedLoansDAO dao;
    private final LoanDAO loanDAO;

    @Override
    public ResultData<CoreError> create(@NotNull String loanExtID) {
        log.info("Received command to create/delete featured loan for loan: {}", loanExtID);

        var loan = loanDAO.getOptionalByExtId(loanExtID)
                .orElseThrow(()-> new EntityNotFoundException("Loan with extId: %s not found!".formatted(loanExtID)));


        var username = SecurityUtils.getUsername();

        var checkResult = SecurityUtils.checkUserToReal(username);
        if (!checkResult.getStatus())
            return new ResultData<>(checkResult.getError().getCode(), checkResult.getError().getMessage());

        var userFLOptional = dao.findByLoanAndUser(loan, username);

        if (userFLOptional.isEmpty()) {
            createFeatureLoan(loan);
        } else {
            log.info("Delete featured loan: {}", loanExtID);
            deleteFeatureLoan(userFLOptional.get());
        }


        var result = ResultData.ok(new CoreError(0, ResponseStatus.SUCCESS.name()));

        log.info("Finished command to create/delete featured loan with response: {}", result);
        return result;
    }


    private void deleteFeatureLoan(FeaturedLoans fl) {
        dao.deleteById(fl.getId());
    }

    private void createFeatureLoan(Loan loan) {
        var pk = new LoanFeaturePK(
                loan.getId(),
                UUID.randomUUID()
        );

        var fl = new FeaturedLoans(
                pk,
                loan
        );

        dao.create(fl);
    }

    @Override
    public ResultData<List<LoanSimpleResponseDTO>> findUserFeaturedLoans() {
        var username = SecurityUtils.getUsername();
        log.info("Received command to get featured loans for user: {}", username);

        return findFeaturedLoansByUser(username);
    }

    @Override
    public ResultData<List<LoanSimpleResponseDTO>> findUserFeaturedLoansForInternal(@NonNull String username) {
        log.info("Received command to get featured loans for user: {}", username);

        return findFeaturedLoansByUser(username);
    }

    private ResultData<List<LoanSimpleResponseDTO>> findFeaturedLoansByUser(String username) {


        var checkResult = SecurityUtils.checkUserToReal(username);
        if (!checkResult.getStatus())
            return new ResultData<>(checkResult.getError().getCode(), checkResult.getError().getMessage());


        var userFLs = dao.findUserFeaturedLoans(username);

        var result = ResultData.ok(userFLs
                .stream()
                .map(z -> new LoanSimpleResponseDTO(z.getLoan(), true))
                .toList());

        log.info("Finished command to get featured loans with size: {}", result.getResult().size());
        return result;
    }

}
