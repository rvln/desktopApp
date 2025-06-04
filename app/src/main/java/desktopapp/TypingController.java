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
import javafx.scene.effect.DropShadow;
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

import java.io.IOException;
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
import java.util.stream.Collectors;

public class TypingController implements Initializable {

    @FXML
    private StackPane rootPage;
    @FXML
    private HBox countdown;
    @FXML
    private Label timerLabel;
    @FXML
    private ImageView wrongIcon;
    @FXML
    private TextFlow textToTypeDisplay;
    @FXML
    private VBox keyboardHuruf;

    @FXML
    private AnchorPane leftHand, rightHand;
    @FXML
    private Circle keyLThumb, keyLIndex, keyLMiddle, keyLRing, keyLPinky;
    @FXML
    private Circle keyRThumb, keyRindex, keyRMiddle, keyRRing, keyRPinky;

    // fx:id untuk tombol keyboard virtual (TextFlow)
    @FXML private TextFlow key_Q, key_W, key_E, key_R, key_T, key_Y, key_U, key_I, key_O, key_P;
    @FXML private TextFlow key_A, key_S, key_D, key_F, key_G, key_H, key_J, key_K, key_L, key_SEMICOLON;
    @FXML private TextFlow key_Z, key_X, key_C, key_V, key_B, key_N, key_M, key_COMMA, key_PERIOD, key_SLASH;
    @FXML private TextFlow key_SPACE, key_LSHIFT, key_RSHIFT; // fx:id untuk Shift bisa tetap ada, tapi tidak akan di-highlight aktif

    @FXML
    private Pane disableLayer;

    private Map<String, TextFlow> virtualKeyboardNodeMap;
    private Map<String, Circle> fingerNodeMap;
    private Map<Character, String> charToFingerFxIdMap; // Kunci akan lowercase atau simbol sederhana

    private AnchorPane completePopupNode;
    private Text durasiTextComplete;
    private HBox btnLanjutkanComplete, btnCobaLagiComplete, btnKembaliComplete;

    private String currentCategory;
    private String currentTheme;
    private List<ExerciseSession> exerciseSessionsList;
    private int currentSessionIndex = 0;
    private String currentTextToType; // Akan selalu lowercase dan minim tanda baca
    private int currentTypedCharIndex = 0;

    private Timeline timerTimeline;
    private long categoryStartTimeMillis;

    private String lastHighlightedKeyString = null; // Tetap UPPERCASE untuk key map tombol
    // private TextFlow lastHighlightedShiftKeyNode = null; // Tidak diperlukan lagi
    private Circle lastHighlightedFingerCircle = null;

