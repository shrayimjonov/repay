package uz.nodir.processing.utils

import uz.nodir.processing.model.entity.Card
import uz.repay.common.exception.InvalidDataException
import uz.repay.common.model.dto.response.core.SimpleResponse
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Processing
import uz.repay.common.model.enums.ResultState
import uz.repay.common.utils.CardUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId

object ServiceUtils {

    @JvmStatic
    fun isExpiredCard(expireDate: String?): Boolean {
        expireDate?.let {
            if (expireDate == "1249") return false

            val dateformat: DateFormat = SimpleDateFormat("yyMM")
            try {
                val expiryDate = dateformat.parse(expireDate)
                val date = LocalDate.ofInstant(
                    expiryDate.toInstant(), ZoneId.of("Asia/Tashkent")
                )
                    .plusMonths(1)
                return LocalDate.now().isAfter(date)
            } catch (e: ParseException) {
                throw InvalidDataException("Can't parse card expire date: %s".formatted(e.message))
            }
        } ?: run {
            return false
        }
    }

    @JvmStatic
    fun improvePhone(phone: String): String {
        var newPhone = phone

        if (phone.startsWith("+"))
            newPhone = phone.replace("\\+".toRegex(), "")

        return newPhone
    }

    /**
     * if add new pc configure this method for handling card balance
     * @param card
     * @return
     */
    @JvmStatic
    fun detectTypeByCard(card: String): SimpleResponse<CardType> {
        return if (card.matches(CardUtils.regexUzCardToken().toRegex()) || card.matches(
                CardUtils.regexDavrMasterCardToken().toRegex()
            )
            || card.matches(CardUtils.regexHuMoToken().toRegex())
        ) SimpleResponse(CardType.token)
        else if (card.matches(CardUtils.regexMasterCardDAVR().toRegex()) || card.matches(
                CardUtils.regexUzCard().toRegex()
            )
            || card.matches(CardUtils.regexHumo().toRegex()) || card.matches(CardUtils.regexVisaDAVR().toRegex())
        ) SimpleResponse(CardType.pan)
        else SimpleResponse(
            ResultState.DataNotFound.code,
            "Card type not detected for card: %s".formatted(card)
        )
    }

    /**
     * if add new pc configure this method for handling card balance
     * @param card
     * @return
     */
    @JvmStatic
    fun takeDebitAccount(card: Card): SimpleResponse<String?> {
        return when (card.processing) {
            Processing.UZCARD, Processing.SVGATE_AUTOPAY, Processing.DAVR_MASTER_CARD -> {
                SimpleResponse(card.token)
            }

            Processing.HUMO, Processing.DAVR_VISA -> {
                SimpleResponse(CardUtils.decryptPan(card.pan))
            }

            else -> {
                SimpleResponse(
                    ResultState.DataNotFound.code,
                    "Take debit account method can't detect debit account for card id: ${card.id}"
                )
            }
        }
    }
}
