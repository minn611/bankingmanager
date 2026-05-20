package com.bankmanager.model;

import java.math.BigDecimal;
import java.sql.Date;

public class LoanPayment {
    private int id;
    private int loanId;
    private BigDecimal amountPaid;
    private Date paymentDate;

    public LoanPayment() {}

    public LoanPayment(int id, int loanId, BigDecimal amountPaid, Date paymentDate) {
        this.id = id;
        this.loanId = loanId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
