<!-- ## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

--- -->

![KidBoard](/documentation/image-13.png)

---

# Preview KidBoard

![KidBoard](https://github.com/user-attachments/assets/ee65ff89-66ef-41ad-b67e-b0745167da08)

---

## Setup di Visual Studio Code

### Hal yang diperlukan (bersifat WAJIB!!)

> Download JavaFX-SDK (disarankan [versi 21](https://drive.google.com/file/d/1sPn8rBDesNyXLIkAG_szFexzpQ8i0HCW/view)) dan juga Java [JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) dalam format .zip

Jika JavaFX-SDK dan JDK sudah di download, tahap berikutnya melakukan [setup environment variables](#setup-environment-variables)

> Install "Extension Pack For Java" by Microsoft.com di Visual Studio Code
> ![Extension Pack For Java](/documentation/image.png)

---

### 1. Setup Environment Variables

> 1. Ekstrak hasil download javafx-sdk dan jdk ke dalam C:\\Program Files\\Java

> 2. Pergi ke Settings > Search > Environment Variable
>    ![System Properties](/documentation/image-1.png)

> 3. System Variables > Edit ![Environment Variables](/documentation/image-2.png)

> 4. Copas Path/Sumber direktori/folder java jdk-21 sebagai berikut: ![PathJDK](/documentation/image-3.png) Selanjutnya tekan OK.

> 5. Pada system variables, scroll kebawah dan klik pada variable 'PATH' dan tekan edit

> 6. Tekan 'New' untuk menambahkan environment baru dan setelah itu masukkan sumber direktori atau path dari javafx-sdk-21, contoh seperti ini ![pathjavafx](/documentation/image-4.png) dan selanjutnya tekan OK.

### 2. Git Clone menggunakan Git Bash

Untuk mengakses tiap file dari repository 'desktopApp' harus melakukan git clone.

> 1. Salin link HTTPS yang ada pada ![coderepo](/documentation/image-5.png) diatas.

Pastikan anda sudah mempunyai 'Git Bash' di laptop/komputer anda (kalau belum ada, silahkan di install [Git Bash](https://gitforwindows.org/) terlebih dahulu)

> 2. Buka Git Bash dan jalankan sebagai administrator, masuk ke salah satu direktori dengan perintah 'cd'
> 3. Selanjutnya, ketik git clone dan paste linknya dengan klik kanan atau tekan Shift + Insert.
>    ![gitbash](/documentation/image-6.png)
> 4. masukkan perintah 'cd desktopApp' untuk memilih direktori tersebut.
> 5. masukkan perintah code . untuk membuka vscode. ![code.](/documentation/image-9.png)

### 3. Build Application dengan Gradle

Pada bagian ini akan dilakukan'./gradlew build' untuk membangun project agar bisa dijalankan (bisa berupa dalam format .exe)

> ./gradlew build
> ![build](/documentation/image-10.png)

Tahap akhir untuk menjalankan aplikasinya dengan memasukkan perintah berikut:

> ./gradlew run

> ./gradlew clean build (untuk merefresh)

> ./gradlew createExecutable (Untuk Build jadi APK)

#### 3.1 Menjalankan dengan Debug (OPSI)

> 1. Buka file App.java
>    ![App](/documentation/image-11.png)
> 2. Scroll kebawah, ada opsi Run || Debug, tekan salah satu.
>    ![debug](/documentation/image-12.png)

---
