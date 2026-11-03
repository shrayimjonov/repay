package uz.nodir.processing.model.dto.huMo.response.result

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardResult(
    @JsonProperty("listSize")
    val listSize: Int? = null,

    @JsonProperty("card")
    val card: HuMoCardInfoResult? = null,

    @JsonProperty("account")
    val account: MutableList<HuMoCardAccountResult>? = null,

    @JsonProperty("balance")
    val balance: HuMoCardBalanceResult? = null,

    @JsonProperty("mb")
    val mb: HuMoCardMBResult? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardInfoItemResult(
    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("actionCode")
    val actionCode: String? = null,

    @JsonProperty("actionDescription")
    val actionDescription: String? = null,

    @JsonProperty("effectiveDate")
    val effectiveDate: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardStatusResult(
    @JsonProperty("item")
    val item: MutableList<HuMoCardInfoItemResult>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardInfoResult(
    @JsonProperty("institutionId")
    val institutionId: String? = null,
    @JsonProperty("primaryAccountNumber")
    val primaryAccountNumber: String? = null,
    @JsonProperty("effectiveDate")
    val effectiveDate: String? = null,
    @JsonProperty("updateDate")
    val updateDate: String? = null,
    @JsonProperty("prefixNumber")
    val prefixNumber: Int? = null,
    @JsonProperty("expiry")
    val expiry: String? = null,
    @JsonProperty("cardSequenceNumber")
    val cardSequenceNumber: String? = null,
    @JsonProperty("cardholderId")
    val cardholderId: String? = null,
    @JsonProperty("nameOnCard")
    val nameOnCard: String? = null,
    @JsonProperty("accountRestrictionsFlag")
    val accountRestrictionsFlag: String? = null,
    @JsonProperty("commissionGroup")
    val commissionGroup: String? = null,
    @JsonProperty("cardUserId")
    val cardUserId: String? = null,
    @JsonProperty("additionalInfo")
    val additionalInfo: String? = null,
    @JsonProperty("riskGroup")
    val riskGroup: String? = null,
    @JsonProperty("riskGroup2")
    val riskGroup2: String? = null,
    @JsonProperty("bankC")
    val bankC: String? = null,
    @JsonProperty("pinTryCount")
    val pinTryCount: String? = null,
    @JsonProperty("statuses")
    val statuses: HuMoCardStatusResult? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardAccountResult(
    @JsonProperty("institutionId")
    val institutionId: String? = null,
    @JsonProperty("accountId")
    val accountId: String? = null,
    @JsonProperty("bankAccountId")
    val bankAccountId: String? = null,
    @JsonProperty("currency")
    val currency: String? = null,
    @JsonProperty("cardholderId")
    val cardholderId: String? = null,
    @JsonProperty("effectiveDate")
    val effectiveDate: String? = null,
    @JsonProperty("updateDate")
    val updateDate: String? = null,
    @JsonProperty("accountType")
    val accountType: String? = null,
    @JsonProperty("lockedBackofficeAmount")
    val lockedBackofficeAmount: String? = null,
    @JsonProperty("lockTime")
    val lockTime: String? = null,
    @JsonProperty("shadowAmount")
    val shadowAmount: String? = null,
    @JsonProperty("commissionGroup")
    val commissionGroup: String? = null,
    @JsonProperty("priority")
    val priority: String? = null,
    @JsonProperty("status")
    val status: String? = null,
    @JsonProperty("availableAmount")
    val availableAmount: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardBalanceResult(
    @JsonProperty("currency")
    val currency: Long? = null,
    @JsonProperty("initialAmount")
    val initialAmount: Long? = null,
    @JsonProperty("bonusAmount")
    val bonusAmount: Long? = null,
    @JsonProperty("creditLimit")
    val creditLimit: Long? = null,
    @JsonProperty("lockedBackofficeAmount")
    val lockedBackofficeAmount: Long? = null,
    @JsonProperty("lockedBackofficeAmountOffline")
    val lockedBackofficeAmountOffline: Long? = null,
    @JsonProperty("lockedAmount")
    val lockedAmount: Long? = null,
    @JsonProperty("availableAmount")
    val availableAmount: Long? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
class HuMoCardMBResult(
    @JsonProperty("state")
    val state: String? = null,

    @JsonProperty("phone")
    val phone: String? = null,

    @JsonProperty("message")
    val message: String? = null
)