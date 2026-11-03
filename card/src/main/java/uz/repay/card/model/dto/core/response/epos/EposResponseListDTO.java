package uz.repay.card.model.dto.core.response.epos;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

public class EposResponseListDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("branch")
    private Branch branch;
    @JsonProperty("processing")
    private Processing processing;
    @JsonProperty("merchantId")
    private String merchantId;
    @JsonProperty("service")
    private ServiceType service;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("account")
    private String account;
    @JsonProperty("exceptional")
    private boolean exceptional;

    public Long getId() {
        return this.id;
    }

    public Branch getBranch() {
        return this.branch;
    }

    public Processing getProcessing() {
        return this.processing;
    }

    public String getMerchantId() {
        return this.merchantId;
    }

    public ServiceType getService() {
        return this.service;
    }

    public String getTerminalId() {
        return this.terminalId;
    }

    public String getAccount() {
        return this.account;
    }

    public boolean getExceptional() {
        return this.exceptional;
    }

    @JsonProperty("id")
    public void setId(final Long id) {
        this.id = id;
    }

    @JsonProperty("branch")
    public void setBranch(final Branch branch) {
        this.branch = branch;
    }

    @JsonProperty("processing")
    public void setProcessing(final Processing processing) {
        this.processing = processing;
    }

    @JsonProperty("merchantId")
    public void setMerchantId(final String merchantId) {
        this.merchantId = merchantId;
    }

    @JsonProperty("service")
    public void setService(final ServiceType service) {
        this.service = service;
    }

    @JsonProperty("terminalId")
    public void setTerminalId(final String terminalId) {
        this.terminalId = terminalId;
    }

    @JsonProperty("account")
    public void setAccount(final String account) {
        this.account = account;
    }

    @JsonProperty("exceptional")
    public void setExceptional(final boolean exceptional) {
        this.exceptional = exceptional;
    }

    public EposResponseListDTO(Long id, Branch branch, Processing processing, String merchantId, ServiceType service, String terminalId, String account, boolean exceptional) {
        this.id = id;
        this.branch = branch;
        this.processing = processing;
        this.merchantId = merchantId;
        this.service = service;
        this.terminalId = terminalId;
        this.account = account;
        this.exceptional = exceptional;
    }
}
