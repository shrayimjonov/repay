package uz.repay.payment.utils;

import uz.repay.common.model.dto.request.card.response.StatusResponseDTO;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.StatusType;

import java.util.List;
import java.util.Map;

public class ErrorBuilder {
    public static String getErrorCodeDescription(SimpleResponse<ResultData<List<StatusResponseDTO>>> errorMessageResult, String codeMessage, Processing processing) {

        String errorMessage = codeMessage;

        if (errorMessageResult.getStatus() && errorMessageResult.getData().getError() == null) {

            var resultOptional = errorMessageResult
                    .getData()
                    .getResult()
                    .stream()
                    .filter(z -> z.getProcessing().equals(processing)
                            && z.getCode().equals(codeMessage)
                            && z.getType().equals(StatusType.TRANSACTION))
                    .findFirst();

            if (resultOptional.isPresent())
                errorMessage = codeMessage + " (" + resultOptional.get().getDefinition() +
                        ") " + resultOptional.get().getDefinitionUz();
        }
        return errorMessage;
    }


    public static List<Integer> uzCardNeedConfirmCodes() {
        return List.of(-300, -301, -302, -307, -308, -309); // Transactions to do clarify after 24 hours from PC
    }

    public static Map<Processing, List<Integer>> notFraudCodes() {
        return Map.of(
                Processing.UZCARD, List.of(51),
                Processing.SVGATE_AUTOPAY, List.of(51),
                Processing.HUMO, List.of(116),
                Processing.DAVR_MASTER_CARD, List.of(51)
        );
    }
}
