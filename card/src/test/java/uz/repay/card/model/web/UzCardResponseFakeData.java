package uz.repay.card.model.web;

import uz.repay.card.model.data.PanFake;
import uz.repay.card.model.dto.uzcard.response.UzCardBaseResponse;
import uz.repay.card.model.dto.uzcard.response.result.P2PInfoResult;

public class UzCardResponseFakeData {


    public static UzCardBaseResponse<P2PInfoResult> fakeP2PInfoSuccessResponse() {
        return new UzCardBaseResponse<>(
                "2.0",
                System.currentTimeMillis(),
                new P2PInfoResult(
                        PanFake.UZCARD_FAKE_MASKED_PAN_1,
                        "Kimdir kimdirovich",
                        "4703",
                        "private",
                        "0",
                        PanFake.UZCARD_TOKEN_2,
                        0
                )
        );
    }

}
