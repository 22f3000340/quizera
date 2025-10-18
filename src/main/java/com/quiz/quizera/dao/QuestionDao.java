package com.quiz.quizera.dao;

import com.quiz.quizera.models.Question;
import com.quiz.quizera.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    public List<Question> pickRandomByCategoryDifficulty(int categoryId, String difficulty, int limit) throws SQLException {
        String sql = "SELECT id, category_id, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option " +
                "FROM questions WHERE category_id = ? AND difficulty = ? ORDER BY RAND() LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, difficulty);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Question> list = new ArrayList<>();
                while (rs.next()) {
                    Question q = map(rs);
                    list.add(q);
                }
                return list;
            }
        }
    }

    public int countByCategoryDifficulty(int categoryId, String difficulty) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE category_id = ? AND difficulty = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, difficulty);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public int create(Question q) throws SQLException {
        String sql = "INSERT INTO questions (category_id, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, q.getCategoryId());
            ps.setString(2, q.getDifficulty());
            ps.setString(3, q.getQuestionText());
            ps.setString(4, q.getOptionA());
            ps.setString(5, q.getOptionB());
            ps.setString(6, q.getOptionC());
            ps.setString(7, q.getOptionD());
            ps.setString(8, q.getCorrectOption());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                return -1;
            }
        }
    }

    public void update(Question q) throws SQLException {
        String sql = "UPDATE questions SET category_id=?, difficulty=?, question_text=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_option=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getCategoryId());
            ps.setString(2, q.getDifficulty());
            ps.setString(3, q.getQuestionText());
            ps.setString(4, q.getOptionA());
            ps.setString(5, q.getOptionB());
            ps.setString(6, q.getOptionC());
            ps.setString(7, q.getOptionD());
            ps.setString(8, q.getCorrectOption());
            ps.setInt(9, q.getId());
            ps.executeUpdate();
        }
    }

    private Question map(ResultSet rs) throws SQLException {
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
        return q;
    }
}
