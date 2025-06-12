# IF2050-2025-K4M-ElderGuard
<h1 align="center">🧓 ElderGuard 👵</h1>
<div align="justify"> ElderGuard adalah sistem pemantauan lansia berbasis teknologi yang dirancang untuk meningkatkan keamanan dan kenyamanan bagi lansia yang tinggal sendiri.
 </div>


## Daftar Isi
* [Penjelasan Singkat Aplikasi](#penjelasan-singkat-aplikasi-🧑‍⚕️)
* [Cara Menjalankan Aplikasi](#cara-menjalankan-aplikasi-📃)
* [Daftar Modul](#daftar-modul-📃)
* [Daftar Tabel Basis Data](#daftar-tabel-basis-data-📃)
* [Kontributor](#kontributor)


## Penjelasan Singkat Aplikasi 🧑‍⚕️
<div align="justify">


Sistem ini berfungsi sebagai solusi pemantauan kesehatan dan deteksi keadaan darurat, dengan kemampuan untuk mengidentifikasi aktivitas pengguna secara real-time seperti mendeteksi kondisi abnormal, jatuh, dan detak jantung tidak normal. Menggunakan perangkat wearable yang dilengkapi dengan berbagai sensor (detak jantung, suhu tubuh, tekanan darah, akselerometer, dan GPS), sistem mengumpulkan data secara kontinu dan mengirimkannya ke cloud database melalui koneksi internet.
</div>


**User Sistem ElderGuard**
| User       | Peran                       |
|----------|---------------------------|
| Lansia | Pengguna yang mengenakan perangkat pemantauan ElderBand             |
| Keluarga | Memantau kondisi dan menerima notifikasi kesehatan Lansia              |
| Petugas Rumah Sakit | Memberi respons medis darurat dan memantau notifikasi darurat dari setiap pengguna ElderBand     |
| Orang Sekitar | Menerima notifikasi suara peringatan keadaan darurat              |


**Fitur Utama:**
* Pemantauan kesehatan secara real-time dari pengguna ElderBand
* Sistem pemantauan kondisi kesehatan lansia terintegrasi dengan Keluarga dan Petugas Rumah Sakit
* Peringatan atas kondisi darurat dari Lansia kepada Keluarga, Petugas Rumah Sakit, dan Orang Sekitar


<!-- **Proses Analisis Data**
1. Memantau kondisi kesehatan Lansia secara real-time
2. Mengidentifikasi kondisi darurat Lansia
3. Memberi notifikasi peringatan kepada Keluarga,  -->


## Cara Menjalankan Aplikasi 📃


### Aplikasi ini dijalankan dengan lingkungan sebagai berikut
SDK : Java SDK 21 dan yang lebih baru  
IDE : IntelliJ IDE versi tahun 2024 dan 2025  
Build automation tool : Gradle v8.14  
GUI Framework : JavaFX v17.0.2  
Java DB Connection : Postgresql v42.7.1  
Cloud DB Service : Supabase  


<!-- Aplikasi ini dapat diakses pada:  
```


``` -->
### Langkah menjalankan aplikasi:


**1. Clone Repository**  
Lakukan clone repository pada software IDE.
```
git clone <repository-url>
```
**2. Konfigurasi Database**  
Buat file `.env` pada folder proyek dengan isi sebagai berikut:
```
SUPABASE_DB_URL=jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?user=postgres.pmgthoodfkhwtwxprewu&password=fDB1qgLlUvshmfpX
```
**3. Install Gradle**  
Lakukan penginstallan extension Gradle pada IDE


**4. Run Aplikasi**  
Run aplikasi dengan mengetik kode berikut di terminal:
```
./gradlew run
```


## Daftar Modul 📃
Berikut modul beserta dengan pembagian tugas yang diimplementasikan pada proyek ini:
| Nama Modul      | Pembagian Tugas        |
|-----------------|------------------------|
| Setup & Struktur Proyek |   Dama Dhananjaya D.          |
| UserAccount & Auth |    Dama Dhananjaya D.       |
| Pemantauan Kondisi Tubuh Lansia (Monitoring) |   Dama Dhananjaya D.          |
| Implementasi Riwayat Medis Lansia |    Dama Dhananjaya D., Daffari Adiyatma        |
| Implementasi Notifikasi Darurat |   Dama Dhananjaya D., Nasha Nasmia, Vincentius Vercellino T.         |
| Unit Testing |   Nasha Nasmia, Zachrin Afian, Vincentius Vercellino T.|


## Daftar Tabel Basis Data 📃
Berikut adalah tabel basis data yang diimplementasikan pada proyek ini:
### 1. Sensor Readings
| sensor_readings                   |
|-------------------------------------|
| id           |
| sensor_id            |
| reading_value     |
| reading_at              |


### 2. Emergency Alert
| emergency_alert                   |
|-------------------------------------|
| alert_id           |
| priority            |
| patient_name     |
| alert_type             |


### 3. Sensor
| sensor                   |
|-------------------------------------|
| sensor_id           |
| type            |
| battery_level     |
| device_id              |
| created_at              |


### 4. Family Member
| family_member                   |
|-------------------------------------|
| family_member_id           |
| lansia_id            |
| created_at     |


### 5. Wearable Device
| wearable_device                   |
|-------------------------------------|
| device_id           |
| model            |
| battery_level     |
| latitude              |
| longitude            |
| lansia_id     |
| created_id              |


### 6. Lansia Medical History
| lansia_medical_history                   |
|-------------------------------------|
| user_id           |
| medical_condition            |
| diagnosis_date     |
| description              |
| severity     |
| history_id              |


### 7. Lansia
| lansia                   |
|-------------------------------------|
| user_id           |
| birthdate            |
| created_at     |


### 8. Medical Staff
| medical_staff                   |
|-------------------------------------|
| user_id           |
| hospital_id            |
| created_at     |


### 9. User Account
| user_account                   |
|-------------------------------------|
| user_id           |
| username            |
| password     |
| contact_info              |
| role     |
| created_at              |


### 10. Hospital
| hospital                   |
|-------------------------------------|
| hospital_id           |
| hospital_name            |
| latitude     |
| longitude              |
| created_at              |


## Kontributor
### Kelompok M - K04


| NIM      | Nama                      |
|----------|---------------------------|
| 14422021 | Zachrin Afian             |
| 14422036 | Nasha Nasmia              |
| 14422037 | Vincentius Vercellino Tanumihardja     |
| 18222003 | Daffari Adiyatma              |
| 18222047 | Dama Dhananjaya Daliman     |



