package com.quiz.quizera.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.quiz.quizera.utils.UserSession;

public class StudentController {
    @FXML private Label welcomeLabel;

    public void initData(String username) {
        welcomeLabel.setText("Welcome, " + username + " (Student)");
    }

    @FXML
    protected void handleViewProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/profile-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open profile: " + e.getMessage());
        }
    }

    @FXML
    protected void handleViewCourses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/category-select-view.fxml"));
            Parent root = loader.load();
            CategorySelectController c = loader.getController();
            c.initData(UserSession.requireUserId());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open categories: " + e.getMessage());
        }
    }

    @FXML
    protected void handleViewGrades() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/grades-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open grades: " + e.getMessage());
        }
    }

    @FXML
    protected void handleViewHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/history-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open history: " + e.getMessage());
        }
    }

    @FXML
    protected void handleLogout() {
        try {
            UserSession.clear();
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/login-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
