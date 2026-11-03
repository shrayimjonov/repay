package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResponseStatus;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.GZip.GZipUtil;
import uz.repay.common.utils.HttpServletUtils;
import uz.repay.common.utils.PageUtils;
import uz.repay.loan.model.dto.core.debtLoan.request.DebtLoansRequestDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoanDailyReportDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansPageResponseDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.DebtLoansResponseDTO;
import uz.repay.loan.model.dto.core.debtLoan.response.result.DebtLoansResult;
import uz.repay.loan.model.entity.DebtLoan;
import uz.repay.loan.model.entity.domain.DebtLoanReport;
import uz.repay.loan.model.entity.meta.DebtLoan_;
import uz.repay.loan.mongo.entity.DebtLoanDailyStats;
import uz.repay.loan.service.business.DebtLoanService;
import uz.repay.loan.service.core.DebtLoanDAO;
import uz.repay.loan.service.core.DebtLoanDailyStatsDAO;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtLoanServiceImpl extends JpaSpecificationBuilder<DebtLoan> implements DebtLoanService {
    private final DebtLoanDAO debtLoanDAO;
    private final DebtLoanDailyStatsDAO debtLoanDailyStatsDAO;

    @Override
    public ResultData<DebtLoansResponseDTO> findAll(@NonNull DebtLoansRequestDTO requestDTO) {
        log.info("Get all debt Loan list with filter: {}", requestDTO);
        var specification = buildSpecification(requestDTO);
        var pageable = PageUtils.pageableBuilder(requestDTO);
        var debtLoans = debtLoanDAO.findAll(specification, pageable);
        return new ResultData<>(new DebtLoansResponseDTO(debtLoans));
    }


    private Specification<DebtLoan> buildSpecification(DebtLoansRequestDTO requestDTO) {
        Specification<DebtLoan> specification = Specification.where(null);

        var branches = SecurityUtils.getUserBranches();

        specification = specification.and(createSpecByStatusInCondition(DebtLoan_.BRANCH, branches));

        if (requestDTO != null) {
            if (requestDTO.getParams() != null) {
                var predicatedValues = requestDTO.getParams();

                if (predicatedValues.getName() != null && !predicatedValues.getName().isEmpty())
                    specification = specification.and(createSpecByLikeCondition(DebtLoan_.FULLNAME, predicatedValues.getName().toUpperCase()));

                if (predicatedValues.getPhone() != null && !predicatedValues.getPhone().isEmpty())
                    specification = specification.and(createSpecByLikeCondition(DebtLoan_.PHONE, predicatedValues.getPhone()));

                if (predicatedValues.getLoanExtId() != null && !predicatedValues.getLoanExtId().isEmpty())
                    specification = specification.and(createSpecByLikeCondition(DebtLoan_.LOAN_ID, predicatedValues.getLoanExtId()));


                if (predicatedValues.getPersonCode() != null && !predicatedValues.getPersonCode().isEmpty())
                    specification = specification.and(createSpecByLikeCondition(DebtLoan_.PINFL, predicatedValues.getPersonCode()));

            }
        }

        return specification;
    }

    @Override
    public ResultData<List<DebtLoansResult>> getAll() {
        log.info("Get DebtLoans List -> IP: {}", HttpServletUtils.getRequestIPAddress());
        return new ResultData<>(debtLoanDAO.findAll()
                .stream()
                .map(DebtLoansResult::new)
                .toList());
    }

    @Override
    public ResultData<byte[]> getAllV2() {
        log.info("Get DebtLoans List -> IP: {}", HttpServletUtils.getRequestIPAddress());
        var compressResult = GZipUtil.compress(debtLoanDAO.findAll()
                .stream()
                .map(DebtLoansResult::new)
                .toList());

        if (!compressResult.getStatus())
            return new ResultData<>(compressResult.getError().getCode(), "Ocurred error while compressing data");

        return new ResultData<>(compressResult.getData());
    }

    @Override
    public ResultData<DebtLoansPageResponseDTO> getAllV2Page(int limit, int pageNumber) {
        log.info("Get paged DebtLoans List with limit: {}, pageNumber: {} -> IP: {}",
                limit, pageNumber, HttpServletUtils.getRequestIPAddress());

        var pageable = PageRequest.of(
                pageNumber,
                limit,
                Sort.by(Sort.Direction.DESC, "id")
        );
        var debtLoans = debtLoanDAO.findAll(pageable);
        return new ResultData<>(new DebtLoansPageResponseDTO(debtLoans));
    }

    @Override
    public ResultData<CoreError> evictCache() {
        debtLoanDAO.evictDebtLoanCache();
        return new ResultData<>(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );
    }

    @Override
    public ResultData<Boolean> deleteById(@NotNull Long id) {
        debtLoanDAO.deleteById(id);
        return ResultData.ok(
                true
        );
    }

    @Override
    public ResultData<List<DebtLoanDailyReportDTO>> findDailyReport() {
        log.info("Received command to get debt loan daily report");

        var dailyReport = debtLoanDAO.findDailyReport();

        if (dailyReport == null || dailyReport.isEmpty()) {
            var message = "No debt loan daily report found";
            log.error("Method finished. Cause: {}", message);
            return new ResultData<>(ResultState.DataNotFound, message);
        }


        var result = ResultData.ok(
                dailyReport
                        .stream()
                        .sorted(Comparator.comparing(DebtLoanReport::getBranch))
                        .map(DebtLoanDailyReportDTO::new)
                        .toList()
        );

        log.info("Finished command to get debt loan daily report with size: {}", result.getResult().size());
        return result;
    }

    @Override
    public ResultData<CoreError> deleteByLoanExtId(@NotNull String loanExtId) {
        log.info("Received command to delete debt loan by loanExtId: {}", loanExtId);
        var debtLoanOptional = debtLoanDAO.findByLoanExtId(loanExtId);

        log.info("Debt loan find result is: {}", debtLoanOptional);

        if (debtLoanOptional.isPresent()) {
            var debtLoan = debtLoanOptional.get();
            debtLoanDAO.deleteById(debtLoan.getId());
        }

        var response = ResultData.ok(
                new CoreError(0, ResponseStatus.SUCCESS.name())
        );
        log.info("Finished command to delete debt loan with response: {}", response);

        return response;
    }

    @Override
    public ResultData<List<DebtLoanDailyReportDTO>> findDailyReportByDate(@NotNull LocalDate date) {
        log.info("Received command to get debt loan report by date: {}", date);

        var dailyReport = debtLoanDailyStatsDAO.findStatsByDate(date);

        if (dailyReport == null || dailyReport.isEmpty()) {
            var message = "No debt loan report found";
            log.error("Method finished. Cause: {}", message);
            return new ResultData<>(ResultState.DataNotFound, message);
        }


        var result = ResultData.ok(
                dailyReport
                        .stream()
                        .sorted(Comparator.comparing(DebtLoanDailyStats::getBranch))
                        .map(DebtLoanDailyReportDTO::new)
                        .toList()
        );

        log.info("Finished command to get debt loan report by date with size: {}", result.getResult().size());
        return result;
    }
}
