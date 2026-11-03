package uz.nodir.processing.model.dto.adapter.request

import uz.nodir.processing.model.entity.base.BaseCardEntity
import uz.repay.common.model.enums.CardType
import uz.repay.common.model.enums.Payer
import uz.repay.common.model.enums.Processing



data class CardBalanceFetchRequest(
    var cardId: Long? = null,
    var processing: Processing? = null,
    var type: CardType? = null,
    var card: String? = null,
    var payer: Payer? = null,
    var baseCard: BaseCardEntity? = null

) {
    constructor(
        baseCard: BaseCardEntity,
        cardType: CardType,
        cardAccount: String
    ): this(){
        this.baseCard = baseCard
        baseCard.card?.let { b->
            this.cardId = b.id
            this.processing = b.processing
        }
        this.type = cardType
        this.payer = baseCard.payer
        this.card = cardAccount
    }
}