package uz.repay.card.service.business;

import uz.repay.card.model.dto.core.request.card.OTPRequestDTO;
import uz.repay.card.model.dto.core.request.card.VerifyRequestDTO;
import uz.repay.card.model.dto.core.response.card.OTPResponseDTO;
import uz.repay.card.model.dto.core.response.card.VerifyResponseDTO;
import uz.repay.card.model.dto.core.response.card.result.CardConfirmResult;
import uz.repay.common.model.dto.response.core.ResultData;

public interface VerifyCardService {

    ResultData<OTPResponseDTO> sendOTP(OTPRequestDTO requestDTO);

    ResultData<VerifyResponseDTO> verify(VerifyRequestDTO requestDTO);

    CardConfirmResult addUzCardToDb(VerifyRequestDTO requestDTO);

    CardConfirmResult addHuMoCardToDB(VerifyRequestDTO requestDTO);

    CardConfirmResult addVisaToDB(VerifyRequestDTO requestDTO);

    CardConfirmResult addMasterToDb(VerifyRequestDTO requestDTO, Boolean isOneTimeCard);

}
