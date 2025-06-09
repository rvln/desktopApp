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
import javafx.scene.control.TextField; // Menggunakan TextField untuk input
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
    private HBox petunjukJawaban; // HBox utama untuk 3 pilihan jawaban
    @FXML
    private TextField answerTextField; // Input field untuk jawaban pengguna
    @FXML
    private ImageView panduanPertanyaan; // ImageView untuk gambar pertanyaan
    @FXML
    private AnchorPane checkAnswer; // AnchorPane untuk ikon checklist/wrong
    @FXML
    private ImageView checkAnswerIcon; // ImageView di dalam AnchorPane checkAnswer

    @FXML
    private Pane disableLayer;

    // Untuk pop-up penyelesaian
    private AnchorPane completePopupNode;
    private Text durasiTextComplete, incorrectCountText; // Tambahkan Text untuk jumlah salah
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (checkAnswer != null) {
            checkAnswer.setVisible(false);
            System.err.println("Hasil Jawaban tidak null"); // Sembunyikan di awal
        }
        if (answerTextField != null) {
            // Event handler untuk saat pengguna menekan Enter
            answerTextField.setOnAction(event -> checkAnswer());
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
        // Logika untuk mengatur tema (misalnya background) bisa ditambahkan di sini
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
        String basePath = "/desain/materi_images/tantangan/"; // Path untuk gambar tantangan

        // Sesi 1-10 (Contoh)
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
            
            // Tampilkan gambar pertanyaan
            loadImage(currentSession.getQuestionImagePath());

            // Tampilkan pilihan jawaban di petunjuk
            populateAnswerChoices(currentSession.getAnswerChoices());
            
            // Bersihkan dan fokus ke TextField
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
                // Asumsi di dalam HBox ada Label
                Label choiceLabel = (Label) choiceBox.getChildren().get(0);
                if (choiceLabel != null) {
                    choiceLabel.setText(choices.get(i));
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
            incorrectAttempts++; // Tambah jumlah kesalahan
            showFeedback(false);
        }
    }
    
    private void showFeedback(boolean isCorrect) {
        answerTextField.setDisable(true); // Nonaktifkan input saat feedback muncul
        if (checkAnswer == null || checkAnswerIcon == null) return;
        
        checkAnswerIcon.setImage(isCorrect ? correctImage : wrongImage);
        checkAnswer.setVisible(true);
        
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            checkAnswer.setVisible(false);
            answerTextField.setDisable(false);
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
    
    // Metode startTimer(), stopTimer(), dan untuk popup bisa disalin dari TypingController
    // atau di-refactor ke kelas utilitas
    // ... (Salin metode timer dan popup dari TypingController.java (ID: javafx_typing_controller_with_disablelayer) dan sesuaikan untuk menampilkan incorrectAttempts)
    // ... handleLanjutkan tidak relevan di sini, handleCobaLagi dan handleKembaliKeMenu akan mirip

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

            incorrectAttempts = Math.max(0, incorrectAttempts); // Pastikan tidak negatif
            
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
    
    // Handler untuk tombol di popup
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
