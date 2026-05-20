package com.bankmanager.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Card {
    private String cardNumber;
    private String accountNumber;
    private String cardType; // DEBIT, CREDIT
    private String pinHash;
    private BigDecimal creditLimit;
    private BigDecimal balanceDue;
    private String status; // ACTIVE, LOCKED
    private Date expiryDate;

    public Card() {}

    public Card(String cardNumber, String accountNumber, String cardType, String pinHash, BigDecimal creditLimit, BigDecimal balanceDue, String status, Date expiryDate) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.cardType = cardType;
        this.pinHash = pinHash;
        this.creditLimit = creditLimit;
        this.balanceDue = balanceDue;
        this.status = status;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
