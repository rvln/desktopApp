package desktopapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainpage.fxml"));
        Pane root = loader.load();

        Controller controller = loader.getController();
        controller.setMainWindow(primaryStage);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.setMaxWidth(1024);
        primaryStage.setMaxHeight(738);

        primaryStage.setScene(scene);
        primaryStage.setTitle("KidBoard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
