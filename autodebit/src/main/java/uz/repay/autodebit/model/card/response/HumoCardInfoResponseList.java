package uz.repay.autodebit.model.card.response;


import java.util.List;

/**
 * Powered by Shukhratjon Rayimjonov
 * Date: 18.06.2026
 */
public record HumoCardInfoResponseList(
        List<HumoCardInfoByLoanResponse> result
) {
}