package desktopapp;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ChallengeSession {
    private String questionImagePath; // Path ke gambar pertanyaan
    private String correctAnswer;     // Jawaban yang benar
    private List<String> answerChoices; // Daftar pilihan jawaban (termasuk yang benar dan pengecoh)

    public ChallengeSession(String questionImagePath, String correctAnswer, List<String> distractors) {
        this.questionImagePath = questionImagePath;
        this.correctAnswer = correctAnswer;
        
        // Gabungkan jawaban benar dan pengecoh, lalu acak
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
