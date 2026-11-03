package uz.repay.autodebit.service.web.impl;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.client.CardFeignClient;
import uz.repay.autodebit.model.card.request.HumoCardInfoRequestDTO;
import uz.repay.autodebit.model.card.request.LoanCardsRequestDTO;
import uz.repay.autodebit.model.card.response.HumoCardInfoResponseList;
import uz.repay.autodebit.service.web.CardFeignService;
import uz.repay.common.model.dto.request.card.request.CardFullInfoRequestDTO;
import uz.repay.common.model.dto.request.card.response.CardFullInfoResponseDTO;
import uz.repay.common.model.dto.response.cardService.EPosResponseDTO;
import uz.repay.common.model.dto.response.core.LoansCardsResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.dto.response.core.result.LoanCardsResult;
import uz.repay.common.model.enums.ResultState;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardFeignServiceImpl implements CardFeignService {
    private final CardFeignClient client;

    @Override
    public ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCards(LoanCardsRequestDTO requestDTO) {
        log.info("Get loan cards request to card-service: {}", requestDTO);
        try {
            return client.getLoansCards(requestDTO);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }


    @Override
    public ResultData<List<EPosResponseDTO>> getAllEPosses() {
        log.info("Get ePosses from card service");
        try {
            var response = client.getAllEPosses();
            log.info("Get ePosses response: {}", response);
            return response;
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<Boolean> deactivateCard(Long cardId) {
        log.info("Deactivate card request by id: {}", cardId);
        try {
            return client.deactivateCard(cardId);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<ArrayList<LoansCardsResponseDTO<LoanCardsResult>>> getLoansCardsV2(LoanCardsRequestDTO requestDTO) {
        log.debug("Get loan cards request to card-service: {}", requestDTO);
        try {
            return client.getLoansCardsV2(requestDTO);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    @Override
    public ResultData<HumoCardInfoResponseList> getHumoCardsInfoByLoanExtIds(HumoCardInfoRequestDTO requestDTO) {
        log.info("Get Humo cards info request to card-service: {}", requestDTO);
        try {
            return client.getHumoCardsInfoByLoanExtIds(requestDTO);
        } catch (RetryableException r) {
            return new ResultData<>(ResultState.TimeOutError, "TIME OUT: " + r.getMessage());
        } catch (FeignException f) {
            return new ResultData<>(ResultState.ThirdPartyError, "THIRD PARTY ERROR: " + f.getMessage());
        } catch (Exception e) {
            return new ResultData<>(ResultState.InternalServerError, e.getMessage());
        }
    }

    public SimpleResponse<ResultData<CardFullInfoResponseDTO>> getCardByPan(CardFullInfoRequestDTO requestDTO) {
        log.debug("Get card full info request to CARD SERVICE: {}", requestDTO);
        try {
            var response = client.getCardFullInfoByPan(requestDTO);

            return new SimpleResponse<>(response);
        } catch (RetryableException r) {
            return new SimpleResponse<>(580, r.getMessage());
        } catch (FeignException f) {
            return new SimpleResponse<>(581, f.getMessage());
        } catch (Exception e) {
            return new SimpleResponse<>(582, e.getMessage());
        }
    }

}
