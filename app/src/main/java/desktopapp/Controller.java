package desktopapp;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ResourceBundle;
import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class Controller implements javafx.fxml.Initializable {

    @FXML
    private AnchorPane mainPage;

    @FXML
    private AnchorPane overlayPopup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sembunyikan popup saat pertama kali aplikasi dibuka
        overlayPopup.setVisible(false);
    }

    // Menampilkan popup overlay
    @FXML
    private void showInfo(MouseEvent event) {
        overlayPopup.setVisible(true);

        // Efek fade-in biar halus
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    // Sembunyikan popup overlay (misalnya pakai tombol close di overlay)
    @FXML
    private void closePopup(MouseEvent event) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> overlayPopup.setVisible(false));
        fadeOut.play();
    }
}
