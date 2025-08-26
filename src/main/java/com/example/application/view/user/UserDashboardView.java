package com.example.application.view.user;

import com.example.application.dao.*;
import com.example.application.model.Titipan;
import com.example.application.model.TitipanDetail;
import com.example.application.model.User;
import com.example.application.session.SessionUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
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

//tes

@PageTitle("JastipKuy • Dashboard Penitip")
@Route("user")
@Uses(Icon.class)
public class UserDashboardView extends Div implements BeforeEnterObserver {

    // DAO
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

    // Color scheme (consistent with JastiperView)
    private static final String NAVY      = "#11284B";
    private static final String SLATE     = "#34465B";
    private static final String WHITE     = "#FFFFFF";
    private static final String LIGHT     = "#E6E6E6";
    private static final String TEXT_DARK = "#0F172A";
    private static final String PRIMARY   = "#3B82F6";
    private static final String SUCCESS   = "#10B981";
    private static final String WARNING   = "#F59E0B";
    private static final String DANGER    = "#EF4444";

    // Layout components
    private final VerticalLayout content = new VerticalLayout();
    private final Map<String, Component> pages = new LinkedHashMap<>();

    public UserDashboardView() {
        setSizeFull();
        getStyle().set("background", WHITE).set("font-family", "Inter, system-ui, -apple-system, Segoe UI, Roboto, Arial");
        
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

        // ======= TOP BAR =======
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setPadding(false);
        topBar.setSpacing(false);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.getStyle().set("background", NAVY).set("color", WHITE).set("height", "64px").set("padding", "0 20px");

        H3 brand = new H3("!! JASTIPKUY !!");
        brand.getStyle().set("margin", "0").set("letter-spacing", "2px").set("color", WHITE);

        Div topRight = new Div();
        topRight.getStyle().set("margin-left", "auto").set("display", "flex").set("align-items", "center").set("gap", "18px");

        Icon bell = VaadinIcon.BELL.create(); 
        bell.setColor(WHITE); 
        bell.setSize("20px");
        
        Button logout = new Button("Logout", VaadinIcon.SIGN_OUT.create()); 
        stylePrimary(logout);
        
        topRight.add(bell, logout);

        logout.addClickListener(e -> {
            try {
                SessionUtils.clearSession();
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                try {
                    VaadinSession.getCurrent().setAttribute("idUser", null);
                    VaadinSession.getCurrent().setAttribute("userRole", null);
                } catch (Exception ignored) {}
                UI.getCurrent().navigate("login");
            }
        });

        topBar.add(brand, topRight);

        // ======= LAYOUT (SIDEBAR + CONTENT) =======
        HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull(); 
        root.setPadding(false); 
        root.setSpacing(false);

        // ======= SIDEBAR =======
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth(280, Unit.PIXELS); 
        sidebar.setPadding(false); 
        sidebar.setSpacing(false);
        sidebar.getStyle().set("background", SLATE).set("color", WHITE).set("min-height", "calc(100vh - 64px)");

        VerticalLayout avatarWrap = new VerticalLayout();
        avatarWrap.setAlignItems(FlexComponent.Alignment.CENTER);
        avatarWrap.getStyle().set("padding", "30px 16px 22px 16px");
        
        Div avatar = circle(100, LIGHT);
        Icon userIco = VaadinIcon.USER.create(); 
        userIco.setSize("44px"); 
        userIco.setColor("#7A8AA0");
        avatar.add(userIco);
        
        Paragraph hi = new Paragraph("Hi, " + (currentUser != null ? currentUser.getName() : "Penitip")); 
        hi.getStyle().set("color", WHITE).set("margin", "14px 0 0 0").set("font-weight", "600");
        
        avatarWrap.add(avatar, hi);

        // Build pages first
        pages.put("Home", buildHome());
        pages.put("Buat Pesanan", buildOrderSection());
        pages.put("Riwayat Pesanan", buildRiwayatSection());
        pages.put("Data Pribadi", buildDataSection());

        sidebar.add(avatarWrap);
        pages.keySet().forEach(name -> sidebar.add(sideItem(name, () -> switchContent(name))));

        // ======= CONTENT WRAPPER =======
        content.setPadding(true); 
        content.setSpacing(false); 
        content.setSizeFull(); 
        content.getStyle().set("padding", "24px");
        
        switchContent("Home");

        root.add(sidebar, content);
        root.setFlexGrow(0, sidebar); 
        root.setFlexGrow(1, content);

        add(topBar, root);
    }

