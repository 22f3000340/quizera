package com.quiz.quizera.controllers;

import com.quiz.quizera.models.User;
import com.quiz.quizera.utils.DatabaseConnection;
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
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML private Label welcomeLabel;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private ObservableList<User> users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        loadUsers();
    }

    public void initData(String username) {
        welcomeLabel.setText("Welcome, " + username + " (Admin)");
    }

    @FXML
    public void loadUsers() {
        users.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
            userTable.setItems(users);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    protected void handleAddUser() {
        // TODO: Implement add user functionality
        showAlert("Info", "Add user functionality to be implemented");
    }

    @FXML
    protected void handleEditUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Warning", "Please select a user to edit");
            return;
        }
        // TODO: Implement edit user functionality
        showAlert("Info", "Edit user functionality to be implemented");
    }

    @FXML
    protected void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Warning", "Please select a user to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, selectedUser.getId());

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    loadUsers();
                    showAlert("Success", "User deleted successfully");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void handleManageQuizzes() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/admin-manage-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open manage view: " + e.getMessage());
        }
    }

    @FXML
    protected void handleLogout() {
        try {
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
