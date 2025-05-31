package desktopapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Menggunakan Parent untuk fleksibilitas
import javafx.scene.Scene;
// import javafx.scene.layout.Pane; // Bisa diganti dengan Parent
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private static Stage primaryStageInstance; // Variabel statis untuk menyimpan primaryStage

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStageInstance = primaryStage; // Simpan instance primaryStage

        // Load mainpage.fxml
        // Pastikan path "/mainpage.fxml" benar (artinya file ada di root classpath)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Parent root = loader.load(); // Menggunakan Parent agar lebih umum

        // Set scene dan stylesheet
        // Pastikan path "/style.css" benar
        Scene scene = new Scene(root);
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Stylesheet /style.css tidak ditemukan.");
        }


        // Set window properties
        // Ukuran yang Anda set di sini akan menjadi ukuran default window.
        // Jika FXML Anda memiliki prefHeight/prefWidth, itu mungkin akan diutamakan
        // tergantung bagaimana root pane di FXML diatur.
        // Untuk aplikasi anak-anak, ukuran tetap biasanya lebih baik.
        // primaryStage.setMinWidth(1024); // Sesuaikan dengan ukuran desain Anda
        // primaryStage.setMinHeight(738); // Sesuaikan
        // primaryStage.setMaxWidth(1024);
        // primaryStage.setMaxHeight(738);
        primaryStageInstance.setWidth(1024); // Set ukuran awal
        primaryStageInstance.setHeight(800);  // Sesuai FXML utama Anda
        primaryStageInstance.setResizable(false); // Membuat ukuran window tetap

        primaryStageInstance.setTitle("KidBoard");
        primaryStageInstance.setScene(scene);
        primaryStageInstance.show();
    }

    // Metode statis untuk mendapatkan primaryStage
    public static Stage getPrimaryStage() {
        if (primaryStageInstance == null) {
            // Ini seharusnya tidak terjadi jika aplikasi dimulai dengan benar
            throw new IllegalStateException("PrimaryStage belum diinisialisasi.");
        }
        return primaryStageInstance;
    }

    // Helper method untuk memuat FXML dan mendapatkan controller (jika diperlukan di luar)
    // atau hanya untuk memuat Parent node.
    public static FXMLLoader getLoader(String fxmlPath) {
        // Pastikan fxmlPath dimulai dengan "/" jika dari root classpath
        if (!fxmlPath.startsWith("/")) {
            fxmlPath = "/" + fxmlPath;
        }
        return new FXMLLoader(App.class.getResource(fxmlPath));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
