package com.challamani.faultinjection.config;

import java.util.List;

public class FaultInjection {

    private String type;
    private Integer fixedDelay;
    private Integer httpStatus;
    private Integer priority;

    private List<Match> matches;

    public FaultInjection() {
    }

    public FaultInjection(String type, Integer fixedDelay, Integer httpStatus, Integer priority, List<Match> matches) {
        this.type = type;
        this.fixedDelay = fixedDelay;
        this.httpStatus = httpStatus;
        this.priority = priority;
        this.matches = matches;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(Integer fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
}

