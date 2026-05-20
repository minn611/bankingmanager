package com.bankmanager.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Loan {
    private int id;
    private int customerId;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int termMonths;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, PAID
    private Date loanDate;
    private Integer approvedBy;

    public Loan() {}

    public Loan(int id, int customerId, BigDecimal loanAmount, BigDecimal interestRate, int termMonths, String status, Date loanDate, Integer approvedBy) {
        this.id = id;
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.status = status;
        this.loanDate = loanDate;
        this.approvedBy = approvedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }
}
