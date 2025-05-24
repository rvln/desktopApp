package desktopapp;

import javafx.animation.FillTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane mainPage;

    @FXML
    private AnchorPane overlayPopup; // Info popup

    @FXML
    private AnchorPane settingsPopupAnchorPane; // Settings popup

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

    // State untuk switches
    private boolean isMusicEnabled = true; // Default ON
    private boolean isSoundEffectsEnabled = true; // Default ON

    // Konstanta warna untuk switch
    private final Color SWITCH_ON_COLOR = Color.web("#ffffff"); // Putih
    private final Color SWITCH_OFF_COLOR = Color.web("#79747E"); // Abu-abu dari desain Anda
    private final Color SWITCH_TRACK_ON_COLOR = Color.web("#6FADCF"); // Biru muda untuk track
    private final Color SWITCH_TRACK_OFF_COLOR = Color.web("#A0A0A0"); // Abu-abu muda untuk track


    private static final String STYLE_ACTIVE_BUTTON = "btn_system";
    private static final String STYLE_INACTIVE_BUTTON = "border_button";
    private static final String STYLE_MEDIUM_RADIUS = "medium_button_radius";
    private static final String STYLE_CONTENT_BACKGROUND = "info_background_content";
    private static final String STYLE_CONTENT_RADIUS = "adn_button_radius";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        overlayPopup.setVisible(false);
        settingsPopupAnchorPane.setVisible(false);
        disableLayer.setVisible(false);

        // Inisialisasi Info Popup
        profilDeveloperNode = createProfilDeveloperContent();
        sumberReferensiNode = createSumberReferensiContent();
        if (profilDeveloperNode != null) {
            infoContent.getChildren().setAll(profilDeveloperNode);
        }
        updateButtonStyles(true); // Profil aktif by default

        // Inisialisasi tampilan switch pengaturan
        updateSwitchVisuals(musicSwitch, musicSwitchCircle, isMusicEnabled, "Musik");
        updateSwitchVisuals(soundEffectSwitch, soundEffectSwitchCircle, isSoundEffectsEnabled, "Efek Suara");
    }

    // --- Metode untuk Info Popup ---
    @FXML
    private void showInfo(MouseEvent event) {
        settingsPopupAnchorPane.setVisible(false); // Sembunyikan popup lain jika terbuka
        showProfilContent(); // Pastikan konten info benar

        overlayPopup.setVisible(true);
        disableLayer.setVisible(true);
        applyFadeInAnimation(overlayPopup);
        applyFadeInAnimation(disableLayer);
    }

    @FXML
    void handleInfoProfilClicked(MouseEvent event) {
        showProfilContent();
    }

    @FXML
    void handleInfoSumberClicked(MouseEvent event) {
        showSumberContent();
    }

    private void updateButtonStyles(boolean isProfilActive) {
        if (infoProfil != null && infoSumber != null) { // Cek null untuk keamanan
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
            // Tidak perlu HBox.setHgrow jika ScrollPane sudah diatur setFitToWidth(true)
            // dan prefSize-nya sesuai.
        }
    }


    // --- Metode untuk Settings Popup ---
    @FXML
    private void showSettingsPopup(MouseEvent event) {
        overlayPopup.setVisible(false); // Sembunyikan popup lain jika terbuka

        settingsPopupAnchorPane.setVisible(true);
        disableLayer.setVisible(true);
        applyFadeInAnimation(settingsPopupAnchorPane);
        applyFadeInAnimation(disableLayer);
    }

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

        // Lebar HBox switch adalah 80, padding kiri/kanan 4, jadi area untuk circle 72.
        // Radius circle 16, diameter 32.
        // Posisi ON: margin kiri = 80 - 4 (padding kanan) - 32 (diameter circle) - 4 (padding kiri) = 40
        // Posisi OFF: margin kiri = 0 (karena sudah ada padding kiri 4 pada HBox)
        double targetTranslateX;
        Color targetTrackColor;

        if (isEnabled) {
            switchBox.setAlignment(Pos.CENTER_RIGHT); // Circle ke kanan
            switchBox.setStyle("-fx-background-color: " + toWebColor(SWITCH_TRACK_ON_COLOR) + "; -fx-background-radius: 20;");
            switchCircle.setFill(SWITCH_ON_COLOR);
            // HBox.setMargin(switchCircle, new Insets(0, 0, 0, 40)); // Kanan
            targetTranslateX = (switchBox.getWidth() - (2 * switchCircle.getRadius())) - (2* switchBox.getPadding().getLeft());

        } else {
            switchBox.setAlignment(Pos.CENTER_LEFT); // Circle ke kiri
            switchBox.setStyle("-fx-background-color: " + toWebColor(SWITCH_TRACK_OFF_COLOR) + "; -fx-background-radius: 20;");
            switchCircle.setFill(SWITCH_OFF_COLOR);
            // HBox.setMargin(switchCircle, new Insets(0, 40, 0, 0)); // Kiri
            targetTranslateX = 0;
        }
        
        // Animasi perpindahan circle
        TranslateTransition tt = new TranslateTransition(Duration.millis(150), switchCircle);
        // tt.setToX(targetTranslateX); // Ini jika circle adalah child langsung dari parent yang lebih besar
        // Karena circle ada di dalam HBox yang alignment-nya diubah, kita tidak perlu translate manual jika HBox sudah benar.
        // Namun, jika ingin animasi slide, kita bisa atur HBox selalu CENTER_LEFT dan mainkan translateX circle.
        // Untuk saat ini, perubahan alignment HBox sudah cukup.
        // Jika ingin animasi slide:
        // switchBox.setAlignment(Pos.CENTER_LEFT); // Selalu kiri
        // tt.setToX(isEnabled ? (switchBox.getWidth() - switchCircle.getRadius()*2 - switchBox.getPadding().getLeft() - switchBox.getPadding().getRight()) : 0);
        // tt.play();

        // Animasi perubahan warna circle (opsional, bisa juga langsung setFill)
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


    // --- Metode Umum ---
    @FXML
    private void closeActivePopup(MouseEvent event) { // Ganti nama dari closePopup
        boolean popupWasVisible = false;
        if (overlayPopup.isVisible()) {
            applyFadeOutAnimation(overlayPopup, () -> overlayPopup.setVisible(false));
            popupWasVisible = true;
        }
        if (settingsPopupAnchorPane.isVisible()) {
            applyFadeOutAnimation(settingsPopupAnchorPane, () -> settingsPopupAnchorPane.setVisible(false));
            popupWasVisible = true;
        }

        if (popupWasVisible && disableLayer.isVisible()) {
            applyFadeOutAnimation(disableLayer, () -> disableLayer.setVisible(false));
        }
    }

    private void applyFadeInAnimation(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void applyFadeOutAnimation(Node node, Runnable onFinishedAction) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), node);
        fadeOut.setFromValue(node.getOpacity()); // Mulai dari opacity saat ini
        fadeOut.setToValue(0.0);
        if (onFinishedAction != null) {
            fadeOut.setOnFinished(e -> onFinishedAction.run());
        }
        fadeOut.play();
    }

    // --- Konten untuk Info Popup (tidak berubah) ---
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
        if (infoContent != null) { // Pastikan infoContent sudah diinisialisasi
            scrollPane.setPrefSize(infoContent.getPrefWidth() - 10, infoContent.getPrefHeight() - 10);
        } else {
            scrollPane.setPrefSize(530, 290); // Fallback size
        }
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("sumber-referensi-scrollpane");
        return scrollPane;
    }
}