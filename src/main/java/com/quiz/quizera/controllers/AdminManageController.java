package com.quiz.quizera.controllers;

import com.quiz.quizera.dao.CategoryDao;
import com.quiz.quizera.dao.QuestionDao;
import com.quiz.quizera.models.Category;
import com.quiz.quizera.models.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static com.quiz.quizera.utils.DatabaseConnection.getConnection;

public class AdminManageController implements Initializable {
    @FXML private TextField categoryNameField;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> catIdCol;
    @FXML private TableColumn<Category, String> catNameCol;

    @FXML private ComboBox<Category> questionCategoryCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private TextArea questionTextArea;
    @FXML private TextField optA, optB, optC, optD;
    @FXML private ComboBox<String> correctCombo;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, Integer> qIdCol;
    @FXML private TableColumn<Question, String> qCatCol;
    @FXML private TableColumn<Question, String> qDiffCol;
    @FXML private TableColumn<Question, String> qTextCol;
    @FXML private Label messageLabel;

    private final CategoryDao categoryDao = new CategoryDao();
    private final QuestionDao questionDao = new QuestionDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        catIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        catNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        qDiffCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        qTextCol.setCellValueFactory(new PropertyValueFactory<>("questionText"));

        correctCombo.setItems(FXCollections.observableArrayList("A","B","C","D"));
        difficultyCombo.setItems(FXCollections.observableArrayList("EASY","MEDIUM","HARD"));
        difficultyCombo.getSelectionModel().selectFirst();
        correctCombo.getSelectionModel().selectFirst();
        refreshCategories();
        refreshQuestions();

        // Populate question edit form when a row is selected
        questionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                populateQuestionForm(sel);
            }
        });
    }

    private void refreshCategories() {
        try {
            List<Category> list = categoryDao.findAll();
            ObservableList<Category> items = FXCollections.observableArrayList(list);
            categoryTable.setItems(items);
            questionCategoryCombo.setItems(items);
            if (!items.isEmpty()) questionCategoryCombo.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            messageLabel.setText("Failed to load categories: " + e.getMessage());
        }
    }

    private void refreshQuestions() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT q.id, q.category_id, c.name AS category, q.difficulty, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option FROM questions q JOIN categories c ON q.category_id=c.id ORDER BY q.id DESC");
             java.sql.ResultSet rs = ps.executeQuery()) {
            ObservableList<Question> items = FXCollections.observableArrayList();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setCategoryId(rs.getInt("category_id"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectOption(rs.getString("correct_option"));
                items.add(q);
            }
            questionTable.setItems(items);
            // Map category name via custom cell factory
            qCatCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(categoryName(data.getValue().getCategoryId())));
        } catch (SQLException e) {
            messageLabel.setText("Failed to load questions: " + e.getMessage());
        }
    }

    private String categoryName(int id) {
        try {
            for (Category c : categoryDao.findAll()) if (c.getId() == id) return c.getName();
        } catch (SQLException ignored) {}
        return String.valueOf(id);
    }

    @FXML public void handleAddCategory() {
        String name = categoryNameField.getText();
        if (name == null || name.isBlank()) { messageLabel.setText("Enter category name"); return; }
        try {
            categoryDao.create(name.trim());
            categoryNameField.clear();
            refreshCategories();
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML public void handleDeleteCategory() {
        Category sel = categoryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a category"); return; }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM categories WHERE id=?")) {
            ps.setInt(1, sel.getId());
            ps.executeUpdate();
            refreshCategories();
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML public void handleAddQuestion() {
        Category cat = questionCategoryCombo.getValue();
        String diff = difficultyCombo.getValue();
        if (cat == null || diff == null) { messageLabel.setText("Select category and difficulty"); return; }
        Question q = new Question();
        q.setCategoryId(cat.getId());
        q.setDifficulty(diff);
        q.setQuestionText(questionTextArea.getText());
        q.setOptionA(optA.getText());
        q.setOptionB(optB.getText());
        q.setOptionC(optC.getText());
        q.setOptionD(optD.getText());
        q.setCorrectOption(correctCombo.getValue());
        try {
            questionDao.create(q);
            clearQuestionForm();
            refreshQuestions();
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    private void populateQuestionForm(Question q) {
        // Category
        if (questionCategoryCombo.getItems() != null) {
            for (Category c : questionCategoryCombo.getItems()) {
                if (c.getId() == q.getCategoryId()) {
                    questionCategoryCombo.getSelectionModel().select(c);
                    break;
                }
            }
        }
        // Difficulty
        if (q.getDifficulty() != null) {
            difficultyCombo.getSelectionModel().select(q.getDifficulty());
        }
        // Text/options
        questionTextArea.setText(q.getQuestionText());
        optA.setText(q.getOptionA());
        optB.setText(q.getOptionB());
        optC.setText(q.getOptionC());
        optD.setText(q.getOptionD());
        if (q.getCorrectOption() != null) {
            correctCombo.getSelectionModel().select(q.getCorrectOption());
        }
    }

    private void clearQuestionForm() {
        questionTextArea.clear(); optA.clear(); optB.clear(); optC.clear(); optD.clear();
        correctCombo.getSelectionModel().selectFirst();
    }

    @FXML public void handleDeleteQuestion() {
        Question sel = questionTable.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a question"); return; }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM questions WHERE id=?")) {
            ps.setInt(1, sel.getId());
            ps.executeUpdate();
            refreshQuestions();
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML public void handleRenameCategory() {
        Category sel = categoryTable.getSelectionModel().getSelectedItem();
        String newName = categoryNameField.getText();
        if (sel == null) { messageLabel.setText("Select a category to rename"); return; }
        if (newName == null || newName.isBlank()) { messageLabel.setText("Enter new name for category"); return; }
        try {
            categoryDao.rename(sel.getId(), newName.trim());
            categoryNameField.clear();
            refreshCategories();
            messageLabel.setText("Category renamed");
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML public void handleUpdateQuestion() {
        Question sel = questionTable.getSelectionModel().getSelectedItem();
        if (sel == null) { messageLabel.setText("Select a question to update"); return; }
        Category cat = questionCategoryCombo.getValue();
        String diff = difficultyCombo.getValue();
        String text = questionTextArea.getText();
        String a = optA.getText(), b = optB.getText(), c = optC.getText(), d = optD.getText();
        String corr = correctCombo.getValue();
        if (cat == null || diff == null || text == null || text.isBlank() ||
                a == null || a.isBlank() || b == null || b.isBlank() || c == null || c.isBlank() || d == null || d.isBlank() ||
                corr == null) {
            messageLabel.setText("Fill all fields before updating");
            return;
        }
        try {
            Question q = new Question();
            q.setId(sel.getId());
            q.setCategoryId(cat.getId());
            q.setDifficulty(diff);
            q.setQuestionText(text);
            q.setOptionA(a); q.setOptionB(b); q.setOptionC(c); q.setOptionD(d);
            q.setCorrectOption(corr);
            questionDao.update(q);
            refreshQuestions();
            messageLabel.setText("Question updated");
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML public void handleBack() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/admin-view.fxml"));
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
