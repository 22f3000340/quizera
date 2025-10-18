package com.quiz.quizera.controllers;

import com.quiz.quizera.models.User;
import com.quiz.quizera.utils.DatabaseConnection;
import com.quiz.quizera.services.AuthService;
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
import javafx.scene.layout.GridPane;
import com.quiz.quizera.utils.UserSession;
// removed unused import of java.sql.Statement

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
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Create a new user");

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6)");
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("STUDENT","ADMIN"));
        roleBox.getSelectionModel().select("STUDENT");

        grid.addRow(0, new Label("Username"), usernameField);
        grid.addRow(1, new Label("Email"), emailField);
        grid.addRow(2, new Label("Password"), passwordField);
        grid.addRow(3, new Label("Role"), roleBox);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                User u = new User();
                u.setUsername(usernameField.getText());
                u.setEmail(emailField.getText());
                u.setRole(roleBox.getValue());
                // Temporarily store password in email field? No, we'll return null and handle directly.
                return u;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            StringBuilder error = new StringBuilder();
            String pwd = passwordField.getText();
            if (pwd == null || pwd.length() < 6) {
                showAlert("Validation", "Password must be at least 6 characters");
                return;
            }
            boolean ok = new AuthService().register(u.getUsername(), u.getEmail(), pwd, u.getRole(), error);
            if (ok) {
                loadUsers();
                showAlert("Success", "User created");
            } else {
                showAlert("Error", error.toString());
            }
        });
    }

    @FXML
    protected void handleEditUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Warning", "Please select a user to edit");
            return;
        }
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Modify user details");

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField(selectedUser.getUsername());
        TextField emailField = new TextField(selectedUser.getEmail());
        ComboBox<String> roleBox = new ComboBox<>(FXCollections.observableArrayList("STUDENT","ADMIN"));
        roleBox.getSelectionModel().select(selectedUser.getRole());
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Leave blank to keep password");

        grid.addRow(0, new Label("Username"), usernameField);
        grid.addRow(1, new Label("Email"), emailField);
        grid.addRow(2, new Label("Role"), roleBox);
        grid.addRow(3, new Label("New Password"), newPasswordField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                User u = new User();
                u.setId(selectedUser.getId());
                u.setUsername(usernameField.getText());
                u.setEmail(emailField.getText());
                u.setRole(roleBox.getValue());
                return u;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(u -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Update username/email/role first
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET username=?, email=?, role=? WHERE id=?")) {
                    ps.setString(1, u.getUsername());
                    ps.setString(2, u.getEmail());
                    ps.setString(3, u.getRole());
                    ps.setInt(4, u.getId());
                    ps.executeUpdate();
                }
                // If password provided, hash and update
                String np = newPasswordField.getText();
                if (np != null && !np.isBlank()) {
                    if (np.length() < 6) {
                        showAlert("Validation", "Password must be at least 6 characters");
                        return;
                    }
                    String hash = org.mindrot.jbcrypt.BCrypt.hashpw(np, org.mindrot.jbcrypt.BCrypt.gensalt());
                    try (PreparedStatement up = conn.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
                        up.setString(1, hash);
                        up.setInt(2, u.getId());
                        up.executeUpdate();
                    }
                }
                loadUsers();
                showAlert("Success", "User updated");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update user: " + e.getMessage());
            }
        });
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

    @FXML
    protected void handleManageQuizzes() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/quiz/quizera/admin-manage-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open quiz management: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    protected void handleViewAttempts() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) { showAlert("Warning", "Please select a user"); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/history-view.fxml"));
            Parent root = loader.load();
            // HistoryController uses UserSession for current user; temporarily set to selected user to view
            User original = UserSession.get();
            try {
                UserSession.set(selectedUser);
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } finally {
                UserSession.set(original);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open attempts: " + e.getMessage());
        }
    }

    @FXML
    protected void handleToggleRole() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) { showAlert("Warning", "Please select a user"); return; }
        String newRole = "ADMIN".equals(selectedUser.getRole()) ? "STUDENT" : "ADMIN";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET role=? WHERE id=?")) {
            ps.setString(1, newRole);
            ps.setInt(2, selectedUser.getId());
            ps.executeUpdate();
            loadUsers();
            showAlert("Success", "Role changed to " + newRole);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update role: " + e.getMessage());
        }
    }
}
