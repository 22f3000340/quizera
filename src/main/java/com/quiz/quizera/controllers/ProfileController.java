package com.quiz.quizera.controllers;

import com.quiz.quizera.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProfileController {
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;

    @FXML public void initialize() {
        var user = UserSession.get();
        if (user != null) {
            usernameLabel.setText("Username: " + user.getUsername());
            emailLabel.setText("Email: " + user.getEmail());
            roleLabel.setText("Role: " + user.getRole());
        }
    }

    @FXML public void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/student-view.fxml"));
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
