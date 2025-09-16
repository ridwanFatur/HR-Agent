package com.hr.agent;

import java.util.Scanner;

public class App {
	public static void main(String[] args) {
		System.out.println("=== Agen HR Cerdas ===");
		System.out.println("Selamat datang! Saya adalah asisten HR yang dapat membantu Anda dengan:");
		System.out.println("• Mencari informasi karyawan (contoh: 'info lengkap rina')");
		System.out.println("• Mengecek manajer (contoh: 'siapa manajer budi?')");
		System.out.println("• Mengecek saldo cuti (contoh: 'sisa cuti budi ada berapa?')");
		System.out.println("• Mengajukan cuti (contoh: 'tolong apply cuti tahunan buat budi dari tgl 3 okt sampe 5 okt')");
		System.out.println("• Dan berbagai tugas HR lainnya");
		System.out.println("\nKetik 'exit' untuk keluar.\n");

		HRAgent agent = new HRAgent();

		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				try {
					System.out.print("Anda: ");
					String userInput = scanner.nextLine().trim();

					if (userInput.equalsIgnoreCase("exit")
							|| userInput.equalsIgnoreCase("quit")
							|| userInput.equalsIgnoreCase("keluar")) {
						System.out.println("Terima kasih! Sampai jumpa!");
						break;
					}

					if (userInput.isEmpty()) {
						continue;
					}

					System.out.println("Memproses...");
					String response = agent.userInput(userInput);
					System.out.println("HR Agent: " + response + "\n");
				} catch (Exception e) {
					System.out.println("Terjadi kesalahan: " + e.getMessage());
				}
			}
		}
		System.exit(0);
	}
}
