package com.hr.agent;

import java.time.LocalDate;

public interface HRFunctions {

    String applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate);

    String schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate);

    String checkLeaveRequestStatus(String employeeName);

    String submitExpenseReport(String employeeName, String category, double amount);

    String lookupColleagueInfo(String colleagueName);

		String checkLeaveBalance(String employeeName);

    String checkPerformanceReview(String employeeName);
}
