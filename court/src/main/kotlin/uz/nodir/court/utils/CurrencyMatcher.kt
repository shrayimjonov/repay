package uz.nodir.court.utils

import uz.nodir.court.model.dto.chambers.response.ChamberClaimCurrencyTypeResponse
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.Currency
import uz.repay.common.model.enums.ResultState

class CurrencyMatcher {

    companion object {
        fun currencyResolver(
            currency: String?,
            currencyResults: List<ChamberClaimCurrencyTypeResponse>
        ): SimpleResponse<Int> {
            val currencyInt = currency?.toIntOrNull()
                ?: return SimpleResponse(ResultState.InvalidRequest.code, "Invalid currency format: $currency")

            val preparedCurrency = Currency.getCurrencyByCode(currencyInt)


            return currencyResults.find {
                it.currencyCode.equals(preparedCurrency.name, ignoreCase = true)
            }?.let {
                SimpleResponse(it.value)
            } ?: SimpleResponse(ResultState.InvalidRequest.code, "Invalid currency: $currency")
        }
    }


}
