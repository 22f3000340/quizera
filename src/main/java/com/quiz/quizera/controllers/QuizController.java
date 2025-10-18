package com.quiz.quizera.controllers;

import com.quiz.quizera.models.Question;
import com.quiz.quizera.services.QuizService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class QuizController {
    @FXML private Label headerLabel;
    @FXML private Label timerLabel;
    @FXML private Label questionLabel;
    @FXML private RadioButton optionA;
    @FXML private RadioButton optionB;
    @FXML private RadioButton optionC;
    @FXML private RadioButton optionD;
    private ToggleGroup optionsGroup = new ToggleGroup();

    private final QuizService quizService = new QuizService();
    private QuizService.QuizSession session;
    private List<Question> questions;
    private int index = 0;
    private int secondsLeft = 60 * 10; // 10 minutes
    private Timeline timeline;
    private int userId;
    private int categoryId;
    private String difficulty;

    public void startFor(int userId, int categoryId, String difficulty) throws Exception {
        this.userId = userId;
        this.categoryId = categoryId;
        this.difficulty = difficulty;
        session = quizService.startQuiz(userId, categoryId, difficulty);
        questions = session.questions;
        headerLabel.setText("Quiz - " + difficulty + " | Question 1/" + questions.size());
        optionA.setToggleGroup(optionsGroup);
        optionB.setToggleGroup(optionsGroup);
        optionC.setToggleGroup(optionsGroup);
        optionD.setToggleGroup(optionsGroup);
        startTimer();
        render();
    }

    private void startTimer() {
        timerLabel.setText(formatTime(secondsLeft));
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            timerLabel.setText(formatTime(secondsLeft));
            if (secondsLeft <= 0) {
                handleSubmit();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String formatTime(int seconds) {
        int m = seconds / 60; int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void saveSelected() {
        RadioButton sel = (RadioButton) optionsGroup.getSelectedToggle();
        if (sel != null) {
            String letter = radioToLetter(sel);
            session.answers.put(current().getId(), letter);
        }
    }

    private String radioToLetter(RadioButton rb) {
        if (rb == optionA) return "A";
        if (rb == optionB) return "B";
        if (rb == optionC) return "C";
        return "D";
    }

    private Question current() { return questions.get(index); }

    private void render() {
        Question q = current();
        questionLabel.setText(q.getQuestionText());
        optionA.setText(q.getOptionA());
        optionB.setText(q.getOptionB());
        optionC.setText(q.getOptionC());
        optionD.setText(q.getOptionD());
        optionsGroup.selectToggle(null);
        String prev = session.answers.get(q.getId());
        if (prev != null) {
            switch (prev) {
                case "A": optionsGroup.selectToggle(optionA); break;
                case "B": optionsGroup.selectToggle(optionB); break;
                case "C": optionsGroup.selectToggle(optionC); break;
                case "D": optionsGroup.selectToggle(optionD); break;
            }
        }
        headerLabel.setText("Quiz - " + difficulty + " | Question " + (index+1) + "/" + questions.size());
    }

    @FXML public void handleNext() {
        saveSelected();
        if (index < questions.size()-1) {
            index++;
            render();
        }
    }

    @FXML public void handlePrev() {
        saveSelected();
        if (index > 0) {
            index--;
            render();
        }
    }

    @FXML public void handleSubmit() {
        try {
            if (timeline != null) timeline.stop();
            saveSelected();
            int score = quizService.submitQuiz(session);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/quiz/quizera/score-view.fxml"));
            Parent root = loader.load();
            ScoreController sc = loader.getController();
            sc.showScore(score, questions.size());
            sc.setAttemptId(session.attemptId);
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
