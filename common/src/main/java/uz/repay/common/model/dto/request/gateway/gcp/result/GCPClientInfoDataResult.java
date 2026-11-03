package uz.repay.common.model.dto.request.gateway.gcp.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.utils.serializer.DateDefaultDeSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCPClientInfoDataResult {
    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("current_pinpp")
    private String currentPersonCode;

    @JsonProperty("pinpps")
    private List<String> oldPersonCodes = new ArrayList<>();

    @JsonProperty("current_document")
    private String currentDocument;

    @JsonProperty("documents")
    private List<GCPClientInfoDocumentResult> documents = new ArrayList<>();

    @JsonProperty("surnamelat")
    private String surnameLatin;

    @JsonProperty("namelat")
    private String nameLatin;

    @JsonProperty("patronymlat")
    private String patronymLatin;

    @JsonProperty("surnamecyr")
    private String surnameCyrillic;

    @JsonProperty("namecyr")
    private String nameCyrillic;

    @JsonProperty("patronymcyr")
    private String patronymCyrillic;

    @JsonProperty("engsurname")
    private String surnameEnglish;

    @JsonProperty("engname")
    private String nameEnglish;

    @JsonProperty("birth_date")
    @JsonDeserialize(using = DateDefaultDeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonProperty("birthplace")
    private String birthPlace;

    @JsonProperty("birthcountry")
    private String birthCountry;

    @JsonProperty("birthcountryid")
    private Integer birthCountryId;

    @JsonProperty("livestatus")
    private Integer liveStatus;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("nationalityid")
    private Integer nationalityId;

    @JsonProperty("citizenship")
    private String citizenship;

    @JsonProperty("citizenshipid")
    private Integer citizenshipId;

    @JsonProperty("sex")
    private Integer sex; // 1 = Male, 2 = Female
}
