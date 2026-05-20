package com.bankmanager.model;

import java.math.BigDecimal;
import java.sql.Date;

public class SavingsDeposit {
    private int id;
    private String accountNumber;
    private int termMonths;
    private BigDecimal interestRate;
    private BigDecimal principalAmount;
    private Date startDate;
    private Date endDate;
    private String status; // ACTIVE, CLOSED

    public SavingsDeposit() {}

    public SavingsDeposit(int id, String accountNumber, int termMonths, BigDecimal interestRate, BigDecimal principalAmount, Date startDate, Date endDate, String status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.principalAmount = principalAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
