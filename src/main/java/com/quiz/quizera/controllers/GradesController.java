package com.quiz.quizera.controllers;

import com.quiz.quizera.dao.CategoryDao;
import com.quiz.quizera.dao.QuizAttemptDao;
import com.quiz.quizera.models.Category;
import com.quiz.quizera.models.QuizAttempt;
import com.quiz.quizera.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class GradesController {
    @FXML private TableView<QuizAttempt> table;
    @FXML private TableColumn<QuizAttempt, String> colCategory;
    @FXML private TableColumn<QuizAttempt, String> colDifficulty;
    @FXML private TableColumn<QuizAttempt, Integer> colScore;
    @FXML private TableColumn<QuizAttempt, String> colDate;

    private final QuizAttemptDao attemptDao = new QuizAttemptDao();
    private final CategoryDao categoryDao = new CategoryDao();

    @FXML public void initialize() {
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getCompletedAt() != null ? data.getValue().getCompletedAt() : data.getValue().getStartedAt())
        ));
        try {
            int uid = UserSession.requireUserId();
            var attempts = attemptDao.findByUser(uid);
            Map<Integer, String> catName = new HashMap<>();
            for (Category c : categoryDao.findAll()) catName.put(c.getId(), c.getName());
            colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    catName.getOrDefault(data.getValue().getCategoryId(), String.valueOf(data.getValue().getCategoryId()))
            ));
            table.setItems(FXCollections.observableArrayList(attempts));
        } catch (Exception e) {
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
}
