package desktopapp;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
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
    private Label promptLabel;
    @FXML
    private ImageView panduanMateri;

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

    @FXML
    private Pane disableLayer;

    private Map<String, TextFlow> virtualKeyboardNodeMap;
    private Map<String, Circle> fingerNodeMap;
    private Map<Character, String> charToFingerFxIdMap;

    private AnchorPane completePopupNode;
    private Text durasiTextComplete;
    private HBox btnLanjutkanComplete, btnCobaLagiComplete, btnKembaliComplete;

    private String currentCategory;
    private String currentTheme;
    private List<ExerciseSession> exerciseSessionsList;
    private int currentSessionIndex = 0;
    private String currentTextToType;
    private int currentTypedCharIndex = 0;

    private Timeline timerTimeline;
    private long categoryStartTimeMillis;
    private HBox incorrectCount;

    private String lastHighlightedKeyString = null;
    private Circle lastHighlightedFingerCircle = null;

    private final String PENDING_CHAR_COLOR = "#EFEFEF";
    private final String CURRENT_CHAR_COLOR = "#F4AC1C";
    private final String CORRECT_CHAR_COLOR = "#544F4F";
    private final String INCORRECT_CHAR_COLOR = "RED";
    private final double TEXT_DISPLAY_NORMAL_OPACITY = 0.8;
    private final Font TEXT_FONT = Font.font("Rubik Bold", 42);
    private final Font PROMPT_FONT = Font.font("Rubik Regular", 24);
    private static final double DEFAULT_OPACITY = 1.0;


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
            promptLabel.setWrapText(true);
        } else {
            System.err.println("TypingController FXML Injection Error: promptLabel is null.");
        }
        if (panduanMateri == null) {
            System.err.println("TypingController FXML Injection Error: panduanMateri (ImageView) is null.");
        } else {
            panduanMateri.setVisible(false);
            panduanMateri.setManaged(false);
        }
        if (textToTypeDisplay != null) {
            textToTypeDisplay.setOpacity(TEXT_DISPLAY_NORMAL_OPACITY);
        } else {
             System.err.println("TypingController FXML Injection Error: textToTypeDisplay is null.");
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
    }

    private void initializeFingerMapping() {
        fingerNodeMap = new HashMap<>();
        charToFingerFxIdMap = new HashMap<>();

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
    
    private String sanitizeTextForTyping(String inputText) {
        if (inputText == null) return "";
        return inputText.toLowerCase().replaceAll("[^a-z0-9 ]", "");
    }
    
    private String sanitizeAnswerForTyping(String inputText) {
        if (inputText == null) return "";
        return inputText.toLowerCase().trim().replaceAll("[^a-z0-9 ]", "");
    }

    private void loadExercisesForCategory(String category) {
        exerciseSessionsList = new ArrayList<>();
        String basePath = "/desain/Materi/"; 

        switch (category.toLowerCase()) {
            case "huruf": 
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("a s d f j k l"), basePath + "huruf_asdf.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("aa ss dd ff jj kk ll"), basePath + "huruf_asdf_double.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("g h g h f d s a"), basePath + "huruf_gh.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("j k l l k j h"), basePath + "huruf_jkl.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("q w e r t y u i o p"), basePath + "huruf_qwert.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("z x c v b n m"), basePath + "huruf_zxcv.png"));
                break;
            case "kata": 
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("aku dan kamu"), basePath + "kata_aku_kamu.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("saya mereka kita"), basePath + "kata_saya_mereka.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("rumah sekolah buku"), basePath + "kata_rumah_sekolah.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("pensil meja kursi"), basePath + "kata_pensil_meja.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("lampu pintu jendela"), basePath + "kata_lampu_pintu.png"));
                
                exerciseSessionsList.add(new ExerciseSession("Bagian tubuh untuk melihat: _____", sanitizeAnswerForTyping("mata"), basePath + "kata_q_mata.png"));
                exerciseSessionsList.add(new ExerciseSession("Warna bendera Indonesia: Merah _____", sanitizeAnswerForTyping("putih"), basePath + "kata_q_bendera.png"));
                exerciseSessionsList.add(new ExerciseSession("Hewan yang menggonggong: _____", sanitizeAnswerForTyping("anjing"), basePath + "kata_q_anjing.png"));
                exerciseSessionsList.add(new ExerciseSession("Alat untuk menulis di buku: _____", sanitizeAnswerForTyping("pensil"), basePath + "kata_q_pensil.png"));
                exerciseSessionsList.add(new ExerciseSession("Tempat kita belajar bersama teman: _______", sanitizeAnswerForTyping("sekolah"), basePath + "kata_q_sekolah.png"));
                break;
            case "kalimat":
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("aku suka makan apel."), basePath + "kalimat_suka_apel.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("saya cinta indonesia."), basePath + "kalimat_cinta_indonesia.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("mari belajar mengetik cepat."), basePath + "kalimat_belajar_mengetik.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("ini adalah rumah saya."), basePath + "kalimat_rumah_saya.png"));
                exerciseSessionsList.add(new ExerciseSession(sanitizeTextForTyping("buku itu di atas meja."), basePath + "kalimat_buku_meja.png"));
                // Sesi 6-10: Pertanyaan
                exerciseSessionsList.add(new ExerciseSession("Bhinneka _______ Ika.", sanitizeAnswerForTyping("tunggal"), basePath + "kalimat_q_bhinneka.png"));
                exerciseSessionsList.add(new ExerciseSession("Sehabis makan harus _____.", sanitizeAnswerForTyping("sikat gigi"), basePath + "kalimat_q_sikat_gigi.png"));
                exerciseSessionsList.add(new ExerciseSession("Besar ____ daripada tiang.", sanitizeAnswerForTyping("pasak"), basePath + "kalimat_q_pasak.png"));
                exerciseSessionsList.add(new ExerciseSession("Kita harus hemat ___ agar tidak habis.", sanitizeAnswerForTyping("air"), basePath + "kalimat_q_air.png"));
                exerciseSessionsList.add(new ExerciseSession("Sampah plastik jadi ____-brick.", sanitizeAnswerForTyping("eco"), basePath + "kalimat_q_ecobrick.png"));
                break;
            default:
                exerciseSessionsList.add(new ExerciseSession("Kategori Tidak Dikenali.", null));
        }
    }

    private void startNextSession() {
        if (currentSessionIndex < exerciseSessionsList.size()) {
            ExerciseSession currentSession = exerciseSessionsList.get(currentSessionIndex);
            currentTextToType = currentSession.getTextToType(); 
            
            currentTypedCharIndex = 0;

            boolean imageLoadedSuccessfully = false;
            if (panduanMateri != null && currentSession.getImagePath() != null && !currentSession.getImagePath().isEmpty()) {
                try {
                    InputStream imageStream = getClass().getResourceAsStream(currentSession.getImagePath());
                    if (imageStream != null) {
                        panduanMateri.setImage(new Image(imageStream));
                        panduanMateri.setVisible(true);
                        panduanMateri.setManaged(true);
                        imageLoadedSuccessfully = true;
                    } else {
                        System.err.println("TypingController Error: Gambar tidak ditemukan di path: " + currentSession.getImagePath());
                    }
                } catch (Exception e) {
                    System.err.println("TypingController Error: Gagal memuat gambar " + currentSession.getImagePath() + " - " + e.getMessage());
                }
            }
            
            if (!imageLoadedSuccessfully && panduanMateri != null) {
                panduanMateri.setImage(null);
                panduanMateri.setVisible(false);
                panduanMateri.setManaged(false);
            }

            if (promptLabel != null) {
                if (currentSession.isQuestionBased() && !imageLoadedSuccessfully) {
                    promptLabel.setText(currentSession.getPromptQuestion());
                    promptLabel.setVisible(true);
                    promptLabel.setManaged(true);
                } else {
                    promptLabel.setVisible(false);
                    promptLabel.setManaged(false);
                }
            }

            if (textToTypeDisplay != null) {
                 textToTypeDisplay.setOpacity(TEXT_DISPLAY_NORMAL_OPACITY); 
                 updateTextDisplay();
            } else {
                return;
            }
            if (currentSessionIndex == 0) { 
                startTimer();
            }
            highlightCharacterVisuals();
        } else {
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
                charTextNode.setFill(Color.web(CORRECT_CHAR_COLOR));
            } else if (i == currentTypedCharIndex) {
                charTextNode.setFill(Color.web(CURRENT_CHAR_COLOR));
                charTextNode.setStyle(charTextNode.getStyle() + "-fx-font-weight: bold;");
            } else {
                charTextNode.setFill(Color.web(PENDING_CHAR_COLOR));
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
        if (lastHighlightedFingerCircle != null) {
            lastHighlightedFingerCircle.getStyleClass().remove("finger-highlighted");
            lastHighlightedFingerCircle.setEffect(null);
        }

        if (currentTypedCharIndex < currentTextToType.length()) {
            char charToHighlight = currentTextToType.charAt(currentTypedCharIndex);
            String charKeyForMap = String.valueOf(charToHighlight).toUpperCase(); 

            if (virtualKeyboardNodeMap != null) {
                TextFlow currentKeyNode = virtualKeyboardNodeMap.get(charKeyForMap);
                if (currentKeyNode != null) {
                    currentKeyNode.getStyleClass().add("key-highlighted-active");
                    lastHighlightedKeyString = charKeyForMap;
                }
            }
            
            String fingerFxId = charToFingerFxIdMap.get(Character.toLowerCase(charToHighlight));
            if (fingerFxId == null) { 
                fingerFxId = charToFingerFxIdMap.get(Character.toUpperCase(charToHighlight));
            }

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
    
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (completePopupNode != null && completePopupNode.isVisible()) {
            event.consume(); return;
        }
        if (currentSessionIndex >= exerciseSessionsList.size() || currentTextToType == null || currentTypedCharIndex >= currentTextToType.length()) {
            event.consume(); return;
        }

        if (code == KeyCode.SHIFT || code.isModifierKey() || code.isNavigationKey() || code.isFunctionKey() || code == KeyCode.CAPS) {
            event.consume();
            return;
        }

        String typedTextFromEvent = event.getText();
        char typedCharInput = 0;

        if (typedTextFromEvent != null && !typedTextFromEvent.isEmpty()) {
            typedCharInput = typedTextFromEvent.charAt(0);
            if (Character.isLetter(typedCharInput)) {
                typedCharInput = Character.toLowerCase(typedCharInput);
            }
        } else if (code == KeyCode.SPACE) { 
            typedCharInput = ' ';
        } else {
            event.consume();
            return;
        }
        
        if (Character.isISOControl(typedCharInput) && typedCharInput != '\b') {
            event.consume();
            return;
        }

        char expectedChar = currentTextToType.charAt(currentTypedCharIndex);

        if (typedCharInput == expectedChar) {
             AudioManager.playTypingSound();
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
            AudioManager.playWrongSound();
            System.out.println("TypingController: Salah ketik! Expected: '" + expectedChar + "', Typed: '" + typedCharInput + "'");
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
        AudioManager.playCompleteSound();
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
            incorrectCount = (HBox) completePopupNode.lookup("#incorrectCount");

            if (durasiTextComplete == null) System.err.println("#durasiTextComplete tidak ditemukan di popup.");
            if (btnLanjutkanComplete == null && !currentCategory.equals("kalimat")) System.err.println("#btnLanjutkanComplete tidak ditemukan di popup, padahal dibutuhkan.");
            if (btnCobaLagiComplete == null) System.err.println("#btnCobaLagiComplete tidak ditemukan di popup.");
            if (btnKembaliComplete == null) System.err.println("#btnKembaliComplete tidak ditemukan di popup.");
            if (incorrectCount == null) System.err.println("#incorrectCount tidak ditemukan di popup.");

            if (incorrectCount != null) {
                incorrectCount.setVisible(false);
                incorrectCount.setManaged(false);
            }

            long elapsedMillis = System.currentTimeMillis() - categoryStartTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            
            if (durasiTextComplete != null) durasiTextComplete.setText("Durasi " + timeFormatted);
            
            if (btnCobaLagiComplete != null) btnCobaLagiComplete.setOnMouseClicked(event -> { AudioManager.playClickSound(); handleCobaLagi(); });
            if (btnKembaliComplete != null) btnKembaliComplete.setOnMouseClicked(event -> { AudioManager.playClickSound(); handleKembaliKeMenu(); });
            
            if (btnLanjutkanComplete != null) {
                if (currentCategory.equals("kalimat")) {
                    btnLanjutkanComplete.setVisible(false);
                    btnLanjutkanComplete.setManaged(false);
                } else {
                    btnLanjutkanComplete.setVisible(true);
                    btnLanjutkanComplete.setManaged(true);
                    btnLanjutkanComplete.setOnMouseClicked(event -> { AudioManager.playClickSound(); handleLanjutkan(); });
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
            AudioManager.playMainMusic();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal kembali ke menu utama: " + e.getMessage());
        }
    }
    
}
