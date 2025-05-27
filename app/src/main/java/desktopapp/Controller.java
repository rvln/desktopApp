package desktopapp;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class Controller implements javafx.fxml.Initializable {

    @FXML
    private AnchorPane mainPage;

    @FXML
    private AnchorPane overlayPopup;

    @FXML
    private Pane disableLayer; // Tambahkan referensi ini

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sembunyikan popup & disableLayer saat pertama kali aplikasi dibuka
        overlayPopup.setVisible(false);
        disableLayer.setVisible(false);
    }

    // Menampilkan popup overlay
    @FXML
    private void showInfo(MouseEvent event) {
        disableLayer.setVisible(true);
        overlayPopup.setVisible(true);

        // Efek fade-in untuk disableLayer dan overlayPopup
        FadeTransition fadeDisable = new FadeTransition(Duration.millis(200), disableLayer);
        fadeDisable.setFromValue(0.0);
        fadeDisable.setToValue(1.0);
        fadeDisable.play();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    // Sembunyikan popup overlay (misalnya pakai tombol close di overlay)
    @FXML
    private void closePopup(MouseEvent event) {
        // Fade out untuk overlay dan disableLayer
        FadeTransition fadeOutOverlay = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeOutOverlay.setFromValue(1.0);
        fadeOutOverlay.setToValue(0.0);
        fadeOutOverlay.setOnFinished(e -> overlayPopup.setVisible(false));
        fadeOutOverlay.play();

        FadeTransition fadeOutDisable = new FadeTransition(Duration.millis(200), disableLayer);
        fadeOutDisable.setFromValue(1.0);
        fadeOutDisable.setToValue(0.0);
        fadeOutDisable.setOnFinished(e -> disableLayer.setVisible(false));
        fadeOutDisable.play();
    }
}
