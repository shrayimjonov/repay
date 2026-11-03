package nodir.paymo_add.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "loan")
public class Loan {
    @Id
    private Long id;
    private String branch_id;
    private String external_id;
    private String recommended;
    private String total;
    private String payer_name;
    private String payer_phone;
    private String is_deleted;
    private String is_confirmed;
    private String date_updated;
    private String date_created;
    private String last_paid;
    private String is_bureau_debt;
}
