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

    @FXML private TextFlow key_Q, key_W, key_E, key_R, key_T, key_Y, key_U, key_I, key_O, key_P;
    @FXML private TextFlow key_A, key_S, key_D, key_F, key_G, key_H, key_J, key_K, key_L, key_SEMICOLON;
    @FXML private TextFlow key_Z, key_X, key_C, key_V, key_B, key_N, key_M, key_COMMA, key_PERIOD, key_SLASH;
    @FXML private TextFlow key_SPACE, key_LSHIFT, key_RSHIFT;

    private Map<String, TextFlow> virtualKeyboardNodeMap;
    private Map<String, Circle> fingerNodeMap;
    private Map<Character, String> charToFingerFxIdMap;

    private AnchorPane completePopupNode;
    private Text durasiTextComplete;
    private HBox btnLanjutkanComplete, btnCobaLagiComplete, btnKembaliComplete;

    private String currentCategory;
    private String currentTheme;
    private List<String> currentExerciseList;
    private int currentExerciseIndex = 0;
    private String currentTextToType;
    private int currentTypedCharIndex = 0;

    private Timeline timerTimeline;
    private long startTimeMillis;
    private final Random random = new Random();

    private List<String> educationalSnippets;
    private static final List<String> MATERI_SD_KELAS_1_6 = Arrays.asList(
        "Indonesia negara kepulauan terbesar!", "Pancasila dasar negara kita.", "Bhinneka Tunggal Ika.",
        "Matahari sumber energi utama.", "Air penting untuk kehidupan."
    );
    private int snippetDisplayCounter = 0;
    private final int SNIPPET_FREQUENCY = 3;
    private String lastHighlightedKeyString = null;
    private Circle lastHighlightedFingerCircle = null;

    private final String PENDING_CHAR_COLOR = "#3E3E3E";
    private final String CURRENT_CHAR_COLOR = "#1C5DF4";
    private final String CORRECT_CHAR_COLOR = "#7E7878";
    private final String INCORRECT_CHAR_COLOR = "RED";
    private final double DEFAULT_OPACITY = 0.8;
    private final Font TEXT_FONT = Font.font("Rubik Bold", 42);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("TypingController: initialize START");
        if (wrongIcon != null) {
            wrongIcon.setVisible(false);
            wrongIcon.setManaged(false);
        } else {
            System.err.println("TypingController FXML Injection Error: wrongIcon is null.");
        }

        if (timerLabel == null && countdown != null) {
            for (Node node : countdown.getChildren()) {
                if (node instanceof Label) {
                    timerLabel = (Label) node;
                    System.out.println("TypingController: timerLabel found inside countdown HBox.");
                    break;
                }
            }
        }
        if (timerLabel == null) {
             System.err.println("TypingController FXML Injection Error: timerLabel is null.");
        } else {
            System.out.println("TypingController: timerLabel injected/found successfully.");
        }

        if (textToTypeDisplay == null) {
             System.err.println("TypingController FXML Injection Error: textToTypeDisplay (TextFlow) is null.");
        } else {
            System.out.println("TypingController: textToTypeDisplay injected successfully.");
        }

        initializeVirtualKeyboardNodeMap();
        initializeFingerMapping();
        educationalSnippets = new ArrayList<>(MATERI_SD_KELAS_1_6);
        Collections.shuffle(educationalSnippets);
        System.out.println("TypingController: initialize END");
    }

    private void initializeVirtualKeyboardNodeMap() {
        System.out.println("TypingController: initializeVirtualKeyboardNodeMap START");
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
        virtualKeyboardNodeMap.put("LSHIFT", key_LSHIFT);
        virtualKeyboardNodeMap.put("RSHIFT", key_RSHIFT);

        int nullKeyCount = 0;
        for(Map.Entry<String, TextFlow> entry : virtualKeyboardNodeMap.entrySet()){
            if(entry.getValue() == null){
                System.err.println("TypingController WARNING: Tombol keyboard virtual TextFlow untuk fx:id='key_" + entry.getKey().replace(";", "SEMICOLON").replace(",", "COMMA").replace(".", "PERIOD").replace("/", "SLASH").replace(" ", "SPACE") + "' TIDAK TER-INJECT (null).");
                nullKeyCount++;
            }
        }
        if (nullKeyCount == 0) {
            System.out.println("TypingController: Semua tombol keyboard virtual berhasil di-map.");
        } else {
            System.err.println("TypingController ERROR: " + nullKeyCount + " tombol keyboard virtual gagal di-inject.");
        }
        System.out.println("TypingController: initializeVirtualKeyboardNodeMap END");
    }

    private void initializeFingerMapping() {
        // ... (sama seperti sebelumnya, tambahkan log jika perlu) ...
        fingerNodeMap = new HashMap<>();
        charToFingerFxIdMap = new HashMap<>();

        if(keyLPinky != null) fingerNodeMap.put("keyLPinky", keyLPinky); else System.err.println("FXML Error: keyLPinky is null");
        if(keyLRing != null) fingerNodeMap.put("keyLRing", keyLRing); else System.err.println("FXML Error: keyLRing is null");
        if(keyLMiddle != null) fingerNodeMap.put("keyLMiddle", keyLMiddle); else System.err.println("FXML Error: keyLMiddle is null");
        if(keyLIndex != null) fingerNodeMap.put("keyLIndex", keyLIndex); else System.err.println("FXML Error: keyLIndex is null");
        if(keyLThumb != null) fingerNodeMap.put("keyLThumb", keyLThumb); else System.err.println("FXML Error: keyLThumb is null");

        if(keyRPinky != null) fingerNodeMap.put("keyRPinky", keyRPinky); else System.err.println("FXML Error: keyRPinky is null");
        if(keyRRing != null) fingerNodeMap.put("keyRRing", keyRRing); else System.err.println("FXML Error: keyRRing is null");
        if(keyRMiddle != null) fingerNodeMap.put("keyRMiddle", keyRMiddle); else System.err.println("FXML Error: keyRMiddle is null");
        if(keyRindex != null) fingerNodeMap.put("keyRindex", keyRindex); else System.err.println("FXML Error: keyRindex is null");
        if(keyRThumb != null) fingerNodeMap.put("keyRThumb", keyRThumb); else System.err.println("FXML Error: keyRThumb is null");

        for (char c : "QAZ1!".toCharArray()) charToFingerFxIdMap.put(c, "keyLPinky");
        for (char c : "WSX2@".toCharArray()) charToFingerFxIdMap.put(c, "keyLRing");
        for (char c : "EDC3#".toCharArray()) charToFingerFxIdMap.put(c, "keyLMiddle");
        for (char c : "RFVTGB4$5%".toCharArray()) charToFingerFxIdMap.put(c, "keyLIndex");
        charToFingerFxIdMap.put(' ', "keyLThumb");

        for (char c : "YUHJNM6^7&".toCharArray()) charToFingerFxIdMap.put(c, "keyRindex");
        for (char c : "IK8*".toCharArray()) charToFingerFxIdMap.put(c, "keyRMiddle"); charToFingerFxIdMap.put(',', "keyRMiddle"); charToFingerFxIdMap.put('<', "keyRMiddle");
        for (char c : "OL9(".toCharArray()) charToFingerFxIdMap.put(c, "keyRRing"); charToFingerFxIdMap.put('.', "keyRRing"); charToFingerFxIdMap.put('>', "keyRRing");
        for (char c : "P0)".toCharArray()) charToFingerFxIdMap.put(c, "keyRPinky"); charToFingerFxIdMap.put(';', "keyRPinky"); charToFingerFxIdMap.put(':', "keyRPinky"); charToFingerFxIdMap.put('/', "keyRPinky"); charToFingerFxIdMap.put('?', "keyRPinky");
        System.out.println("TypingController: initializeFingerMapping COMPLETE");
    }


    public void initContent(String category, String theme) {
        System.out.println("TypingController: initContent START - Category: " + category + ", Theme: " + theme);
        this.currentCategory = category;
        this.currentTheme = theme;
        this.currentExerciseIndex = 0;
        this.currentTypedCharIndex = 0;

        loadExercisesForCategory(category);
        startNextExercise();

        if (rootPage != null) {
            Platform.runLater(() -> {
                if (rootPage.getScene() != null) {
                    System.out.println("TypingController: Requesting focus for rootPage.");
                    rootPage.requestFocus(); // Penting untuk memastikan rootPage bisa menerima event jika Scene tidak secara eksplisit
                } else {
                    System.err.println("TypingController: rootPage.getScene() is null in initContent's Platform.runLater. Fokus mungkin tidak ter-set.");
                }
            });
        } else {
            System.err.println("TypingController: rootPage is null at the end of initContent.");
        }
        System.out.println("TypingController: initContent END");
    }

    private void loadExercisesForCategory(String category) {
        System.out.println("TypingController: loadExercisesForCategory - " + category);
        currentExerciseList = new ArrayList<>();
        switch (category.toLowerCase()) {
            case "huruf":
                currentExerciseList.addAll(Arrays.asList("ASDF JKL;", "FJDKSLA;", "QWERT YUIOP", "ASDFGHJKL;", "ZXCVBNM,./"));
                break;
            case "kata":
                currentExerciseList.addAll(Arrays.asList("RUMAH", "SEKOLAH", "BELAJAR", "KOMPUTER", "ANAK", "PINTAR", "CERDAS"));
                break;
            case "kalimat":
                currentExerciseList.addAll(Arrays.asList("SAYA SUKA BELAJAR MENGETIK.", "MARI KITA JAGA KEBERSIHAN LINGKUNGAN.", "INDONESIA TANAH AIRKU."));
                break;
            default:
                currentExerciseList.add("KATEGORI TIDAK ADA");
        }
        Collections.shuffle(currentExerciseList);
        System.out.println("TypingController: Exercises loaded: " + currentExerciseList.size());
    }

    private void startNextExercise() {
        System.out.println("TypingController: startNextExercise - Index: " + currentExerciseIndex);
        if (currentExerciseIndex < currentExerciseList.size()) {
            currentTextToType = currentExerciseList.get(currentExerciseIndex).toUpperCase();
            System.out.println("TypingController: Current text to type: " + currentTextToType);
            currentTypedCharIndex = 0;
            snippetDisplayCounter = 0;
            if (textToTypeDisplay != null) {
                 updateTextDisplay();
            } else {
                System.err.println("TypingController Error: textToTypeDisplay (TextFlow) is null when starting exercise.");
                return;
            }
            startTimer();
            highlightCharacterVisuals();
        } else {
            System.out.println("TypingController: Semua latihan untuk kategori " + currentCategory + " selesai.");
            handleExitToMainMenu();
        }
    }

    private void startTimer() {
        System.out.println("TypingController: startTimer");
        startTimeMillis = System.currentTimeMillis();
        if (timerTimeline != null) timerTimeline.stop();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            if (timerLabel != null) {
                timerLabel.setText(timeFormatted);
            } else {
                // System.err.println("TypingController: timerLabel is null during timer update."); // Bisa terlalu berisik
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void stopTimer() {
        System.out.println("TypingController: stopTimer");
        if (timerTimeline != null) timerTimeline.stop();
    }

    private void updateTextDisplay() {
        // System.out.println("TypingController: updateTextDisplay"); // Bisa terlalu berisik
        if (textToTypeDisplay == null || currentTextToType == null) {
             System.err.println("TypingController Error: updateTextDisplay called with null textToTypeDisplay or currentTextToType.");
            return;
        }
        textToTypeDisplay.getChildren().clear();

        for (int i = 0; i < currentTextToType.length(); i++) {
            Text charTextNode = new Text(String.valueOf(currentTextToType.charAt(i)));
            charTextNode.setFont(TEXT_FONT);

            if (i < currentTypedCharIndex) {
                charTextNode.setFill(Color.web(CORRECT_CHAR_COLOR));
                charTextNode.setOpacity(DEFAULT_OPACITY);
            } else if (i == currentTypedCharIndex) {
                charTextNode.setFill(Color.web(CURRENT_CHAR_COLOR));
                charTextNode.setOpacity(DEFAULT_OPACITY);
                charTextNode.setStyle(charTextNode.getStyle() + "-fx-font-weight: bold;");
            } else {
                charTextNode.setFill(Color.web(PENDING_CHAR_COLOR));
                charTextNode.setOpacity(DEFAULT_OPACITY);
            }
            textToTypeDisplay.getChildren().add(charTextNode);
        }
    }

    private void highlightCharacterVisuals() {
        // System.out.println("TypingController: highlightCharacterVisuals"); // Bisa terlalu berisik
        if (lastHighlightedKeyString != null) {
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
            String charKeyForKeyboard = String.valueOf(charToHighlight);

            TextFlow currentKeyNode = virtualKeyboardNodeMap.get(charKeyForKeyboard);
            if (currentKeyNode != null) {
                currentKeyNode.getStyleClass().add("key-highlighted-active");
                lastHighlightedKeyString = charKeyForKeyboard;
            } else {
                System.out.println("TypingController Highlight: Tombol virtual (TextFlow) untuk '" + charKeyForKeyboard + "' tidak ditemukan di map.");
            }

            String fingerFxId = charToFingerFxIdMap.get(charToHighlight);
            if (fingerFxId != null) {
                Circle fingerCircle = fingerNodeMap.get(fingerFxId);
                if (fingerCircle != null) {
                    fingerCircle.getStyleClass().add("finger-highlighted");
                    DropShadow dropShadow = new DropShadow(10, Color.web(CURRENT_CHAR_COLOR, 0.7));
                    fingerCircle.setEffect(dropShadow);
                    lastHighlightedFingerCircle = fingerCircle;
                } else {
                     System.out.println("TypingController Highlight: Circle untuk jari fx:id='" + fingerFxId + "' tidak ditemukan.");
                }
            } else {
                System.out.println("TypingController Highlight: Mapping jari untuk karakter '" + charToHighlight + "' tidak ditemukan.");
            }
        }
        updateTextDisplay();
    }

    public void handleKeyPress(KeyEvent event) {
        System.out.println("--- TypingController: KeyPress Event: " + event.getCode() + " | Text: '" + event.getText() + "' | Char: '" + event.getCharacter() + "' ---");

        if (completePopupNode != null && completePopupNode.isVisible()) {
            System.out.println("TypingController: Popup visible, ignoring key press.");
            event.consume();
            return;
        }
        if (currentTextToType == null || currentTypedCharIndex >= currentTextToType.length()) {
            System.out.println("TypingController: Exercise complete or not started, ignoring key press.");
            event.consume();
            return;
        }

        String typedTextFromEvent = event.getText(); // Ini bisa berbeda dari event.getCharacter()
        KeyCode code = event.getCode();
        char typedCharInput = 0;

        if (code.isLetterKey()) {
            typedCharInput = typedTextFromEvent.toUpperCase().charAt(0);
        } else if (code == KeyCode.SPACE) {
            typedCharInput = ' ';
        } else if (code == KeyCode.SEMICOLON) { // Titik koma atau titik dua
            typedCharInput = event.isShiftDown() ? ':' : ';';
        } else if (code == KeyCode.COMMA) { // Koma atau <
            typedCharInput = event.isShiftDown() ? '<' : ',';
        } else if (code == KeyCode.PERIOD) { // Titik atau >
            typedCharInput = event.isShiftDown() ? '>' : '.';
        } else if (code == KeyCode.SLASH) { // Garis miring atau ?
            typedCharInput = event.isShiftDown() ? '?' : '/';
        } else if (code.isDigitKey()) { // Angka di baris atas (bukan numpad)
            if (event.isShiftDown()) { // Angka dengan Shift (simbol)
                switch (code) {
                    case DIGIT1: typedCharInput = '!'; break;
                    case DIGIT2: typedCharInput = '@'; break;
                    case DIGIT3: typedCharInput = '#'; break;
                    case DIGIT4: typedCharInput = '$'; break;
                    case DIGIT5: typedCharInput = '%'; break;
                    case DIGIT6: typedCharInput = '^'; break;
                    case DIGIT7: typedCharInput = '&'; break;
                    case DIGIT8: typedCharInput = '*'; break;
                    case DIGIT9: typedCharInput = '('; break;
                    case DIGIT0: typedCharInput = ')'; break;
                    default: break;
                }
            } else { // Angka tanpa Shift
                 if (typedTextFromEvent != null && !typedTextFromEvent.isEmpty()) {
                    typedCharInput = typedTextFromEvent.charAt(0);
                }
            }
        }
        // Tambahkan simbol lain jika perlu
        else if (code == KeyCode.MINUS) typedCharInput = event.isShiftDown() ? '_' : '-';
        else if (code == KeyCode.EQUALS) typedCharInput = event.isShiftDown() ? '+' : '=';
        else if (code == KeyCode.OPEN_BRACKET) typedCharInput = event.isShiftDown() ? '{' : '[';
        else if (code == KeyCode.CLOSE_BRACKET) typedCharInput = event.isShiftDown() ? '}' : ']';
        else if (code == KeyCode.BACK_SLASH) typedCharInput = event.isShiftDown() ? '|' : '\\';
        else if (code == KeyCode.QUOTE) typedCharInput = event.isShiftDown() ? '"' : '\'';
        else if (code == KeyCode.SHIFT) {
            // System.out.println("TypingController: SHIFT key pressed, consuming.");
            event.consume(); // Konsumsi event SHIFT agar tidak diproses sebagai karakter
            return;
        }
        else {
            // System.out.println("TypingController: Unhandled key code: " + code);
            event.consume();
            return;
        }

        if (typedCharInput == 0) { // Jika tidak ada karakter yang valid dihasilkan
            // System.out.println("TypingController: No valid character from key press.");
            event.consume();
            return;
        }

        char expectedChar = currentTextToType.charAt(currentTypedCharIndex);
        System.out.println("TypingController: Expected: '" + expectedChar + "', Typed: '" + typedCharInput + "'");

        if (typedCharInput == expectedChar) {
            if (wrongIcon != null) {
                wrongIcon.setVisible(false);
                wrongIcon.setManaged(false);
            }
            currentTypedCharIndex++;
            highlightCharacterVisuals();

            if (currentTypedCharIndex == currentTextToType.length()) {
                stopTimer();
                showCompletePopup();
            } else {
                if (expectedChar == ' ') {
                    snippetDisplayCounter++;
                    if (snippetDisplayCounter % SNIPPET_FREQUENCY == 0) {
                        showEducationalSnippet();
                    }
                }
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
                    textNode.setStyle(textNode.getStyle().replace("-fx-font-weight: normal;", "-fx-font-weight: bold;").replace("-fx-font-weight: bold;", "-fx-font-weight: bold;"));

                    PauseTransition pt = new PauseTransition(Duration.millis(500));
                    pt.setOnFinished(e -> {
                        if (currentTypedCharIndex < currentTextToType.length() &&
                            textToTypeDisplay.getChildren().size() > currentTypedCharIndex &&
                            textToTypeDisplay.getChildren().get(currentTypedCharIndex) == textNode) {
                             textNode.setFill(Color.web(CURRENT_CHAR_COLOR));
                             textNode.setStyle("-fx-font-weight: bold; -fx-opacity: " + DEFAULT_OPACITY + ";");
                             textNode.setFont(TEXT_FONT);
                        }
                    });
                    pt.play();
                }
            }
        }
        event.consume();
    }

    private void showEducationalSnippet() {
        // ... (sama seperti sebelumnya) ...
        if (educationalSnippets.isEmpty() || rootPage == null) return;
        String snippet = educationalSnippets.get(random.nextInt(educationalSnippets.size()));
        Label snippetLabel = new Label(snippet);
        snippetLabel.getStyleClass().add("educational-snippet");
        StackPane.setAlignment(snippetLabel, Pos.BOTTOM_CENTER);
        snippetLabel.setTranslateY(-180);
        snippetLabel.setOpacity(0);
        rootPage.getChildren().add(snippetLabel);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), snippetLabel);
        fadeIn.setToValue(1);
        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), snippetLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            if (rootPage != null) rootPage.getChildren().remove(snippetLabel);
        });
        SequentialTransition seq = new SequentialTransition(fadeIn, pause, fadeOut);
        seq.play();
    }

    private void showCompletePopup() {
        // ... (sama seperti sebelumnya) ...
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
            // ... (cek null lainnya) ...

            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            
            if (durasiTextComplete != null) durasiTextComplete.setText("Durasi " + timeFormatted + " Menit");
            
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
                rootPage.getChildren().add(completePopupNode);
                completePopupNode.requestFocus();
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat atau menampilkan popup penyelesaian: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hideCompletePopup() {
        // ... (sama seperti sebelumnya) ...
        if (completePopupNode != null && rootPage != null) {
            rootPage.getChildren().remove(completePopupNode);
            completePopupNode = null;
            Platform.runLater(() -> {
                if (rootPage != null && rootPage.getScene() != null && rootPage.getScene().getWindow() != null && rootPage.getScene().getWindow().isShowing()) {
                     rootPage.requestFocus();
                }
            });
        }
    }

    private void handleLanjutkan() {
        // ... (sama seperti sebelumnya, pastikan scene.setOnKeyPressed(nextTypingController::handleKeyPress) ada) ...
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
                
                // PENTING: Set handler di scene baru
                scene.setOnKeyPressed(nextTypingController::handleKeyPress);
                primaryStage.setScene(scene);
                nextTypingController.initContent(nextCategory, currentTheme);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman latihan berikutnya: " + e.getMessage());
            }
        }
    }

    private void handleCobaLagi() {
        // ... (sama seperti sebelumnya) ...
        hideCompletePopup();
        currentTypedCharIndex = 0;
        if (textToTypeDisplay != null) updateTextDisplay();
        startTimer();
        highlightCharacterVisuals();
        Platform.runLater(() -> {
            if (rootPage != null && rootPage.getScene() != null && rootPage.getScene().getWindow() != null && rootPage.getScene().getWindow().isShowing()) {
                rootPage.requestFocus();
            }
        });
    }

    private void handleKembaliKeMenu() {
        // ... (sama seperti sebelumnya) ...
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
        // ... (sama seperti sebelumnya) ...
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
