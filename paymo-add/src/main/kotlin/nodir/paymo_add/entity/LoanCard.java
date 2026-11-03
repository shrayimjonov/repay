package nodir.paymo_add.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "loan_card")
public class LoanCard {
    @Id
    private Long id;
    private String external_id;
    private String loan_id;
    private String card_id;
    private String uzcard_contract_id;
    private String card_pan;
    private String card_holder;
    private String card_phone;
    private Boolean sms;
    private String uzcard_client_id;

}
