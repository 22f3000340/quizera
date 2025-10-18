package com.quiz.quizera.services;

import com.quiz.quizera.dao.AttemptAnswerDao;
import com.quiz.quizera.dao.QuestionDao;
import com.quiz.quizera.dao.QuizAttemptDao;
import com.quiz.quizera.models.Question;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizService {
    private final QuestionDao questionDao = new QuestionDao();
    private final QuizAttemptDao attemptDao = new QuizAttemptDao();
    private final AttemptAnswerDao answerDao = new AttemptAnswerDao();

    public static class QuizSession {
        public final int attemptId;
        public final List<Question> questions;
        public final Map<Integer, String> answers = new HashMap<>(); // questionId -> selected option
        public QuizSession(int attemptId, List<Question> questions) {
            this.attemptId = attemptId; this.questions = questions;
        }
    }

    public QuizSession startQuiz(int userId, int categoryId, String difficulty) throws SQLException, IllegalStateException {
        int available = questionDao.countByCategoryDifficulty(categoryId, difficulty);
        if (available < 10) {
            throw new IllegalStateException("Not enough questions (" + available + ") for the selected category/difficulty. Need at least 10.");
        }
        List<Question> picked = questionDao.pickRandomByCategoryDifficulty(categoryId, difficulty, 10);
        int attemptId = attemptDao.createAttempt(userId, categoryId, difficulty, 10);
        return new QuizSession(attemptId, picked);
    }

    public int submitQuiz(QuizSession session) throws SQLException {
        int score = 0;
        for (Question q : session.questions) {
            String selected = session.answers.getOrDefault(q.getId(), null);
            boolean correct = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());
            if (correct) score++;
            answerDao.saveAnswer(session.attemptId, q.getId(), selected, correct);
        }
        attemptDao.completeAttempt(session.attemptId, score);
        return score;
    }
}
