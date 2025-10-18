package com.quiz.quizera.controllers;

import com.quiz.quizera.dao.CategoryDao;
import com.quiz.quizera.dao.QuestionDao;
import com.quiz.quizera.models.Category;
import com.quiz.quizera.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategorySelectController implements Initializable {
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private Label messageLabel;

    private int currentUserId; // set via initData

    private final CategoryDao categoryDao = new CategoryDao();
    private final QuestionDao questionDao = new QuestionDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        difficultyCombo.setItems(FXCollections.observableArrayList("EASY","MEDIUM","HARD"));
        difficultyCombo.getSelectionModel().selectFirst();
        try {
            List<Category> categories = categoryDao.findAll();
            categoryCombo.setItems(FXCollections.observableArrayList(categories));
            if (!categories.isEmpty()) categoryCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            messageLabel.setText("Failed to load categories: " + e.getMessage());
        }
    }

    public void initData(int userId) { this.currentUserId = userId; }

    @FXML
    public void handleStartQuiz() {
        Category cat = categoryCombo.getValue();
        String diff = difficultyCombo.getValue();
        if (cat == null || diff == null) {
            messageLabel.setText("Please select category and difficulty");
            return;
        }
        // Ensure user session exists
        if (UserSession.get() == null || userId() <= 0) {
            messageLabel.setText("Session expired. Please log in again.");
            return;
        }
        try {
            int available = questionDao.countByCategoryDifficulty(cat.getId(), diff);
            if (available < 10) {
                messageLabel.setText("Not enough questions (" + available + ") for this selection. Please choose another or ask admin to add more.");
                return;
            }
        } catch (Exception ex) {
            messageLabel.setText("Failed to verify question pool: " + ex.getMessage());
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/quiz-view.fxml"));
            Parent root = loader.load();
            QuizController controller = loader.getController();
            controller.startFor(userId(), cat.getId(), diff);
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    private int userId() {
        if (currentUserId > 0) return currentUserId;
        var u = UserSession.get();
        return u != null ? u.getId() : -1;
    }

    @FXML
    public void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/student-view.fxml"));
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }
}
