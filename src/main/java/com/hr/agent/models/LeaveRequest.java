package com.hr.agent.models;

import java.time.LocalDate;

public class LeaveRequest {

    private final String requestId;
    private final int employeeId;
    private final String leaveType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String requestStatus;

    public LeaveRequest(String requestId, int employeeId, String leaveType,
            LocalDate startDate, LocalDate endDate, String requestStatus) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestStatus = requestStatus;
    }

    public String getRequestId() {
        return requestId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }
}
