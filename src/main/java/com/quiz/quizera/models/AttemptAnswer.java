package com.quiz.quizera.models;

public class AttemptAnswer {
    private int id;
    private int attemptId;
    private int questionId;
    private String selectedOption; // A, B, C, D
    private boolean correct;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
