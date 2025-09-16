package com.hr.agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import com.openai.models.chat.completions.ChatCompletionMessageFunctionToolCall;
import com.openai.models.chat.completions.ChatCompletionMessageToolCall;
import org.json.JSONObject;

public class HRAgent {
	private HRFunctions hrFunctions = new HRFunctionsImpl();
	private OpenAIClient client;

	public HRAgent() {
		Properties envProps = new Properties();
		try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#") && line.contains("=")) {
					String[] parts = line.split("=", 2);
					envProps.setProperty(parts[0].trim(), parts[1].trim());
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to load .env: " + e.getMessage());
		}

		String apiKey = envProps.getProperty("OPENAI_API_KEY");
		if (apiKey != null && !apiKey.isEmpty()) {
			client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();
		}
	}

	public String getCurrentDate() {
		LocalDate now = LocalDate.now();
		DayOfWeek day = now.getDayOfWeek();
		String dayName = switch (day) {
			case MONDAY -> "Senin";
			case TUESDAY -> "Selasa";
			case WEDNESDAY -> "Rabu";
			case THURSDAY -> "Kamis";
			case FRIDAY -> "Jumat";
			case SATURDAY -> "Sabtu";
			case SUNDAY -> "Minggu";
		};
		String monthName = switch (now.getMonth()) {
			case JANUARY -> "Januari";
			case FEBRUARY -> "Februari";
			case MARCH -> "Maret";
			case APRIL -> "April";
			case MAY -> "Mei";
			case JUNE -> "Juni";
			case JULY -> "Juli";
			case AUGUST -> "Agustus";
			case SEPTEMBER -> "September";
			case OCTOBER -> "Oktober";
			case NOVEMBER -> "November";
			case DECEMBER -> "Desember";
		};
		return String.format("%s, %02d %s %d", dayName, now.getDayOfMonth(), monthName, now.getYear());
	}

	public String askBasedKnowledge(String userPrompt, String knowledge) {
		String systemPrompt = """
				Kamu adalah asisten AI yang hanya boleh menjawab berdasarkan knowledge yang diberikan.
				Jawaban harus menggunakan bahasa Indonesia. Jangan menambahkan informasi di luar knowledge tersebut.
				Catatan penting:
				- Nama yang diberikan user mungkin mengandung typo, singkatan, atau variasi ejaan.
				- Jika ada nama yang mirip dengan yang ada di knowledge, awali jawaban dengan 'Mungkin maksud kamu <nama>:' dan kemudian berikan jawaban berdasarkan knowledge.

				Knowledge:
				%s
				"""
				.formatted(knowledge);

		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
				.model(ChatModel.GPT_3_5_TURBO)
				.addSystemMessage(systemPrompt)
				.addUserMessage(userPrompt)
				.temperature(0.0)
				.build();

		ChatCompletion chatCompletion = client.chat().completions().create(params);

		return chatCompletion.choices().get(0)
				.message()
				.content()
				.orElse("Maaf, saya tidak dapat memberikan jawaban saat ini.");
	}

	public String userInput(String prompt) {
		String systemPrompt = String.format(
				"Kamu adalah asisten HR AI. Keterangan hari ini: %s.%n" +
						"Peraturan untuk field 'nama':%n" +
						"- Jangan menggunakan kata ganti seperti 'saya', 'Anda', atau gelar seperti 'Bu', 'Pak'.",
				getCurrentDate());

		ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
				.model(ChatModel.GPT_3_5_TURBO)
				.temperature(0)
				.addSystemMessage(systemPrompt)
				.addUserMessage(prompt);

		createParamsBuilder
				.addTool(LeaveApply.class)
				.addTool(LeaveStatus.class)
				.addTool(LeaveBalance.class)
				.addTool(ReviewSchedule.class)
				.addTool(ReviewResult.class)
				.addTool(ExpenseSubmit.class)
				.addTool(EmployeeInfo.class);
		var response = client.chat().completions().create(createParamsBuilder.build());

		ChatCompletionMessage message = response.choices().get(0).message();
		Optional<List<ChatCompletionMessageToolCall>> optionalToolCalls = message.toolCalls();

		if (optionalToolCalls.isPresent() && !message.toolCalls().isEmpty()) {
			List<ChatCompletionMessageToolCall> toolCalls = optionalToolCalls.get();
			if (!toolCalls.isEmpty()) {
				ChatCompletionMessageToolCall firstToolCall = toolCalls.get(0);
				var function = firstToolCall.asFunction().function();
				String intent = function.name();
				String arguments = function.arguments();
				JSONObject jsonObject = new JSONObject(arguments);
				String employeeName = jsonObject.optString("nama", null);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

				switch (intent) {
					case "LeaveApply":
						String startStr = jsonObject.optString("tanggalMulai", null);
						String endStr = jsonObject.optString("tanggalSelesai", null);
						LocalDate startDate = LocalDate.parse(startStr, formatter);
						LocalDate endDate = LocalDate.parse(endStr, formatter);
						return hrFunctions.applyForLeave(
								employeeName,
								jsonObject.optString("jenisCuti", null),
								startDate,
								endDate);

					case "LeaveStatus": {
						String knowledge = hrFunctions.checkLeaveRequestStatus(employeeName);
						return askBasedKnowledge(prompt, knowledge);
					}

					case "LeaveBalance": {
						String knowledge = hrFunctions.checkLeaveBalance(employeeName);
						return askBasedKnowledge(prompt, knowledge);
					}

					case "ReviewSchedule":
						String reviewDateStr = jsonObject.optString("tanggalReview", null);
						LocalDate reviewDate = LocalDate.parse(reviewDateStr, formatter);
						return hrFunctions.schedulePerformanceReview(
								employeeName,
								jsonObject.optString("namaReviewer", null),
								reviewDate);

					case "ReviewResult": {
						String knowledge = hrFunctions.checkPerformanceReview(employeeName);
						return askBasedKnowledge(prompt, knowledge);
					}

					case "ExpenseSubmit":
						String amountStr = jsonObject.optString("jumlah", "0");
						double amount = Double.parseDouble(amountStr);
						return hrFunctions.submitExpenseReport(
								employeeName,
								jsonObject.optString("kategori", null),
								amount);

					case "EmployeeInfo": {
						String knowledge = hrFunctions.lookupColleagueInfo(employeeName);
						return askBasedKnowledge(prompt, knowledge);
					}

					default:
						return "Sorry, the intent '" + intent + "' is not supported yet.";
				}
			} else {
				return message.content().orElse("Maaf, saya tidak bisa memberikan jawaban saat ini.");
			}
		} else {
			return message.content().orElse("Maaf, saya tidak bisa memberikan jawaban saat ini.");
		}
	}

	@JsonClassDescription("Mengajukan cuti")
	static class LeaveApply {
		public String nama;
		@JsonPropertyDescription("Jenis cuti yang tersedia: tahunan, sakit, cuti melahirkan")
		public String jenisCuti;
		@JsonPropertyDescription("Format dd-MM-yyyy")
		public String tanggalMulai;
		@JsonPropertyDescription("Format dd-MM-yyyy")
		public String tanggalSelesai;
	}

	@JsonClassDescription("Status permintaan cuti")
	static class LeaveStatus {
		public String nama;
	}

	@JsonClassDescription("Sisa cuti yang tersedia")
	static class LeaveBalance {
		public String nama;
	}

	@JsonClassDescription("Menjadwalkan review kinerja")
	static class ReviewSchedule {
		public String nama;
		public String namaReviewer;
		@JsonPropertyDescription("Format dd-MM-yyyy")
		public String tanggalReview;
	}

	@JsonClassDescription("Hasil/riwayat review kinerja")
	static class ReviewResult {
		public String nama;
	}

	@JsonClassDescription("Laporan pengeluaran")
	static class ExpenseSubmit {
		public String nama;
		public String kategori;
		public double jumlah;
	}

	@JsonClassDescription("Informasi tentang seorang karyawan")
	static class EmployeeInfo {
		public String nama;
	}
}
