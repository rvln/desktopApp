package desktopapp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage; // ini perlu di-import buat Stage
import javafx.scene.input.MouseEvent;

public class Controller {

    private Stage mainWindow; // bikin variabel buat nyimpan Stage

    public void setMainWindow(Stage stage) {
        this.mainWindow = stage;
    }

    @FXML
    private void showInfo(MouseEvent event) {
        try {
            // Load FXML untuk pop-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/infoPopup.fxml"));
            Parent root = loader.load();

            // Buat stage baru untuk pop-up
            Stage popupStage = new Stage();
            popupStage.setTitle("Informasi");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Block window lain sampai ditutup
            popupStage.setResizable(false);

            // Tampilkan scene dari popupinfo.fxml
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace(); // Debug output
        }
    }
}
