package desktopapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage; // ini perlu di-import buat Stage

public class Controller {

    @FXML
    private Button textField1;

    @FXML
    private Button textField2;

    private Stage mainWindow; // bikin variabel buat nyimpan Stage

    public void setMainWindow(Stage stage) {
        this.mainWindow = stage;
    }

    @FXML
    void handleButtonAction(ActionEvent event) {
        // Contoh: dari sini kamu sekarang bisa akses mainWindow kalau mau
        System.out.println("Button diklik!");
        if (mainWindow != null) {
            System.out.println("Window title: " + mainWindow.getTitle());
        }
    }
}
