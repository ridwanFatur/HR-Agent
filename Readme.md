# HR Agent - Java CLI Application

**HR Agent** is an intelligent command-line assistant for HR tasks. It helps you manage employee information, check managers, leave balances, submit leave requests, and more.

---

## Prerequisites

- Java JDK installed
- [Maven](https://maven.apache.org/install.html) installed on Windows

---

## Design

In the CLI, the user prompt is parsed using OpenAI function calls. The available function calls are:

- applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate)
- schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate)
- checkLeaveRequestStatus(String employeeName)
- submitExpenseReport(String employeeName, String category, double amount)
- lookupColleagueInfo(String colleagueName)
- checkLeaveBalance(String employeeName)
- checkPerformanceReview(String employeeName)

If no function matches the user prompt, a normal message is returned.  
Example:
Anda: Halo Memproses... 
HR Agent: Halo! Ada yang bisa saya bantu hari ini?

If parameters are missing, the agent will ask for them:
Anda: Ajukan cuti Memproses... 
HR Agent: Tentu, saya bisa membantu Anda mengajukan cuti. Silakan berikan informasi berikut: 
1. Jenis cuti (tahunan, sakit, cuti melahirkan) 
2. Nama Anda 
3. Tanggal mulai cuti (format dd-MM-yyyy) 
4. Tanggal selesai cuti (format dd-MM-yyyy)

Next, if the function requires an employeeName, it is matched against employee.csv using full name or substring via FuzzyScore. If no match is found, a fallback error is returned.

For functions that only require confirmation (no CSV lookup), the following functions are called and return a confirmation message:

- applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate)
- schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate)
- submitExpenseReport(String employeeName, String category, double amount)

For the following functions, when called, data is fetched from CSV, passed to the LLM, and the response is returned in Indonesian:

- checkLeaveRequestStatus(String employeeName)
- lookupColleagueInfo(String colleagueName)
- checkLeaveBalance(String employeeName)
- checkPerformanceReview(String employeeName)

## Getting Started

1. **Clone the repository**

```bash
git clone https://github.com/ridwanFatur/HR-Agent
cd HR-Agent
```

2. **Compile the project**

```bash
mvn compile
```

3. **Run the application**

```bash
mvn exec:java -Dexec.mainClass=com.hr.agent.App
```

---

## How to Use

Once the application is running, you will see a CLI interface:

```
=== Agen HR Cerdas ===
Selamat datang! Saya adalah asisten HR yang dapat membantu Anda dengan:
? Mencari informasi karyawan (contoh: 'info lengkap rina')
? Mengecek manajer (contoh: 'siapa manajer budi?')
? Mengecek saldo cuti (contoh: 'sisa cuti budi ada berapa?')
? Mengajukan cuti (contoh: 'tolong apply cuti tahunan buat budi dari tgl 3 okt sampe 5 okt')
? Dan berbagai tugas HR lainnya

Ketik 'exit' untuk keluar.
```

### Example Interactions

```
Anda: info lengkap rina
Memproses...
HR Agent: Nama: Rina Wijaya
Email: rina.w@examplecorp.com
Jabatan: QA Engineer
Departemen: Teknologi
Nama Manajer: Santi Putri
Tanggal Bergabung: 20 Nov 2022
Status Karyawan: Aktif

Anda: Siapa manajer Budi?
Memproses...
HR Agent: Manajer Budi adalah Santi Putri.

Anda: sisa cuti budi ada berapa?
Memproses...
HR Agent: Budi Santoso memiliki sisa cuti sebanyak 19 hari.

Anda: tolong apply cuti tahunan buat budi dari tgl 3 okt sampe 5 okt
Memproses...
HR Agent: KONFIRMASI: Pengajuan cuti untuk Budi Santoso (jenis: tahunan) dari tanggal 3 October 2025 hingga 5 October 2025 telah dicatat.
```

