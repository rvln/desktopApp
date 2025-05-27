package desktopapp;

import javafx.animation.FillTransition;
// import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane mainPage;

    // Popups
    @FXML
    private AnchorPane overlayPopup; // Info popup
    @FXML
    private AnchorPane settingsPopupAnchorPane; // Settings popup
    @FXML
    private AnchorPane mulaiPopup; // CTA Mulai popup
    @FXML
    private AnchorPane tutorialPage1Popup; // CTA Tutorial popup - Halaman 1 (rename dari tutorialPopup)
    @FXML
    private AnchorPane tutorialPage2Popup; // CTA Tutorial popup - Halaman 2 (BARU)
    @FXML
    private AnchorPane keluarPopup; // CTA Keluar popup


    @FXML
    private Pane disableLayer;

    // Untuk Info Popup
    @FXML
    private HBox infoProfil;
    @FXML
    private HBox infoSumber;
    @FXML
    private HBox infoContent;

    private Node profilDeveloperNode;
    private Node sumberReferensiNode;

    // Untuk Settings Popup Switches
    @FXML
    private HBox musicSwitch;
    @FXML
    private Circle musicSwitchCircle;
    @FXML
    private HBox soundEffectSwitch;
    @FXML
    private Circle soundEffectSwitchCircle;

    // Untuk tombol di dalam Mulai Popup
    @FXML
    private HBox btnLatihanHuruf;
    @FXML
    private HBox btnLatihanKata;
    @FXML
    private HBox btnLatihanKalimat;

    // Untuk tombol di dalam Keluar Popup
    @FXML
    private HBox btnKeluarYa;
    @FXML
    private HBox btnKeluarTidak;

    // Untuk tombol di dalam Tutorial Popup (Page 1 & 2)
    // fx:id untuk tombol kembali di tutorialPage1Popup mungkin tidak perlu jika hanya memanggil closeActivePopup
    // fx:id untuk tombol lanjut di tutorialPage1Popup (btnTutorialLanjutPage1) sudah ada di FXML
    // fx:id untuk tombol kembali di tutorialPage2Popup (btnTutorialKembaliPage2) sudah ada di FXML


    // State untuk switches
    private boolean isMusicEnabled = true;
    private boolean isSoundEffectsEnabled = true;

    // Konstanta warna untuk switch
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tambahkan tutorialPage2Popup ke daftar dan pastikan nama tutorialPage1Popup benar
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

        updateSwitchVisuals(musicSwitch, musicSwitchCircle, isMusicEnabled, "Musik");
        updateSwitchVisuals(soundEffectSwitch, soundEffectSwitchCircle, isSoundEffectsEnabled, "Efek Suara");
    }

    private void showSpecificPopup(AnchorPane popupToShow) {
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
            disableLayer.setVisible(true);
            applyFadeInAnimation(disableLayer);
        }
    }

    @FXML
    private void showInfo(MouseEvent event) {
        showProfilContent();
        showSpecificPopup(overlayPopup);
    }

    // ... (Metode Info Popup lainnya tetap sama) ...
    @FXML
    void handleInfoProfilClicked(MouseEvent event) {
        showProfilContent();
    }

    @FXML
    void handleInfoSumberClicked(MouseEvent event) {
        showSumberContent();
    }

    private void updateButtonStyles(boolean isProfilActive) {
        if (infoProfil != null && infoSumber != null) {
            if (isProfilActive) {
                infoProfil.getStyleClass().setAll(STYLE_ACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
                infoSumber.getStyleClass().setAll(STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
            } else {
                infoSumber.getStyleClass().setAll(STYLE_ACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
                infoProfil.getStyleClass().setAll(STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
            }
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
        showSpecificPopup(settingsPopupAnchorPane);
    }

    // ... (Metode Settings Popup lainnya tetap sama) ...
    @FXML
    private void toggleMusic(MouseEvent event) {
        isMusicEnabled = !isMusicEnabled;
        updateSwitchVisuals(musicSwitch, musicSwitchCircle, isMusicEnabled, "Musik");
        System.out.println("Musik: " + (isMusicEnabled ? "ON" : "OFF"));
        // TODO: Tambahkan logika untuk benar-benar memutar/menghentikan musik
    }

    @FXML
    private void toggleSoundEffect(MouseEvent event) {
        isSoundEffectsEnabled = !isSoundEffectsEnabled;
        updateSwitchVisuals(soundEffectSwitch, soundEffectSwitchCircle, isSoundEffectsEnabled, "Efek Suara");
        System.out.println("Efek Suara: " + (isSoundEffectsEnabled ? "ON" : "OFF"));
        // TODO: Tambahkan logika untuk benar-benar mengaktifkan/menonaktifkan efek suara
    }

    private void updateSwitchVisuals(HBox switchBox, Circle switchCircle, boolean isEnabled, String switchName) {
        if (switchBox == null || switchCircle == null) return;
        if (isEnabled) {
            switchBox.setAlignment(Pos.CENTER_RIGHT);
            switchBox.setStyle("-fx-background-color: " + toWebColor(SWITCH_TRACK_ON_COLOR) + "; -fx-background-radius: 20;");
            switchCircle.setFill(SWITCH_ON_COLOR);
        } else {
            switchBox.setAlignment(Pos.CENTER_LEFT);
            switchBox.setStyle("-fx-background-color: " + toWebColor(SWITCH_TRACK_OFF_COLOR) + "; -fx-background-radius: 20;");
            switchCircle.setFill(SWITCH_OFF_COLOR);
        }
        FillTransition ft = new FillTransition(Duration.millis(150), switchCircle);
        ft.setToValue(isEnabled ? SWITCH_ON_COLOR : SWITCH_OFF_COLOR);
        ft.play();
    }

    private String toWebColor(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    // --- Metode untuk CTA Popups ---
    @FXML
    private void showMulaiPopup(MouseEvent event) {
        showSpecificPopup(mulaiPopup);
    }

    @FXML
    private void showTutorialPopup(MouseEvent event) { // Ini akan menampilkan halaman 1 tutorial
        showSpecificPopup(tutorialPage1Popup);
    }

    @FXML
    private void showKeluarPopup(MouseEvent event) {
        showSpecificPopup(keluarPopup);
    }

    // Handler untuk tombol di dalam Popup Mulai
    @FXML
    private void handlePilihLatihanHuruf(MouseEvent event) {
        System.out.println("Pilihan Latihan: Huruf");
        closeActivePopup(null);
    }

    @FXML
    private void handlePilihLatihanKata(MouseEvent event) {
        System.out.println("Pilihan Latihan: Kata");
        closeActivePopup(null);
    }

    @FXML
    private void handlePilihLatihanKalimat(MouseEvent event) {
        System.out.println("Pilihan Latihan: Kalimat");
        closeActivePopup(null);
    }

    // Handler untuk tombol di dalam Popup Tutorial
    @FXML
    private void handleTutorialLanjut(MouseEvent event) { // Dipanggil dari tutorialPage1Popup
        System.out.println("Tombol Lanjut Tutorial (dari Hal 1 ke Hal 2) diklik.");
        showSpecificPopup(tutorialPage2Popup); // Tampilkan halaman 2
    }

    @FXML
    private void handleTutorialKembaliKeHalaman1(MouseEvent event) { // Dipanggil dari tutorialPage2Popup
        System.out.println("Tombol Kembali Tutorial (dari Hal 2 ke Hal 1) diklik.");
        showSpecificPopup(tutorialPage1Popup); // Tampilkan halaman 1
    }


    // Handler untuk tombol di dalam Popup Keluar
    @FXML
    private void handleKeluarYa(MouseEvent event) {
        System.out.println("Keluar dari aplikasi.");
        Platform.exit();
    }

    @FXML
    private void handleKeluarTidak(MouseEvent event) {
        System.out.println("Batal keluar.");
        closeActivePopup(null);
    }


    @FXML
    private void closeActivePopup(MouseEvent event) {
        boolean popupWasVisible = false;
        for (AnchorPane popup : allPopups) {
            if (popup != null && popup.isVisible()) {
                final AnchorPane currentPopup = popup;
                applyFadeOutAnimation(currentPopup, () -> currentPopup.setVisible(false));
                popupWasVisible = true;
                // Hanya satu popup yang seharusnya aktif, jadi bisa break jika mau,
                // tapi loop melalui semua tidak masalah.
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
    
    // ... (createProfilDeveloperContent, createSingleDeveloperVBox, createSumberReferensiContent tetap sama) ...
    private Node createProfilDeveloperContent() {
        HBox developersContainer = new HBox();
        developersContainer.setAlignment(Pos.CENTER);
        developersContainer.setSpacing(25.0);
        VBox fricoVBox = createSingleDeveloperVBox("@desain/default_avatar.png", "Frico Putung", "220211060359", 130.0);
        VBox syifabelaVBox = createSingleDeveloperVBox("@desain/default_avatar.png", "Syifabela Suratinoyo", "220211060344", 150.0);
        VBox kevinVBox = createSingleDeveloperVBox("@desain/default_avatar.png", "Kevin Rimper", "220211060364", 130.0);
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
        HBox namaHBox = new HBox(new Label(nama) {{ setTextFill(Color.WHITE); setFont(Font.font("Lapsus Pro Bold", 18.0)); }});
        namaHBox.setAlignment(Pos.CENTER);
        namaHBox.setPrefHeight(30.0);
        HBox nimHBox = new HBox(new Label(nim) {{ setTextFill(Color.WHITE); setFont(Font.font("Lapsus Pro Bold", 16.0)); }});
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
        Label materi1 = new Label("- Konsep latihan mengetik per huruf, kata, dan kalimat diadaptasi dari berbagai sumber online terkemuka seperti TypingClub.com dan Ratatype.com.");
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
        Label aset2 = new Label("- Font \"Lapsus Pro Bold\" (dan varian lainnya jika ada) digunakan untuk konsistensi visual.");
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