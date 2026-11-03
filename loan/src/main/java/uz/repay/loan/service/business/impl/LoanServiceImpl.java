package uz.repay.loan.service.business.impl;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.nodir.repayautoconfigure.utils.SecurityUtils;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.request.loan.params.LoanSearchParams;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.*;
import uz.repay.common.model.enums.Currency;
import uz.repay.common.service.JpaSpecificationBuilder;
import uz.repay.common.utils.DateUtils;
import uz.repay.common.utils.PageUtils;
import uz.repay.loan.model.dto.core.guarantor.response.GuarantorShortResult;
import uz.repay.loan.model.dto.core.loan.request.*;
import uz.repay.loan.model.dto.core.loan.response.LoanABSTranResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSearchResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;
import uz.repay.loan.model.entity.Loan;
import uz.repay.loan.model.entity.meta.Loan_;
import uz.repay.loan.service.business.LoanService;
import uz.repay.loan.service.business.component.LogProtocolComponent;
import uz.repay.loan.service.core.FeaturedLoansDAO;
import uz.repay.loan.service.core.LoanDAO;
import uz.repay.loan.service.web.NCIBillingService;
import uz.repay.loan.service.web.NCIGatewayClientService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl extends JpaSpecificationBuilder<Loan> implements LoanService {
    private final LoanDAO loanDAO;
    private final NCIGatewayClientService nciGatewayClientService;
    private final NCIBillingService nciBillingService;
    private final FeaturedLoansDAO featuredLoansDAO;
    private final LogProtocolComponent logProtocolComponent;

    @Override
    public ResultData<LoanSimpleResponseDTO> create(@NonNull LoanSimpleRequestDTO requestDTO) {

        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        return createLoan(requestDTO);
    }

    public ResultData<LoanSimpleResponseDTO> createLoan(@NonNull LoanSimpleRequestDTO requestDTO) {
        var loanResponse = nciGatewayClientService.getLoanInfoByLoanId(requestDTO.getLoanExtId());

        if (loanResponse.getError() != null)
            return new ResultData<>(loanResponse.getError(), false);

        if (!loanResponse.getData().getIsSuccess())
            return new ResultData<>(loanResponse.getData().getError(), false);


        var loanOptional = loanDAO.findByExtIdV2(requestDTO.getLoanExtId());

        LoanInfoResponse loanInfoResult = loanResponse.getData().getResult();

        if (loanOptional.isPresent()) {
            Loan loan = loanOptional.get();

            if (loan.getIsDeleted()) {
                var message = "Loan %s is deleted".formatted(requestDTO.getLoanExtId());
                log.error(message);
                return new ResultData<>(ResultState.BlockOperation, message);
            }

            if (loan.getCurrency() == null) {
                loan.setCurrency(Currency.getCurrencyByCode(Integer.valueOf(loanInfoResult.getCurrency())));
                loanDAO.save(loan);
            }

            var message = "Loan %s already has in database".formatted(requestDTO.getLoanExtId());
            log.error(message);
            return new ResultData<>(ResultState.InvalidRequest, message);
        }

        var clientResponse = nciGatewayClientService.getClientInfoByLoanId(requestDTO.getLoanExtId());

        if (clientResponse.getError() != null)
            return new ResultData<>(clientResponse.getError(), false);

        if (clientResponse.getData().getError() != null)
            return new ResultData<>(clientResponse.getData().getError(), false);

        if (clientResponse.getData().getPinfl() == null || clientResponse.getData().getPinfl().isEmpty())
            return new ResultData<>(ResultState.DataNotFound, "Pinfl can not be null");


        var preparedEntity = new Loan(
                requestDTO.getLoanExtId(), clientResponse.getData(),
                Language.RU,
                loanInfoResult
        );

        var loan = loanDAO.save(preparedEntity);

        return new ResultData<>(new LoanSimpleResponseDTO(loan, false));
    }

    @Override
    public ResultData<List<LoanABSTranResponseDTO>> getLoanABSTransactions(@NonNull LoanABSTranRequestDTO requestDTO) {
        log.info("Get loan: {} transaction from ABS", requestDTO.getLoanExtId());

        var response = nciGatewayClientService.getLoanTransactions(
                requestDTO.getLoanExtId(),
                requestDTO.getDateFrom());

        if (response.getData() == null)
            return new ResultData<>(response.getError(), false);

        return new ResultData<>(response
                .getData()
                .stream()
                .map(LoanABSTranResponseDTO::new)
                .toList());
    }

    @Override
    public ResultData<LoanSearchResponseDTO> findLoansByFilter(@NonNull LoanSearchRequestDTO requestDTO) {
        log.info("Find Loan with filter params: {}", requestDTO);

        var pageable = PageUtils.pageableBuilder(requestDTO);
        var specificationResult = buildSpecification(requestDTO.getParams());

        if (!specificationResult.getStatus()) {
            return new ResultData<>(new LoanSearchResponseDTO(
                    List.of()
            ));
        }


        var loans = loanDAO.findAll(specificationResult.getData(), pageable);

        return new ResultData<>(new LoanSearchResponseDTO(loans));
    }

    @Override
    public ResultData<CoreError> changeLoanData(@NonNull LoanChangeDataRequestDTO requestDTO) {
        log.info("Received command to change loan data with request: {}", requestDTO);
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        var loan = loanDAO.findByExtId(requestDTO.getLoanExtId());

        log.info("Loan: {} Change phone from: {} to: {} ", loan.getExtId(), loan.getPayerPhone(), requestDTO.getPhone());

        loan.setPayerPhone(requestDTO.getPhone());
        loan.setLanguage(requestDTO.getLanguage());

        loanDAO.save(loan);


        var result = new CoreError(0, ResponseStatus.SUCCESS.name());
        log.info("Finished command to change loan data with response: {}", result);
        return new ResultData<>(result);
    }

    private static HashMap<Object, Object> putOldAndNewValuesToMap(Object oldValue, Object newValue) {
        var params = new HashMap<>();
        params.put("oldValue", oldValue);
        params.put("newValue", newValue);
        return params;
    }

    private SimpleResponse<Specification<Loan>> buildSpecification(LoanSearchParams params) {

        var username = SecurityUtils.getUsername();

        return specificationCreator(params, username);
    }

    @Deprecated
    private SimpleResponse<Specification<Loan>> buildSpecificationForInternal(LoanReportRequestDTO requestDTO) {

        var username = "requestDTO.getUsername()";

        return specificationCreator(requestDTO.getParams(), username);
    }

    private SimpleResponse<Specification<Loan>> specificationCreator(LoanSearchParams params, String username) {
        Specification<Loan> specification = Specification.where(null);


        if (params != null) {
            specification = specification.and(createSpecByBoolEqualCondition(Loan_.IS_DELETED, false));
            log.debug("Search only isDeleted = false loans!");

            if (params.getName() != null && !params.getName().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Loan_.FULL_NAME, params.getName().toUpperCase()));

            if (params.getPhone() != null && !params.getPhone().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Loan_.PHONE, params.getPhone()));

            if (params.getExtId() != null && !params.getExtId().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Loan_.LOAN_ID, params.getExtId()));

            if (params.getPersonCode() != null && !params.getPersonCode().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Loan_.PINFL, params.getPersonCode()));

            if (params.getIsConvicted() != null)
                specification = specification.and(createSpecByBoolEqualCondition(Loan_.IS_CONVICTED, params.getIsConvicted()));

            if (params.getClientId() != null && !params.getClientId().isEmpty())
                specification = specification.and(createSpecByLikeCondition(Loan_.CLIENT_ID, params.getClientId()));

            if (SecurityUtils.checkUserToReal(username).getStatus()) {
                var branches = SecurityUtils.getUserBranches();
                if (params.getBranches() != null && !params.getBranches().isEmpty()) {
                    var userBranches = new ArrayList<Branch>();
                    params.getBranches()
                            .forEach(
                                    u -> {
                                        if (branches
                                                .stream()
                                                .anyMatch(s -> s.equals(u.getCode()))) {
                                            userBranches.add(u);
                                        }
                                    }
                            );
                    log.info("User branches: {}", userBranches);
                    specification = specification.and(createSpecByBranchInCondition(Loan_.BRANCH, userBranches));
                } else {
                    var userBranches = new ArrayList<Branch>();
                    branches
                            .forEach(
                                    b -> Arrays.stream(Branch.values())
                                            .forEach(s -> {
                                                if (b.equals(s.getCode()))
                                                    userBranches.add(s);
                                            })
                            );

                    specification = specification.and(createSpecByBranchInCondition(Loan_.BRANCH, userBranches));
                }
            }

            if (params.getRangeParams() != null)
                if (params.getRangeParams().getStartDate() != null && !params.getRangeParams().getStartDate().isEmpty()
                        && params.getRangeParams().getEndDate() != null && !params.getRangeParams().getEndDate().isEmpty()) {
                    DateUtils.validateDateFields(params.getRangeParams().getStartDate(), params.getRangeParams().getEndDate());
                    specification = specification.and(createSpecByDateBetweenCondition(Loan_.CREATED_DATE, params.getRangeParams().getStartDate(), params.getRangeParams().getEndDate()));
                }

            us:
            if (params.getIsFeatured() != null && params.getIsFeatured()) {
                var userRealResult = SecurityUtils.checkUserToReal(username);
                log.info("Get featured loans for user: {}", username);

                if (!userRealResult.getStatus()) {
                    log.warn("User real result is failed and will be exit");
                    break us;
                }


                var userFLs = featuredLoansDAO.findUserFeaturedLoans(username);


                if (userFLs.isEmpty()) {
                    log.warn("User featured loans is empty and will be exit");
                    return new SimpleResponse<>(404, "Featured Loans is empty!");
                    // break us;
                }

                var flLoans = userFLs.stream().map(z -> z.getLoan().getExtId()).toList();

                log.info("Featured loans size: {}", flLoans.size());

                specification = specification.and(createSpecByStatusInCondition(Loan_.LOAN_ID, flLoans));
            }


            ownLoans:
            if (params.getIsOwnLoans() != null && params.getIsOwnLoans()) {
                log.info("Take user: {} own loans...", username);

                var checkUserResult = SecurityUtils.checkUserToReal(username);

                if (!checkUserResult.getStatus()) {
                    log.warn("User real result is failed and will be exited");
                    break ownLoans;
                }

                specification = specification.and(createSpecByStringEqualCondition(Loan_.CREATED_BY, username));
            }

            if (params.getIsInsolvent() != null && params.getIsInsolvent())
                specification = specification.and(createSpecByBoolEqualCondition(Loan_.IS_INSOLVENT, params.getIsInsolvent()));


        }

        return new SimpleResponse<>(specification);
    }


    @Override
    public ResultData<LoanSimpleResponseDTO> findByExtId(@NonNull LoanSimpleRequestDTO requestDTO) {
        log.info("Received command to find loan by ext id with request dto: {}", requestDTO);
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        var loan = loanDAO.findByExtId(requestDTO.getLoanExtId());

        LoanSimpleResponseDTO loanSimpleResponseDTO = new LoanSimpleResponseDTO(loan);

        if (loanSimpleResponseDTO.getGuarantors() != null && !loanSimpleResponseDTO.getGuarantors().isEmpty())
            loanSimpleResponseDTO
                    .getGuarantors()
                    .sort(Comparator.comparing(GuarantorShortResult::getId));

        ResultData<LoanSimpleResponseDTO> resultData = new ResultData<>(loanSimpleResponseDTO);

        log.info("Finished command to find loan by ext id with response dto: {}", resultData);
        return resultData;
    }


    @Override
    public ResultData<LoanInfoResponse> getLoanBalance(@NonNull LoanSimpleRequestDTO requestDTO) {
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());
        return getLoanBalanceInfo(requestDTO);
    }

    @Override
    public ResultData<CoreError> convict(@NonNull LoanSimpleRequestDTO requestDTO) {
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        log.info("Convict loan: {}", requestDTO.getLoanExtId());

        var loan = loanDAO.findByExtId(requestDTO.getLoanExtId());

        loan.setIsConvicted(!loan.getIsConvicted());

        loanDAO.save(loan);

        return new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));
    }


    @Override
    public ResultData<CoreError> deleteLoanByExt(@NonNull LoanSimpleRequestDTO requestDTO) {
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        log.info("Delete loan: {}", requestDTO.getLoanExtId());

        var loan = loanDAO.findByExtId(requestDTO.getLoanExtId());
        loan.setIsDeleted(true);
        loanDAO.save(loan);

        var params = putOldAndNewValuesToMap(loan.getIsDeleted(), true);

        return new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));
    }

    @Override
    public ResultData<List<LoanSimpleResponseDTO>> findAll(@NotNull LoanReportRequestDTO requestDTO) {
        var specificationResult = buildSpecification(requestDTO.getParams());

        if (!specificationResult.getStatus())
            return new ResultData<>(List.of());

        return new ResultData<>(
                loanDAO
                        .findAll(specificationResult.getData())
                        .stream()
                        .map(z -> new LoanSimpleResponseDTO(z, false))
                        .toList()
        );
    }

    @Override
    public ResultData<CoreError> evictCache() {
        loanDAO.cacheEvict();
        return new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));
    }

    @Override
    public ResultData<ProblemResponse> repay(LoanRepayRequestDTO requestDTO) {
        log.info("Loan repayment request: {}", requestDTO);

        logProtocolComponent.saveLog(
                requestDTO.getLoanExternal(),
                ProtocolType.REPAY,
                requestDTO.toMap()
        );
        return nciBillingService.loanRepay(requestDTO.getLoanExternal(), requestDTO.getAmount());
    }

    @Override
    public ResultData<CoreError> autoDebitTurn(@NonNull LoanSimpleRequestDTO requestDTO) {
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        log.info("Auto debit turn for loan: {}", requestDTO.getLoanExtId());

        var loan = loanDAO.findByExtId(requestDTO.getLoanExtId());

        log.info("Loan autoDebit status: {} will be changed to: {}", loan.getIsAutoDebit(), !loan.getIsAutoDebit());

        var params = putOldAndNewValuesToMap(loan.getIsConvicted(), !loan.getIsConvicted());

        loan.setIsAutoDebit(!loan.getIsAutoDebit());

        loanDAO.save(loan);

        return new ResultData<>(new CoreError(0, ResponseStatus.SUCCESS.name()));
    }

    @Override
    public ResultData<LoanInfoResponse> getLoanBalanceForInternal(@NonNull LoanSimpleRequestDTO requestDTO) {
        return getLoanBalanceInfo(requestDTO);
    }

    private ResultData<LoanInfoResponse> getLoanBalanceInfo(LoanSimpleRequestDTO requestDTO) {
        var loanResponse = nciGatewayClientService.getLoanInfoByLoanId(requestDTO.getLoanExtId());

        if (!loanResponse.getStatus())
            return new ResultData<>(loanResponse.getError(), false);

        return loanResponse.getData();
    }


    @Override
    public ResultData<List<String>> takeLoanFeaturedCreators(@NonNull String extId) {
        log.info("Received command to get loan: {} for get featured loans users", extId);

        var loan = loanDAO.findByExtId(extId);

        var result = ResultData.ok(
                loan.getFeaturedLoans()
                        .stream()
                        .map(BaseEntity::getCreatedBy)
                        .toList()
        );
        log.info("Finished command to get loan with response size: {}", result);
        return result;
    }

    @Override
    public ResultData<LoanInfoV3Response> getLoanBalanceInfoV3(LoanSimpleRequestDTO requestDTO) {
        var loanResponse = nciGatewayClientService.getLoanInfoV3(requestDTO.getLoanExtId());

        if (!loanResponse.getStatus())
            return new ResultData<>(loanResponse.getError(), false);

        return loanResponse.getData();
    }

    @Override
    public ResultData<LoanAdditionalResponseDTO> takeLoanAdditionalInfo(LoanSimpleRequestDTO requestDTO) {
        log.info("Received command to take loan additional info with request: {}", requestDTO);
        var loanOptional = loanDAO.getOptionalByExtId(requestDTO.getLoanExtId());

        if (loanOptional.isEmpty()) {
            var message = "Loan: %s not found in database. Maybe deleted or not created!".formatted(requestDTO.getLoanExtId());
            log.error("Method finished with error: {}", message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var loan = loanOptional.get();

        var loanResponse = nciGatewayClientService.getLoanInfoV3(requestDTO.getLoanExtId());

        if (!loanResponse.getStatus()) {
            log.error("Method finished with error: {}", loanResponse.getError());
            return new ResultData<>(loanResponse.getError(), false);
        }

        if (loanResponse.getData().getError() != null) {
            log.error("Method finished with error: {}", loanResponse.getData().getError());
            return new ResultData<>(loanResponse.getData().getError(), false);
        }


        var loanResult = loanResponse.getData().getResult();

        var response = ResultData.ok(
                new LoanAdditionalResponseDTO(
                        loan.getId(),
                        loan.getExtId(),
                        loan.getBranch().getCode(),
                        Long.valueOf(loanResult.getTransit()),
                        loanResult.getTransitState(),
                        loanResult.getTransitAccount(),
                        loanResult.getRecommendedAmount(),
                        loan.getPayerName(),
                        loan.getIsInsolvent(),
                        loan.getLanguage(),
                        loan.getCurrency()
                )
        );

        log.info("Finished command to take loan additional info with response: {}", response);
        return response;
    }

    @Override
    public ResultData<List<LoanShortV2ResponseDTO>> takeOnlyInsolventLoans() {
        log.info("Received command to take insolvent loans...");

        var loanList = loanDAO.findAllIsInsolvents();

        if (loanList.isEmpty()) {
            log.info("Finished command to take insolvent loans with 0 size");
            return ResultData.ok(
                    List.of()
            );
        }

        var resultList = loanList
                .stream()
                .map(z -> new LoanShortV2ResponseDTO(z.getId(), z.getExtId(), z.getPayerName(), z.getBranch()))
                .toList();

        var response = ResultData.ok(
                resultList
        );

        log.info("Finished command to take insolvent loans with {} size", response.getResult().size());
        return response;
    }

    @Override
    public ResultData<CoreError> insolvent(@NonNull LoanSimpleRequestDTO requestDTO) {
        log.info("Received command to insolvent loan with request: {}", requestDTO);
        SecurityUtils.checkUserRequestedBranch(requestDTO.getLoanExtId());

        var loanOptional = loanDAO.getOptionalByExtId(requestDTO.getLoanExtId());

        if (loanOptional.isEmpty()) {
            var message = "Loan with extId: %s not found!".formatted(requestDTO.getLoanExtId());
            log.error("Method finished with error: {}", message);
            return new ResultData<>(ResultState.EntityNotFound, message);
        }

        var loan = loanOptional.get();
        loan.setIsInsolvent(!loan.getIsInsolvent());
        loanDAO.save(loan);

        logProtocolComponent.saveLog(
                requestDTO.getLoanExtId(),
                ProtocolType.INSOLVENT,
                Map.of(
                        "loanExtId", requestDTO.getLoanExtId(),
                        "insolvent", loan.getIsInsolvent())
        );

        var response = new CoreError(
                0, ResponseStatus.SUCCESS.name()
        );

        log.info("Finished command to insolvent loan with response: {}", response);
        return new ResultData<>(response);
    }


}
