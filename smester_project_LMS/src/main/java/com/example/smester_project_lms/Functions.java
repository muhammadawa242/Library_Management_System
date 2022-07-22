package com.example.smester_project_lms;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Functions {
    public void switchScenes(ActionEvent event, String fxml) {
        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource(fxml));
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        Scene scene = new Scene(root, 1370, 700);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    static void warningLabel(Label label, String statement, String color) {
        label.setText(statement);
        label.setTextFill(Paint.valueOf(color));
        PauseTransition p = new PauseTransition(Duration.seconds(2.5));
        p.setOnFinished(k -> label.setText(null));
        p.play();
    }
}
