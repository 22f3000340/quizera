package com.quiz.quizera.controllers;

import com.quiz.quizera.services.AuthService;
import com.quiz.quizera.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;
import com.quiz.quizera.models.User;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    private final AuthService authService = new AuthService();

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        try {
            Optional<User> userOpt = authService.authenticate(username, password);
            if (userOpt.isEmpty()) {
                messageLabel.setText("Invalid username or password");
                return;
            }
            User user = userOpt.get();
        UserSession.set(user);
        String fxmlPath = user.getRole().equals("ADMIN")
                    ? "/com/quiz/quizera/admin-view.fxml"
                    : "/com/quiz/quizera/student-view.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if ("ADMIN".equals(user.getRole())) {
                AdminController controller = loader.getController();
                controller.initData(user.getUsername());
            } else {
                StudentController controller = loader.getController();
                controller.initData(user.getUsername());
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void switchToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/register-view.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
