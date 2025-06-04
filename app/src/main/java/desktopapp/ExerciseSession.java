package desktopapp;

public class ExerciseSession {
    private String textToType; // Teks yang harus diketik pengguna
    private boolean isQuestionBased;
    private String promptQuestion; // Pertanyaan atau kalimat rumpang (misal "Bhinneka _____ Ika")
    // Jawaban untuk promptQuestion akan disimpan di textToType

    // Konstruktor untuk sesi mengetik biasa
    public ExerciseSession(String textToType) {
        this.textToType = textToType;
        this.isQuestionBased = false;
        this.promptQuestion = null;
    }

    // Konstruktor untuk sesi berbasis pertanyaan/lengkapi kalimat
    public ExerciseSession(String promptQuestion, String answerToType) {
        this.promptQuestion = promptQuestion;
        this.textToType = answerToType; // Jawaban yang harus diketik pengguna
        this.isQuestionBased = true;
    }

    public String getTextToType() {
        return textToType;
    }

    public boolean isQuestionBased() {
        return isQuestionBased;
    }

    public String getPromptQuestion() {
        return promptQuestion;
    }

    @Override
    public String toString() {
        if (isQuestionBased) {
            return "Prompt: \"" + promptQuestion + "\", Answer: \"" + textToType + "\"";
        } else {
            return "Type: \"" + textToType + "\"";
        }
    }
}
