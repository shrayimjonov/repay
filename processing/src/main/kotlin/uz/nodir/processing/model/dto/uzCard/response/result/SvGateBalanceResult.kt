package uz.nodir.processing.model.dto.uzCard.response.result

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
class BalanceResult(
    @JsonProperty("balanceInfo")
    val balanceInfo: List<BalanceInfoResult> = listOf(),
    @JsonProperty("totalBalance")
    val totalBalance: Long? = 0
)

@JsonIgnoreProperties(ignoreUnknown = true)
class BalanceInfoResult(
    @JsonProperty("id")
    val token: String? = null,

    @JsonProperty("pan")
    val pan: String? = null,

    @JsonProperty("balance")
    val balance: Long? = 0,
)