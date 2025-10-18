package com.quiz.quizera.controllers;

import com.quiz.quizera.dao.AttemptAnswerDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReviewController {
    @FXML private TableView<Row> table;
    @FXML private TableColumn<Row, String> colQuestion;
    @FXML private TableColumn<Row, String> colSelected;
    @FXML private TableColumn<Row, String> colCorrect;
    @FXML private TableColumn<Row, String> colIsCorrect;

    private final AttemptAnswerDao dao = new AttemptAnswerDao();

    public static class Row {
        public String question;
        public String selected;
        public String correct;
        public String isCorrect;
        public String getQuestion() { return question; }
        public String getSelected() { return selected; }
        public String getCorrect() { return correct; }
        public String getIsCorrect() { return isCorrect; }
    }

    public void loadForAttempt(int attemptId) {
        try {
            var rows = dao.reviewForAttempt(attemptId);
            ObservableList<Row> items = FXCollections.observableArrayList();
            for (var r : rows) {
                Row row = new Row();
                row.question = r.question;
                row.selected = String.valueOf(r.selected);
                row.correct = String.valueOf(r.correct);
                row.isCorrect = r.isCorrect ? "Yes" : "No";
                items.add(row);
            }
            colQuestion.setCellValueFactory(new PropertyValueFactory<>("question"));
            colSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
            colCorrect.setCellValueFactory(new PropertyValueFactory<>("correct"));
            colIsCorrect.setCellValueFactory(new PropertyValueFactory<>("isCorrect"));
            table.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
