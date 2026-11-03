package uz.repay.loan.service.business;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import uz.repay.common.model.dto.request.loan.LoanSimpleRequestDTO;
import uz.repay.common.model.dto.response.core.CoreError;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanAdditionalResponseDTO;
import uz.repay.common.model.dto.response.loan.LoanInfoResponse;
import uz.repay.common.model.dto.response.loan.LoanInfoV3Response;
import uz.repay.common.model.dto.response.loan.LoanShortV2ResponseDTO;
import uz.repay.loan.model.dto.core.loan.request.*;
import uz.repay.loan.model.dto.core.loan.response.LoanABSTranResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSearchResponseDTO;
import uz.repay.loan.model.dto.core.loan.response.LoanSimpleResponseDTO;
import uz.repay.loan.model.dto.nciBilling.response.ProblemResponse;

import java.util.List;

public interface LoanService {
    @Deprecated
    ResultData<LoanSimpleResponseDTO> create(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<LoanSearchResponseDTO> findLoansByFilter(@NonNull LoanSearchRequestDTO requestDTO);

    ResultData<CoreError> changeLoanData(@NonNull LoanChangeDataRequestDTO requestDTO);

    @Deprecated
    ResultData<LoanSimpleResponseDTO> findByExtId(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<LoanInfoResponse> getLoanBalance(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<CoreError> convict(@NonNull LoanSimpleRequestDTO requestDTO);

    @Deprecated
    ResultData<LoanSimpleResponseDTO> createLoan(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<List<LoanABSTranResponseDTO>> getLoanABSTransactions(@NonNull LoanABSTranRequestDTO requestDTO);

    ResultData<CoreError> deleteLoanByExt(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<List<LoanSimpleResponseDTO>> findAll(@NotNull LoanReportRequestDTO requestDTO);

    ResultData<CoreError> evictCache();

    ResultData<ProblemResponse> repay(LoanRepayRequestDTO requestDTO);

    ResultData<CoreError> autoDebitTurn(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<LoanInfoResponse> getLoanBalanceForInternal(@NonNull LoanSimpleRequestDTO requestDTO);

    ResultData<List<String>> takeLoanFeaturedCreators(@NonNull String extId);

    ResultData<LoanInfoV3Response> getLoanBalanceInfoV3(LoanSimpleRequestDTO requestDTO);

    ResultData<LoanAdditionalResponseDTO> takeLoanAdditionalInfo(LoanSimpleRequestDTO requestDTO);

    ResultData<List<LoanShortV2ResponseDTO>> takeOnlyInsolventLoans();

    ResultData<CoreError> insolvent(@NonNull LoanSimpleRequestDTO requestDTO);

}
