package com.quiz.quizera.dao;

import com.quiz.quizera.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AttemptAnswerDao {
    public void saveAnswer(int attemptId, int questionId, String selectedOption, boolean isCorrect) throws SQLException {
        String sql = "INSERT INTO attempt_answers (attempt_id, question_id, selected_option, is_correct) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.setInt(2, questionId);
            ps.setString(3, selectedOption);
            ps.setBoolean(4, isCorrect);
            ps.executeUpdate();
        }
    }

    public java.util.List<ReviewRow> reviewForAttempt(int attemptId) throws SQLException {
        String sql = "SELECT q.question_text, q.correct_option, aa.selected_option, aa.is_correct FROM attempt_answers aa " +
                "JOIN questions q ON q.id = aa.question_id WHERE aa.attempt_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                java.util.List<ReviewRow> list = new java.util.ArrayList<>();
                while (rs.next()) {
                    ReviewRow r = new ReviewRow();
                    r.question = rs.getString(1);
                    r.correct = rs.getString(2);
                    r.selected = rs.getString(3);
                    r.isCorrect = rs.getBoolean(4);
                    list.add(r);
                }
                return list;
            }
        }
    }

    public static class ReviewRow {
        public String question;
        public String selected;
        public String correct;
        public boolean isCorrect;
    }
}
