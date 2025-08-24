package com.example.application.view.user;

import com.example.application.dao.*;
import com.example.application.model.Titipan;
import com.example.application.model.TitipanDetail;
import com.example.application.model.User;
import com.example.application.session.SessionUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Route("user")
@PageTitle("Dashboard User | JastipKuy")
public class UserDashboardView extends HorizontalLayout implements BeforeEnterObserver {

    private final TitipanDAO titipanDAO = new TitipanDAO();
    private final TitipanDetailDAO detailDAO = new TitipanDetailDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final LaporanDAO laporanDAO = new LaporanDAO();

    // State
    private User currentUser;

    // Formatter
    private final Locale ID = new Locale("id","ID");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", ID);
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(ID);

    // Sidebar buttons
    private Button homeBtn, dataBtn, orderBtn, riwayatBtn;

    // Sections
    private VerticalLayout contentWrap;
    private Div homeSection, dataSection, orderSection, riwayatSection;

    // Grids
    private Grid<Titipan> historyGridHome = new Grid<>(Titipan.class, false);
    private Grid<Titipan> historyGridFull = new Grid<>(Titipan.class, false);

    public UserDashboardView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("overflow", "hidden"); // Prevent double scroll
        Integer userId = SessionUtils.getUserId();
        if (userId == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        // Load current user data
        currentUser = userDAO.getUserById(userId);
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }


        // UI Structure
        VerticalLayout sidebar = buildSidebar();
        contentWrap = new VerticalLayout();
        contentWrap.setSizeFull();
        contentWrap.setPadding(false);
        contentWrap.getStyle()
                .set("margin-left", "250px")
                .set("background-color", "#f8fafc")
                .set("min-height", "100vh");

        // Build all sections
        homeSection = buildHomeSection();
        dataSection = buildDataSection();
        orderSection = buildOrderSection();
        riwayatSection = buildRiwayatSection();

        // Default: HOME
        contentWrap.add(homeSection, dataSection, orderSection, riwayatSection);
        showSection("HOME");

