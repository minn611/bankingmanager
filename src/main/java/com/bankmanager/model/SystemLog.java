package com.bankmanager.model;

import java.sql.Timestamp;

public class SystemLog {
    private int id;
    private Integer employeeId;
    private String action;
    private String detail;
    private Timestamp createdAt;

    public SystemLog() {}

    public SystemLog(int id, Integer employeeId, String action, String detail, Timestamp createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.action = action;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
