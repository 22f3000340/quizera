package com.quiz.quizera.dao;

import com.quiz.quizera.models.QuizAttempt;
import com.quiz.quizera.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptDao {
    public int createAttempt(int userId, int categoryId, String difficulty, int total) throws SQLException {
        String sql = "INSERT INTO quiz_attempts (user_id, category_id, difficulty, total) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setString(3, difficulty);
            ps.setInt(4, total);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void completeAttempt(int attemptId, int score) throws SQLException {
        String sql = "UPDATE quiz_attempts SET score = ?, completed_at = NOW() WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setInt(2, attemptId);
            ps.executeUpdate();
        }
    }

    public List<QuizAttempt> findByUser(int userId) throws SQLException {
        String sql = "SELECT id, user_id, category_id, difficulty, started_at, completed_at, score, total FROM quiz_attempts WHERE user_id = ? ORDER BY started_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<QuizAttempt> list = new ArrayList<>();
                while (rs.next()) {
                    QuizAttempt qa = new QuizAttempt();
                    qa.setId(rs.getInt("id"));
                    qa.setUserId(rs.getInt("user_id"));
                    qa.setCategoryId(rs.getInt("category_id"));
                    qa.setDifficulty(rs.getString("difficulty"));
                    qa.setScore(rs.getInt("score"));
                    qa.setTotal(rs.getInt("total"));
                    Timestamp s = rs.getTimestamp("started_at");
                    Timestamp c = rs.getTimestamp("completed_at");
                    if (s != null) qa.setStartedAt(s.toLocalDateTime());
                    if (c != null) qa.setCompletedAt(c.toLocalDateTime());
                    list.add(qa);
                }
                return list;
            }
        }
    }
}
