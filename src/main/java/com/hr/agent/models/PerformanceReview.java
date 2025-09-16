package com.hr.agent.models;

import java.time.LocalDate;

public class PerformanceReview {

    private final String reviewId;
    private final int employeeId;
    private final int reviewerId;
    private final LocalDate reviewDate;
    private int performanceScore;
    private String reviewStatus;

    public PerformanceReview(String reviewId, int employeeId, int reviewerId,
            LocalDate reviewDate, int performanceScore, String reviewStatus) {
        this.reviewId = reviewId;
        this.employeeId = employeeId;
        this.reviewerId = reviewerId;
        this.reviewDate = reviewDate;
        this.performanceScore = performanceScore;
        this.reviewStatus = reviewStatus;
    }

    public String getReviewId() {
        return reviewId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public int getPerformanceScore() {
        return performanceScore;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setPerformanceScore(int performanceScore) {
        this.performanceScore = performanceScore;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
