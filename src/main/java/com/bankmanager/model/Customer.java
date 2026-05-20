package com.bankmanager.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Customer {
    private int id;
    private String cccd;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private Date dob;
    private Timestamp createdAt;

    public Customer() {}

    public Customer(int id, String cccd, String fullName, String address, String phone, String email, Date dob, Timestamp createdAt) {
        this.id = id;
        this.cccd = cccd;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.dob = dob;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + cccd + ")";
    }
}
