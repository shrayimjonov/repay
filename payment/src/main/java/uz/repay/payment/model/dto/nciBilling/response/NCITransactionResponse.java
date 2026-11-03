package uz.repay.payment.model.dto.nciBilling.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NCITransactionResponse {
    private String id;
    private String state;
    private String branch;
    private String currency;
    private String kod_ERR;
    private String doc_NUM;
    private String d_DATE;
    private String bank_CL;
    private String acc_CL;
    private String name_CL;
    private String bank_CO;
    private String acc_CO;
    private String name_CO;
    private String purpose;
    private String summa;
    private String type_DOC;
    private String s_DEAL_ID;
    private String v_DATE;
    private String pdc;
    private String parent_GROUP_ID;
    private String parent_ID;
    private String child_ID;
    private String emp_ID;
    private String id_TRANSH;
    private String id_TRANSH_PURP;
    private String batch_TYPE_ID;
}
