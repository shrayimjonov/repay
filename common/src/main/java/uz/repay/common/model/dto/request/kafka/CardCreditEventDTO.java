package uz.repay.common.model.dto.request.kafka;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardCreditEventDTO {

    @JsonProperty("card")
    @JsonAlias(value = {"card", "CARD", "Card"})
    private String card;

    @JsonProperty("amount")
    @JsonAlias(value = {"amount", "AMOUNT", "Amount"})
    private Long amount;

    @JsonProperty("rrn")
    @JsonAlias(value = {"rrn", "RRN", "Rrn"})
    private String rrn;

    public String toJson() {
        return """
                {
                    "card": "%s",
                    "amount": %d,
                    "rrn": "%s"
                }"""
                .formatted(this.card, this.amount, this.rrn);
    }
}
