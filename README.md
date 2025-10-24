# UAS-IPBO
# Student Master - UAS PBO

## Persyaratan
- Java 8+ (disarankan Java 11)
- MySQL
- Libraries:
    - jasperreports (mis. net.sf.jasperreports:jasperreports:6.20.0 atau sejenis)
    - mysql-connector-java

## Setup
1. Buat database dan tabel (lihat file SQL di README).
2. Tambahkan `jasperreports` dan `mysql-connector-java` ke project dependencies (IntelliJ -> Project Structure -> Libraries).
3. Jalankan `MainForm.java` (class dengan `main`).
4. Folder `captures/` akan berisi screenshot setiap aksi CRUD dan report (create.png, read di table, update.png, delete.png, report.png).

## Output
- Laporan PDF akan disimpan di folder `reports/`.
- Screenshots akan disimpan di folder `captures/`.