package desktopapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load mainpage.fxml yang sudah berisi mainPage dan overlayPopup
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Pane root = loader.load();

        // Set scene dan stylesheet
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Set window properties
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.setMaxWidth(1024);
        primaryStage.setMaxHeight(738);
        primaryStage.setTitle("KidBoard");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
