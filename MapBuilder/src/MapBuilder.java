//This is pretty much just a main class and window display. Consider the Controller the actual host of the program

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class MapBuilder extends Application {

    Controller c;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxml = new FXMLLoader();
        Parent root = fxml.load(new FileInputStream("resources/fxml/mapbuilder.fxml"));
        c = fxml.getController();
        c.setStage(primaryStage);
        primaryStage.setTitle("Map Builder");
        primaryStage.setScene(new Scene(root, 640,400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
