package com.example.application.view.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class LandingPage extends Div {

    public LandingPage () {
        // Global styles
        getElement().getStyle().set("margin", "0");
        getElement().getStyle().set("padding", "0");
        getElement().getStyle().set("min-height", "100vh");
        getElement().getStyle().set("overflow-x", "hidden");

        add(createHeader());
        add(createHeroSection());
        add(createAboutSection());
        add(createWhyChooseUsSection());
        add(createCallToActionSection());
//        add(createOurJastipersSection());
        add(createFooter());
    }

    private Component createHeader() {
        Div header = new Div();
        header.addClassName("header-container");
        header.getStyle()
                .set("width", "92%")
                .set("height", "80px")
                .set("background-color", "transparent") // Mengubah background menjadi transparan
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0")
                .set("z-index", "1000")
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("padding", "0 5%");

//        // Logo with click navigation to home
//        H2 logo = new H2("JASTIPKUY");
//        logo.addClassName("logo");
//        logo.getStyle()
//                .set("color", "#1e3a8a")
//                .set("margin", "0")
//                .set("font-size", "2rem")
//                .set("font-weight", "bold")
//                .set("cursor", "pointer");
//
//        logo.addClickListener(e -> UI.getCurrent().navigate(""));

        // Logo dengan gambar dan click navigation ke home
        Image logo = new Image("images/logo.png", "JASTIPKUY Logo");
        logo.addClassName("logo");
        logo.getStyle()
                .set("width", "150px") // Sesuaikan ukuran gambar
                .set("height", "auto")
                .set("margin", "0")
                .set("cursor", "pointer");
        logo.addClickListener(event -> UI.getCurrent().navigate("")); // Navigasi ke halaman home

        // Main navigation
        Div navigation = new Div();
        navigation.addClassName("main-navigation");
        navigation.getStyle()
                .set("display", "flex")
                .set("gap", "2rem")
                .set("align-items", "center");


        // User actions section
        Div userActions = new Div();
        userActions.addClassName("user-actions");
        userActions.getStyle()
                .set("display", "flex")
                .set("gap", "1rem")
                .set("align-items", "center");

        // Check if user is logged in (replace with your authentication logic)
        boolean isLoggedIn = isUserLoggedIn();

        if (isLoggedIn) {
            // User is logged in - show profile and logout
            Button profileBtn = new Button("Profile");
            profileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            profileBtn.getStyle()
                    .set("font-size", "1rem")
                    .set("color", "#1e3a8a");
            profileBtn.addClickListener(e -> UI.getCurrent().navigate("profile"));

            Button logoutBtn = new Button("Logout");
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            logoutBtn.getStyle()
                    .set("font-size", "1rem");

            userActions.add(profileBtn, logoutBtn);
        } else {
            Button loginBtn = new Button();
            Image loginImage = new Image("images/loginicon.png", "Login Icon"); // Ganti dengan path gambar Anda
            loginImage.getStyle()
                    .set("width", "24px") // Sesuaikan ukuran gambar
                    .set("height", "24px");

            loginBtn.setIcon(loginImage); // Menetapkan gambar sebagai ikon tombol
            loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Mempertahankan tema LUMO_PRIMARY
            loginBtn.getStyle()
                    .set("background-color", "#394867")
                    .set("border", "2px solid #394867")
                    .set("border-radius", "8px")
                    .set("padding", "8px") // Padding lebih kecil karena hanya ikon
                    .set("transition", "background-color 0.3s, transform 0.2s")
                    .set("--lumo-button-size", "36px")
                    .set("display", "flex")
                    .set("justify-content", "center")
                    .set("align-items", "center");
            loginBtn.addClickListener(e -> UI.getCurrent().navigate("login"));

            // Menambahkan efek hover menggunakan CSS kustom
            loginBtn.getElement().executeJs(
                    "this.addEventListener('mouseenter', () => this.style.backgroundColor = '#2c3a57');" +
                            "this.addEventListener('mouseleave', () => this.style.backgroundColor = '#394867');"
            );

//            Button registerBtn = new Button("Register");
//            registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//            registerBtn.getStyle()
//                    .set("font-size", "1rem")
//                    .set("background-color", "#1e3a8a")
//                    .set("border-color", "#1e3a8a");
//            registerBtn.addClickListener(e -> UI.getCurrent().navigate("register"));

            userActions.add(loginBtn); //tambahin registerBtn kl mw nampilin button regis
        }

        header.add(logo, navigation, userActions);
        return header;
    }


    private Button createNavButton(String text, String route) {
        Button btn = new Button(text);
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn.getStyle()
                .set("font-size", "1.1rem")
                .set("color", "#1e3a8a")
                .set("font-weight", "500");

        // Add hover effect
        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("color", "#1d4ed8");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("color", "#1e3a8a");
        });

        btn.addClickListener(e -> UI.getCurrent().navigate(route));

        return btn;
    }
    private boolean isUserLoggedIn() {
        return false;
    }

    private Component createHeroSection() {
        // Parent
        Div heroSection = new Div();
        heroSection.getStyle()
                .set("width", "100vw")
                .set("height", "100vh")
                .set("position", "relative") // penting biar overlay bisa absolute
                .set("background-image", "url('images/schbackground.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("overflow", "hidden");

        // Overlay dengan warna #9BA4B4 transparan
        Div overlay = new Div();
        overlay.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100%")
                .set("height", "100%")
                .set("background-color", "rgba(0, 0, 0, 0.2)") // Warna #9BA4B4 dengan transparansi 50%
                .set("z-index", "1");

        // Teks judul di atas overlay
        Div content = new Div();
        content.getStyle()
                .set("position", "relative")
                .set("z-index", "2")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("height", "100%")
                .set("color", "white")
                .set("text-align", "center")
                .set("font-family", "Poppins, sans-serif"); // Menambahkan font Poppins

        H2 welcomeTitle = new H2("WELCOME TO");
        welcomeTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "3rem")
                .set("font-weight", "normal") // Mengatur font-weight ke normal agar tidak bold
                .set("color", "white")
                .set("font-family", "Poppins, sans-serif"); // Menambahkan font Poppins

        H1 brandTitle = new H1("JASTIPKUY!");
        brandTitle.getStyle()
                .set("margin", "1rem 0 0 0")
                .set("font-size", "4rem")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("font-family", "Poppins, sans-serif"); // Menambahkan font Poppins

        content.add(welcomeTitle, brandTitle);
        heroSection.add(overlay, content);

        return heroSection;
    }


    private Component createAboutSection() {
        Div aboutSection = new Div();
        aboutSection.getStyle().set("height", "100vh");
        aboutSection.getStyle().set("background-color", "#ffffff");
        aboutSection.getStyle().set("display", "flex");
        aboutSection.getStyle().set("flex-direction", "column");
        aboutSection.getStyle().set("justify-content", "center");
        aboutSection.getStyle().set("align-items", "center");
        aboutSection.getStyle().set("padding", "0 5%");

        H2 aboutTitle = new H2("ABOUT US");
//        aboutTitle.getStyle().set("text-align", "center");
//        aboutTitle.getStyle().set("margin-bottom", "3rem");
//        aboutTitle.getStyle().set("font-size", "3rem");
//        aboutTitle.getStyle().set("font-weight", "normal");
        aboutTitle.getStyle().set("color", "#14274E");
        aboutTitle.getStyle().set("text-align", "center");
        aboutTitle.getStyle().set("font-size", "clamp(2rem, 4vw, 3rem)");
        aboutTitle.getStyle().set("font-weight", "700");
        aboutTitle.getStyle().set("margin-bottom", "4rem");
        aboutTitle.getStyle().set("text-shadow", "0 4px 8px rgba(0,0,0,0.3)");
        aboutTitle.getStyle().set("letter-spacing", "-0.02em");
        aboutTitle.getStyle().set("line-height", "1.2");

        // About text box
        Div aboutText = new Div();
        aboutText.getStyle().set("max-width", "800px");
        aboutText.getStyle().set("width", "100%");
        aboutText.getStyle().set("background", "linear-gradient(135deg, #9BA4B4, #14274E)");
        aboutText.getStyle().set("color", "white");
        aboutText.getStyle().set("padding", "2.5rem");
        aboutText.getStyle().set("border-radius", "20px");
        aboutText.getStyle().set("box-shadow", "0 20px 40px rgba(0,0,0,0.1)");
        aboutText.getStyle().set("text-align", "left");
        aboutText.getStyle().set("margin-bottom", "3rem");

        Paragraph aboutParagraph = new Paragraph(
                "JASTIPKUY! adalah layanan jastip praktis untuk siswa, guru, dan warga " +
                        "sekolah yang ingin titip barang tanpa harus ribet, ribet, club. Dengan " +
                        "motto \"Titip Aja, Beres!\", kami melayani titipan makanan, alat tulis, " +
                        "perlengkapan sekolah, hingga kebutuhan pribadi lainnya. Dikelola oleh " +
                        "tim muda yang paham kebutuhan sekolah."
        );
        aboutParagraph.getStyle().set("font-size", "1.1rem");
        aboutParagraph.getStyle().set("line-height", "1.6");
        aboutParagraph.getStyle().set("font-family", "Poppins,sans-serif");
        aboutParagraph.getStyle().set("margin", "0");

        aboutText.add(aboutParagraph);

        // Circular buttons container
        Div buttonContainer = new Div();
        buttonContainer.getStyle().set("display", "flex");
        buttonContainer.getStyle().set("justify-content", "center");
        buttonContainer.getStyle().set("gap", "2rem");
        buttonContainer.getStyle().set("flex-wrap", "wrap");
        buttonContainer.getStyle().set("max-width", "800px");
        buttonContainer.getStyle().set("font-family", "Poppins,sans-serif");

        // Create circular service buttons
        Button buatPesananBtn = createCircularServiceButton("Buat pesanan", "#9BA4B4");
        Button memprosesPesananBtn = createCircularServiceButton("Memproses", "#394867");
        Button pengantaranBtn = createCircularServiceButton("Pengantaran", "#9BA4B4");
        Button pembayaranBtn = createCircularServiceButton("Pembayaran", "#394867");

        buttonContainer.add(buatPesananBtn, memprosesPesananBtn, pengantaranBtn, pembayaranBtn);

        aboutSection.add(aboutTitle, aboutText, buttonContainer);

        return aboutSection;
    }

    private Button createCircularServiceButton(String text, String bgColor) {
        Button button = new Button(text);
        button.getStyle()
                .set("width", "150px")
                .set("height", "150px")
                .set("background-color", bgColor)
                .set("color", "white")
                .set("border", "none")
                .set("border-radius", "50%") // Perfect circle
                .set("font-size", "0.9rem")
                .set("font-weight", "500")
                .set("cursor", "pointer")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("text-align", "center")
                .set("line-height", "1.2")
                .set("padding", "1rem")
                .set("box-shadow", "0 8px 20px rgba(0,0,0,0.15)")
                .set("transition", "transform 0.2s ease, box-shadow 0.2s ease");

        // Add hover effect
        button.getElement().setAttribute("onmouseover",
                "this.style.transform='translateY(-5px)'; this.style.boxShadow='0 12px 25px rgba(0,0,0,0.2)'");
        button.getElement().setAttribute("onmouseout",
                "this.style.transform='translateY(0)'; this.style.boxShadow='0 8px 20px rgba(0,0,0,0.15)'");

        return button;
    }





    private Button createServiceButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.getStyle().set("background-color", backgroundColor);
        button.getStyle().set("color", "white");
        button.getStyle().set("border", "none");
        button.getStyle().set("border-radius", "50px");
        button.getStyle().set("padding", "1.5rem 3rem");
        button.getStyle().set("font-weight", "bold");
        button.getStyle().set("font-size", "1.1rem");
        button.getStyle().set("width", "250px");
        button.getStyle().set("height", "80px");
        button.getStyle().set("box-shadow", "0 10px 20px rgba(0,0,0,0.1)");
        button.getStyle().set("cursor", "pointer");
        button.getStyle().set("transition", "all 0.3s ease");

        return button;
    }

    private Component createWhyChooseUsSection() {
        Div whySection = new Div();
        whySection.getStyle().set("min-height", "100vh");
        whySection.getStyle().set("background-color", "#14274E"); // Mengubah background ke warna solid #14274E
        whySection.getStyle().set("color", "#14274E");
        whySection.getStyle().set("display", "flex");
        whySection.getStyle().set("flex-direction", "column");
        whySection.getStyle().set("justify-content", "center");
        whySection.getStyle().set("align-items", "center");
        whySection.getStyle().set("padding", "4rem 5%");
        whySection.getStyle().set("position", "relative");
        whySection.getStyle().set("overflow", "hidden");

        H2 whyTitle = new H2("Why JASTIPKUY! is Your Best In-School Buddy?");
        whyTitle.getStyle().set("text-align", "center");
        whyTitle.getStyle().set("font-size", "clamp(2rem, 4vw, 3rem)");
        whyTitle.getStyle().set("font-weight", "700");
        whyTitle.getStyle().set("color", "white");
        whyTitle.getStyle().set("margin-bottom", "4rem");
        whyTitle.getStyle().set("text-shadow", "0 4px 8px rgba(0,0,0,0.3)");
        whyTitle.getStyle().set("letter-spacing", "-0.02em");
        whyTitle.getStyle().set("line-height", "1.2");

        Div reasonsContainer = new Div();
        reasonsContainer.getStyle().set("display", "grid");
        reasonsContainer.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))");
        reasonsContainer.getStyle().set("gap", "2rem");
        reasonsContainer.getStyle().set("max-width", "1200px");
        reasonsContainer.getStyle().set("width", "100%");

        Div reason1 = createReasonCard(
                "Tetap di kelas atau ruang kerja, dan barang akan diantar langsung ke lokasi Anda.",
                "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                "📍"
        );

        Div reason2 = createReasonCard(
                "Titip ambil barang, makanan, minuman, atau perlengkapan sekolah.",
                "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                "🎒"
        );

        Div reason3 = createReasonCard(
                "Layanan cepat, aman, murah dan terpercaya.",
                "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                "⚡"
        );

        reasonsContainer.add(reason1, reason2, reason3);
        whySection.add(whyTitle, reasonsContainer);

        return whySection;
    }

    private Div createReasonCard(String text, String backgroundGradient, String emoji) {
        Div card = new Div();
        card.getStyle().set("background", backgroundGradient);
        card.getStyle().set("padding", "2.5rem");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("min-height", "200px");
        card.getStyle().set("text-align", "center");
        card.getStyle().set("color", "white");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flex-direction", "column");
        card.getStyle().set("align-items", "center");
        card.getStyle().set("justify-content", "center");
        card.getStyle().set("box-shadow", "0 20px 40px rgba(0,0,0,0.15)");
        card.getStyle().set("backdrop-filter", "blur(10px)");
        card.getStyle().set("border", "1px solid rgba(255,255,255,0.2)");
        card.getStyle().set("transition", "all 0.3s ease");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("position", "relative");
        card.getStyle().set("overflow", "hidden");

        // Add hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-8px) scale(1.02)");
            card.getStyle().set("box-shadow", "0 30px 60px rgba(0,0,0,0.25)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateY(0) scale(1)");
            card.getStyle().set("box-shadow", "0 20px 40px rgba(0,0,0,0.15)");
        });

        // Add emoji icon
        Div iconDiv = new Div();
        iconDiv.getStyle().set("font-size", "3rem");
        iconDiv.getStyle().set("margin-bottom", "1rem");
        iconDiv.getStyle().set("opacity", "0.9");
        iconDiv.setText(emoji);

        Paragraph paragraph = new Paragraph(text);
        paragraph.getStyle().set("margin", "0");
        paragraph.getStyle().set("font-size", "1.1rem");
        paragraph.getStyle().set("line-height", "1.6");
        paragraph.getStyle().set("font-weight", "500");
        paragraph.getStyle().set("text-shadow", "0 2px 4px rgba(0,0,0,0.2)");

        card.add(iconDiv, paragraph);
        return card;
    }

    private Component createCallToActionSection() {
        Div ctaSection = new Div();
        ctaSection.getStyle()
                .set("min-height", "60vh")
                .set("background-color", "#394867")
                .set("color", "white")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("padding", "4rem 5%")
                .set("text-align", "center")
                .set("position", "relative")
                .set("font-family", "Poppins, sans-serif");

        H2 ctaTitle = new H2("Gabung JASTIPKUY! Titip barang atau dapetin cuan sambil bantu orang!");
        ctaTitle.getStyle()
                .set("font-size", "clamp(1.5rem, 3vw, 2.2rem)")
                .set("font-weight", "700")
                .set("color", "white")
                .set("margin-bottom", "3rem")
                .set("text-shadow", "0 4px 8px rgba(0,0,0,0.3)")
                .set("line-height", "1.3")
                .set("max-width", "800px")
                .set("font-family", "Poppins, sans-serif");

        Div buttonContainer = new Div();
        buttonContainer.getStyle()
                .set("display", "flex")
                .set("gap", "1.5rem")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("align-items", "center");

        Button daftarBtn = createCtaButton("GABUNG JADI JASTIPER", "#F3C623", "#BE3D2A", "register");
        Button mulaiBtn = createCtaButton("MULAI TITIP SEKARANG", "#E78B48", "#E3DE61", "register");

        buttonContainer.add(daftarBtn, mulaiBtn);
        ctaSection.add(ctaTitle, buttonContainer);

        return ctaSection;
    }

    private Button createCtaButton(String text, String primaryColor, String hoverColor, String route) {
        Button button = new Button(text);
        button.getStyle()
                .set("background", "linear-gradient(135deg, " + primaryColor + " 0%, " + hoverColor + " 100%)")
                .set("color", "white")
                .set("border", "none")
                .set("padding", "1.2rem 2.5rem")
                .set("border-radius", "50px")
                .set("font-weight", "600")
                .set("font-size", "1rem")
                .set("cursor", "pointer")
                .set("box-shadow", "0 8px 25px rgba(0,0,0,0.2)")
                .set("transition", "all 0.3s ease")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("min-width", "220px")
                .set("font-family", "Poppins, sans-serif");

        button.getElement().addEventListener("mouseenter", e -> {
            button.getStyle()
                    .set("transform", "translateY(-3px)")
                    .set("box-shadow", "0 15px 35px rgba(0,0,0,0.3)")
                    .set("filter", "brightness(1.1)");
        });

        button.getElement().addEventListener("mouseleave", e -> {
            button.getStyle()
                    .set("transform", "translateY(0)")
                    .set("box-shadow", "0 8px 25px rgba(0,0,0,0.2)")
                    .set("filter", "brightness(1)");
        });

        button.getElement().addEventListener("mousedown", e -> {
            button.getStyle().set("transform", "translateY(-1px) scale(0.98)");
        });

        button.getElement().addEventListener("mouseup", e -> {
            button.getStyle().set("transform", "translateY(-3px) scale(1)");
        });

        button.addClickListener(e -> UI.getCurrent().navigate(route));

        return button;
    }

