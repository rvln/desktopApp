package desktopapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class App extends Application {

    private static Stage primaryStageInstance;

    private void loadCustomFonts() {
        String[] fontPaths = {
            "/fonts/static/Rubik-Regular.ttf",
            "/fonts/static/Rubik-Bold.ttf",
            "/fonts/static/LapsusPro-Bold.otf"
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

        AudioManager.loadAudio();
        System.out.println("Audio assets loaded successfully.");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Stylesheet /style.css tidak ditemukan.");
        }

        primaryStageInstance.setWidth(1366);
        primaryStageInstance.setHeight(768);
        primaryStageInstance.setMinWidth(1024);
        primaryStageInstance.setMinHeight(768);
        primaryStageInstance.setMaxWidth(1366);
        primaryStageInstance.setMaxHeight(768);
        primaryStageInstance.setResizable(true);
        primaryStageInstance.setTitle("KidBoard");
        primaryStageInstance.setScene(scene);
        primaryStageInstance.show();

        
        AudioManager.playMainMusic();
        
    }

    @Override
    public void stop() throws Exception {
        System.out.println("App: stop() called, stopping music.");
        AudioManager.stopMainMusic();
        super.stop();
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
