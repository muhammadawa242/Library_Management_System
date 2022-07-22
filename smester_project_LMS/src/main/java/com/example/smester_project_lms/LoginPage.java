package com.example.smester_project_lms;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginPage {
    public static int user_id;
    @FXML TextField newUser;@FXML TextField newPass;
    @FXML TextField confirmPass;@FXML TextField user;
    @FXML TextField pass;@FXML Label warningLabel;

    @FXML
    void login(ActionEvent e) {
        String username = user.getText();
        String password = pass.getText();

        if(username.equals(DatabaseHandler.ADMIN) && password.equals(DatabaseHandler.ADM_PASS))
            new Functions().switchScenes(e, "adminAccount.fxml");
        else
        {
            String query = String.format("SELECT * FROM users WHERE name == \"%s\";", username);
            ArrayList<String[]> data = DatabaseHandler.execute(query, "users");

            if (!data.isEmpty()) {
                user_id = Integer.parseInt(data.get(0)[0]);         // Cookies

                String hash = data.get(0)[2];
                if(new PasswordAuthentication().authenticate(password.toCharArray(), hash))
                    new Functions().switchScenes(e, "userAccount.fxml");
                else
                    Functions.warningLabel(warningLabel, "Wrong Password!", "RED");
            }
            else
                Functions.warningLabel(warningLabel, "Wrong username!", "RED");
        }
    }

    @FXML
    void createAccountRequest(){
        String user = newUser.getText();
        String pass = newPass.getText();
        String confPass = confirmPass.getText();

        if(!pass.equals(confPass)){
            Functions.warningLabel(warningLabel, "Password didn't match!", "RED");
        }
        else {
            // Create hash of password
            pass = new PasswordAuthentication().hash(pass.toCharArray());
            String stmt = String.format("INSERT INTO users_pending(name, password) VALUES(\"%s\", \"%s\");", user, pass);
            DatabaseHandler.execute(stmt, "users_pending");

            Functions.warningLabel(warningLabel, "Your account will be created within\n\t24 hours approximately!", "white");

            // Clear inputs
            newUser.clear();
            newPass.clear();
            confirmPass.clear();
        }
    }
}