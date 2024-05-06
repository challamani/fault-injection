package com.challamani.faultinjection.config;

import java.util.List;

public class Match {

    public Match() {
    }

    private List<String> methods;
    private List<RequestHeader> headers;

    public Match(List<String> methods, List<RequestHeader> headers) {
        this.methods = methods;
        this.headers = headers;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<RequestHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<RequestHeader> headers) {
        this.headers = headers;
    }
}
