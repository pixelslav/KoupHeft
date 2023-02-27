package com.bookSuppliment.server.app.dtos;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponse {
    private boolean success;
    private Date challengeTs;
    private String hostname;
    private List<String> errorCodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonProperty("challenge_ts")
    public Date getChallengeTs() {
        return challengeTs;
    }

    @JsonProperty("challenge_ts")
    public void setChallengeTs(Date challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty("error-codes")
    public List<String> getErrorCodes() {
        return errorCodes;
    }

    @JsonProperty("error-codes")
    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }
}