    // ------------------- PAGES -------------------

    private Component buildHome() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false); 
        wrap.setSpacing(false); 
        wrap.setWidthFull();

        H1 title = new H1("DASHBOARD PENITIP");
        title.getStyle().set("margin", "0 0 16px 0").set("font-size", "28px").set("letter-spacing", "1px").set("color", TEXT_DARK);

        // Status card
        Component statusCard = buildStatusCard();
        wrap.add(title, statusCard);

        // Quick actions
        H3 actionsTitle = new H3("Aksi Cepat");
        actionsTitle.getStyle().set("margin", "24px 0 16px 0").set("font-size", "20px").set("color", TEXT_DARK);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.getStyle().set("margin-bottom", "24px");

        Button buatPesanan = new Button("Buat Pesanan Baru", VaadinIcon.PLUS.create());
        stylePrimary(buatPesanan);
        buatPesanan.setWidth("200px");
        buatPesanan.setHeight("50px");
        buatPesanan.addClickListener(e -> switchContent("Buat Pesanan"));

        Button editData = new Button("Edit Data", VaadinIcon.EDIT.create());
        styleSecondary(editData);
        editData.setWidth("200px");
        editData.setHeight("50px");
        editData.addClickListener(e -> switchContent("Data Pribadi"));

        actions.add(buatPesanan, editData);

        // Recent orders
        H3 historyTitle = new H3("Pesanan Terbaru");
        historyTitle.getStyle().set("margin", "24px 0 16px 0").set("font-size", "20px").set("color", TEXT_DARK);

        Grid<Titipan> recentGrid = new Grid<>(Titipan.class, false);
        setupRecentGrid(recentGrid);
        recentGrid.setWidthFull();
        recentGrid.getStyle().set("background", WHITE).set("border-radius", "8px").set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

        wrap.add(actionsTitle, actions, historyTitle, recentGrid);
        
        return wrap;
    }

    private Component buildStatusCard() {
        Integer userId = SessionUtils.getUserId();
        Titipan activeOrder = titipanDAO.getActiveByUser(userId);

        Div card = new Div();
        card.getStyle()
                .set("background", "linear-gradient(135deg, " + PRIMARY + ", " + SLATE + ")")
                .set("color", WHITE)
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.15)")
                .set("width", "100%");

        if (activeOrder == null) {
            VerticalLayout noOrder = new VerticalLayout();
            noOrder.setAlignItems(FlexComponent.Alignment.CENTER);
            noOrder.setSpacing(false);
            noOrder.setPadding(false);

            Icon noOrderIcon = VaadinIcon.CART.create();
            noOrderIcon.setSize("48px");
            noOrderIcon.setColor(WHITE);

            H2 noOrderTitle = new H2("Tidak ada pesanan aktif");
            noOrderTitle.getStyle().set("margin", "16px 0 8px 0").set("color", WHITE);

            Span noOrderDesc = new Span("Buat pesanan baru untuk memulai");
            noOrderDesc.getStyle().set("color", "rgba(255,255,255,0.8)");

            noOrder.add(noOrderIcon, noOrderTitle, noOrderDesc);
            card.add(noOrder);
        } else {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setWidthFull();

            VerticalLayout orderInfo = new VerticalLayout();
            orderInfo.setPadding(false);
            orderInfo.setSpacing(false);

            Span orderId = new Span("Pesanan #" + activeOrder.getId());
            orderId.getStyle().set("font-size", "14px").set("color", "rgba(255,255,255,0.8)");

            H2 orderTitle = new H2(activeOrder.getNama_barang() != null ? activeOrder.getNama_barang() : "Pesanan");
            orderTitle.getStyle().set("margin", "8px 0").set("color", WHITE);

            Span status = new Span(activeOrder.getStatus());
            status.getStyle().set("font-size", "16px").set("font-weight", "600").set("color", WHITE);

            orderInfo.add(orderId, orderTitle, status);

            Button detailBtn = new Button("Lihat Detail", VaadinIcon.ELLIPSIS_DOTS_H.create());
            detailBtn.getStyle()
                    .set("background-color", "rgba(255,255,255,0.2)")
                    .set("color", WHITE)
                    .set("border", "none")
                    .set("border-radius", "8px")
                    .set("padding", "12px 20px");
            detailBtn.addClickListener(e -> openDetailDialog(activeOrder.getId()));

            layout.add(orderInfo, detailBtn);
            layout.setFlexGrow(1, orderInfo);

            card.add(layout);
        }

        return card;
    }

    private Component buildOrderSection() {
        Integer userId = SessionUtils.getUserId();
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false);
        wrap.setSpacing(false);
        wrap.setWidthFull();

        H1 title = new H1("Buat Pesanan Baru");
        title.getStyle().set("margin", "0 0 24px 0").set("font-size", "28px").set("color", TEXT_DARK);

        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", WHITE)
                .set("padding", "32px")
                .set("border-radius", "16px")
                .set("max-width", "800px")
                .set("margin", "0 auto")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)")
                .set("border", "1px solid " + LIGHT);

        H2 formTitle = new H2("Form Order JastipKuy");
        formTitle.getStyle()
                .set("color", TEXT_DARK)
                .set("text-align", "center")
                .set("margin-bottom", "32px")
                .set("font-size", "24px");

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
        styleTextField(namaBarang);

        TextField lokasiJemput = new TextField("Lokasi Jemput");
        lokasiJemput.setWidthFull();
        lokasiJemput.setRequired(true);
        styleTextField(lokasiJemput);

        TextField lokasiAntar = new TextField("Lokasi Antar");
        lokasiAntar.setWidthFull();
        lokasiAntar.setRequired(true);
        styleTextField(lokasiAntar);

        NumberField biayaBarang = new NumberField("Biaya Barang (Opsional)");
        biayaBarang.setPlaceholder("Masukkan biaya barang dalam rupiah");
        biayaBarang.setWidthFull();
        biayaBarang.setMin(0);
        biayaBarang.setStep(1000);
        styleTextField(biayaBarang);

        leftColumn.add(namaBarang, lokasiJemput, lokasiAntar, biayaBarang);

        // Right column
        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");
        rightColumn.setPadding(false);
        rightColumn.setSpacing(true);

        DatePicker tanggal = new DatePicker("Tanggal");
        tanggal.setWidthFull();
        tanggal.setValue(LocalDate.now());
        styleTextField(tanggal);

        TextField biayaJastiper = new TextField("Biaya Jastiper");
        biayaJastiper.setWidthFull();
        biayaJastiper.setValue("Rp 2.000");
        biayaJastiper.setReadOnly(true);
        styleTextField(biayaJastiper);

        TextField totalBiaya = new TextField("Total Biaya");
        totalBiaya.setWidthFull();
        totalBiaya.setReadOnly(true);
        totalBiaya.setValue("Rp 2.000");
        styleTextField(totalBiaya);

        // Calculate total when biaya barang changes
        biayaBarang.addValueChangeListener(event -> {
            Double biayaBarangValue = event.getValue();
            if (biayaBarangValue != null && biayaBarangValue > 0) {
                long total = biayaBarangValue.longValue() + 2000;
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
        buttons.getStyle().set("margin-top", "32px");

        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create());
        cancel.setWidth("150px");
        cancel.setHeight("45px");
        styleSecondary(cancel);
        cancel.addClickListener(e -> switchContent("Home"));

        Button submit = new Button("Submit Order", VaadinIcon.CHECK.create());
        submit.setWidth("150px");
        submit.setHeight("45px");
        stylePrimary(submit);
        submit.addClickListener(e -> handleOrderSubmission(namaBarang, lokasiJemput, lokasiAntar, biayaBarang, tanggal));

        buttons.add(cancel, submit);

        formContainer.add(formTitle, formLayout, buttons);
        wrap.add(title, formContainer);
        return wrap;
    }

    private Component buildRiwayatSection() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false);
        wrap.setSpacing(false);
        wrap.setWidthFull();

        H1 title = new H1("Riwayat Pesanan");
        title.getStyle().set("margin", "0 0 24px 0").set("font-size", "28px").set("color", TEXT_DARK);

        Grid<Titipan> historyGrid = new Grid<>(Titipan.class, false);
        setupHistoryGrid(historyGrid);
        historyGrid.setWidthFull();
        historyGrid.getStyle()
                .set("background", WHITE)
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

        wrap.add(title, historyGrid);
        return wrap;
    }

    private Component buildDataSection() {
        Integer userId = SessionUtils.getUserId();
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false);
        wrap.setSpacing(false);
        wrap.setWidthFull();

        H1 title = new H1("Data Pribadi");
        title.getStyle().set("margin", "0 0 24px 0").set("font-size", "28px").set("color", TEXT_DARK);

        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", WHITE)
                .set("padding", "32px")
                .set("border-radius", "16px")
                .set("max-width", "600px")
                .set("margin", "0 auto")
                .set("box-shadow", "0 4px 20px rgba(0,0,0,0.1)")
                .set("border", "1px solid " + LIGHT);

        H2 formTitle = new H2("Edit Data Pribadi");
        formTitle.getStyle()
                .set("color", TEXT_DARK)
                .set("text-align", "center")
                .set("margin-bottom", "32px")
                .set("font-size", "24px");

        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.setPadding(false);
        fieldsLayout.setSpacing(true);

        TextField nisn = createEditableField("NISN", currentUser != null ? Objects.toString(currentUser.getNisn(),"") : "");
        TextField nama = createEditableField("Nama", currentUser != null ? Objects.toString(currentUser.getName(),"") : "");
        TextField email = createEditableField("Email", currentUser != null ? Objects.toString(currentUser.getEmail(),"") : "");
        TextField pass = createEditableField("Password", currentUser != null ? Objects.toString(currentUser.getPassword(),"") : "");

        fieldsLayout.add(nisn, nama, email, pass);

        Button simpan = new Button("Simpan Perubahan", VaadinIcon.CHECK.create());
        simpan.setWidthFull();
        simpan.setHeight("45px");
        stylePrimary(simpan);
        simpan.getStyle().set("margin-top", "24px");
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

        formContainer.add(formTitle, fieldsLayout, simpan);
        wrap.add(title, formContainer);
        return wrap;
    }

    // ------------------- HELPERS -------------------

    private void switchContent(String name) {
        content.removeAll();
        Component page = pages.getOrDefault(name, buildHome());
        content.add(page);
    }

    private void handleOrderSubmission(TextField namaBarang, TextField lokasiJemput, TextField lokasiAntar, 
                                    NumberField biayaBarang, DatePicker tanggal) {
        Integer userId = SessionUtils.getUserId();
        
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
                Notification.show("Order dibuat dengan ID #" + idBaru + " tetapi detail tidak tersimpan");
                System.err.println("Failed to insert detail for titipan ID: " + idBaru);
            } else {
                Notification.show("Order berhasil dibuat (#" + idBaru + ")");
            }
            
            // Clear form and switch to home
            namaBarang.clear();
            lokasiJemput.clear();
            lokasiAntar.clear();
            biayaBarang.clear();
            tanggal.setValue(LocalDate.now());
            
            switchContent("Home");
            
        } catch (Exception ex) {
            System.err.println("Error creating titipan: " + ex.getMessage());
            ex.printStackTrace();
            Notification.show("Terjadi kesalahan sistem. Silakan coba lagi atau hubungi administrator.");
        }
    }

    private void setupRecentGrid(Grid<Titipan> grid) {
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

        grid.addColumn(t -> t.getDiambil_oleh() != null && t.getDiambil_oleh() != 0 ?
                        userDAO.getUserNameById(t.getDiambil_oleh()) : "-")
                .setHeader("JASTIPER");

        grid.addColumn(Titipan::getStatus)
                .setHeader("Status")
                .setWidth("120px");

        grid.addComponentColumn(t -> createRatingButton(t)).setHeader("Aksi")
          .setWidth("120px")
          .setFlexGrow(0);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        var history = titipanDAO.getHistoryByUser(userId);
        if (history != null && !history.isEmpty()) {
            // Show only recent 5 orders
            List<Titipan> recentOrders = history.subList(0, Math.min(5, history.size()));
            grid.setItems(recentOrders);
        }
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

        grid.addColumn(t -> t.getDiambil_oleh() != null && t.getDiambil_oleh() != 0 ?
                        userDAO.getUserNameById(t.getDiambil_oleh()) : "-")
                .setHeader("JASTIPER");

        grid.addColumn(Titipan::getStatus)
                .setHeader("Status")
                .setWidth("120px");

        grid.addComponentColumn(t -> createRatingButton(t)).setHeader("Aksi")
          .setWidth("120px")
          .setFlexGrow(0);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        var history = titipanDAO.getHistoryByUser(userId);
        if (history != null) grid.setItems(history);
    }

    private TextField createEditableField(String label, String value) {
        TextField field = new TextField(label);
        field.setValue(value);
        field.setWidthFull();
        styleTextField(field);
        return field;
    }

    private void styleTextField(Component field) {
        field.getStyle()
                .set("background-color", WHITE)
                .set("border-radius", "8px")
                .set("border", "1px solid " + LIGHT);
    }

    private void stylePrimary(Button button) {
        button.getStyle()
                .set("background", PRIMARY)
                .set("color", WHITE)
                .set("border", "none")
                .set("border-radius", "8px")
                .set("font-weight", "600")
                .set("box-shadow", "0 2px 8px rgba(59, 130, 246, 0.3)");
    }

    private void styleSecondary(Button button) {
        button.getStyle()
                .set("background", WHITE)
                .set("color", TEXT_DARK)
                .set("border", "1px solid " + LIGHT)
                .set("border-radius", "8px")
                .set("font-weight", "600");
    }

    private Div sideItem(String label, Runnable onClick) {
        Div item = new Div(); 
        item.setText(label);
        item.getStyle()
                .set("padding", "16px 20px")
                .set("border-top", "1px solid rgba(255,255,255,.08)")
                .set("cursor", "pointer")
                .set("font-weight", "600")
                .set("transition", "background-color 0.2s");
        item.addClickListener(e -> { 
            onClick.run(); 
            highlight(item); 
        });
        return item;
    }

    private void highlight(Div current) {
        // reset all siblings
        current.getParent().ifPresent(parent -> 
            parent.getChildren().forEach(c -> c.getElement().getStyle().remove("background"))
        );
        current.getStyle().set("background", "rgba(255,255,255,.10)");
    }

    private static Div circle(int sizePx, String bg) {
        Div d = new Div();
        d.getStyle()
                .set("width", sizePx + "px")
                .set("height", sizePx + "px")
                .set("border-radius", "999px")
                .set("background", bg)
                .set("border", "6px solid " + WHITE)
                .set("box-shadow", "0 2px 6px rgba(0,0,0,.08)");
        return d;
    }

    private void openDetailDialog(int titipanId) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");

        H3 header = new H3("Detail Titipan #" + titipanId);
        header.getStyle().set("color", TEXT_DARK);

        Grid<TitipanDetail> grid = new Grid<>(TitipanDetail.class, false);
        grid.addColumn(TitipanDetail::getDeskripsi).setHeader("Deskripsi");
        grid.addColumn(TitipanDetail::getCatatan_opsional).setHeader("Catatan");
        grid.setItems(detailDAO.getDetailsByTransaksiId(titipanId));

        Button close = new Button("Tutup", VaadinIcon.CLOSE.create(), e -> dialog.close());
        stylePrimary(close);

        VerticalLayout box = new VerticalLayout(header, grid, close);
        box.setAlignItems(FlexComponent.Alignment.END);
        dialog.add(box);
        dialog.open();
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Integer userId = SessionUtils.getUserId();
        String userRole = SessionUtils.getUserRole();

        if (userId == null) {
            event.rerouteTo("login");
            return;
        }

        if (!"Penitip".equalsIgnoreCase(userRole)) {
            if ("Admin".equalsIgnoreCase(userRole)) {
                event.rerouteTo("admin");
            } else if ("Jastiper".equalsIgnoreCase(userRole)) {
                event.rerouteTo("jastiper");
            } else {
                event.rerouteTo("");
            }
        }
    }

    private String formatRupiah(Long amount) {
        if (amount == null) return "-";
        return rupiah.format(amount).replace(",00", "");
    }

    // ------------------- REVIEW SYSTEM -------------------
    
    private void openRatingDialog(Titipan t) {
        Integer userId = SessionUtils.getUserId();

        // Additional safety check - prevent rating if already rated
        if (hasUserRatedJastiperForOrder(userId, t.getDiambil_oleh(), t.getId())) {
            Notification.show("Anda sudah memberikan rating untuk jastiper ini!");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        H3 title = new H3("Beri Rating JASTIPER");
        title.getStyle().set("color", TEXT_DARK);

        String driverName = (t.getDiambil_oleh() == null || t.getDiambil_oleh() == 0)
                ? "-" : userDAO.getUserNameById(t.getDiambil_oleh());
        Span s1 = new Span("Nama JASTIPER: " + driverName);
        s1.getStyle().set("font-weight", "500");

        // Rating Ketepatan Waktu (1-10)
        VerticalLayout ratingKetepatanContainer = new VerticalLayout();
        ratingKetepatanContainer.setPadding(false);
        ratingKetepatanContainer.setSpacing(false);
        
        Span ratingKetepatanLabel = new Span("Rating Ketepatan Waktu (1-10):");
        ratingKetepatanLabel.getStyle().set("font-weight", "600").set("margin-bottom", "8px");
        
        NumberField ratingKetepatanField = new NumberField();
        ratingKetepatanField.setMin(1);
        ratingKetepatanField.setMax(10);
        ratingKetepatanField.setValue(10.0); // Default to 10
        ratingKetepatanField.setWidth("100px");
        ratingKetepatanField.setPlaceholder("1-10");
        styleTextField(ratingKetepatanField);
        
        ratingKetepatanContainer.add(ratingKetepatanLabel, ratingKetepatanField);

        // Rating Pelayanan (1-10)
        VerticalLayout ratingPelayananContainer = new VerticalLayout();
        ratingPelayananContainer.setPadding(false);
        ratingPelayananContainer.setSpacing(false);
        
        Span ratingPelayananLabel = new Span("Rating Pelayanan (1-10):");
        ratingPelayananLabel.getStyle().set("font-weight", "600").set("margin-bottom", "8px");
        
        NumberField ratingPelayananField = new NumberField();
        ratingPelayananField.setMin(1);
        ratingPelayananField.setMax(10);
        ratingPelayananField.setValue(10.0); // Default to 10
        ratingPelayananField.setWidth("100px");
        ratingPelayananField.setPlaceholder("1-10");
        styleTextField(ratingPelayananField);
        
        ratingPelayananContainer.add(ratingPelayananLabel, ratingPelayananField);

        // Deskripsi/Komentar
        TextArea deskripsi = new TextArea("Deskripsi/Komentar");
        deskripsi.setWidthFull();
        deskripsi.setHeight("120px");
        styleTextField(deskripsi);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        styleSecondary(cancel);

        Button submit = new Button("Submit", VaadinIcon.CHECK.create(), e -> {
            Double ratingKetepatanValue = ratingKetepatanField.getValue();
            Double ratingPelayananValue = ratingPelayananField.getValue();
            
            if (ratingKetepatanValue == null || ratingKetepatanValue < 1 || ratingKetepatanValue > 10) {
                Notification.show("Rating ketepatan harus antara 1-10!");
                return;
            }
            
            if (ratingPelayananValue == null || ratingPelayananValue < 1 || ratingPelayananValue > 10) {
                Notification.show("Rating pelayanan harus antara 1-10!");
                return;
            }
            
            int ratingKetepatan = ratingKetepatanValue.intValue();
            int ratingPelayanan = ratingPelayananValue.intValue();
            String deskripsiText = deskripsi.getValue();
            // Prefix deskripsi dengan ORDER ID agar bisa dicek per-order tanpa ubah schema
            try {
                if (t != null && t.getId() != null) {
                    String prefix = "[ORDER#" + t.getId() + "] ";
                    deskripsiText = (deskripsiText != null && !deskripsiText.isBlank()) ? prefix + deskripsiText : prefix;
                }
            } catch (Exception ignore) {}
            
            // Hitung overall rating
            double overallRating = (ratingKetepatan + ratingPelayanan) / 2.0;
            
            boolean ratingSuccess = ratingDAO.insertRating(userId, t.getDiambil_oleh(), ratingKetepatan, ratingPelayanan, deskripsiText);
            
            if (ratingSuccess) {
                Notification.show("Terima kasih atas rating Anda! Overall rating: " + String.format("%.1f/10", overallRating));
                // Refresh the grid to show updated data
                refreshGrids();
            } else {
                Notification.show("Gagal menyimpan rating. Silakan coba lagi.");
            }
            
            dialog.close();
        });
        stylePrimary(submit);

        buttons.add(cancel, submit);

        VerticalLayout content = new VerticalLayout(title, s1, ratingKetepatanContainer, ratingPelayananContainer, deskripsi, buttons);
        content.setPadding(false);
        content.setSpacing(true);
        dialog.add(content);
        dialog.open();
    }
    
    private void refreshGrids() {
        // Refresh the current page content
        String currentPageName = getCurrentPageName();
        switchContent(currentPageName);
    }
    
    private String getCurrentPageName() {
        // Determine current page based on content
        if (content.getComponentCount() > 0) {
            Component currentPage = content.getComponentAt(0);
            if (currentPage instanceof VerticalLayout) {
                // Check for specific elements to identify page
                VerticalLayout page = (VerticalLayout) currentPage;
                if (page.getComponentCount() > 0) {
                    Component firstComponent = page.getComponentAt(0);
                    if (firstComponent instanceof H1) {
                        H1 title = (H1) firstComponent;
                        String titleText = title.getText();
                        if (titleText.contains("DASHBOARD")) return "Home";
                        if (titleText.contains("Buat Pesanan")) return "Buat Pesanan";
                        if (titleText.contains("Riwayat")) return "Riwayat Pesanan";
                        if (titleText.contains("Data Pribadi")) return "Data Pribadi";
                    }
                }
            }
        }
        return "Home";
    }
    
    // Check if user already rated a specific jastiper for a specific order
    private boolean hasUserRatedJastiper(Integer userId, Integer jastiperId, Integer titipanId) {
        try {
            return ratingDAO.hasUserRatedJastiperForOrder(userId, jastiperId, titipanId);
        } catch (Exception e) {
            System.err.println("Error checking if user rated jastiper: " + e.getMessage());
            return false;
        }
    }
    
    // More specific check - check if user rated this specific jastiper for this specific order
    private boolean hasUserRatedJastiperForOrder(Integer userId, Integer jastiperId, Integer titipanId) {
        try {
            return ratingDAO.hasUserRatedJastiperForOrder(userId, jastiperId, titipanId);
        } catch (Exception e) {
            System.err.println("Error checking if user rated jastiper for order: " + e.getMessage());
            return false;
        }
    }
    
    // Helper method to create rating button with proper state
    private Button createRatingButton(Titipan t) {
        Button btnRate = new Button("Rating 1-10", VaadinIcon.STAR.create());
        stylePrimary(btnRate);
        btnRate.getStyle().set("font-size", "13px");
        
        // Check if order is completed and has a jastiper
        boolean orderCompleted = "SELESAI".equalsIgnoreCase(t.getStatus())
                && t.getDiambil_oleh() != null && t.getDiambil_oleh() != 0;
        
        if (!orderCompleted) {
            // Order not yet completed
            btnRate.setText("Belum Selesai");
            btnRate.getStyle().set("background", WARNING);
            btnRate.setEnabled(false);
            return btnRate;
        }
        
        // Check if user already rated this jastiper
        boolean alreadyRated = hasUserRatedJastiperForOrder(SessionUtils.getUserId(), t.getDiambil_oleh(), t.getId());
        
        if (alreadyRated) {
            // User already rated this jastiper
            btnRate.setText("Sudah Rating");
            btnRate.getStyle().set("background", SUCCESS);
            btnRate.setEnabled(false);
        } else {
            // User can rate this jastiper
            btnRate.setText("Rating 1-10");
            btnRate.getStyle().set("background", PRIMARY);
            btnRate.setEnabled(true);
            btnRate.addClickListener(e -> openRatingDialog(t));
        }
        
        return btnRate;
    }
    
    // ------------------- DATA CLASSES -------------------
}