package com.quiz.quizera.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ScoreController {
    @FXML private Label scoreLabel;
    @FXML private Button reviewButton;
    private int attemptId;

    public void showScore(int score, int total) {
        scoreLabel.setText("Your Score: " + score + "/" + total);
    }

    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }

    @FXML public void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/category-select-view.fxml"));
            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleReview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/review-view.fxml"));
            Parent root = loader.load();
            ReviewController rc = loader.getController();
            rc.loadForAttempt(attemptId);
            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
