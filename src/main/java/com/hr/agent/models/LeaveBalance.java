package com.hr.agent.models;

public class LeaveBalance {

    private final int employeeId;
    private final String leaveType;
    private int remainingDays;

    public LeaveBalance(int employeeId, String leaveType, int remainingDays) {
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.remainingDays = remainingDays;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public int getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
}