//    private Component createOurJastipersSection() {
//        Div jastipersSection = new Div();
//        jastipersSection.getStyle().set("height", "100vh");
//        jastipersSection.getStyle().set("background-color", "#f8fafc");
//        jastipersSection.getStyle().set("display", "flex");
//        jastipersSection.getStyle().set("flex-direction", "column");
//        jastipersSection.getStyle().set("justify-content", "center");
//        jastipersSection.getStyle().set("align-items", "center");
//        jastipersSection.getStyle().set("padding", "0 5%");
//
//        H3 jastipersTitle = new H3("Our JASTIPERS");
//        jastipersTitle.getStyle().set("color", "#1e3a8a");
//        jastipersTitle.getStyle().set("margin-bottom", "4rem");
//        jastipersTitle.getStyle().set("font-size", "2.5rem");
//        jastipersTitle.getStyle().set("font-weight", "bold");
//        jastipersTitle.getStyle().set("text-align", "center");
//
//        Div jastipersContainer = new Div();
//        jastipersContainer.getStyle().set("display", "flex");
//        jastipersContainer.getStyle().set("justify-content", "center");
//        jastipersContainer.getStyle().set("align-items", "center");
//        jastipersContainer.getStyle().set("gap", "3rem");
//        jastipersContainer.getStyle().set("flex-wrap", "wrap");
//
//        // Ganti dengan nama dan path foto yang sesuai
//        Div jastipper1 = createJastipperCard("Ngger", "#3b82f6", "images/person1.jpg");
//        Div jastipper2 = createJastipperCard("Paiq", "#dc2626", "images/person2.jpg");
//        Div jastipper3 = createJastipperCard("Triston", "#f59e0b", "images/person3.jpg");
//        Div jastipper4 = createJastipperCard("Osksar", "#8b5cf6", "images/person4.jpg");
//
//        jastipersContainer.add(jastipper1, jastipper2, jastipper3, jastipper4);
//        jastipersSection.add(jastipersTitle, jastipersContainer);
//        return jastipersSection;
//    }
//
//    private Div createJastipperCard(String name, String backgroundColor, String imagePath) {
//        Div card = new Div();
//        card.getStyle().set("background-color", backgroundColor);
//        card.getStyle().set("width", "200px");
//        card.getStyle().set("height", "350px");
//        card.getStyle().set("border-radius", "20px");
//        card.getStyle().set("display", "flex");
//        card.getStyle().set("flex-direction", "column");
//        card.getStyle().set("align-items", "center");
//        card.getStyle().set("justify-content", "space-between");
//        card.getStyle().set("padding", "2rem");
//        card.getStyle().set("color", "white");
//        card.getStyle().set("box-shadow", "0 15px 30px rgba(0,0,0,0.2)");
//        card.getStyle().set("cursor", "pointer");
//        card.getStyle().set("transition", "all 0.3s ease");
//        card.getStyle().set("position", "relative");
//        card.getStyle().set("overflow", "hidden");
//
//        // Container untuk foto
//        Div imageContainer = new Div();
//        imageContainer.getStyle().set("width", "120px");
//        imageContainer.getStyle().set("height", "120px");
//        imageContainer.getStyle().set("border-radius", "50%");
//        imageContainer.getStyle().set("overflow", "hidden");
//        imageContainer.getStyle().set("border", "4px solid white");
//        imageContainer.getStyle().set("margin-top", "1rem");
//        imageContainer.getStyle().set("box-shadow", "0 5px 15px rgba(0,0,0,0.3)");
//
//        // Gambar/foto
//        Image profileImage = new Image(imagePath, name);
//        profileImage.getStyle().set("width", "100%");
//        profileImage.getStyle().set("height", "100%");
//        profileImage.getStyle().set("object-fit", "cover");
//        profileImage.getStyle().set("display", "block");
//
//        imageContainer.add(profileImage);
//
//        // Container untuk nama
//        Div nameContainer = new Div();
//        nameContainer.getStyle().set("text-align", "center");
//        nameContainer.getStyle().set("margin-bottom", "1rem");
//
//        Span nameSpan = new Span(name);
//        nameSpan.getStyle().set("font-weight", "bold");
//        nameSpan.getStyle().set("font-size", "1.2rem");
//        nameSpan.getStyle().set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");
//
//        nameContainer.add(nameSpan);
//
//        card.add(imageContainer, nameContainer);
//
//        // Tambahkan hover effect
//        card.getElement().addEventListener("mouseenter", e -> {
//            card.getStyle().set("transform", "translateY(-10px) scale(1.05)");
//            card.getStyle().set("box-shadow", "0 25px 50px rgba(0,0,0,0.3)");
//        });
//
//        card.getElement().addEventListener("mouseleave", e -> {
//            card.getStyle().set("transform", "translateY(0) scale(1)");
//            card.getStyle().set("box-shadow", "0 15px 30px rgba(0,0,0,0.2)");
//        });
//
//        return card;
//    }
//
//    // Alternatif: Jika ingin menggunakan placeholder/avatar default
//    private Div createJastipperCardWithAvatar(String name, String backgroundColor, String initials) {
//        Div card = new Div();
//        card.getStyle().set("background-color", backgroundColor);
//        card.getStyle().set("width", "200px");
//        card.getStyle().set("height", "350px");
//        card.getStyle().set("border-radius", "20px");
//        card.getStyle().set("display", "flex");
//        card.getStyle().set("flex-direction", "column");
//        card.getStyle().set("align-items", "center");
//        card.getStyle().set("justify-content", "space-between");
//        card.getStyle().set("padding", "2rem");
//        card.getStyle().set("color", "white");
//        card.getStyle().set("box-shadow", "0 15px 30px rgba(0,0,0,0.2)");
//        card.getStyle().set("cursor", "pointer");
//        card.getStyle().set("transition", "all 0.3s ease");
//
//        // Avatar dengan inisial
//        Div avatar = new Div();
//        avatar.getStyle().set("width", "120px");
//        avatar.getStyle().set("height", "120px");
//        avatar.getStyle().set("border-radius", "50%");
//        avatar.getStyle().set("background-color", "rgba(255,255,255,0.2)");
//        avatar.getStyle().set("display", "flex");
//        avatar.getStyle().set("align-items", "center");
//        avatar.getStyle().set("justify-content", "center");
//        avatar.getStyle().set("border", "4px solid white");
//        avatar.getStyle().set("margin-top", "1rem");
//        avatar.getStyle().set("box-shadow", "0 5px 15px rgba(0,0,0,0.3)");
//
//        Span initialsSpan = new Span(initials);
//        initialsSpan.getStyle().set("font-size", "2.5rem");
//        initialsSpan.getStyle().set("font-weight", "bold");
//        initialsSpan.getStyle().set("color", "white");
//
//        avatar.add(initialsSpan);
//
//        // Container untuk nama
//        Div nameContainer = new Div();
//        nameContainer.getStyle().set("text-align", "center");
//        nameContainer.getStyle().set("margin-bottom", "1rem");
//
//        Span nameSpan = new Span(name);
//        nameSpan.getStyle().set("font-weight", "bold");
//        nameSpan.getStyle().set("font-size", "1.2rem");
//        nameSpan.getStyle().set("text-shadow", "0 2px 4px rgba(0,0,0,0.3)");
//
//        nameContainer.add(nameSpan);
//
//        card.add(avatar, nameContainer);
//        return card;
//    }

    private Component createFooter() {
        Div footer = new Div();
        footer.getStyle()
                .set("background-color", "#9BA4B4")
                .set("color", "white")
                .set("padding", "2rem 5%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("text-align", "center")
                .set("font-family", "Poppins, sans-serif")
                .set("width", "100%")
                .set("justify-content", "center")
                .set("position", "relative");

        H3 brandName = new H3("JASTIPKUY!");
        brandName.getStyle()
                .set("color", "white")
                .set("font-size", "1.8rem")
                .set("font-weight", "700")
                .set("margin", "0 0 1rem 0")
                .set("font-family", "Poppins, sans-serif");

        Div navLinks = new Div();
        navLinks.getStyle()
                .set("display", "flex")
                .set("gap", "1.5rem")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("margin-bottom", "1rem");

        Button homeLink = createFooterLink("Home", "");
        Button aboutLink = createFooterLink("About", "");
        Button contactLink = createFooterLink("Contact", "");

        navLinks.add(homeLink, aboutLink, contactLink);

        Div contactInfo = new Div();
        contactInfo.getStyle()
                .set("font-size", "1rem")
                .set("margin-bottom", "1rem")
                .set("line-height", "1.6")
                .set("font-family", "Poppins, sans-serif");

        Span email = new Span("Email: contact@jastipkuy.com");
        Span phone = new Span("Phone: +62 123 456 7890");

        Paragraph copyright = new Paragraph("© 2025 JASTIPKUY! All rights reserved.");
        copyright.getStyle()
                .set("font-size", "0.9rem")
                .set("opacity", "0.8")
                .set("margin", "0")
                .set("font-family", "Poppins, sans-serif");

        footer.add(brandName, navLinks, contactInfo, copyright);

        return footer;
    }

    private Button createFooterLink(String text, String route) {
        Button link = new Button(text);
        link.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        link.getStyle()
                .set("font-size", "1rem")
                .set("color", "white")
                .set("font-weight", "500")
                .set("font-family", "Poppins, sans-serif");

        link.getElement().addEventListener("mouseenter", e -> {
            link.getStyle().set("color", "#9BA4B4");
        });
        link.getElement().addEventListener("mouseleave", e -> {
            link.getStyle().set("color", "white");
        });

        link.addClickListener(e -> UI.getCurrent().navigate(route));

        return link;
    }
}