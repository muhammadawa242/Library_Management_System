package com.example.smester_project_lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class app extends Application {
    @Override
    public void start(Stage stage) {
        try{
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1370, 700);
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}