package uz.repay.common.model.dto.request.gateway.gcp.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.GCPDocument;
import uz.repay.common.utils.serializer.DateDefaultDeSerializer;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCPClientInfoDocumentResult {
    @JsonProperty("document")
    private String passportData;

    @JsonProperty("type")
    private GCPDocument type;

    @JsonProperty("docgiveplace")
    private String docGivenPlace;

    @JsonProperty("docgiveplaceid")
    private Integer docGivenPlaceId;

    @JsonProperty("datebegin")
    @JsonDeserialize(using = DateDefaultDeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateBegin;

    @JsonProperty("dateend")
    @JsonDeserialize(using = DateDefaultDeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEnd;

    @JsonProperty("status")
    private Integer status;

}
