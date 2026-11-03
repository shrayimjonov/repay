package uz.repay.report.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MostDebitUsers {

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("count")
    private Integer count;
}
