package com.bankmanager.model;

import java.math.BigDecimal;
import java.sql.Date;

public class InterestRate {
    private int id;
    private String rateType; // SAVINGS, LOAN
    private int termMonths;
    private BigDecimal value; // Lãi suất % năm
    private Date effectiveDate;

    public InterestRate() {}

    public InterestRate(int id, String rateType, int termMonths, BigDecimal value, Date effectiveDate) {
        this.id = id;
        this.rateType = rateType;
        this.termMonths = termMonths;
        this.value = value;
        this.effectiveDate = effectiveDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
