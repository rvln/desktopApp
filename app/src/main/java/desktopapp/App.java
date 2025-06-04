package desktopapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
// import java.util.Objects; // Tidak digunakan jika tidak ada Objects.requireNonNull

public class App extends Application {

    private static Stage primaryStageInstance;

    private void loadCustomFonts() {
        String[] fontPaths = {
            "/fonts/static/Rubik-Regular.ttf",
            "/fonts/static/Rubik-Bold.ttf",
            "/fonts/static/LapsusPro-Bold.otf"
            // Tambahkan path font lainnya jika ada
        };

        for (String fontPath : fontPaths) {
            try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                if (fontStream != null) {
                    Font.loadFont(fontStream, 10);
                    System.out.println("Font loaded: " + fontPath);
                } else {
                    System.err.println("Error loading font: Cannot find font resource at " + fontPath);
                }
            } catch (Exception e) {
                System.err.println("Error loading font " + fontPath + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStageInstance = primaryStage;

        loadCustomFonts();
        // System.out.println("Available font families: " + Font.getFamilies()); // Bisa di-uncomment untuk debug font

        AudioManager.loadAudio(); // Muat semua aset audio
        System.out.println("Audio assets loaded successfully."); // Log dari Anda

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Stylesheet /style.css tidak ditemukan.");
        }

        primaryStageInstance.setWidth(1024);
        primaryStageInstance.setHeight(800);
        primaryStageInstance.setResizable(false);
        primaryStageInstance.setTitle("KidBoard");
        primaryStageInstance.setScene(scene);
        primaryStageInstance.show();

        // === MULAI MUSIK LATAR UTAMA DI SINI ===
        AudioManager.playMainMusic();
        // =======================================
    }

    /**
     * Metode ini dipanggil ketika aplikasi ditutup.
     * Kita gunakan untuk menghentikan musik.
     */
    @Override
    public void stop() throws Exception {
        System.out.println("App: stop() called, stopping music.");
        AudioManager.stopMainMusic(); // Hentikan musik utama
        super.stop(); // Panggil implementasi super
    }

    public static Stage getPrimaryStage() {
        if (primaryStageInstance == null) {
            throw new IllegalStateException("PrimaryStage belum diinisialisasi.");
        }
        return primaryStageInstance;
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        if (!fxmlPath.startsWith("/")) {
            fxmlPath = "/" + fxmlPath;
        }
        return new FXMLLoader(App.class.getResource(fxmlPath));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
