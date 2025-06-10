package desktopapp;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ChallengeController implements Initializable {

    @FXML
    private StackPane rootPage;
    @FXML
    private HBox countdown;
    @FXML
    private Label timerLabel;
    @FXML
    private VBox keyboardHuruf;
    @FXML
    private AnchorPane leftHand, rightHand;
    @FXML
    private Circle keyLThumb, keyLIndex, keyLMiddle, keyLRing, keyLPinky;
    @FXML
    private Circle keyRThumb, keyRindex, keyRMiddle, keyRRing, keyRPinky;

    @FXML private TextFlow key_Q, key_W, key_E, key_R, key_T, key_Y, key_U, key_I, key_O, key_P;
    @FXML private TextFlow key_A, key_S, key_D, key_F, key_G, key_H, key_J, key_K, key_L, key_SEMICOLON;
    @FXML private TextFlow key_Z, key_X, key_C, key_V, key_B, key_N, key_M, key_COMMA, key_PERIOD, key_SLASH;
    @FXML private TextFlow key_SPACE, key_LSHIFT, key_RSHIFT;
    private Map<String, TextFlow> virtualKeyboardNodeMap;
    private Map<String, Circle> fingerNodeMap;
    private Map<Character, String> charToFingerFxIdMap;
    @FXML
    private HBox petunjukJawaban;
    @FXML
    private TextField answerTextField;
    @FXML
    private ImageView panduanPertanyaan;
    @FXML
    private AnchorPane checkAnswer;
    @FXML
    private ImageView checkAnswerIcon;

    @FXML
    private Pane disableLayer;
    private AnchorPane completePopupNode;
    private Text durasiTextComplete, incorrectCountText;
    private HBox btnLanjutkanComplete, btnCobaLagiComplete, btnKembaliComplete;

    private List<ChallengeSession> challengeSessionsList;
    private String currentTheme;
    private int currentSessionIndex = 0;
    private int currentTypedCharIndex = 0;
    private int incorrectAttempts = 0;

    private Timeline timerTimeline;
    private long startTimeMillis;

    private Image correctImage;
    private Image wrongImage;

    private String lastHighlightedKeyString = null;
    private Circle lastHighlightedFingerCircle = null;

    private final String CURRENT_CHAR_COLOR = "#1C5DF4";
    private final String PROMPT_TEXT_COLOR = "#4a4a4a";
    private final Font TEXT_FONT = Font.font("Rubik Bold", 36);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (checkAnswer != null) {
            checkAnswer.setVisible(false);
            System.err.println("Hasil Jawaban tidak null");
        }
        if (answerTextField != null) {
            answerTextField.setOnAction(event -> checkAnswer());
            answerTextField.setFont(TEXT_FONT);
            answerTextField.setPromptText("Ketik jawaban anda disini...");
            answerTextField.setStyle("-fx-text-fill: white;");
            answerTextField.setOnKeyTyped(event -> {
                if (!event.getCharacter().isEmpty()) {
                    AudioManager.playTypingSound();
                }
            });
        }
        
        // Pra-muat gambar feedback
        try {
             correctImage = new Image(getClass().getResourceAsStream("/desain/icon/correct-answer.png"));
             wrongImage = new Image(getClass().getResourceAsStream("/desain/icon/wrong-answer.png"));
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar correct/wrong answer: " + e.getMessage());
        }
    }
    
    public void initGame(String theme) {
        // Logika untuk mengatur tema
        currentTheme = theme;
        
        loadChallengeSessions();
        System.err.println("ChallengeSessionsList loaded with " + challengeSessionsList.size() + " sessions.");
        if (challengeSessionsList.isEmpty()) {
            System.err.println("Error: ChallengeSessionsList is empty. Tidak ada sesi tantangan yang dimuat.");
            return;
        }
        startNextSession(); 

        if (rootPage != null) {
            Platform.runLater(() -> rootPage.requestFocus());
        }
    }

    private void loadChallengeSessions() {
        challengeSessionsList = new ArrayList<>();
        String basePath = "/desain/materi_images/tantangan/";
        challengeSessionsList.add(new ChallengeSession(basePath + "q_lambang_negara.png", "garuda", Arrays.asList("pancasila", "elang")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_ibukota_sulawesi_utara.png", "manado", Arrays.asList("surabaya", "bandung")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_bendera_indonesia.png", "merah", Arrays.asList("pakaian", "hiasan")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_pernapasan_ikan.png", "insang", Arrays.asList("paru-paru", "kulit")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_planet_terdekat_matahari.png", "merkurius", Arrays.asList("venus", "bumi")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_presiden_pertama.png", "soekarno", Arrays.asList("soeharto", "habibie")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_pahlawan_pendidikan.png", "kartini", Arrays.asList("cut nyak dien", "soekarno")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_pencipta_indonesia_raya.png", "supratman", Arrays.asList("simanjuntak", "kusbini")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_kepala_keluarga.png", "ayah", Arrays.asList("ibu", "kakak")));
        challengeSessionsList.add(new ChallengeSession(basePath + "q_bulan_kemerdekaan.png", "agustus", Arrays.asList("juli", "september")));
    }

    private void startNextSession() {
        if (currentSessionIndex < challengeSessionsList.size()) {
            ChallengeSession currentSession = challengeSessionsList.get(currentSessionIndex);
            
            loadImage(currentSession.getQuestionImagePath());
            populateAnswerChoices(currentSession.getAnswerChoices());
            
            answerTextField.clear();
            answerTextField.requestFocus();

            if (currentSessionIndex == 0) { 
                startTimer();
            }
        } else {
            System.out.println("Tantangan Selesai!");
            stopTimer();
            showCompletePopup();
        }
    }
    
    private void loadImage(String imagePath) {
        if (panduanPertanyaan != null && imagePath != null) {
            try {
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream != null) {
                    panduanPertanyaan.setImage(new Image(imageStream));
                } else {
                    System.err.println("Error: Gambar tidak ditemukan di path: " + imagePath);
                    panduanPertanyaan.setImage(null);
                }
            } catch (Exception e) {
                System.err.println("Error: Gagal memuat gambar " + imagePath);
                panduanPertanyaan.setImage(null);
            }
        }
    }
    
    private void populateAnswerChoices(List<String> choices) {
        if (petunjukJawaban == null || petunjukJawaban.getChildren().size() < 3) {
            System.err.println("Error: HBox petunjukJawaban atau children-nya tidak ditemukan/tidak cukup.");
            return;
        }
        for (int i = 0; i < 3; i++) {
            Node node = petunjukJawaban.getChildren().get(i);
            if (node instanceof HBox) {
                HBox choiceBox = (HBox) node;
                Label choiceLabel = (Label) choiceBox.getChildren().get(0);
                if (choiceLabel != null) {
                    choiceLabel.setText(choices.get(i));
                    choiceLabel.setFont(TEXT_FONT);
                }
            }
        }
    }

    private void checkAnswer() {
        String userAnswer = answerTextField.getText().trim().toLowerCase();
        String correctAnswer = challengeSessionsList.get(currentSessionIndex).getCorrectAnswer().toLowerCase();
        
        if (userAnswer.equals(correctAnswer)) {
            System.out.println("Jawaban Benar!");
            showFeedback(true);
        } else {
            System.out.println("Jawaban Salah!");
            incorrectAttempts++;
            showFeedback(false);
        }
    }
    
    private void showFeedback(boolean isCorrect) {
        answerTextField.setDisable(true);
        if (checkAnswer == null || checkAnswerIcon == null) return;
        
        checkAnswerIcon.setImage(isCorrect ? correctImage : wrongImage);
        checkAnswer.setVisible(true);
        disableLayer.setVisible(true);

        if (isCorrect) {
            AudioManager.playCorrectSound(); 
        } else {
            AudioManager.playWrongSound();
        }
        
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            checkAnswer.setVisible(false);
            answerTextField.setDisable(false);
            disableLayer.setVisible(false);
            if (isCorrect) {
                currentSessionIndex++;
                startNextSession();
            } else {
                answerTextField.clear();
                answerTextField.requestFocus();
            }
        });
        pause.play();
    }
    
    private void startTimer() {
        startTimeMillis = System.currentTimeMillis();
        if (timerTimeline != null) timerTimeline.stop();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            if (timerLabel != null) timerLabel.setText(timeFormatted);
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void stopTimer() {
        if (timerTimeline != null) timerTimeline.stop();
    }
    
    private void applyFadeInAnimation(Node node) {
        if (node == null) return;
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), node);
        fadeIn.setFromValue(0.0);
        node.setOpacity(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void applyFadeOutAnimation(Node node, Runnable onFinishedAction) {
        if (node == null) return;
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), node);
        fadeOut.setFromValue(node.getOpacity());
        fadeOut.setToValue(0.0);
        if (onFinishedAction != null) {
            fadeOut.setOnFinished(e -> onFinishedAction.run());
        }
        fadeOut.play();
    }

    private void showCompletePopup() {
        AudioManager.playCompleteSound();
        System.out.println("ChallengeController: showCompletePopup dipanggil.");
        try {
            String fxmlFile;
            if ("darkmode".equalsIgnoreCase(currentTheme)) {
                fxmlFile = "/complete_popup_darkmode.fxml";
            } else {
                fxmlFile = "/complete_popup.fxml";
            }
            FXMLLoader loader = App.getLoader(fxmlFile);
            Parent popupRootParent = loader.load();

            if (popupRootParent instanceof StackPane) {
                StackPane loadedStackPane = (StackPane) popupRootParent;
                Node mainPopupContent = loadedStackPane.lookup("#mainPage");
                if (mainPopupContent instanceof AnchorPane) {
                    completePopupNode = (AnchorPane) mainPopupContent;
                } else if (loadedStackPane.getChildren().size() > 2 && loadedStackPane.getChildren().get(2) instanceof AnchorPane) {
                    completePopupNode = (AnchorPane) loadedStackPane.getChildren().get(2);
                } else {
                    System.err.println("Struktur FXML complete_popup tidak memiliki AnchorPane #mainPage atau di indeks yang diharapkan.");
                    return;
                }
            } else {
                 System.err.println("Root dari " + fxmlFile + " bukan StackPane.");
                 return;
            }

            durasiTextComplete = (Text) completePopupNode.lookup("#durasiTextComplete");
            incorrectCountText = (Text) completePopupNode.lookup("#incorrectCountText");
            btnLanjutkanComplete = (HBox) completePopupNode.lookup("#btnLanjutkanComplete");
            btnCobaLagiComplete = (HBox) completePopupNode.lookup("#btnCobaLagiComplete");
            btnKembaliComplete = (HBox) completePopupNode.lookup("#btnKembaliComplete");

            if (durasiTextComplete == null) System.err.println("#durasiTextComplete tidak ditemukan di popup.");
            if (incorrectCountText != null) System.err.println("#incorrectCountText tidak ditemukan di popup.");
            if (btnLanjutkanComplete == null) System.err.println("#btnLanjutkanComplete tidak ditemukan di popup, padahal dibutuhkan.");
            if (btnCobaLagiComplete == null) System.err.println("#btnCobaLagiComplete tidak ditemukan di popup.");
            if (btnKembaliComplete == null) System.err.println("#btnKembaliComplete tidak ditemukan di popup.");

            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);

            incorrectAttempts = Math.max(0, incorrectAttempts);
            
            if (durasiTextComplete != null) durasiTextComplete.setText("Durasi " + timeFormatted);
            if (incorrectCountText != null) {
                incorrectCountText.setText("Jumlah Salah: " + incorrectAttempts);
            }
            if (btnCobaLagiComplete != null) btnCobaLagiComplete.setOnMouseClicked(event -> { AudioManager.playClickSound(); handleCobaLagi(); });
            if (btnKembaliComplete != null) btnKembaliComplete.setOnMouseClicked(event -> { AudioManager.playClickSound(); handleKembaliKeMenu(); });
            
            if (btnLanjutkanComplete != null) {
                btnLanjutkanComplete.setVisible(false);
                btnLanjutkanComplete.setManaged(false);
            }
            
            if (rootPage != null) {
                if (disableLayer != null) {
                    disableLayer.setOpacity(0.0);
                    disableLayer.setVisible(true);
                    applyFadeInAnimation(disableLayer);
                }
                completePopupNode.setOpacity(0.0);
                rootPage.getChildren().add(completePopupNode);
                applyFadeInAnimation(completePopupNode);
                completePopupNode.requestFocus();
                 System.out.println("ChallengeController: Complete popup ditampilkan.");
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat atau menampilkan popup penyelesaian: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hideCompletePopup() {
        if (rootPage == null) return;
        boolean popupWasVisible = (completePopupNode != null && completePopupNode.getScene() != null);

        if (completePopupNode != null) {
            final Node nodeToRemove = completePopupNode;
            applyFadeOutAnimation(nodeToRemove, () -> {
                if (rootPage != null) {
                    rootPage.getChildren().remove(nodeToRemove);
                }
                if (disableLayer != null && disableLayer.isVisible()) {
                    applyFadeOutAnimation(disableLayer, () -> {
                        if (disableLayer != null) {
                            disableLayer.setVisible(false);
                        }
                    });
                }
            });
            this.completePopupNode = null;
        } else {
            if (disableLayer != null && disableLayer.isVisible()) {
                 applyFadeOutAnimation(disableLayer, () -> {
                    if (disableLayer != null) {
                        disableLayer.setVisible(false);
                    }
                });
            }
        }
        
        if (popupWasVisible) {
            Platform.runLater(() -> {
                if (rootPage != null && rootPage.getScene() != null && rootPage.getScene().getWindow() != null && rootPage.getScene().getWindow().isShowing()) {
                     rootPage.requestFocus();
                }
            });
        }
    }
    
    private void handleCobaLagi() {
        hideCompletePopup();
        currentSessionIndex = 0;
        currentTypedCharIndex = 0;
        startNextSession();
    }

    private void handleKembaliKeMenu() {
        hideCompletePopup();
        stopTimer();
        try {
            FXMLLoader loader = App.getLoader("/mainpage.fxml");
            Parent mainRoot = loader.load();
            Stage primaryStage = App.getPrimaryStage();
            Scene mainScene = new Scene(mainRoot, primaryStage.getWidth(), primaryStage.getHeight());
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            if (cssPath != null) mainScene.getStylesheets().add(cssPath);
            primaryStage.setScene(mainScene);
            AudioManager.playMainMusic();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal kembali ke menu utama: " + e.getMessage());
        }
    }

}
