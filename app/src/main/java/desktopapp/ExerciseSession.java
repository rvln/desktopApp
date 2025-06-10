package desktopapp;

public class ExerciseSession {
    private String textToType;
    private boolean isQuestionBased;
    private String promptQuestion;
    private String imagePath;

    public ExerciseSession(String textToType, String imagePath) {
        this.textToType = textToType;
        this.imagePath = imagePath; 
        this.isQuestionBased = false;
        this.promptQuestion = null;
    }

    public ExerciseSession(String promptQuestion, String answerToType, String imagePath) {
        this.promptQuestion = promptQuestion;
        this.textToType = answerToType;
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
        throw new UnsupportedOperationException("Unimplemented method 'setTextToType'");
    }
}
