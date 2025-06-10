package desktopapp;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ChallengeSession {
    private String questionImagePath;
    private String correctAnswer;
    private List<String> answerChoices;

    public ChallengeSession(String questionImagePath, String correctAnswer, List<String> distractors) {
        this.questionImagePath = questionImagePath;
        this.correctAnswer = correctAnswer;
        
        this.answerChoices = new ArrayList<>(distractors);
        this.answerChoices.add(correctAnswer);
        Collections.shuffle(this.answerChoices);
    }

    public String getQuestionImagePath() {
        return questionImagePath;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAnswerChoices() {
        return answerChoices;
    }
}
