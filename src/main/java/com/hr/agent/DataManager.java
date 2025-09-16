package com.hr.agent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hr.agent.models.Employee;
import com.hr.agent.models.LeaveBalance;
import com.hr.agent.models.LeaveRequest;
import com.hr.agent.models.PerformanceReview;
import com.hr.agent.utils.CSVLoader;
import org.apache.commons.text.similarity.FuzzyScore;

public class DataManager {

	private final List<Employee> employees;
	private final List<PerformanceReview> reviews;
	private final List<LeaveBalance> leaveBalances;
	private final List<LeaveRequest> leaveRequests;
	private final Map<Integer, String> idToName;

	public DataManager() {
		this.employees = CSVLoader.loadEmployees();
		this.reviews = CSVLoader.loadPerformanceReviews();
		this.leaveBalances = CSVLoader.loadLeaveBalances();
		this.leaveRequests = CSVLoader.loadLeaveRequests();
		this.idToName = employees.stream()
				.collect(Collectors.toMap(Employee::getId, Employee::getName));
	}

	public Optional<Employee> checkName(String name) {
		int threshold = 80;
		if (name == null || name.isBlank()) {
			return Optional.empty();
		}

		String nameLower = name.toLowerCase().trim();
		FuzzyScore fuzzy = new FuzzyScore(Locale.ENGLISH);

		for (Employee emp : employees) {
			if (emp.getName().equalsIgnoreCase(nameLower)) {
				return Optional.of(emp);
			}
		}

		for (Employee emp : employees) {
			String empLower = emp.getName().toLowerCase();
			if (empLower.contains(nameLower) || nameLower.contains(empLower)) {
				return Optional.of(emp);
			}
		}

		List<FuzzyCandidate> candidates = new ArrayList<>();
		for (Employee emp : employees) {
			String empLower = emp.getName().toLowerCase();

			int scoreFull = fuzzy.fuzzyScore(nameLower, empLower);
			if (scoreFull >= threshold) {
				candidates.add(new FuzzyCandidate(emp, 2, scoreFull));
				continue;
			}

			for (String word : empLower.split(" ")) {
				int scoreWord = fuzzy.fuzzyScore(nameLower, word);
				if (scoreWord >= threshold) {
					candidates.add(new FuzzyCandidate(emp, 1, scoreWord));
					break;
				}
			}
		}

		if (!candidates.isEmpty()) {
			candidates.sort(Comparator.comparingInt(FuzzyCandidate::getPriority)
					.thenComparingInt(FuzzyCandidate::getScore).reversed());
			return Optional.of(candidates.get(0).getEmployee());
		}

		return Optional.empty();
	}

	private static class FuzzyCandidate {
		private Employee employee;
		private int priority;
		private int score;

		public FuzzyCandidate(Employee employee, int priority, int score) {
			this.employee = employee;
			this.priority = priority;
			this.score = score;
		}

		public Employee getEmployee() {
			return employee;
		}

		public int getPriority() {
			return priority;
		}

		public int getScore() {
			return score;
		}
	}

	public String getEmployeeInfo(String name) {
		Optional<Employee> optEmp = checkName(name);
		if (optEmp.isEmpty()) {
			return "Tidak ditemukan karyawan dengan nama " + name + ".";
		}

		Employee emp = optEmp.get();
		String managerName = emp.getManagerId() != null ? idToName.getOrDefault(emp.getManagerId(), "Tidak ada")
				: "Tidak ada";
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

		return String.format(
				"Informasi Karyawan:\nNama: %s\nEmail: %s\nJabatan: %s\nDepartemen: %s\nNama Manajer: %s\nTanggal Bergabung: %s\nStatus Karyawan: %s",
				emp.getName(),
				emp.getEmail(),
				emp.getPosition(),
				emp.getDepartment(),
				managerName,
				emp.getJoinDate().format(fmt),
				emp.getEmploymentStatus());
	}

	public String getLeaveBalance(String name) {
		Optional<Employee> optEmp = checkName(name);
		if (optEmp.isEmpty())
			return "Tidak ditemukan karyawan dengan nama " + name + ".";
		Employee emp = optEmp.get();

		List<LeaveBalance> balances = leaveBalances.stream()
				.filter(lb -> lb.getEmployeeId() == emp.getId())
				.toList();

		if (balances.isEmpty())
			return "Tidak ada informasi cuti untuk " + emp.getName() + ".";

		StringBuilder sb = new StringBuilder("Informasi cuti untuk " + emp.getName() + ":\n");
		for (LeaveBalance lb : balances) {
			sb.append(String.format("- %s: %d hari\n", lb.getLeaveType(), lb.getRemainingDays()));
		}
		return sb.toString();
	}

	public String getLeaveRequest(String name) {
		Optional<Employee> optEmp = checkName(name);
		if (optEmp.isEmpty())
			return "Tidak ditemukan karyawan dengan nama " + name + ".";
		Employee emp = optEmp.get();

		List<LeaveRequest> requests = leaveRequests.stream()
				.filter(lr -> lr.getEmployeeId() == emp.getId())
				.toList();

		if (requests.isEmpty())
			return "Tidak ada pengajuan cuti untuk " + emp.getName() + ".";

		StringBuilder sb = new StringBuilder("Pengajuan cuti untuk " + emp.getName() + ":\n");
		for (LeaveRequest lr : requests) {
			sb.append(String.format("- %s dari %s hingga %s (Status: %s)\n",
					lr.getLeaveType(),
					lr.getStartDate(),
					lr.getEndDate(),
					lr.getRequestStatus()));
		}
		return sb.toString();
	}

	public String getPerformanceReview(String name) {
		Optional<Employee> optEmp = checkName(name);
		if (optEmp.isEmpty())
			return "Tidak ditemukan karyawan dengan nama " + name + ".";
		Employee emp = optEmp.get();

		List<PerformanceReview> empReviews = reviews.stream()
				.filter(r -> r.getEmployeeId() == emp.getId())
				.toList();

		if (empReviews.isEmpty())
			return "Tidak ada review performa untuk " + emp.getName() + ".";

		StringBuilder sb = new StringBuilder("Review performa untuk " + emp.getName() + ":\n");
		for (PerformanceReview r : empReviews) {
			String reviewerName = idToName.getOrDefault(r.getReviewerId(), "Tidak ada");
			sb.append(String.format("- Reviewer: %s, Tanggal: %s, Skor: %d, Status: %s\n",
					reviewerName,
					r.getReviewDate(),
					r.getPerformanceScore(),
					r.getReviewStatus()));
		}
		return sb.toString();
	}
}