package desktopapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private Label timerLabel;
    @FXML
    private TextFlow textToTypeDisplay;
    @FXML
    private ImageView exitButton;

    @FXML private HBox keyQ, keyW, keyE, keyR, keyT, keyY, keyU, keyI, keyO, keyP;
    @FXML private HBox keyA, keyS, keyD, keyF, keyG, keyH, keyJ, keyK, keyL, keySemicolon;
    @FXML private HBox keyZ, keyX, keyC, keyV, keyB, keyN, keyM, keyComma, keyPeriod, keySlash;
    @FXML private HBox keySpace;

    private Map<String, HBox> virtualKeyboardMap;

    private AnchorPane completePopupNode;
    private Text durasiTextComplete;
    private HBox btnLanjutkanComplete;
    private HBox btnCobaLagiComplete;
    private HBox btnKembaliComplete;

    private String currentCategory;
    private String currentTheme; // Akan berisi "ori" atau "darkmode"
    private List<String> currentExerciseList;
    private int currentExerciseIndex = 0;
    private String currentTextToType;
    private int currentTypedCharIndex = 0;

    private Timeline timerTimeline;
    private long startTimeMillis;
    private final Random random = new Random();

    private List<String> educationalSnippets;
    private static final List<String> MATERI_SD_KELAS_1_6 = Arrays.asList(
        "Tahukah kamu? Indonesia adalah negara kepulauan terbesar di dunia!",
        "Pancasila adalah dasar negara kita. Sila pertama adalah Ketuhanan Yang Maha Esa.",
        "Bhinneka Tunggal Ika artinya berbeda-beda tetapi tetap satu jua."
        // ... (tambahkan materi lainnya)
    );
    private int snippetDisplayCounter = 0;
    private final int SNIPPET_FREQUENCY = 5;
    private String lastHighlightedKey = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeVirtualKeyboardMap();
        educationalSnippets = new ArrayList<>(MATERI_SD_KELAS_1_6);
        Collections.shuffle(educationalSnippets);

        if (exitButton != null) {
            exitButton.setOnMouseClicked(event -> handleExitToMainMenu());
        } else {
            System.out.println("exitButton (tombol X) tidak ditemukan di FXML latihan.");
        }
    }

    private void initializeVirtualKeyboardMap() {
        virtualKeyboardMap = new HashMap<>();
        // Baris atas
        if (keyQ != null) virtualKeyboardMap.put("Q", keyQ); else System.err.println("keyQ is null");
        if (keyW != null) virtualKeyboardMap.put("W", keyW); else System.err.println("keyW is null");
        if (keyE != null) virtualKeyboardMap.put("E", keyE); else System.err.println("keyE is null");
        if (keyR != null) virtualKeyboardMap.put("R", keyR); else System.err.println("keyR is null");
        if (keyT != null) virtualKeyboardMap.put("T", keyT); else System.err.println("keyT is null");
        if (keyY != null) virtualKeyboardMap.put("Y", keyY); else System.err.println("keyY is null");
        if (keyU != null) virtualKeyboardMap.put("U", keyU); else System.err.println("keyU is null");
        if (keyI != null) virtualKeyboardMap.put("I", keyI); else System.err.println("keyI is null");
        if (keyO != null) virtualKeyboardMap.put("O", keyO); else System.err.println("keyO is null");
        if (keyP != null) virtualKeyboardMap.put("P", keyP); else System.err.println("keyP is null");
        // Baris tengah
        if (keyA != null) virtualKeyboardMap.put("A", keyA); else System.err.println("keyA is null");
        if (keyS != null) virtualKeyboardMap.put("S", keyS); else System.err.println("keyS is null");
        if (keyD != null) virtualKeyboardMap.put("D", keyD); else System.err.println("keyD is null");
        if (keyF != null) virtualKeyboardMap.put("F", keyF); else System.err.println("keyF is null");
        if (keyG != null) virtualKeyboardMap.put("G", keyG); else System.err.println("keyG is null");
        if (keyH != null) virtualKeyboardMap.put("H", keyH); else System.err.println("keyH is null");
        if (keyJ != null) virtualKeyboardMap.put("J", keyJ); else System.err.println("keyJ is null");
        if (keyK != null) virtualKeyboardMap.put("K", keyK); else System.err.println("keyK is null");
        if (keyL != null) virtualKeyboardMap.put("L", keyL); else System.err.println("keyL is null");
        if (keySemicolon != null) virtualKeyboardMap.put(";", keySemicolon); else System.err.println("keySemicolon is null");
        // Baris bawah
        if (keyZ != null) virtualKeyboardMap.put("Z", keyZ); else System.err.println("keyZ is null");
        if (keyX != null) virtualKeyboardMap.put("X", keyX); else System.err.println("keyX is null");
        if (keyC != null) virtualKeyboardMap.put("C", keyC); else System.err.println("keyC is null");
        if (keyV != null) virtualKeyboardMap.put("V", keyV); else System.err.println("keyV is null");
        if (keyB != null) virtualKeyboardMap.put("B", keyB); else System.err.println("keyB is null");
        if (keyN != null) virtualKeyboardMap.put("N", keyN); else System.err.println("keyN is null");
        if (keyM != null) virtualKeyboardMap.put("M", keyM); else System.err.println("keyM is null");
        if (keyComma != null) virtualKeyboardMap.put(",", keyComma); else System.err.println("keyComma is null");
        if (keyPeriod != null) virtualKeyboardMap.put(".", keyPeriod); else System.err.println("keyPeriod is null");
        if (keySlash != null) virtualKeyboardMap.put("/", keySlash); else System.err.println("keySlash is null");
        // Spacebar
        if (keySpace != null) virtualKeyboardMap.put(" ", keySpace); else System.err.println("keySpace is null");
    }

    public void initContent(String category, String theme) {
        this.currentCategory = category;
        this.currentTheme = theme; // "ori" atau "darkmode"
        this.currentExerciseIndex = 0;
        this.currentTypedCharIndex = 0;

        loadExercisesForCategory(category);
        startNextExercise();

        if (rootPage != null) {
            rootPage.setFocusTraversable(true);
            rootPage.setOnKeyPressed(this::handleKeyPress);
            Platform.runLater(() -> rootPage.requestFocus());
        } else {
            System.err.println("rootPage is null in TypingController. Periksa injeksi FXML.");
        }
    }

    private void loadExercisesForCategory(String category) {
        currentExerciseList = new ArrayList<>();
        switch (category.toLowerCase()) {
            case "huruf":
                currentExerciseList.addAll(Arrays.asList(
                        "asdf jkl;", "fjdksla;", "aa ss dd ff jj kk ll ;;",
                        "qwert yuiop", "trewq poiuy", "qq ww ee rr tt yy uu ii oo pp"
                ));
                break;
            case "kata":
                currentExerciseList.addAll(Arrays.asList(
                        "aku", "kamu", "dia", "kita", "mereka",
                        "rumah", "sekolah", "buku", "pensil", "meja", "kursi",
                        "merah", "kuning", "hijau", "biru", "putih", "hitam"
                ));
                break;
            case "kalimat":
                currentExerciseList.addAll(Arrays.asList(
                        "saya suka membaca buku cerita.",
                        "ibu pergi ke pasar pagi ini.",
                        "adik sedang bermain bola di taman.",
                        "langit hari ini sangat cerah.",
                        "kita harus rajin belajar setiap hari."
                ));
                break;
            default:
                currentExerciseList.add("Kategori tidak ditemukan.");
        }
        Collections.shuffle(currentExerciseList);
    }

    private void startNextExercise() {
        if (currentExerciseIndex < currentExerciseList.size()) {
            currentTextToType = currentExerciseList.get(currentExerciseIndex);
            currentTypedCharIndex = 0;
            snippetDisplayCounter = 0;
            if (textToTypeDisplay != null) {
                 updateTextDisplay();
            } else {
                System.err.println("textToTypeDisplay is null. Periksa injeksi FXML.");
            }
            startTimer();
            highlightNextKey();
        } else {
            System.out.println("Semua latihan untuk kategori ini selesai.");
            handleExitToMainMenu();
        }
    }

    private void startTimer() {
        startTimeMillis = System.currentTimeMillis();
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            if (timerLabel != null) {
                timerLabel.setText(timeFormatted);
            }
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }

    private void updateTextDisplay() {
        if (textToTypeDisplay == null || currentTextToType == null) {
            System.err.println("textToTypeDisplay atau currentTextToType null. Tidak bisa update.");
            return;
        }
        textToTypeDisplay.getChildren().clear();
        for (int i = 0; i < currentTextToType.length(); i++) {
            Text charText = new Text(String.valueOf(currentTextToType.charAt(i)));
            if (i < currentTypedCharIndex) {
                charText.getStyleClass().add("typed-char-correct");
            } else if (i == currentTypedCharIndex) {
                charText.getStyleClass().add("current-char-to-type");
            } else {
                charText.getStyleClass().add("pending-char");
            }
            textToTypeDisplay.getChildren().add(charText);
        }
    }

    private void highlightNextKey() {
        if (lastHighlightedKey != null && virtualKeyboardMap.containsKey(lastHighlightedKey.toUpperCase())) {
            Node keyNode = virtualKeyboardMap.get(lastHighlightedKey.toUpperCase());
            if (keyNode != null) {
                keyNode.getStyleClass().remove("key-highlighted");
            }
        } else if (lastHighlightedKey != null && lastHighlightedKey.equals(" ")) {
             Node keyNode = virtualKeyboardMap.get(" ");
             if (keyNode != null) keyNode.getStyleClass().remove("key-highlighted");
        }

        if (currentTypedCharIndex < currentTextToType.length()) {
            String charToType = String.valueOf(currentTextToType.charAt(currentTypedCharIndex));
            String mapKey = charToType.toUpperCase();
            if (charToType.equals(" ")) {
                mapKey = " ";
            }

            if (virtualKeyboardMap.containsKey(mapKey)) {
                 Node keyNode = virtualKeyboardMap.get(mapKey);
                 if (keyNode != null) {
                    keyNode.getStyleClass().add("key-highlighted");
                    lastHighlightedKey = charToType;
                 } else {
                    System.err.println("Node untuk key '" + mapKey + "' null di virtualKeyboardMap.");
                 }
            } else {
                 System.out.println("Key '" + mapKey + "' tidak ditemukan di virtualKeyboardMap untuk highlight.");
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (currentTextToType == null || currentTypedCharIndex >= currentTextToType.length()) {
            return;
        }

        String typedCharStr = event.getText();
        char expectedChar = currentTextToType.charAt(currentTypedCharIndex);

        if (typedCharStr != null && typedCharStr.length() == 1) {
            char typedChar = typedCharStr.charAt(0);
            if (typedChar == expectedChar) {
                currentTypedCharIndex++;
                updateTextDisplay();
                highlightNextKey();

                if (currentTypedCharIndex == currentTextToType.length()) {
                    stopTimer();
                    showCompletePopup();
                } else {
                    if (currentCategory.equals("kata") || currentCategory.equals("kalimat")) {
                        if (expectedChar == ' ') {
                            snippetDisplayCounter++;
                            if (snippetDisplayCounter % SNIPPET_FREQUENCY == 0) {
                                showEducationalSnippet();
                            }
                        }
                    } else if (currentCategory.equals("huruf")) {
                        snippetDisplayCounter++;
                        if (snippetDisplayCounter % (SNIPPET_FREQUENCY * 5) == 0) {
                            showEducationalSnippet();
                        }
                    }
                }
            } else {
                System.out.println("Salah ketik! Diharapkan: " + expectedChar + ", Diketik: " + typedChar);
                if (textToTypeDisplay.getChildren().size() > currentTypedCharIndex) {
                    Node charNode = textToTypeDisplay.getChildren().get(currentTypedCharIndex);
                    charNode.getStyleClass().add("typed-char-incorrect");
                    Timeline t = new Timeline(new KeyFrame(Duration.millis(500), ae -> charNode.getStyleClass().remove("typed-char-incorrect")));
                    t.play();
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
        
        StackPane.setAlignment(snippetLabel, javafx.geometry.Pos.BOTTOM_CENTER);
        javafx.geometry.Insets margin = new javafx.geometry.Insets(0,0,280,0);
        StackPane.setMargin(snippetLabel, margin);

        rootPage.getChildren().add(snippetLabel);

        Timeline snippetTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (rootPage != null) rootPage.getChildren().remove(snippetLabel);
        }));
        snippetTimer.play();
    }

    private void showCompletePopup() {
        try {
            // Penyesuaian nama file popup berdasarkan tema
            String fxmlFile;
            if (currentTheme.equals("darkmode")) {
                fxmlFile = "/complete_popup_darkmode.fxml";
            } else { // Asumsi tema "ori"
                fxmlFile = "/complete_popup.fxml";
            }
            System.out.println("Mencoba memuat popup: " + fxmlFile);

            FXMLLoader loader = App.getLoader(fxmlFile);
            Parent popupRootParent = loader.load();

            if (popupRootParent instanceof StackPane) {
                StackPane loadedStackPane = (StackPane) popupRootParent;
                if (loadedStackPane.getChildren().size() > 2 && loadedStackPane.getChildren().get(2) instanceof AnchorPane) {
                     completePopupNode = (AnchorPane) loadedStackPane.getChildren().get(2);
                } else {
                    System.err.println("Struktur FXML complete_popup tidak seperti yang diharapkan untuk menemukan mainPage AnchorPane.");
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

            if (durasiTextComplete == null) System.err.println("#durasiTextComplete tidak ditemukan di popup FXML.");
            if (btnLanjutkanComplete == null) System.err.println("#btnLanjutkanComplete tidak ditemukan di popup FXML.");
            if (btnCobaLagiComplete == null) System.err.println("#btnCobaLagiComplete tidak ditemukan di popup FXML.");
            if (btnKembaliComplete == null) System.err.println("#btnKembaliComplete tidak ditemukan di popup FXML.");

            long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
            String timeFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60);
            
            if (durasiTextComplete != null) {
                durasiTextComplete.setText("Durasi " + timeFormatted + " Menit");
            }
            
            if (btnCobaLagiComplete != null) {
                btnCobaLagiComplete.setOnMouseClicked(event -> handleCobaLagi());
            }
            if (btnKembaliComplete != null) {
                btnKembaliComplete.setOnMouseClicked(event -> handleKembaliKeMenu());
            }
            
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
            } else {
                System.err.println("rootPage dari TypingController null, tidak bisa menambahkan popup.");
            }

        } catch (IOException e) {
            System.err.println("Gagal memuat popup penyelesaian: " + e.getMessage());
            e.printStackTrace();
        }  catch (NullPointerException e) {
            System.err.println("NullPointerException saat memuat atau memproses popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hideCompletePopup() {
        if (completePopupNode != null && rootPage != null) {
            rootPage.getChildren().remove(completePopupNode);
            completePopupNode = null;
            rootPage.requestFocus();
        }
    }

    private void handleLanjutkan() {
        hideCompletePopup();
        String nextCategory = "";
        if (currentCategory.equals("huruf")) {
            nextCategory = "kata";
        } else if (currentCategory.equals("kata")) {
            nextCategory = "kalimat";
        }

        if (!nextCategory.isEmpty()) {
            try {
                // Penyesuaian nama file latihan berikutnya berdasarkan tema
                String fxmlFile;
                if (currentTheme.equals("darkmode")) {
                    fxmlFile = "/latihan_" + nextCategory + "_darkmode.fxml";
                } else { // Asumsi tema "ori"
                    fxmlFile = "/latihan_" + nextCategory + ".fxml";
                }
                System.out.println("Mencoba memuat latihan berikutnya: " + fxmlFile);

                FXMLLoader loader = App.getLoader(fxmlFile);
                Parent typingRoot = loader.load();
                TypingController nextTypingController = loader.getController();
                 if (nextTypingController == null) {
                    System.err.println("Gagal mendapatkan TypingController untuk kategori berikutnya dari: " + fxmlFile);
                    return;
                }
                nextTypingController.initContent(nextCategory, currentTheme);

                Stage primaryStage = App.getPrimaryStage();
                Scene scene = new Scene(typingRoot, primaryStage.getWidth(), primaryStage.getHeight());
                String cssPath = getClass().getResource("/style.css").toExternalForm();
                if (cssPath != null) {
                    scene.getStylesheets().add(cssPath);
                }
                primaryStage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Gagal memuat halaman latihan berikutnya.");
            } catch (IllegalStateException e) {
                System.err.println("Error mendapatkan primary stage untuk melanjutkan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleCobaLagi() {
        hideCompletePopup();
        currentTypedCharIndex = 0;
        if (textToTypeDisplay != null) updateTextDisplay();
        startTimer();
        highlightNextKey();
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
            if (cssPath != null) {
                mainScene.getStylesheets().add(cssPath);
            }
            primaryStage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal kembali ke menu utama.");
        } catch (IllegalStateException e) {
            System.err.println("Error mendapatkan primary stage untuk kembali ke menu: " + e.getMessage());
            e.printStackTrace();
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
            if (cssPath != null) {
                mainScene.getStylesheets().add(cssPath);
            }
            primaryStage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal keluar ke menu utama dari tombol X.");
        } catch (IllegalStateException e) {
            System.err.println("Error mendapatkan primary stage untuk keluar dari X: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
