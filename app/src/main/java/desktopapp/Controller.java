package desktopapp;

import javafx.animation.FillTransition;
import javafx.animation.FadeTransition;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane mainPage;
    @FXML
    private AnchorPane overlayPopup;
    @FXML
    private AnchorPane settingsPopupAnchorPane;
    @FXML
    private AnchorPane mulaiPopup;
    @FXML
    private AnchorPane tutorialPage1Popup;
    @FXML
    private AnchorPane tutorialPage2Popup;
    @FXML
    private AnchorPane keluarPopup;
    @FXML
    private Pane disableLayer;
    @FXML
    private HBox infoProfil;
    @FXML
    private HBox infoSumber;
    @FXML
    private HBox infoContent;
    @FXML
    private HBox musicSwitch;
    @FXML
    private Circle musicSwitchCircle;
    @FXML
    private HBox soundEffectSwitch;
    @FXML
    private Circle soundEffectSwitchCircle;
    // Pastikan fx:id ini ada di FXML Anda jika ingin menambahkan suara klik pada mereka
    @FXML private HBox btnMulai;
    @FXML private HBox btnTutorial;
    @FXML private HBox btnKeluar;
    @FXML
    private HBox btnLatihanHuruf;
    @FXML
    private HBox btnLatihanKata;
    @FXML
    private HBox btnLatihanKalimat;
    @FXML
    private HBox btnKeluarYa;
    @FXML
    private HBox btnKeluarTidak;
    // Tombol kembali di popup info, settings, tutorial (jika ada fx:id nya)
    // @FXML private HBox btnInfoKembali, btnSettingsKembali, btnTutorialKembaliPage1, btnTutorialKembaliPage2;
    // @FXML private HBox btnTutorialLanjutPage1;


    private Node profilDeveloperNode;
    private Node sumberReferensiNode;
    private final Color SWITCH_ON_COLOR = Color.web("#ffffff");
    private final Color SWITCH_OFF_COLOR = Color.web("#79747E");
    private final Color SWITCH_TRACK_ON_COLOR = Color.web("#6FADCF");
    private final Color SWITCH_TRACK_OFF_COLOR = Color.web("#A0A0A0");
    private static final String STYLE_ACTIVE_BUTTON = "btn_system";
    private static final String STYLE_INACTIVE_BUTTON = "border_button";
    private static final String STYLE_MEDIUM_RADIUS = "medium_button_radius";
    private static final String STYLE_CONTENT_BACKGROUND = "info_background_content";
    private static final String STYLE_CONTENT_RADIUS = "adn_button_radius";
    private List<AnchorPane> allPopups;

    private static String selectedTheme = null;
    private static final Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allPopups = new ArrayList<>(Arrays.asList(
                overlayPopup, settingsPopupAnchorPane,
                mulaiPopup, tutorialPage1Popup, tutorialPage2Popup, keluarPopup
        ));

        for (AnchorPane popup : allPopups) {
            if (popup != null) {
                popup.setVisible(false);
            }
        }
        if (disableLayer != null) {
            disableLayer.setVisible(false);
        }

        profilDeveloperNode = createProfilDeveloperContent();
        sumberReferensiNode = createSumberReferensiContent();
        if (profilDeveloperNode != null && infoContent != null) {
            infoContent.getChildren().setAll(profilDeveloperNode);
        }
        updateButtonStyles(true);

        updateSwitchVisuals(musicSwitch, musicSwitchCircle, AudioManager.isMusicEnabled(), "Musik");
        updateSwitchVisuals(soundEffectSwitch, soundEffectSwitchCircle, AudioManager.isSoundEffectsEnabled(), "Efek Suara");
    }

    private void showSpecificPopup(AnchorPane popupToShow) {
        AudioManager.playClickSound(); // Mainkan SFX klik umum saat popup akan muncul
        for (AnchorPane popup : allPopups) {
            if (popup != null && popup != popupToShow) {
                popup.setVisible(false);
            }
        }
        if (popupToShow != null) {
            popupToShow.setVisible(true);
            applyFadeInAnimation(popupToShow);
        }
        if (disableLayer != null) {
            boolean shouldDisable = popupToShow != null && popupToShow.isVisible();
            if (shouldDisable && !disableLayer.isVisible()) {
                disableLayer.setVisible(true);
                applyFadeInAnimation(disableLayer);
            } else if (!shouldDisable && disableLayer.isVisible()) {
                applyFadeOutAnimation(disableLayer, () -> disableLayer.setVisible(false));
            }
        }
    }

    @FXML
    private void showInfo(MouseEvent event) {
        // AudioManager.playClickSound(); // Sudah dipanggil di showSpecificPopup jika ini memicu popup
        showProfilContent();
        showSpecificPopup(overlayPopup); // Ini akan memutar suara klik
    }

    @FXML
    void handleInfoProfilClicked(MouseEvent event) {
        AudioManager.playClickSound();
        showProfilContent();
    }

    @FXML
    void handleInfoSumberClicked(MouseEvent event) {
        AudioManager.playClickSound();
        showSumberContent();
    }

    private void updateButtonStyles(boolean isProfilActive) {
        if (infoProfil != null && infoSumber != null) {
            infoProfil.getStyleClass().setAll(isProfilActive ? STYLE_ACTIVE_BUTTON : STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
            infoSumber.getStyleClass().setAll(!isProfilActive ? STYLE_ACTIVE_BUTTON : STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
        }
    }

    private void showProfilContent() {
        updateButtonStyles(true);
        if (infoContent != null && profilDeveloperNode != null) {
            infoContent.getChildren().setAll(profilDeveloperNode);
            infoContent.getStyleClass().setAll(STYLE_CONTENT_BACKGROUND, STYLE_CONTENT_RADIUS);
            infoContent.setAlignment(Pos.CENTER);
        }
    }

    private void showSumberContent() {
        updateButtonStyles(false);
        if (infoContent != null && sumberReferensiNode != null) {
            infoContent.getChildren().setAll(sumberReferensiNode);
            infoContent.getStyleClass().setAll(STYLE_CONTENT_BACKGROUND, STYLE_CONTENT_RADIUS);
        }
    }

    @FXML
    private void showSettingsPopup(MouseEvent event) {
        showSpecificPopup(settingsPopupAnchorPane); // Ini akan memutar suara klik
    }

    @FXML
    private void toggleMusic(MouseEvent event) {
        AudioManager.playClickSound();
        AudioManager.setMusicEnabled(!AudioManager.isMusicEnabled());
        updateSwitchVisuals(musicSwitch, musicSwitchCircle, AudioManager.isMusicEnabled(), "Musik");
        System.out.println("Controller: Musik toggled to: " + (AudioManager.isMusicEnabled() ? "ON" : "OFF"));
    }

    @FXML
    private void toggleSoundEffect(MouseEvent event) {
        AudioManager.playClickSound();
        AudioManager.setSoundEffectsEnabled(!AudioManager.isSoundEffectsEnabled());
        updateSwitchVisuals(soundEffectSwitch, soundEffectSwitchCircle, AudioManager.isSoundEffectsEnabled(), "Efek Suara");
        System.out.println("Controller: Efek Suara toggled to: " + (AudioManager.isSoundEffectsEnabled() ? "ON" : "OFF"));
    }


    private void updateSwitchVisuals(HBox switchBox, Circle switchCircle, boolean isEnabled, String switchName) {
        if (switchBox == null || switchCircle == null) return;
        switchBox.setAlignment(isEnabled ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        switchBox.setStyle("-fx-background-color: " + toWebColor(isEnabled ? SWITCH_TRACK_ON_COLOR : SWITCH_TRACK_OFF_COLOR) + "; -fx-background-radius: 20;");
        FillTransition ft = new FillTransition(Duration.millis(150), switchCircle);
        ft.setToValue(isEnabled ? SWITCH_ON_COLOR : SWITCH_OFF_COLOR);
        ft.play();
    }

    private String toWebColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @FXML
    private void showMulaiPopup(MouseEvent event) {
        showSpecificPopup(mulaiPopup); // Ini akan memutar suara klik
    }

    @FXML
    private void showTutorialPopup(MouseEvent event) {
        showSpecificPopup(tutorialPage1Popup); // Ini akan memutar suara klik
    }

    @FXML
    private void showKeluarPopup(MouseEvent event) {
        showSpecificPopup(keluarPopup); // Ini akan memutar suara klik
    }

    private void ensureThemeSelected() {
        if (selectedTheme == null) {
            selectedTheme = random.nextBoolean() ? "ori" : "darkmode";
            System.out.println("Tema dipilih untuk sesi ini: " + selectedTheme);
        }
    }

    @FXML
    private void handlePilihLatihanHuruf(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Pilihan Latihan: Huruf");
        ensureThemeSelected();
        switchToLatihanScene("huruf", selectedTheme);
        closeActivePopup(null); // Tidak perlu suara klik lagi di sini karena popup sudah ditutup
    }

    @FXML
    private void handlePilihLatihanKata(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Pilihan Latihan: Kata");
        ensureThemeSelected();
        switchToLatihanScene("kata", selectedTheme);
        closeActivePopup(null);
    }

    @FXML
    private void handlePilihLatihanKalimat(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Pilihan Latihan: Kalimat");
        ensureThemeSelected();
        switchToLatihanScene("kalimat", selectedTheme);
        closeActivePopup(null);
    }

    private void switchToLatihanScene(String category, String theme) {
        AudioManager.stopMainMusic(); // Hentikan musik utama saat pindah ke scene latihan
        try {
            String fxmlFile;
            if (theme.equals("ori")) {
                 fxmlFile = "/latihan_" + category + ".fxml";
            } else { 
                 fxmlFile = "/latihan_" + category + "_" + theme + ".fxml";
            }
            // Jika Anda menggunakan satu file FXML untuk semua latihan:
            // fxmlFile = "/latihan_view.fxml"; // Ganti ini jika Anda sudah beralih ke satu file

            FXMLLoader loader = App.getLoader(fxmlFile);
            Parent typingRoot = loader.load();

            TypingController typingController = loader.getController();
            if (typingController == null) {
                System.err.println("Controller ERROR: TypingController tidak terinisialisasi dari FXML: " + fxmlFile);
                return;
            }

            Stage primaryStage = App.getPrimaryStage();
            Scene scene = new Scene(typingRoot, primaryStage.getWidth(), primaryStage.getHeight());
            String cssPath = getClass().getResource("/style.css").toExternalForm();
             if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }
            
            scene.setOnKeyPressed(typingController::handleKeyPress);
            // scene.setOnKeyReleased(typingController::handleKeyReleased); // Dihapus jika tidak ada logika shift case

            primaryStage.setScene(scene);
            
            typingController.initContent(category, theme);
            // Musik untuk halaman latihan akan dimulai dari TypingController.initContent() jika ada

        } catch (Exception e) {
            System.err.println("Controller ERROR: Gagal memuat halaman latihan: " + category + " tema: " + theme);
            e.printStackTrace();
             AudioManager.playMainMusic(); // Putar kembali musik utama jika gagal pindah scene
        }
    }


    @FXML
    private void handleTutorialLanjut(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Tombol Lanjut Tutorial (dari Hal 1 ke Hal 2) diklik.");
        showSpecificPopup(tutorialPage2Popup);
    }

    @FXML
    private void handleTutorialKembaliKeHalaman1(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Tombol Kembali Tutorial (dari Hal 2 ke Hal 1) diklik.");
        showSpecificPopup(tutorialPage1Popup);
    }

    @FXML
    private void handleKeluarYa(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Keluar dari aplikasi.");
        Platform.exit(); // Ini akan memicu App.stop()
    }

    @FXML
    private void handleKeluarTidak(MouseEvent event) {
        AudioManager.playClickSound();
        System.out.println("Batal keluar.");
        closeActivePopup(null); // Tidak perlu suara klik lagi
    }

    @FXML
    private void closeActivePopup(MouseEvent event) {
        // Jika event null (dipanggil programatik), jangan mainkan suara.
        // Jika event tidak null (diklik pengguna), mainkan suara.
        // Namun, showSpecificPopup sudah memainkan suara, jadi mungkin tidak perlu di sini
        // kecuali jika ada tombol "Kembali" spesifik di dalam popup yang langsung memanggil ini.
        if (event != null) {
             AudioManager.playClickSound();
        }

        boolean popupWasVisible = false;
        for (AnchorPane popup : allPopups) {
            if (popup != null && popup.isVisible()) {
                final AnchorPane currentPopup = popup;
                applyFadeOutAnimation(currentPopup, () -> currentPopup.setVisible(false));
                popupWasVisible = true;
            }
        }
        if (popupWasVisible && disableLayer != null && disableLayer.isVisible()) {
            applyFadeOutAnimation(disableLayer, () -> disableLayer.setVisible(false));
        }
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

    private Node createProfilDeveloperContent() {
        HBox developersContainer = new HBox();
        developersContainer.setAlignment(Pos.CENTER);
        developersContainer.setSpacing(25.0);
        VBox fricoVBox = createSingleDeveloperVBox("/desain/default_avatar.png", "Frico Putung", "220211060359", 130.0);
        VBox syifabelaVBox = createSingleDeveloperVBox("/desain/default_avatar.png", "Syifabela Suratinoyo", "220211060344", 150.0);
        VBox kevinVBox = createSingleDeveloperVBox("/desain/default_avatar.png", "Kevin Rimper", "220211060364", 130.0);
        developersContainer.getChildren().addAll(fricoVBox, syifabelaVBox, kevinVBox);
        return developersContainer;
    }

    private VBox createSingleDeveloperVBox(String avatarResourcePath, String nama, String nim, double prefWidth) {
        VBox devVBox = new VBox();
        devVBox.setAlignment(Pos.CENTER);
        devVBox.setPrefHeight(200.0);
        devVBox.setPrefWidth(prefWidth);
        devVBox.setSpacing(25.0);
        HBox avatarHBox = new HBox();
        avatarHBox.setMaxHeight(100.0);
        avatarHBox.setMaxWidth(100.0);
        avatarHBox.setPrefHeight(100.0);
        avatarHBox.setPrefWidth(100.0);
        avatarHBox.getStyleClass().addAll("info_background", "info_avatar");
        Label namaLabel = new Label(nama);
        namaLabel.setTextFill(Color.WHITE);
        namaLabel.setFont(Font.font("Lapsus Pro Bold", 18.0));
        HBox namaHBox = new HBox(namaLabel);
        namaHBox.setAlignment(Pos.CENTER);
        namaHBox.setPrefHeight(30.0);
        Label nimLabel = new Label(nim);
        nimLabel.setTextFill(Color.WHITE);
        nimLabel.setFont(Font.font("Lapsus Pro Bold", 16.0));
        HBox nimHBox = new HBox(nimLabel);
        nimHBox.setAlignment(Pos.CENTER);
        nimHBox.setPrefHeight(30.0);
        VBox.setMargin(nimHBox, new Insets(-30.0, 0, 0, 0));
        devVBox.getChildren().addAll(avatarHBox, namaHBox, nimHBox);
        return devVBox;
    }

    private Node createSumberReferensiContent() {
        VBox contentVBox = new VBox(15);
        contentVBox.setPadding(new Insets(15, 20, 15, 20));
        contentVBox.setAlignment(Pos.TOP_LEFT);
        Label title = new Label("Sumber Pembelajaran & Referensi");
        title.setFont(Font.font("Lapsus Pro Bold", 22)); title.setTextFill(Color.WHITE); title.setWrapText(true);
        Label descMateri = new Label("Materi Pembelajaran:");
        descMateri.setFont(Font.font("Lapsus Pro Bold", 18)); descMateri.setTextFill(Color.WHITE); VBox.setMargin(descMateri, new Insets(10,0,5,0));
        Label materi1 = new Label("- Konten game ini diadaptasi dari buku-buku resmi Bahasa Indonesia Kurikulum Merdeka dari Kemendikbudristek untuk SD Kelas 1 dan 2."); // Diperbarui
        materi1.setFont(Font.font("Lapsus Pro Regular", 14)); materi1.setTextFill(Color.LIGHTGRAY); materi1.setWrapText(true);
        Label descTeknologi = new Label("Teknologi yang Digunakan:");
        descTeknologi.setFont(Font.font("Lapsus Pro Bold", 18)); descTeknologi.setTextFill(Color.WHITE); VBox.setMargin(descTeknologi, new Insets(15,0,5,0));
        Label tech1 = new Label("- JavaFX: Framework utama untuk membangun Antarmuka Pengguna Grafis (GUI).");
        tech1.setFont(Font.font("Lapsus Pro Regular", 14)); tech1.setTextFill(Color.LIGHTGRAY); tech1.setWrapText(true);
        Label tech2 = new Label("- Scene Builder: Alat bantu visual untuk mendesain tata letak FXML.");
        tech2.setFont(Font.font("Lapsus Pro Regular", 14)); tech2.setTextFill(Color.LIGHTGRAY); tech2.setWrapText(true);
        Label tech3 = new Label("- Visual Studio Code: Integrated Development Environment (IDE) untuk penulisan kode.");
        tech3.setFont(Font.font("Lapsus Pro Regular", 14)); tech3.setTextFill(Color.LIGHTGRAY); tech3.setWrapText(true);
        Label descAset = new Label("Aset Grafis & Font:");
        descAset.setFont(Font.font("Lapsus Pro Bold", 18)); descAset.setTextFill(Color.WHITE); VBox.setMargin(descAset, new Insets(15,0,5,0));
        Label aset1 = new Label("- Sebagian ikon dan elemen desain dibuat khusus untuk aplikasi KidBoard.");
        aset1.setFont(Font.font("Lapsus Pro Regular", 14)); aset1.setTextFill(Color.LIGHTGRAY); aset1.setWrapText(true);
        Label aset2 = new Label("- Font \"Lapsus Pro\" dan \"Rubik\" digunakan untuk konsistensi visual."); // Diperbarui
        aset2.setFont(Font.font("Lapsus Pro Regular", 14)); aset2.setTextFill(Color.LIGHTGRAY); aset2.setWrapText(true);
        contentVBox.getChildren().addAll(title, descMateri, materi1, descTeknologi, tech1, tech2, tech3, descAset, aset1, aset2);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentVBox);
        scrollPane.setFitToWidth(true);
        if (infoContent != null) {
            scrollPane.setPrefSize(infoContent.getPrefWidth() - 10, infoContent.getPrefHeight() - 10);
        } else {
            scrollPane.setPrefSize(530, 290);
        }
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("sumber-referensi-scrollpane");
        return scrollPane;
    }
}
