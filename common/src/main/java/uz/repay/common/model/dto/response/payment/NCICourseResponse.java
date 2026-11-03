package uz.repay.common.model.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NCICourseResponse {
    private String name;
    private String abbr;
    private String currency;
    private Long centralbank;
    private Long purchase;
    private Long sell;
    private Long atm_purchase;
    private Long atm_sell;
    private Long mobile_purchase;
    private Long mobile_sell;
}
