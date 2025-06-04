package desktopapp;

public class ExerciseSession {
    private String textToType; // Teks yang harus diketik pengguna (cocok dengan teks di gambar)
    private boolean isQuestionBased;
    private String promptQuestion; // Pertanyaan atau kalimat rumpang (misal "Bhinneka _____ Ika")
    private String imagePath; // Path ke file gambar untuk sesi ini

    // Konstruktor untuk sesi mengetik biasa dengan gambar
    public ExerciseSession(String textToType, String imagePath) {
        this.textToType = textToType; // Simpan dengan casing asli
        this.imagePath = imagePath; 
        this.isQuestionBased = false;
        this.promptQuestion = null;
    }

    // Konstruktor untuk sesi berbasis pertanyaan/lengkapi kalimat dengan gambar
    public ExerciseSession(String promptQuestion, String answerToType, String imagePath) {
        this.promptQuestion = promptQuestion;
        this.textToType = answerToType; // Simpan jawaban dengan casing asli
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        String sessionInfo = isQuestionBased ? "Prompt: \"" + promptQuestion + "\", Answer: \"" + textToType + "\"" : "Type: \"" + textToType + "\"";
        return sessionInfo + (imagePath != null ? ", Image: \"" + imagePath + "\"" : "");
    }

    public void setTextToType(String sanitizeTextForTyping) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTextToType'");
    }
}
