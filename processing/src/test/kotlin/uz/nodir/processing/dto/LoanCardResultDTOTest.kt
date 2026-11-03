package uz.nodir.processing.dto

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import uz.nodir.processing.model.dto.card.response.LoanCardsResult
import uz.nodir.processing.model.dto.master.response.result.MasterGetBalanceResult
import uz.repay.common.model.dto.response.core.params.DebitCardParams
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Currency
import uz.repay.common.model.enums.Payer
import uz.repay.common.model.enums.Processing
import kotlin.test.assertEquals


class LoanCardResultDTOTest {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val CARD_ID = 290353L

        private val successMasterCard = LoanCardsResult(
            id = CARD_ID,
            pan = "527375******9328",
            holder = "NODIRBEK SIROJIDDINOV",
            phone = "998901234567",
            sms = true,
            amount = 122679,
            state = 0,
            stateDefinition = null,
            payer = Payer.AUTO,
            processing = Processing.DAVR_MASTER_CARD,
            debitParams = DebitCardParams(
                CardType.token,
                "B0CF17CFD2054614AA4AEAB5ECD09C19",
                Processing.DAVR_MASTER_CARD
            ),
            ipAddress = null,
            currency = Currency.UZS,
            isConfirmed = true,
        )
    }

    @Test
    fun testMasterResult() {
        val masterResult = MasterGetBalanceResult(
            "527375******9328",
            "B0CF17CFD2054614AA4AEAB5ECD09C19",
            "repay",
            0,
            122679,
            "998901234567",
            "NODIRBEK SIROJIDDINOV",
            0,
            "60272385",
            "22618000260272385362",
            "860"
        )

        val lcr = LoanCardsResult(CARD_ID, masterResult)
        log.info("Const loan card result is: $successMasterCard")
        log.info("Prepared loan card for test is: $lcr")

        assertEquals(
            lcr, successMasterCard
        )


    }
}