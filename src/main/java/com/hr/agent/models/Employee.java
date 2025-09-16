package com.hr.agent.models;

import java.time.LocalDate;

public class Employee {

    private final int id;
    private final String name;
    private final String email;
    private final String position;
    private final String department;
    private final Integer managerId;
    private final LocalDate joinDate;
    private final String employmentStatus;

    public Employee(int id, String name, String email, String position, String department,
            Integer managerId, LocalDate joinDate, String employmentStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = position;
        this.department = department;
        this.managerId = managerId;
        this.joinDate = joinDate;
        this.employmentStatus = employmentStatus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }
}
