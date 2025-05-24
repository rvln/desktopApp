package desktopapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane; // Import ScrollPane
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority; // Untuk Hgrow jika diperlukan
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML
    private AnchorPane mainPage;

    @FXML
    private AnchorPane overlayPopup;

    @FXML
    private Pane disableLayer;

    @FXML
    private HBox infoProfil;

    @FXML
    private HBox infoSumber;

    @FXML
    private HBox infoContent;

    private static final String STYLE_ACTIVE_BUTTON = "btn_system";
    private static final String STYLE_INACTIVE_BUTTON = "border_button";
    private static final String STYLE_MEDIUM_RADIUS = "medium_button_radius";
    private static final String STYLE_CONTENT_BACKGROUND = "info_background_content";
    private static final String STYLE_CONTENT_RADIUS = "adn_button_radius";

    private Node profilDeveloperNode;
    private Node sumberReferensiNode; // Ini akan menjadi ScrollPane

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        overlayPopup.setVisible(false);
        disableLayer.setVisible(false);

        profilDeveloperNode = createProfilDeveloperContent();
        sumberReferensiNode = createSumberReferensiContent(); // Membuat ScrollPane

        if (profilDeveloperNode != null) {
            infoContent.getChildren().setAll(profilDeveloperNode);
        }
        updateButtonStyles(true);
    }

    @FXML
    private void showInfo(MouseEvent event) {
        showProfilContent();
        disableLayer.setVisible(true);
        overlayPopup.setVisible(true);

        FadeTransition fadeDisable = new FadeTransition(Duration.millis(200), disableLayer);
        fadeDisable.setFromValue(0.0);
        fadeDisable.setToValue(1.0);
        fadeDisable.play();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void closePopup(MouseEvent event) {
        FadeTransition fadeOutOverlay = new FadeTransition(Duration.millis(200), overlayPopup);
        fadeOutOverlay.setFromValue(1.0);
        fadeOutOverlay.setToValue(0.0);
        fadeOutOverlay.setOnFinished(e -> overlayPopup.setVisible(false));
        fadeOutOverlay.play();

        FadeTransition fadeOutDisable = new FadeTransition(Duration.millis(200), disableLayer);
        fadeOutDisable.setFromValue(1.0);
        fadeOutDisable.setToValue(0.0);
        fadeOutDisable.setOnFinished(e -> disableLayer.setVisible(false));
        fadeOutDisable.play();
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
        if (isProfilActive) {
            infoProfil.getStyleClass().setAll(STYLE_ACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
            infoSumber.getStyleClass().setAll(STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
        } else {
            infoSumber.getStyleClass().setAll(STYLE_ACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
            infoProfil.getStyleClass().setAll(STYLE_INACTIVE_BUTTON, STYLE_MEDIUM_RADIUS);
        }
    }

    private void showProfilContent() {
        updateButtonStyles(true);
        infoContent.getChildren().clear();
        if (profilDeveloperNode != null) {
            infoContent.getChildren().add(profilDeveloperNode);
            // Jika profilDeveloperNode adalah HBox dengan beberapa VBox, pastikan HBox.hgrow diatur pada VBox jika perlu
            // Namun, karena profilDeveloperNode adalah HBox yang di-return dari createProfilDeveloperContent()
            // dan infoContent juga HBox, ini seharusnya sudah oke jika alignment & prefWidth VBox di dalamnya benar.
        }
        infoContent.getStyleClass().setAll(STYLE_CONTENT_BACKGROUND, STYLE_CONTENT_RADIUS);
        infoContent.setAlignment(Pos.CENTER); // Pastikan alignment default HBox untuk konten profil
    }

    private void showSumberContent() {
        updateButtonStyles(false);
        infoContent.getChildren().clear();
        if (sumberReferensiNode != null) { // sumberReferensiNode sekarang adalah ScrollPane
            infoContent.getChildren().add(sumberReferensiNode);
            // Agar ScrollPane mengisi HBox infoContent jika infoContent lebih besar
            // HBox.setHgrow(sumberReferensiNode, Priority.ALWAYS); // Aktifkan jika perlu
        }
        infoContent.getStyleClass().setAll(STYLE_CONTENT_BACKGROUND, STYLE_CONTENT_RADIUS);
        // Untuk ScrollPane, kita mungkin ingin agar ia tidak ter-center jika lebarnya lebih kecil,
        // tapi mengisi. Namun, karena kita set prefWidth pada ScrollPane, alignment CENTER mungkin tidak masalah.
        // infoContent.setAlignment(Pos.TOP_LEFT); // Atau alignment lain jika perlu untuk ScrollPane
    }

    private Node createProfilDeveloperContent() {
        HBox developersContainer = new HBox();
        developersContainer.setAlignment(Pos.CENTER);
        developersContainer.setSpacing(25.0);

        VBox fricoVBox = createSingleDeveloperVBox(
                "@desain/default_avatar.png",
                "Frico Putung",
                "220211060359",
                130.0
        );
        VBox syifabelaVBox = createSingleDeveloperVBox(
                "@desain/default_avatar.png",
                "Syifabela Suratinoyo",
                "220211060344",
                150.0
        );
        VBox kevinVBox = createSingleDeveloperVBox(
                "@desain/default_avatar.png",
                "Kevin Rimper",
                "220211060364",
                130.0
        );

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

        HBox namaHBox = new HBox(new Label(nama) {{
            setTextFill(Color.WHITE);
            setFont(Font.font("Lapsus Pro Bold", 18.0));
        }});
        namaHBox.setAlignment(Pos.CENTER);
        namaHBox.setPrefHeight(30.0);

        HBox nimHBox = new HBox(new Label(nim) {{
            setTextFill(Color.WHITE);
            setFont(Font.font("Lapsus Pro Bold", 16.0));
        }});
        nimHBox.setAlignment(Pos.CENTER);
        nimHBox.setPrefHeight(30.0);
        VBox.setMargin(nimHBox, new Insets(-30.0, 0, 0, 0));

        devVBox.getChildren().addAll(avatarHBox, namaHBox, nimHBox);
        return devVBox;
    }

    // --- MODIFIKASI DI SINI ---
    private Node createSumberReferensiContent() {
        // 1. Buat VBox untuk konten teks
        VBox contentVBox = new VBox(15); // Spacing antar elemen di dalam VBox
        // Atur padding agar konten tidak terlalu mepet ke tepi ScrollPane
        contentVBox.setPadding(new Insets(15, 20, 15, 20)); // top, right, bottom, left
        contentVBox.setAlignment(Pos.TOP_LEFT); // Teks rata kiri atas

        Label title = new Label("Sumber Pembelajaran & Referensi");
        title.setFont(Font.font("Lapsus Pro Bold", 22));
        title.setTextFill(Color.WHITE);
        title.setWrapText(true);

        Label descMateri = new Label("Materi Pembelajaran:");
        descMateri.setFont(Font.font("Lapsus Pro Bold", 18));
        descMateri.setTextFill(Color.WHITE);
        VBox.setMargin(descMateri, new Insets(10,0,5,0));

        Label materi1 = new Label("- Konsep latihan mengetik per huruf, kata, dan kalimat diadaptasi dari berbagai sumber online terkemuka seperti TypingClub.com dan Ratatype.com.");
        materi1.setFont(Font.font("Lapsus Pro Regular", 14));
        materi1.setTextFill(Color.LIGHTGRAY);
        materi1.setWrapText(true);

        Label descTeknologi = new Label("Teknologi yang Digunakan:");
        descTeknologi.setFont(Font.font("Lapsus Pro Bold", 18));
        descTeknologi.setTextFill(Color.WHITE);
        VBox.setMargin(descTeknologi, new Insets(15,0,5,0));

        Label tech1 = new Label("- JavaFX: Framework utama untuk membangun Antarmuka Pengguna Grafis (GUI).");
        tech1.setFont(Font.font("Lapsus Pro Regular", 14));
        tech1.setTextFill(Color.LIGHTGRAY);
        tech1.setWrapText(true);

        Label tech2 = new Label("- Scene Builder: Alat bantu visual untuk mendesain tata letak FXML.");
        tech2.setFont(Font.font("Lapsus Pro Regular", 14));
        tech2.setTextFill(Color.LIGHTGRAY);
        tech2.setWrapText(true);

        Label tech3 = new Label("- Visual Studio Code: Integrated Development Environment (IDE) untuk penulisan kode.");
        tech3.setFont(Font.font("Lapsus Pro Regular", 14));
        tech3.setTextFill(Color.LIGHTGRAY);
        tech3.setWrapText(true);
        
        Label descAset = new Label("Aset Grafis & Font:");
        descAset.setFont(Font.font("Lapsus Pro Bold", 18));
        descAset.setTextFill(Color.WHITE);
        VBox.setMargin(descAset, new Insets(15,0,5,0));

        Label aset1 = new Label("- Sebagian ikon dan elemen desain dibuat khusus untuk aplikasi KidBoard.");
        aset1.setFont(Font.font("Lapsus Pro Regular", 14));
        aset1.setTextFill(Color.LIGHTGRAY);
        aset1.setWrapText(true);
        
        Label aset2 = new Label("- Font \"Lapsus Pro Bold\" (dan varian lainnya jika ada) digunakan untuk konsistensi visual.");
        aset2.setFont(Font.font("Lapsus Pro Regular", 14));
        aset2.setTextFill(Color.LIGHTGRAY);
        aset2.setWrapText(true);

        // Tambahkan semua label ke VBox
        contentVBox.getChildren().addAll(title, descMateri, materi1, descTeknologi, tech1, tech2, tech3, descAset, aset1, aset2);

        // 2. Buat ScrollPane dan masukkan VBox ke dalamnya
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentVBox);
        scrollPane.setFitToWidth(true); // Penting! Agar lebar contentVBox menyesuaikan lebar ScrollPane

        // Atur ukuran preferensi ScrollPane agar pas di dalam infoContent
        // infoContent memiliki prefHeight="300.0" dan prefWidth="540.0"
        // VBox.margin untuk infoContent adalah top="20.0", jadi tinggi efektif adalah 300.
        // Kita beri sedikit ruang untuk padding di dalam scrollpane atau margin.
        scrollPane.setPrefSize(infoContent.getPrefWidth() - 10, infoContent.getPrefHeight() - 10); // Kurangi sedikit dari ukuran infoContent
        // scrollPane.setMaxSize(infoContent.getPrefWidth(), infoContent.getPrefHeight());


        // Kebijakan Scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Tidak perlu scroll horizontal
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Scroll vertikal muncul jika perlu

        // Anda bisa menambahkan style class ke ScrollPane jika ingin kustomisasi lebih lanjut via CSS
        // Misalnya, untuk menghilangkan border default atau mengubah warna background viewport
        scrollPane.getStyleClass().add("sumber-referensi-scrollpane");
        // Di style.css:
        // .sumber-referensi-scrollpane { -fx-background-color: transparent; }
        // .sumber-referensi-scrollpane > .viewport { -fx-background-color: transparent; }
        // .sumber-referensi-scrollpane .scroll-bar:vertical { -fx-background-color: #2A2A2A; } /* Contoh warna gelap */
        // .sumber-referensi-scrollpane .scroll-bar:vertical .thumb { -fx-background-color: #555555; -fx-background-radius: 4; }

        return scrollPane; // Kembalikan ScrollPane
    }
}