package com.jiobuddytw.jiobuddytwmerchant.Class;

public class ScanHistoryClass {

    private String no;
    private String code;
    private String denom;

    public ScanHistoryClass(String no,String code,String denom) {
        this.no = no;
        this.code = code;
        this.denom = denom;
    }

    public String getNo() {
        return no;
    }

    public String getCode() {
        return code;
    }

    public String getDenom() {
        return denom;
    }
}
