package uz.repay.common.model.dto.request.gateway.gcp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.dto.request.gateway.gcp.result.GCPClientInfoDataResult;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayClientInfoGCPResponse {

    @JsonProperty("result")
    private String result;

    @JsonProperty("data")
    private List<GCPClientInfoDataResult> data = new ArrayList<>();

    @JsonProperty("comments")
    private String comments;

}