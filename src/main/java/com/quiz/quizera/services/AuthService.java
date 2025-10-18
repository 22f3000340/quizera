package com.quiz.quizera.services;

import com.quiz.quizera.models.User;
import com.quiz.quizera.utils.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    public Optional<User> authenticate(String username, String password) {
        String sql = "SELECT id, username, email, password, role FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String stored = rs.getString("password");
                boolean match;
                if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
                    match = BCrypt.checkpw(password, stored);
                } else {
                    // Fallback for legacy plain-text records; also migrate to bcrypt on successful login
                    match = password.equals(stored);
                    if (match) {
                        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
                        try (PreparedStatement up = conn.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
                            up.setString(1, newHash);
                            up.setInt(2, rs.getInt("id"));
                            up.executeUpdate();
                        }
                    }
                }
                if (!match) return Optional.empty();

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                // Do not set password field with hash
                return Optional.of(u);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean register(String username, String email, String password, String role, StringBuilder errorOut) {
        errorOut.setLength(0);
        if (username == null || username.isBlank() || email == null || email.isBlank() || password == null || password.length() < 6) {
            errorOut.append("Invalid input; ensure username/email present and password length >= 6");
            return false;
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setString(4, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("Duplicate")) {
                errorOut.append("Username or email already exists");
            } else {
                errorOut.append("Database error: ").append(msg);
            }
            return false;
        }
    }
}
