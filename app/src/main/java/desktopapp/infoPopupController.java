package desktopapp;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;

public class infoPopupController {

    @FXML
    private HBox ClosePopUp;

    @FXML
    private void closePopup(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}