    private final String PENDING_CHAR_COLOR = "#3E3E3E";
    private final String CURRENT_CHAR_COLOR = "#1C5DF4";
    private final String CORRECT_CHAR_COLOR = "#7E7878";
    private final String INCORRECT_CHAR_COLOR = "RED";
    private final double DEFAULT_OPACITY = 0.8;
    private final Font TEXT_FONT = Font.font("Rubik Bold", 42);
    private final Font PROMPT_FONT = Font.font("Rubik Regular", 24);
    @FXML
    private Label promptLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (wrongIcon != null) {
            wrongIcon.setVisible(false);
            wrongIcon.setManaged(false);
        }
        if (timerLabel == null && countdown != null) {
            for (Node node : countdown.getChildren()) {
                if (node instanceof Label) {
                    timerLabel = (Label) node;
                    break;
                }
            }
        }
        if (disableLayer != null) {
            disableLayer.setVisible(false);
            disableLayer.setOpacity(1.0);
        }
        if (promptLabel != null) {
            promptLabel.setVisible(false);
            promptLabel.setManaged(false);
            promptLabel.setFont(PROMPT_FONT);
            promptLabel.setTextFill(Color.WHITE);
        }
        initializeVirtualKeyboardNodeMap();
        initializeFingerMapping();
    }

    private void initializeVirtualKeyboardNodeMap() {
        virtualKeyboardNodeMap = new HashMap<>();
        virtualKeyboardNodeMap.put("Q", key_Q); virtualKeyboardNodeMap.put("W", key_W); virtualKeyboardNodeMap.put("E", key_E);
        virtualKeyboardNodeMap.put("R", key_R); virtualKeyboardNodeMap.put("T", key_T); virtualKeyboardNodeMap.put("Y", key_Y);
        virtualKeyboardNodeMap.put("U", key_U); virtualKeyboardNodeMap.put("I", key_I); virtualKeyboardNodeMap.put("O", key_O);
        virtualKeyboardNodeMap.put("P", key_P);
        virtualKeyboardNodeMap.put("A", key_A); virtualKeyboardNodeMap.put("S", key_S); virtualKeyboardNodeMap.put("D", key_D);
        virtualKeyboardNodeMap.put("F", key_F); virtualKeyboardNodeMap.put("G", key_G); virtualKeyboardNodeMap.put("H", key_H);
        virtualKeyboardNodeMap.put("J", key_J); virtualKeyboardNodeMap.put("K", key_K); virtualKeyboardNodeMap.put("L", key_L);
        virtualKeyboardNodeMap.put(";", key_SEMICOLON); 
        virtualKeyboardNodeMap.put("Z", key_Z); virtualKeyboardNodeMap.put("X", key_X); virtualKeyboardNodeMap.put("C", key_C);
        virtualKeyboardNodeMap.put("V", key_V); virtualKeyboardNodeMap.put("B", key_B); virtualKeyboardNodeMap.put("N", key_N);
        virtualKeyboardNodeMap.put("M", key_M);
        virtualKeyboardNodeMap.put(",", key_COMMA); 
        virtualKeyboardNodeMap.put(".", key_PERIOD);
        virtualKeyboardNodeMap.put("/", key_SLASH); 
        virtualKeyboardNodeMap.put(" ", key_SPACE);
        // Tombol Shift tidak akan digunakan untuk mengubah case, jadi tidak perlu di-map untuk highlight aktif
        // virtualKeyboardNodeMap.put("LSHIFT", key_LSHIFT);
        // virtualKeyboardNodeMap.put("RSHIFT", key_RSHIFT);
    }

    private void initializeFingerMapping() {
        fingerNodeMap = new HashMap<>();
        charToFingerFxIdMap = new HashMap<>(); // Kunci akan lowercase atau simbol sederhana

        if(keyLPinky != null) fingerNodeMap.put("keyLPinky", keyLPinky);
        if(keyLRing != null) fingerNodeMap.put("keyLRing", keyLRing);
        if(keyLMiddle != null) fingerNodeMap.put("keyLMiddle", keyLMiddle);
        if(keyLIndex != null) fingerNodeMap.put("keyLIndex", keyLIndex);
        if(keyLThumb != null) fingerNodeMap.put("keyLThumb", keyLThumb);

        if(keyRPinky != null) fingerNodeMap.put("keyRPinky", keyRPinky);
        if(keyRRing != null) fingerNodeMap.put("keyRRing", keyRRing);
        if(keyRMiddle != null) fingerNodeMap.put("keyRMiddle", keyRMiddle);
        if(keyRindex != null) fingerNodeMap.put("keyRindex", keyRindex);
        if(keyRThumb != null) fingerNodeMap.put("keyRThumb", keyRThumb);

        // Mapping Karakter (sekarang lowercase) ke fx:id Jari
        for (char c : "qaz1".toCharArray()) charToFingerFxIdMap.put(c, "keyLPinky");
        for (char c : "wsx2".toCharArray()) charToFingerFxIdMap.put(c, "keyLRing");
        for (char c : "edc3".toCharArray()) charToFingerFxIdMap.put(c, "keyLMiddle");
        for (char c : "rfvtgb45".toCharArray()) charToFingerFxIdMap.put(c, "keyLIndex");
        charToFingerFxIdMap.put(' ', "keyLThumb");

        for (char c : "yuhjnm67".toCharArray()) charToFingerFxIdMap.put(c, "keyRindex");
        for (char c : "ik8".toCharArray()) charToFingerFxIdMap.put(c, "keyRMiddle"); charToFingerFxIdMap.put(',', "keyRMiddle");
        for (char c : "ol9".toCharArray()) charToFingerFxIdMap.put(c, "keyRRing"); charToFingerFxIdMap.put('.', "keyRRing");
        for (char c : "p0".toCharArray()) charToFingerFxIdMap.put(c, "keyRPinky"); charToFingerFxIdMap.put(';', "keyRPinky");  charToFingerFxIdMap.put('/', "keyRPinky");
    }

    public void initContent(String category, String theme) {
        this.currentCategory = category;
        this.currentTheme = theme;
        this.currentSessionIndex = 0;
        this.currentTypedCharIndex = 0;

        loadExercisesForCategory(category);
        startNextSession(); 

        if (rootPage != null) {
            Platform.runLater(() -> {
                if (rootPage.getScene() != null) {
                    rootPage.requestFocus();
                }
            });
        }
    }
    
    // Helper untuk membersihkan teks menjadi lowercase dan hanya karakter yang diizinkan
    private String sanitizeText(String inputText) {
        if (inputText == null) return "";
        // Hanya izinkan huruf kecil, angka, dan spasi. Hapus tanda baca lainnya.
        return inputText.toLowerCase().replaceAll("[^a-z0-9 ]", "");
    }
    
    private String sanitizeAnswer(String inputText) {
        if (inputText == null) return "";
        // Untuk jawaban, mungkin hanya perlu lowercase dan trim spasi berlebih
        return inputText.toLowerCase().trim();
    }


    private void loadExercisesForCategory(String category) {
        exerciseSessionsList = new ArrayList<>();
        switch (category.toLowerCase()) {
            case "huruf": // Semua akan menjadi lowercase dan tanpa tanda baca kompleks
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("a s d f j k l"))); // ; dihapus
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("aa ss dd ff jj kk ll")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("g h g h f d s a")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("j k l l k j h"))); // ; dihapus
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("q w e r t y u i o p")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("z x c v b n m")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("latihan huruf kecil semua")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("ini juga huruf kecil")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("tes angka 12345 dan 09876"))); // Angka dipertahankan
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("selamat belajar mengetik")));
                break;
            case "kata": 
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("aku dan kamu")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("saya mereka kita")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("rumah sekolah buku")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("pensil meja kursi")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("lampu pintu jendela")));
                // Sesi 6-10: Pertanyaan (Jawaban akan disanitasi menjadi lowercase)
                exerciseSessionsList.add(new ExerciseSession("Bagian tubuh untuk melihat: _____", sanitizeAnswer("mata")));
                exerciseSessionsList.add(new ExerciseSession("Warna bendera Indonesia: Merah _____", sanitizeAnswer("Putih")));
                exerciseSessionsList.add(new ExerciseSession("Hewan yang menggonggong: _____", sanitizeAnswer("anjing")));
                exerciseSessionsList.add(new ExerciseSession("Alat untuk menulis di buku: _____", sanitizeAnswer("pensil")));
                exerciseSessionsList.add(new ExerciseSession("Tempat kita belajar bersama teman: _______", sanitizeAnswer("sekolah")));
                break;
            case "kalimat":
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("aku suka makan apel.")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("saya cinta indonesia.")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("mari belajar mengetik cepat.")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("ini adalah rumah saya.")));
                exerciseSessionsList.add(new ExerciseSession(sanitizeText("buku itu di atas meja.")));
                // Sesi 6-10: Pertanyaan
                exerciseSessionsList.add(new ExerciseSession("Bhinneka _______ Ika.", sanitizeAnswer("Tunggal")));
                exerciseSessionsList.add(new ExerciseSession("Untuk menjaga kesehatan mata, istirahatlah setiap __ menit.", sanitizeAnswer("20")));
                exerciseSessionsList.add(new ExerciseSession("Peribahasa: Besar ____ daripada tiang.", sanitizeAnswer("pasak")));
                exerciseSessionsList.add(new ExerciseSession("Kita harus hemat ___ agar tidak habis.", sanitizeAnswer("air")));
                exerciseSessionsList.add(new ExerciseSession("Sampah plastik bisa diolah menjadi ____-brick.", sanitizeAnswer("eco")));
                break;
            default:
                exerciseSessionsList.add(new ExerciseSession("Kategori Tidak Dikenali."));
        }
        System.out.println("TypingController: Exercises loaded ("+ exerciseSessionsList.size() +" sesi) untuk kategori " + category);
    }

    private void startNextSession() {
        if (currentSessionIndex < exerciseSessionsList.size()) {
            ExerciseSession currentSession = exerciseSessionsList.get(currentSessionIndex);
            currentTextToType = currentSession.getTextToType(); // Ini sudah lowercase dan disanitasi
            
            System.out.println("TypingController: Sesi " + (currentSessionIndex + 1) + ": " + currentSession.toString());
            currentTypedCharIndex = 0;

            if (promptLabel != null) {
                if (currentSession.isQuestionBased()) {
                    promptLabel.setText(currentSession.getPromptQuestion());
                    promptLabel.setVisible(true);
                    promptLabel.setManaged(true);
                } else {
                    promptLabel.setVisible(false);
                    promptLabel.setManaged(false);
                }
            }

            if (textToTypeDisplay != null) {
                 updateTextDisplay();
            } else {
                System.err.println("TypingController Error: textToTypeDisplay (TextFlow) is null.");
                return;
            }
            if (currentSessionIndex == 0) { 
                startTimer();
            }
            highlightCharacterVisuals();
        } else {
            System.out.println("TypingController: Semua sesi untuk kategori " + currentCategory + " selesai.");
            stopTimer();
            showCompletePopup();
        }
    }

    private void startTimer() {
        categoryStartTimeMillis = System.currentTimeMillis();
        if (timerTimeline != null) timerTimeline.stop();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long elapsedMillis = System.currentTimeMillis() - categoryStartTimeMillis;
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

    private void updateTextDisplay() {
        if (textToTypeDisplay == null || currentTextToType == null) return;
        textToTypeDisplay.getChildren().clear();

        for (int i = 0; i < currentTextToType.length(); i++) {
            Text charTextNode = new Text(String.valueOf(currentTextToType.charAt(i)));
            charTextNode.setFont(TEXT_FONT);

            if (i < currentTypedCharIndex) {
                charTextNode.setFill(Color.web(CORRECT_CHAR_COLOR, DEFAULT_OPACITY));
            } else if (i == currentTypedCharIndex) {
                charTextNode.setFill(Color.web(CURRENT_CHAR_COLOR, DEFAULT_OPACITY));
                charTextNode.setStyle(charTextNode.getStyle() + "-fx-font-weight: bold;");
            } else {
                charTextNode.setFill(Color.web(PENDING_CHAR_COLOR, DEFAULT_OPACITY));
            }
            textToTypeDisplay.getChildren().add(charTextNode);
        }
    }

    private void highlightCharacterVisuals() {
        if (lastHighlightedKeyString != null && virtualKeyboardNodeMap != null) {
            TextFlow prevKeyNode = virtualKeyboardNodeMap.get(lastHighlightedKeyString);
            if (prevKeyNode != null) {
                prevKeyNode.getStyleClass().remove("key-highlighted-active");
            }
        }
        // Hapus highlight Shift karena tidak lagi relevan untuk case
        // (Kode untuk lastHighlightedShiftKeyNode dihapus karena tidak digunakan lagi)
        if (lastHighlightedFingerCircle != null) {
            lastHighlightedFingerCircle.getStyleClass().remove("finger-highlighted");
            lastHighlightedFingerCircle.setEffect(null);
        }

        if (currentTypedCharIndex < currentTextToType.length()) {
            char charToHighlight = currentTextToType.charAt(currentTypedCharIndex); // Ini sudah lowercase
            String charKeyForMap = String.valueOf(charToHighlight).toUpperCase(); // Map keyboard tetap uppercase

            if (virtualKeyboardNodeMap != null) {
                TextFlow currentKeyNode = virtualKeyboardNodeMap.get(charKeyForMap);
                if (currentKeyNode != null) {
                    currentKeyNode.getStyleClass().add("key-highlighted-active");
                    lastHighlightedKeyString = charKeyForMap;
                }
            }
            
            // Highlight jari menggunakan karakter lowercase (atau uppercase jika map jari Anda uppercase)
            // Untuk konsistensi, kita akan menggunakan uppercase untuk charToFingerFxIdMap
            String fingerFxId = charToFingerFxIdMap.get(Character.toUpperCase(charToHighlight));
            if (fingerFxId != null && fingerNodeMap != null) {
                Circle fingerCircle = fingerNodeMap.get(fingerFxId);
                if (fingerCircle != null) {
                    fingerCircle.getStyleClass().add("finger-highlighted");
                    DropShadow dropShadow = new DropShadow(10, Color.web(CURRENT_CHAR_COLOR, 0.7));
                    fingerCircle.setEffect(dropShadow);
                    lastHighlightedFingerCircle = fingerCircle;
                }
            }
        }
        updateTextDisplay();
    }
    
    // handleKeyReleased tidak lagi diperlukan untuk logika Shift case
    // public void handleKeyReleased(KeyEvent event) { ... }

    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        System.out.println("--- TypingController: KeyPress: Code=" + code + ", Text='" + event.getText() + "' ---");

        if (completePopupNode != null && completePopupNode.isVisible()) {
            event.consume(); return;
        }
        if (currentSessionIndex >= exerciseSessionsList.size() || currentTextToType == null || currentTypedCharIndex >= currentTextToType.length()) {
            event.consume(); return;
        }

        // Abaikan tombol Shift dan modifier lainnya secara eksplisit
        if (code == KeyCode.SHIFT || code.isModifierKey() || code.isNavigationKey() || code.isFunctionKey() || code == KeyCode.CAPS) {
            event.consume();
            return;
        }

        String typedTextFromEvent = event.getText();
        char typedCharInput = 0;

        if (typedTextFromEvent != null && !typedTextFromEvent.isEmpty()) {
            typedCharInput = typedTextFromEvent.charAt(0);
            if (Character.isLetter(typedCharInput)) {
                typedCharInput = Character.toLowerCase(typedCharInput); // Selalu konversi huruf ke lowercase
            }
            // Untuk simbol dan angka, biarkan apa adanya dari event.getText()
        } else if (code == KeyCode.SPACE) { 
            typedCharInput = ' ';
        } else {
            System.out.println("TypingController: Key press ignored (no text from event and not space): " + code);
            event.consume();
            return;
        }
        
        if (Character.isISOControl(typedCharInput) && typedCharInput != '\b') {
            event.consume();
            return;
        }

        char expectedChar = currentTextToType.charAt(currentTypedCharIndex); // Ini sudah lowercase dari loadExercises
        System.out.println("TypingController: Expected: '" + expectedChar + "', Processed Typed: '" + typedCharInput + "'");

        if (typedCharInput == expectedChar) {
            if (wrongIcon != null) {
                wrongIcon.setVisible(false);
                wrongIcon.setManaged(false);
            }
            currentTypedCharIndex++;
            
            if (currentTypedCharIndex == currentTextToType.length()) {
                currentSessionIndex++;
                startNextSession();
            } else {
                highlightCharacterVisuals();
            }
        } else { 
            System.out.println("TypingController: Salah ketik!");
            if (wrongIcon != null) {
                wrongIcon.setVisible(true);
                wrongIcon.setManaged(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                pause.setOnFinished(e -> {
                    if(wrongIcon != null) {
                        wrongIcon.setVisible(false);
                        wrongIcon.setManaged(false);
                    }
                });
                pause.play();
            }
            if (textToTypeDisplay != null && textToTypeDisplay.getChildren().size() > currentTypedCharIndex) {
                Node charNode = textToTypeDisplay.getChildren().get(currentTypedCharIndex);
                if (charNode instanceof Text) {
                    Text textNode = (Text) charNode;
                    textNode.setFill(Color.web(INCORRECT_CHAR_COLOR));
                    textNode.setFont(TEXT_FONT);
                    String currentStyle = textNode.getStyle();
                    if (!currentStyle.contains("-fx-font-weight: bold;")) {
                         textNode.setStyle(currentStyle + "-fx-font-weight: bold;");
                    }

                    PauseTransition pt = new PauseTransition(Duration.millis(500));
                    pt.setOnFinished(e -> {
                        if (currentTypedCharIndex < currentTextToType.length() &&
                            textToTypeDisplay.getChildren().size() > currentTypedCharIndex &&
                            textToTypeDisplay.getChildren().get(currentTypedCharIndex) == textNode) {
                             textNode.setFill(Color.web(CURRENT_CHAR_COLOR));
                             textNode.setStyle("-fx-opacity: " + DEFAULT_OPACITY + "; -fx-font-weight: bold;");
                             textNode.setFont(TEXT_FONT);
                        }
                    });
                    pt.play();
                }
            }
        }
        event.consume();
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
        System.out.println("TypingController: showCompletePopup dipanggil.");
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
            btnLanjutkanComplete = (HBox) completePopupNode.lookup("#btnLanjutkanComplete");
            btnCobaLagiComplete = (HBox) completePopupNode.lookup("#btnCobaLagiComplete");
            btnKembaliComplete = (HBox) completePopupNode.lookup("#btnKembaliComplete");

            if (durasiTextComplete == null) System.err.println("#durasiTextComplete tidak ditemukan di popup.");
            if (btnLanjutkanComplete == null && !currentCategory.equals("kalimat")) System.err.println("#btnLanjutkanComplete tidak ditemukan di popup, padahal dibutuhkan.");
            if (btnCobaLagiComplete == null) System.err.println("#btnCobaLagiComplete tidak ditemukan di popup.");
            if (btnKembaliComplete == null) System.err.println("#btnKembaliComplete tidak ditemukan di popup.");

            long elapsedMillis = System.currentTimeMillis() - categoryStartTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            
            if (durasiTextComplete != null) durasiTextComplete.setText("Durasi " + timeFormatted);
            
            if (btnCobaLagiComplete != null) btnCobaLagiComplete.setOnMouseClicked(event -> handleCobaLagi());
            if (btnKembaliComplete != null) btnKembaliComplete.setOnMouseClicked(event -> handleKembaliKeMenu());
            
            if (btnLanjutkanComplete != null) {
                if (currentCategory.equals("kalimat")) {
                    btnLanjutkanComplete.setVisible(false);
                    btnLanjutkanComplete.setManaged(false);
                } else {
                    btnLanjutkanComplete.setVisible(true);
                    btnLanjutkanComplete.setManaged(true);
                    btnLanjutkanComplete.setOnMouseClicked(event -> handleLanjutkan());
                }
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
                 System.out.println("TypingController: Complete popup ditampilkan.");
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


    private void handleLanjutkan() {
        hideCompletePopup();
        String nextCategory = "";
        if (currentCategory.equals("huruf")) nextCategory = "kata";
        else if (currentCategory.equals("kata")) nextCategory = "kalimat";

        if (!nextCategory.isEmpty()) {
            try {
                String fxmlFile;
                 if ("darkmode".equalsIgnoreCase(currentTheme)) {
                    fxmlFile = "/latihan_" + nextCategory + "_darkmode.fxml";
                } else {
                    fxmlFile = "/latihan_" + nextCategory + ".fxml";
                }
                FXMLLoader loader = App.getLoader(fxmlFile);
                Parent typingRoot = loader.load();
                TypingController nextTypingController = loader.getController();
                 if (nextTypingController == null) {
                    System.err.println("Gagal mendapatkan TypingController untuk: " + fxmlFile);
                    return;
                }
                Stage primaryStage = App.getPrimaryStage();
                Scene scene = new Scene(typingRoot, primaryStage.getWidth(), primaryStage.getHeight());
                String cssPath = getClass().getResource("/style.css").toExternalForm();
                if (cssPath != null) scene.getStylesheets().add(cssPath);
                
                scene.setOnKeyPressed(nextTypingController::handleKeyPress);
                // scene.setOnKeyReleased(nextTypingController::handleKeyReleased); // Dihapus karena handleKeyReleased dihapus
                primaryStage.setScene(scene);
                nextTypingController.initContent(nextCategory, currentTheme);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman latihan berikutnya: " + e.getMessage());
            }
        } else {
            handleKembaliKeMenu();
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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal kembali ke menu utama: " + e.getMessage());
        }
    }
    
    private void handleExitToMainMenu() {
        stopTimer();
        try {
            FXMLLoader loader = App.getLoader("/mainpage.fxml");
            Parent mainRoot = loader.load();
            Stage primaryStage = App.getPrimaryStage();
            if (primaryStage == null && rootPage != null && rootPage.getScene() != null) {
                 primaryStage = (Stage) rootPage.getScene().getWindow();
            }
            if (primaryStage == null) {
                 System.err.println("Tidak bisa mendapatkan PrimaryStage untuk keluar.");
                 return;
            }
            Scene mainScene = new Scene(mainRoot, primaryStage.getWidth(), primaryStage.getHeight());
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            if (cssPath != null) mainScene.getStylesheets().add(cssPath);
            primaryStage.setScene(mainScene);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal keluar ke menu utama dari tombol X: " + e.getMessage());
        }
    }
}
