package com.quiz.quizera.controllers;

import com.quiz.quizera.dao.CategoryDao;
import com.quiz.quizera.dao.QuizAttemptDao;
import com.quiz.quizera.models.Category;
import com.quiz.quizera.models.QuizAttempt;
import com.quiz.quizera.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    @FXML private TableView<QuizAttempt> table;
    @FXML private TableColumn<QuizAttempt, String> colCategory;
    @FXML private TableColumn<QuizAttempt, String> colDifficulty;
    @FXML private TableColumn<QuizAttempt, Integer> colScore;
    @FXML private TableColumn<QuizAttempt, String> colStarted;
    @FXML private TableColumn<QuizAttempt, String> colCompleted;

    private final QuizAttemptDao attemptDao = new QuizAttemptDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        // For started/completed, show as strings via custom cell value factory
        colStarted.setCellValueFactory(data -> javafx.beans.property.SimpleStringProperty.stringExpression(
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getStartedAt()))
        ));
        colCompleted.setCellValueFactory(data -> javafx.beans.property.SimpleStringProperty.stringExpression(
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getCompletedAt()))
        ));
        loadData();
    }

    private void loadData() {
        try {
            int userId = UserSession.requireUserId();
            List<QuizAttempt> attempts = attemptDao.findByUser(userId);
            Map<Integer, String> catName = new HashMap<>();
            for (Category c : categoryDao.findAll()) catName.put(c.getId(), c.getName());
            // Category name mapping by custom factory
            colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    catName.getOrDefault(data.getValue().getCategoryId(), String.valueOf(data.getValue().getCategoryId()))
            ));
            ObservableList<QuizAttempt> items = FXCollections.observableArrayList(attempts);
            table.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/student-view.fxml"));
            Stage stage = (Stage) table.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleReviewSelected() {
        try {
            var selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/review-view.fxml"));
            Parent root = loader.load();
            ReviewController rc = loader.getController();
            rc.loadForAttempt(selected.getId());
            Stage stage = (Stage) table.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
