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
    private List<String> exerciseSessionsList;
    private int currentSessionIndex = 0;
    private String currentTextToType;
    private int currentTypedCharIndex = 0;

    private Timeline timerTimeline;
    private long categoryStartTimeMillis;

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
                    break;
                }
            }
        }
        if (timerLabel == null) {
             System.err.println("TypingController FXML Injection Error: timerLabel is null.");
        }

        if (textToTypeDisplay == null) {
             System.err.println("TypingController FXML Injection Error: textToTypeDisplay (TextFlow) is null.");
        }

        if (disableLayer != null) {
            disableLayer.setVisible(false);
            disableLayer.setOpacity(1.0); // Opacity diatur oleh CSS background rgba
        } else {
            System.err.println("TypingController FXML Injection Error: disableLayer is null.");
        }

        initializeVirtualKeyboardNodeMap();
        initializeFingerMapping();
        educationalSnippets = new ArrayList<>(MATERI_SD_KELAS_1_6);
        Collections.shuffle(educationalSnippets);
        System.out.println("TypingController: initialize END");
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
        virtualKeyboardNodeMap.put("LSHIFT", key_LSHIFT);
        virtualKeyboardNodeMap.put("RSHIFT", key_RSHIFT);

        // Verifikasi injeksi (opsional, bisa dihapus jika sudah yakin)
        // virtualKeyboardNodeMap.forEach((key, node) -> {
        //     if (node == null) System.err.println("Key " + key + " is null in virtualKeyboardNodeMap");
        // });
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

        for (char c : "QAZ1!".toCharArray()) charToFingerFxIdMap.put(c, "keyLPinky");
        for (char c : "WSX2@".toCharArray()) charToFingerFxIdMap.put(c, "keyLRing");
        for (char c : "EDC3#".toCharArray()) charToFingerFxIdMap.put(c, "keyLMiddle");
        for (char c : "RFVTGB4$5%".toCharArray()) charToFingerFxIdMap.put(c, "keyLIndex");
        charToFingerFxIdMap.put(' ', "keyLThumb");

        for (char c : "YUHJNM6^7&".toCharArray()) charToFingerFxIdMap.put(c, "keyRindex");
        for (char c : "IK8*".toCharArray()) charToFingerFxIdMap.put(c, "keyRMiddle"); charToFingerFxIdMap.put(',', "keyRMiddle"); charToFingerFxIdMap.put('<', "keyRMiddle");
        for (char c : "OL9(".toCharArray()) charToFingerFxIdMap.put(c, "keyRRing"); charToFingerFxIdMap.put('.', "keyRRing"); charToFingerFxIdMap.put('>', "keyRRing");
        for (char c : "P0)".toCharArray()) charToFingerFxIdMap.put(c, "keyRPinky"); charToFingerFxIdMap.put(';', "keyRPinky"); charToFingerFxIdMap.put(':', "keyRPinky"); charToFingerFxIdMap.put('/', "keyRPinky"); charToFingerFxIdMap.put('?', "keyRPinky");
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

    private void loadExercisesForCategory(String category) {
        exerciseSessionsList = new ArrayList<>();
        switch (category.toLowerCase()) {
            case "huruf":
                exerciseSessionsList.addAll(Arrays.asList(
                        "A AA AAA AAAA B BB BBB BBBB", "C CC CCC CCCC D DD DDD DDDD",
                        "E EE EEE EEEE I II III IIII", "F FF FFF FFFF J JJ JJJ JJJJ",
                        "G GG GGG GGGG H HH HHH HHHH", "K KK KKK KKKK L LL LLL LLLL",
                        "M MM MMM MMMM N NN NNN NNNN", "O OO OOO OOOO P PP PPP PPPP",
                        "Q QQ QQQ QQQQ R RR RRR RRRR", "S SS SSS SSSS T TT TTT TTTT"
                ));
                break;
            case "kata":
                 exerciseSessionsList.addAll(Arrays.asList(
                        "AKU KAMU DIA KITA", "SAYA MEREKA ITU INI",
                        "RUMAH SEKOLAH BUKU PENSIL", "MEJA KURSI LAMPU PINTU",
                        "MERAH PUTIH HIJAU BIRU"
                ));
                break;
            case "kalimat":
                exerciseSessionsList.addAll(Arrays.asList(
                        "AKU SUKA APEL", "SAYA CINTA INDONESIA", 
                        "MARI BELAJAR MENGETIK", "INI ADALAH RUMAH SAYA", 
                        "BUKU ITU DI ATAS MEJA"
                ));
                break;
            default:
                exerciseSessionsList.add("KATEGORI TIDAK DIKENALI.");
        }
        System.out.println("TypingController: Exercises loaded ("+ exerciseSessionsList.size() +" sesi) untuk kategori " + category);
    }

    private void startNextSession() {
        System.out.println("TypingController: startNextSession - Index: " + currentSessionIndex);
        if (currentSessionIndex < exerciseSessionsList.size()) {
            currentTextToType = exerciseSessionsList.get(currentSessionIndex).toUpperCase();
            System.out.println("TypingController: Sesi " + (currentSessionIndex + 1) + ": " + currentTextToType);
            currentTypedCharIndex = 0;
            snippetDisplayCounter = 0; 
            if (textToTypeDisplay != null) {
                 updateTextDisplay();
            } else {
                System.err.println("TypingController Error: textToTypeDisplay (TextFlow) is null. Tidak bisa memulai sesi.");
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
        System.out.println("TypingController: startTimer (untuk kategori)");
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
        System.out.println("TypingController: stopTimer (untuk kategori)");
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
        if (lastHighlightedFingerCircle != null) {
            lastHighlightedFingerCircle.getStyleClass().remove("finger-highlighted");
            lastHighlightedFingerCircle.setEffect(null);
        }

        if (currentTypedCharIndex < currentTextToType.length()) {
            char charToHighlight = currentTextToType.charAt(currentTypedCharIndex);
            String charKeyForKeyboard = String.valueOf(charToHighlight);

            if (virtualKeyboardNodeMap != null) {
                TextFlow currentKeyNode = virtualKeyboardNodeMap.get(charKeyForKeyboard);
                if (currentKeyNode != null) {
                    currentKeyNode.getStyleClass().add("key-highlighted-active");
                    lastHighlightedKeyString = charKeyForKeyboard;
                }
            }

            String fingerFxId = charToFingerFxIdMap.get(charToHighlight);
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
        System.out.println("--- TypingController: KeyPress Event RECEIVED: " + event.getCode() + " | Text: '" + event.getText() + "' ---");

        if (completePopupNode != null && completePopupNode.isVisible()) {
            System.out.println("TypingController: Popup visible, ignoring key press.");
            event.consume();
            return;
        }
        if (currentSessionIndex >= exerciseSessionsList.size()) {
             System.out.println("TypingController: Semua sesi sudah selesai, mengabaikan key press.");
             event.consume();
             return;
        }
        if (currentTextToType == null || currentTypedCharIndex >= currentTextToType.length()) {
            System.out.println("TypingController: Teks sesi saat ini selesai atau null, menunggu sesi berikutnya. Mengabaikan key press.");
            event.consume();
            return;
        }

        String typedTextFromEvent = event.getText();
        KeyCode code = event.getCode();
        char typedCharInput = 0;

        if (code.isLetterKey()) {
            if (typedTextFromEvent != null && !typedTextFromEvent.isEmpty()) {
                typedCharInput = typedTextFromEvent.toUpperCase().charAt(0);
            } else {
                event.consume(); return;
            }
        } else if (code == KeyCode.SPACE) {
            typedCharInput = ' ';
        } else if (code == KeyCode.SEMICOLON) {
            typedCharInput = event.isShiftDown() ? ':' : ';';
        } else if (code == KeyCode.COMMA) {
            typedCharInput = event.isShiftDown() ? '<' : ',';
        } else if (code == KeyCode.PERIOD) {
            typedCharInput = event.isShiftDown() ? '>' : '.';
        } else if (code == KeyCode.SLASH) {
            typedCharInput = event.isShiftDown() ? '?' : '/';
        }
        else if (code.isDigitKey()) {
             if (event.isShiftDown()) {
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
            } else {
                if (typedTextFromEvent != null && !typedTextFromEvent.isEmpty()) {
                    typedCharInput = typedTextFromEvent.charAt(0);
                }
            }
        }
        else if (code == KeyCode.MINUS) typedCharInput = event.isShiftDown() ? '_' : '-';
        else if (code == KeyCode.EQUALS) typedCharInput = event.isShiftDown() ? '+' : '=';
        else if (code == KeyCode.OPEN_BRACKET) typedCharInput = event.isShiftDown() ? '{' : '[';
        else if (code == KeyCode.CLOSE_BRACKET) typedCharInput = event.isShiftDown() ? '}' : ']';
        else if (code == KeyCode.BACK_SLASH) typedCharInput = event.isShiftDown() ? '|' : '\\';
        else if (code == KeyCode.QUOTE) typedCharInput = event.isShiftDown() ? '"' : '\'';
        else if (code == KeyCode.SHIFT || code == KeyCode.CONTROL || code == KeyCode.ALT || code == KeyCode.CAPS) {
            event.consume();
            return;
        }
        else {
            event.consume();
            return;
        }

        if (typedCharInput == 0) {
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
            
            if (currentTypedCharIndex == currentTextToType.length()) {
                currentSessionIndex++;
                System.out.println("TypingController: Sesi teks selesai. Pindah ke sesi index: " + currentSessionIndex);
                startNextSession();
            } else {
                highlightCharacterVisuals();
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

    private void showEducationalSnippet() {
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

    // Metode animasi (bisa juga dibuat sebagai static utility jika dipakai di banyak controller)
    private void applyFadeInAnimation(Node node) {
        if (node == null) return;
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), node); // Durasi lebih cepat
        fadeIn.setFromValue(0.0);
        node.setOpacity(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void applyFadeOutAnimation(Node node, Runnable onFinishedAction) {
        if (node == null) return;
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), node); // Durasi lebih cepat
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
                // 1. Tampilkan disableLayer dulu (instan atau fade cepat)
                if (disableLayer != null) {
                    disableLayer.setOpacity(0.0); // Mulai dari transparan
                    disableLayer.setVisible(true);
                    applyFadeInAnimation(disableLayer); // Fade in disableLayer
                    System.out.println("TypingController: disableLayer fade-in dimulai.");
                }

                // 2. Tambahkan popup dan fade in popup
                completePopupNode.setOpacity(0.0); // Mulai transparan
                rootPage.getChildren().add(completePopupNode);
                applyFadeInAnimation(completePopupNode);
                completePopupNode.requestFocus();
                System.out.println("TypingController: Complete popup ditampilkan dan fade-in dimulai.");
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
                // Sembunyikan disableLayer SETELAH popup selesai fade out dan dihapus
                if (disableLayer != null && disableLayer.isVisible()) {
                    applyFadeOutAnimation(disableLayer, () -> {
                        if (disableLayer != null) {
                            disableLayer.setVisible(false);
                        }
                         System.out.println("TypingController: disableLayer disembunyikan setelah popup.");
                    });
                }
            });
            this.completePopupNode = null;
        } else {
            // Jika tidak ada popup, tapi disableLayer mungkin masih terlihat
            if (disableLayer != null && disableLayer.isVisible()) {
                 applyFadeOutAnimation(disableLayer, () -> {
                    if (disableLayer != null) {
                        disableLayer.setVisible(false);
                    }
                    System.out.println("TypingController: disableLayer (tanpa popup) disembunyikan.");
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
        System.out.println("TypingController: Proses penyembunyian complete popup dimulai.");
    }


    private void handleLanjutkan() {
        System.out.println("TypingController: handleLanjutkan dipanggil.");
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
                
                System.out.println("TypingController: Memasang KeyPress handler untuk scene latihan berikutnya.");
                scene.setOnKeyPressed(nextTypingController::handleKeyPress);
                primaryStage.setScene(scene);
                nextTypingController.initContent(nextCategory, currentTheme);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman latihan berikutnya: " + e.getMessage());
            }
        } else {
            System.out.println("TypingController: Tidak ada kategori berikutnya, kembali ke menu.");
            handleKembaliKeMenu();
        }
    }

    private void handleCobaLagi() {
        System.out.println("TypingController: handleCobaLagi dipanggil.");
        hideCompletePopup();
        currentSessionIndex = 0;
        currentTypedCharIndex = 0;
        startNextSession();
    }

    private void handleKembaliKeMenu() {
        System.out.println("TypingController: handleKembaliKeMenu dipanggil.");
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
        System.out.println("TypingController: handleExitToMainMenu dipanggil (tombol X).");
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