        add(sidebar, contentWrap);
    }

    /* ========================= Sidebar ========================= */
    private VerticalLayout buildSidebar() {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth("250px");
        sidebar.setHeight("100vh");
        sidebar.setPadding(false);
        sidebar.setSpacing(false);
        sidebar.getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0")
                .set("background-color", "#1e293b")
                .set("color", "white")
                .set("box-shadow", "2px 0 10px rgba(0,0,0,0.1)");

        // Logo section
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.setPadding(true);
        logoLayout.setSpacing(true);
        logoLayout.setWidthFull();

        Image logoImg = new Image("logo.png", "JastipKuy");
        logoImg.setHeight("32px");
        H3 logoText = new H3("JASTIPKUY");
        logoText.getStyle()
                .set("color", "white")
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "600");
        logoLayout.add(logoImg, logoText);

        // Profile section
        VerticalLayout profileLayout = new VerticalLayout();
        profileLayout.setPadding(true);
        profileLayout.setSpacing(false);
        profileLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        profileLayout.getStyle().set("border-bottom", "1px solid #334155");

        Div avatar = new Div(new Span("👤"));
        avatar.getStyle()
                .set("width", "64px")
                .set("height", "64px")
                .set("border-radius", "50%")
                .set("background", "#334155")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "24px")
                .set("margin-bottom", "8px");

        Span greet = new Span("Hi, " + (currentUser != null ? currentUser.getName() : "User"));
        greet.getStyle()
                .set("color", "#e2e8f0")
                .set("font-size", "14px")
                .set("font-weight", "500");

        profileLayout.add(avatar, greet);

        // Menu items
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(false);
        menuLayout.setSpacing(false);
        menuLayout.setWidthFull();

        homeBtn = createMenuButton("Home", VaadinIcon.HOME, "HOME");
        dataBtn = createMenuButton("Data Saya", VaadinIcon.USER, "DATA");
        orderBtn = createMenuButton("Order", VaadinIcon.CART, "ORDER");
        riwayatBtn = createMenuButton("Riwayat", VaadinIcon.CLOCK, "RIWAYAT");

        menuLayout.add(homeBtn, dataBtn, orderBtn, riwayatBtn);

        // Logout button
        Button logoutBtn = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutBtn.setWidthFull();
        logoutBtn.getStyle()
                .set("color", "#e2e8f0")
                .set("margin-top", "auto")
                .set("padding", "12px 16px")
                .set("border-top", "1px solid #334155");
        logoutBtn.addClickListener(e -> {
            try {
                SessionUtils.clearSession();
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                // Fallback logout - clear session manually and navigate
                try {
                    VaadinSession.getCurrent().setAttribute("idUser", null);
                    VaadinSession.getCurrent().setAttribute("userRole", null);
                } catch (Exception ignored) {}
                UI.getCurrent().navigate("login");
            }
        });

        sidebar.add(logoLayout, profileLayout, menuLayout, logoutBtn);
        sidebar.setFlexGrow(1, menuLayout);

        return sidebar;
    }

    private Button createMenuButton(String text, VaadinIcon icon, String sectionKey) {
        Button button = new Button(text, new Icon(icon));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.setWidthFull();
        button.getStyle()
                .set("color", "#e2e8f0")
                .set("justify-content", "flex-start")
                .set("padding", "12px 16px")
                .set("border-radius", "4px")
                .set("transition", "background-color 0.2s");

        button.addClickListener(e -> {
            resetMenuButtons();
            button.getStyle().set("background-color", "#334155");
            showSection(sectionKey);
        });

        if ("HOME".equals(sectionKey)) {
            button.getStyle().set("background-color", "#334155");
        }

        return button;
    }

    private void resetMenuButtons() {
        homeBtn.getStyle().remove("background-color");
        dataBtn.getStyle().remove("background-color");
        orderBtn.getStyle().remove("background-color");
        riwayatBtn.getStyle().remove("background-color");
    }

    /* ========================= Sections ========================= */
    private Div buildHomeSection() {
        Div wrap = new Div();
        wrap.setWidthFull();
        wrap.getStyle().set("padding", "24px");

        // Title
        H2 title = new H2("Status Pesanan Saat Ini");
        title.getStyle()
                .set("margin", "0 0 20px 0")
                .set("color", "#1e293b");

        // Active order card
        Component activeCard = buildActiveCard();

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.getStyle()
                .set("flex-wrap", "wrap")
                .set("gap", "16px")
                .set("margin", "30px 0");

        Button editData = new Button("Edit Data", new Icon(VaadinIcon.EDIT));
        editData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editData.getStyle()
                .set("background-color", "#3b82f6")
                .set("font-size", "16px");
        editData.setWidth("280px");
        editData.setHeight("50px");
        editData.addClickListener(e -> showSection("DATA"));

        Button buatPesanan = new Button("Buat Pesanan", new Icon(VaadinIcon.PLUS));
        buatPesanan.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buatPesanan.getStyle()
                .set("background-color", "#4f46e5")
                .set("font-size", "16px");
        buatPesanan.setWidth("280px");
        buatPesanan.setHeight("50px");
        buatPesanan.addClickListener(e -> showSection("ORDER"));

        actions.add(editData, buatPesanan);

        // History
        H3 historyTitle = new H3("Riwayat Pesanan");
        historyTitle.getStyle()
                .set("margin", "30px 0 20px 0")
                .set("color", "#1e293b");

        setupHistoryGrid(historyGridHome);
        historyGridHome.setWidthFull();

        wrap.add(title, activeCard, actions, historyTitle, historyGridHome);
        return wrap;
    }

    private Div buildDataSection() {
        Integer userId = SessionUtils.getUserId();
        Div wrap = new Div();
        wrap.setWidthFull();
        wrap.getStyle().set("padding", "24px");

        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", "white")
                .set("padding", "32px")
                .set("border-radius", "12px")
                .set("max-width", "600px")
                .set("margin", "0 auto")
                .set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");

        H2 title = new H2("Data Pribadi");
        title.getStyle()
                .set("color", "#1e293b")
                .set("text-align", "center")
                .set("margin-bottom", "24px");

        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.setPadding(false);
        fieldsLayout.setSpacing(true);

        TextField nisn = createEditableField("NISN", currentUser != null ? Objects.toString(currentUser.getNisn(),"") : "");
        TextField nama = createEditableField("Nama", currentUser != null ? Objects.toString(currentUser.getName(),"") : "");
        TextField email = createEditableField("Email", currentUser != null ? Objects.toString(currentUser.getEmail(),"") : "");
        TextField pass = createEditableField("Password", currentUser != null ? Objects.toString(currentUser.getPassword(),"") : "");

        fieldsLayout.add(
                createFieldRow(nisn),
                createFieldRow(nama),
                createFieldRow(email),
                createFieldRow(pass)
        );

        Button simpan = new Button("Simpan Perubahan", new Icon(VaadinIcon.CHECK));
        simpan.setWidthFull();
        simpan.setHeight("45px");
        simpan.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        simpan.getStyle()
                .set("background-color", "#3b82f6")
                .set("margin-top", "24px");
        simpan.addClickListener(e -> {
            boolean ok = userDAO.update(
                    userId,
                    nisn.getValue(),
                    nama.getValue(),
                    email.getValue(),
                    pass.getValue(),
                    currentUser != null ? currentUser.getRole() : "USER"
            );
            Notification.show(ok ? "Data berhasil disimpan" : "Gagal menyimpan data");
            if (ok) currentUser = userDAO.getUserById(userId);
        });

        formContainer.add(title, fieldsLayout, simpan);
        wrap.add(formContainer);
        return wrap;
    }

    private TextField createEditableField(String label, String value) {
        TextField field = new TextField(label);
        field.setValue(value);
        field.setWidthFull();
        field.getStyle()
                .set("background-color", "white")
                .set("border-radius", "6px");
        return field;
    }

    private HorizontalLayout createFieldRow(TextField field) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);

        field.setWidthFull();

        Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
        editBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        editBtn.getStyle()
                .set("background-color", "#e2e8f0")
                .set("color", "#1e293b")
                .set("min-width", "40px");
        editBtn.addClickListener(e -> {
            boolean ro = field.isReadOnly();
            field.setReadOnly(!ro);
            editBtn.setIcon(new Icon(!ro ? VaadinIcon.CHECK : VaadinIcon.EDIT));
        });

        row.add(field, editBtn);
        row.setFlexGrow(1, field);
        return row;
    }

    private Div buildOrderSection() {
        Integer userId = SessionUtils.getUserId();
        Div wrap = new Div();
        wrap.setWidthFull();
        wrap.getStyle().set("padding", "24px");

        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", "white")
                .set("padding", "32px")
                .set("border-radius", "12px")
                .set("max-width", "800px")
                .set("margin", "0 auto")
                .set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");

        H2 title = new H2("Form Order JastipKuy");
        title.getStyle()
                .set("color", "#1e293b")
                .set("text-align", "center")
                .set("margin-bottom", "24px");

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setWidthFull();
        formLayout.setSpacing(true);

        // Left column
        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("50%");
        leftColumn.setPadding(false);
        leftColumn.setSpacing(true);

        TextField namaBarang = new TextField("Nama Barang");
        namaBarang.setWidthFull();
        namaBarang.setRequired(true);

        TextField lokasiJemput = new TextField("Lokasi Jemput");
        lokasiJemput.setWidthFull();
        lokasiJemput.setRequired(true);

        TextField lokasiAntar = new TextField("Lokasi Antar");
        lokasiAntar.setWidthFull();
        lokasiAntar.setRequired(true);

        NumberField biayaBarang = new NumberField("Biaya Barang (Opsional)");
        biayaBarang.setPlaceholder("Masukkan biaya barang dalam rupiah");
        biayaBarang.setWidthFull();
        biayaBarang.setMin(0);
        biayaBarang.setStep(1000);

        leftColumn.add(namaBarang, lokasiJemput, lokasiAntar, biayaBarang);

        // Right column
        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");
        rightColumn.setPadding(false);
        rightColumn.setSpacing(true);

        DatePicker tanggal = new DatePicker("Tanggal");
        tanggal.setWidthFull();
        tanggal.setValue(LocalDate.now());

        TextField biayaJastiper = new TextField("Biaya Jastiper");
        biayaJastiper.setWidthFull();
        biayaJastiper.setValue("Rp 2.000");
        biayaJastiper.setReadOnly(true);

        TextField totalBiaya = new TextField("Total Biaya");
        totalBiaya.setWidthFull();
        totalBiaya.setReadOnly(true);
        totalBiaya.setValue("Rp 2.000");
        
        // Calculate total when biaya barang changes
        biayaBarang.addValueChangeListener(event -> {
            Double biayaBarangValue = event.getValue();
            if (biayaBarangValue != null && biayaBarangValue > 0) {
                long total = biayaBarangValue.longValue() + 2000; // 2000 is jastiper fee
                totalBiaya.setValue("Rp " + total);
            } else {
                totalBiaya.setValue("Rp 2.000");
            }
        });

        rightColumn.add(tanggal, biayaJastiper, totalBiaya);

        formLayout.add(leftColumn, rightColumn);

        // Buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "24px");

        Button cancel = new Button("Batal", new Icon(VaadinIcon.CLOSE));
        cancel.setWidth("150px");
        cancel.setHeight("45px");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle()
                .set("background-color", "#e2e8f0")
                .set("color", "#1e293b");
        cancel.addClickListener(e -> showSection("HOME"));

        Button submit = new Button("Submit Order", new Icon(VaadinIcon.CHECK));
        submit.setWidth("150px");
        submit.setHeight("45px");
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.getStyle().set("background-color", "#3b82f6");
        submit.addClickListener(e -> {
            // Validate required fields
            if (namaBarang.isEmpty()) {
                Notification.show("Nama barang harus diisi!");
                namaBarang.focus();
                return;
            }
            if (lokasiJemput.isEmpty()) {
                Notification.show("Lokasi jemput harus diisi!");
                lokasiJemput.focus();
                return;
            }
            if (lokasiAntar.isEmpty()) {
                Notification.show("Lokasi antar harus diisi!");
                lokasiAntar.focus();
                return;
            }

            try {
                // Create titipan record
                Titipan t = new Titipan();
                t.setUser_id(userId);
                t.setStatus("MENUNGGU");
                t.setDiambil_oleh(null);
                t.setCreated_at(new Date());
                
                // Biaya barang dari input, default 0 jika kosong
                Long biayaBarangValue = 0L;
                try {
                    Double val = biayaBarang.getValue();
                    if (val != null && val > 0) {
                        biayaBarangValue = val.longValue();
                    }
                } catch (Exception ex) {
                    biayaBarangValue = 0L;
                }
                t.setHarga_estimasi(biayaBarangValue);
                t.setLokasi_antar(lokasiAntar.getValue());
                t.setLokasi_jemput(lokasiJemput.getValue());
                t.setNama_barang(namaBarang.getValue());

                // Insert header and get new ID
                int idBaru = titipanDAO.insertTitipanReturnId(t);
                if (idBaru <= 0) {
                    Notification.show("Gagal membuat titipan. Silakan coba lagi.");
                    System.err.println("Failed to insert titipan for user: " + userId);
                    return;
                }

                // Create detail record
                TitipanDetail detail = new TitipanDetail();
                detail.setIdTransaksi(idBaru);
                detail.setDeskripsi(namaBarang.getValue());
                detail.setCatatan_opsional(lokasiJemput.getValue() + " - " + lokasiAntar.getValue());
                
                boolean detailSuccess = detailDAO.insertDetail(detail);
                if (!detailSuccess) {
                    // If detail insertion fails, we should ideally rollback the titipan
                    // For now, just show a warning
                    Notification.show("Order dibuat dengan ID #" + idBaru + " tetapi detail tidak tersimpan");
                    System.err.println("Failed to insert detail for titipan ID: " + idBaru);
                } else {
                    Notification.show("Order berhasil dibuat (#" + idBaru + ")");
                }
                
                reloadHomeAndHistory();
                showSection("HOME");
                
                // Clear form for next use
                namaBarang.clear();
                lokasiJemput.clear();
                lokasiAntar.clear();
                biayaBarang.clear();
                tanggal.setValue(LocalDate.now());
                
            } catch (Exception ex) {
                System.err.println("Error creating titipan: " + ex.getMessage());
                ex.printStackTrace();
                Notification.show("Terjadi kesalahan sistem. Silakan coba lagi atau hubungi administrator.");
            }
        });

        buttons.add(cancel, submit);

        formContainer.add(title, formLayout, buttons);
        wrap.add(formContainer);
        return wrap;
    }

    private Div buildRiwayatSection() {
        Div wrap = new Div();
        wrap.setWidthFull();
        wrap.getStyle().set("padding", "24px");

        H2 title = new H2("Riwayat Pesanan");
        title.getStyle()
                .set("color", "#1e293b")
                .set("margin-bottom", "20px");

        // Grid for history
        setupHistoryGrid(historyGridFull);
        historyGridFull.setWidthFull();
        historyGridFull.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");

        wrap.add(title, historyGridFull);
        return wrap;
    }

    /* ========================= Helpers ========================= */
    private void showSection(String key) {
        homeSection.setVisible("HOME".equals(key));
        dataSection.setVisible("DATA".equals(key));
        orderSection.setVisible("ORDER".equals(key));
        riwayatSection.setVisible("RIWAYAT".equals(key));
    }

    private Component buildActiveCard() {
        Integer userId = SessionUtils.getUserId();

        Div card = new Div();
        card.getStyle()
                .set("padding", "24px")
                .set("border-radius", "12px")
                .set("background", "linear-gradient(135deg, #3b82f6, #6366f1)")
                .set("color", "white")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)")
                .set("width", "100%");

        Titipan t = titipanDAO.getActiveByUser(userId);
        if (t == null) {
            Span noOrder = new Span("Tidak ada pesanan aktif saat ini.");
            noOrder.getStyle().set("font-size", "16px");
            card.add(noOrder);
            return card;
        }

        String driver = (t.getDiambil_oleh() == null || t.getDiambil_oleh() == 0)
                ? "Belum ada driver" : userDAO.getUserNameById(t.getDiambil_oleh());
        String jam = new SimpleDateFormat("HH.mm", ID).format(t.getCreated_at());
        String tanggal = new SimpleDateFormat("EEEE, dd MMM yyyy", ID).format(t.getCreated_at());

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();

        VerticalLayout leftInfo = new VerticalLayout();
        leftInfo.setPadding(false);
        leftInfo.setSpacing(false);

        Span driverInfo = new Span(driver);
        driverInfo.getStyle().set("font-size", "16px");

        H1 time = new H1(jam);
        time.getStyle()
                .set("font-size", "48px")
                .set("font-weight", "bold")
                .set("margin", "10px 0");

        Span statusText = new Span("DIPROSES".equalsIgnoreCase(t.getStatus()) ?
                "Memproses pesanan" : t.getStatus());
        statusText.getStyle().set("font-size", "18px");

        leftInfo.add(driverInfo, time, statusText);

        Button detailBtn = new Button("Detail", new Icon(VaadinIcon.ELLIPSIS_DOTS_H));
        detailBtn.getStyle()
                .set("background-color", "rgba(255,255,255,0.2)")
                .set("color", "white")
                .set("min-width", "120px");
        detailBtn.addClickListener(e -> openDetailDialog(t.getId()));

        layout.add(leftInfo, detailBtn);
        layout.setFlexGrow(1, leftInfo);

        card.add(layout);
        return card;
    }

    private void setupHistoryGrid(Grid<Titipan> grid) {
        Integer userId = SessionUtils.getUserId();

        grid.removeAllColumns();
        grid.addColumn(Titipan::getId)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(t -> sdf.format(t.getCreated_at()))
                .setHeader("Tanggal")
                .setWidth("150px");

        grid.addColumn(t -> t.getNama_barang() != null ? t.getNama_barang() : "-")
                .setHeader("Nama Barang");

        // Hapus kolom Customer
        // grid.addColumn(t -> currentUser != null ? currentUser.getName() : "-")
        //        .setHeader("Customer");

        grid.addColumn(t -> t.getDiambil_oleh() != null && t.getDiambil_oleh() != 0 ?
                        userDAO.getUserNameById(t.getDiambil_oleh()) : "-")
                .setHeader("JASTIPER");

        grid.addColumn(Titipan::getStatus)
                .setHeader("Status")
                .setWidth("120px");

        grid.addComponentColumn(t -> {
                    Button btnRate = new Button("Rating", new Icon(VaadinIcon.STAR));
                    btnRate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    btnRate.getStyle()
                            .set("background-color", "#3b82f6")
                            .set("font-size", "13px");
                    boolean enableRate = "SELESAI".equalsIgnoreCase(t.getStatus())
                            && t.getDiambil_oleh() != null && t.getDiambil_oleh() != 0;
                    btnRate.setEnabled(enableRate);
                    btnRate.addClickListener(e -> openRatingDialog(t));
                    return btnRate;
                }).setHeader("Aksi")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        var history = titipanDAO.getHistoryByUser(userId);
        if (history != null) grid.setItems(history);
    }

    private void reloadHomeAndHistory() {
        Integer userId = SessionUtils.getUserId();

        // Rebuild Home section
        int idxHome = contentWrap.indexOf(homeSection);
        if (idxHome >= 0) {
            contentWrap.remove(homeSection);
            homeSection = buildHomeSection();
            contentWrap.addComponentAtIndex(idxHome, homeSection);
        }

        // Refresh grids
        var history = titipanDAO.getHistoryByUser(userId);
        if (historyGridHome != null) historyGridHome.setItems(history);
        if (historyGridFull != null) historyGridFull.setItems(history);

        // Rebuild Riwayat section
        int idxR = contentWrap.indexOf(riwayatSection);
        if (idxR >= 0) {
            contentWrap.remove(riwayatSection);
            riwayatSection = buildRiwayatSection();
            contentWrap.addComponentAtIndex(idxR, riwayatSection);
        }
    }

    private void openDetailDialog(int titipanId) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");

        H3 header = new H3("Detail Titipan #" + titipanId);
        header.getStyle().set("color", "#1e293b");

        Grid<TitipanDetail> grid = new Grid<>(TitipanDetail.class, false);
        grid.addColumn(TitipanDetail::getDeskripsi).setHeader("Deskripsi");
        grid.addColumn(TitipanDetail::getCatatan_opsional).setHeader("Catatan");
        grid.setItems(detailDAO.getDetailsByTransaksiId(titipanId));

        Button close = new Button("Tutup", new Icon(VaadinIcon.CLOSE), e -> dialog.close());
        close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.getStyle().set("background-color", "#3b82f6");

        VerticalLayout box = new VerticalLayout(header, grid, close);
        box.setAlignItems(FlexComponent.Alignment.END);
        dialog.add(box);
        dialog.open();
    }

    private void openRatingDialog(Titipan t) {
        Integer userId = SessionUtils.getUserId();

        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H3 title = new H3("Beri Rating JASTIPER");
        title.getStyle().set("color", "#1e293b");

        String driverName = (t.getDiambil_oleh() == null || t.getDiambil_oleh() == 0)
                ? "-" : userDAO.getUserNameById(t.getDiambil_oleh());
        Span s1 = new Span("Nama JASTIPER: " + driverName);
        s1.getStyle().set("font-weight", "500");

        NumberField rating = new NumberField("Rating (1-10)");
        rating.setMin(1);
        rating.setMax(10);
        rating.setStep(1);
        rating.setValue(5d);
        rating.setWidth("120px");

        TextArea saran = new TextArea("Saran/Komentar");
        saran.setWidthFull();
        saran.setHeight("120px");

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button cancel = new Button("Batal", new Icon(VaadinIcon.CLOSE), e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button submit = new Button("Submit", new Icon(VaadinIcon.CHECK), e -> {
            int r = rating.getValue() == null ? 0 : rating.getValue().intValue();
            boolean ok1 = ratingDAO.insertRating(t.getDiambil_oleh(), r);
            boolean ok2 = laporanDAO.insertLaporan(userId, t.getDiambil_oleh(), saran.getValue());
            Notification.show((ok1 && ok2) ? "Terima kasih atas rating Anda!" : "Gagal menyimpan rating");
            dialog.close();
        });
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.getStyle().set("background-color", "#3b82f6");

        buttons.add(cancel, submit);

        VerticalLayout content = new VerticalLayout(title, s1, rating, saran, buttons);
        content.setPadding(false);
        dialog.add(content);
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Integer userId = SessionUtils.getUserId();
        String userRole = SessionUtils.getUserRole();

        if (userId == null) {
            event.rerouteTo("login"); // belum login, balik ke login
            return;
        }

        if (!"Penitip".equalsIgnoreCase(userRole)) {
            if ("Admin".equalsIgnoreCase(userRole)) {
                event.rerouteTo("admin");
            } else if ("Jastiper".equalsIgnoreCase(userRole)) {
                event.rerouteTo("jastiper");
            } else {
                event.rerouteTo(""); // fallback ke home
            }
        }
    }


    private String formatRupiah(Long amount) {
        if (amount == null) return "-";
        return rupiah.format(amount).replace(",00", "");
    }

}