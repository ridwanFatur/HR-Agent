package com.hr.agent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.hr.agent.models.Employee;

public class HRFunctionsImpl implements HRFunctions {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
	private final DataManager dataManager;

	public HRFunctionsImpl() {
		this.dataManager = new DataManager();
	}

	@Override
	public String applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			return "Harap berikan tanggal mulai dan tanggal selesai cuti.";
		}

		Optional<Employee> empOpt = dataManager.checkName(employeeName);
		if (empOpt.isEmpty()) {
			return "Tidak ada nama karyawan untuk " + employeeName + ".";
		}
		String realName = empOpt.get().getName();
		return String.format("KONFIRMASI: Pengajuan cuti untuk %s (jenis: %s) dari tanggal %s hingga %s telah dicatat.",
				realName, leaveType, startDate.format(formatter), endDate.format(formatter));
	}

	@Override
	public String schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate) {
		if (reviewDate == null) {
			return "Harap berikan tanggal review performa.";
		}

		Optional<Employee> empOpt = dataManager.checkName(employeeName);
		if (empOpt.isEmpty()) {
			return "Tidak ada nama karyawan untuk " + employeeName + ".";
		}

		Optional<Employee> reviewerOpt = dataManager.checkName(reviewerName);
		if (reviewerOpt.isEmpty()) {
			return "Tidak ada nama karyawan untuk reviewer " + reviewerName + ".";
		}

		String realName = empOpt.get().getName();
		String realReviewer = reviewerOpt.get().getName();

		return String.format("KONFIRMASI: Sesi review performa untuk %s dengan %s telah dijadwalkan pada %s.",
				realName, realReviewer, reviewDate.format(formatter));
	}

	@Override
	public String submitExpenseReport(String employeeName, String category, double amount) {
		Optional<Employee> empOpt = dataManager.checkName(employeeName);
		if (empOpt.isEmpty()) {
			return "Tidak ada nama karyawan untuk " + employeeName + ".";
		}
		String realName = empOpt.get().getName();
		return String.format(
				"KONFIRMASI: Laporan pengeluaran untuk %s sebesar Rp%,.2f (kategori: %s) telah diajukan untuk diproses.",
				realName, amount, category);
	}

	@Override
	public String checkLeaveRequestStatus(String employeeName) {
		return dataManager.getLeaveRequest(employeeName);
	}

	@Override
	public String lookupColleagueInfo(String colleagueName) {
		return dataManager.getEmployeeInfo(colleagueName);
	}

	@Override
	public String checkLeaveBalance(String employeeName) {
		return dataManager.getLeaveBalance(employeeName);
	}

	@Override
	public String checkPerformanceReview(String employeeName) {
		return dataManager.getPerformanceReview(employeeName);
	}
}
