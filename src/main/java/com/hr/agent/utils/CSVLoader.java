package com.hr.agent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.hr.agent.models.Employee;
import com.hr.agent.models.LeaveBalance;
import com.hr.agent.models.LeaveRequest;
import com.hr.agent.models.PerformanceReview;

public class CSVLoader {

    private static List<String> loadLinesFromResource(String resourcePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (InputStream input = CSVLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public static List<PerformanceReview> loadPerformanceReviews() {
        List<PerformanceReview> reviews = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            List<String> lines = loadLinesFromResource("data/performance_reviews.csv");
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 6) {
                    continue;
                }

                String reviewId = parts[0].trim();
                int employeeId = Integer.parseInt(parts[1].trim());
                int reviewerId = Integer.parseInt(parts[2].trim());
                LocalDate reviewDate = LocalDate.parse(parts[3].trim(), formatter);
                int performanceScore = Integer.parseInt(parts[4].trim());
                String reviewStatus = parts[5].trim();

                reviews.add(new PerformanceReview(
                        reviewId, employeeId, reviewerId, reviewDate, performanceScore, reviewStatus
                ));
            }
        } catch (IOException e) {
            System.err.println("Error loading performance reviews: " + e.getMessage());
        }
        return reviews;
    }

    public static List<LeaveRequest> loadLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        try {
            List<String> lines = loadLinesFromResource("data/leave_requests.csv");
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 6) {
                    continue;
                }

                String requestId = parts[0].trim();
                int employeeId = Integer.parseInt(parts[1].trim());
                String leaveType = parts[2].trim();
                LocalDate startDate = LocalDate.parse(parts[3].trim());
                LocalDate endDate = LocalDate.parse(parts[4].trim());
                String requestStatus = parts[5].trim();

                requests.add(new LeaveRequest(requestId, employeeId, leaveType, startDate, endDate, requestStatus));
            }
        } catch (IOException e) {
            System.err.println("Error loading leave requests: " + e.getMessage());
        }
        return requests;
    }

    public static List<LeaveBalance> loadLeaveBalances() {
        List<LeaveBalance> list = new ArrayList<>();
        try {
            List<String> lines = loadLinesFromResource("data/leave_balances.csv");
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 3) {
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String type = parts[1].trim();
                int remaining = Integer.parseInt(parts[2].trim());

                list.add(new LeaveBalance(id, type, remaining));
            }
        } catch (IOException e) {
            System.err.println("Error loading leave balances: " + e.getMessage());
        }
        return list;
    }

    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {
            List<String> lines = loadLinesFromResource("data/employees.csv");
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(",");
                if (parts.length < 8) {
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String email = parts[2].trim();
                String position = parts[3].trim();
                String department = parts[4].trim();

                Integer managerId = null;
                if (!parts[5].trim().isEmpty()) {
                    managerId = Integer.valueOf(parts[5].trim());
                }

                LocalDate joinDate = LocalDate.parse(parts[6].trim());
                String employmentStatus = parts[7].trim();

                employees.add(new Employee(id, name, email, position, department,
                        managerId, joinDate, employmentStatus));
            }
        } catch (IOException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
        return employees;
    }
}
