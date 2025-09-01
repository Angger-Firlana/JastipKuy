package com.example.application.view.user;

import com.example.application.dao.*;
import com.example.application.model.Titipan;
import com.example.application.model.User;
import com.example.application.session.SessionUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;
import java.util.Calendar;
import java.util.stream.Collectors;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.combobox.ComboBox;

@PageTitle("JastipKuy • Dashboard")
@Route("jastiper")
@Uses(Icon.class)
public class JastiperView extends Div{
    private final TitipanDAO titipanDAO = new TitipanDAO();
    private final TitipanDetailDAO detailDAO = new TitipanDetailDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final LaporanDAO laporanDAO = new LaporanDAO();

    private static final String NAVY      = "#11284B";
    private static final String SLATE     = "#34465B";
    private static final String WHITE     = "#FFFFFF";
    private static final String LIGHT     = "#E6E6E6";
    private static final String TEXT_DARK = "#0F172A";

    private final Locale ID = new Locale("id","ID");
    private final NumberFormat rupiah = NumberFormat.getCurrencyInstance(ID);

    private final VerticalLayout content = new VerticalLayout();
    private final Map<String, Component> pages = new LinkedHashMap<>();
    private String currentPesananStatusFilter = "Belum Selesai";
    private String currentKeywordFilter = "";
    private ComboBox<String> keywordBox;
    private VerticalLayout pesananContent;

    public JastiperView() {
        setSizeFull();
        getStyle().set("background", WHITE).set("font-family", "Inter, system-ui, -apple-system, Segoe UI, Roboto, Arial");
        Integer userId = SessionUtils.getUserId();
        String userRole = SessionUtils.getUserRole();
        if (userId != null){
            if (!Objects.equals(userRole, "Jastiper")){
                if (userRole.equals("Admin")){
                    UI.getCurrent().navigate("admin");
                    return;
                }else if(userRole.equals("Penitip")){
                    UI.getCurrent().navigate("user");
                    return;
                }else{
                    UI.getCurrent().navigate("");
                    return;
                }
            }
        }else{
            UI.getCurrent().navigate("login");
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

        Icon bell = VaadinIcon.BELL.create(); bell.setColor(WHITE); bell.setSize("20px");
        Button logout = new Button("Logout", VaadinIcon.SIGN_OUT.create()); stylePrimary(logout);
        topRight.add(bell, logout);

        logout.addClickListener(ce -> {
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

        topBar.add(brand, topRight);

        // ======= LAYOUT (SIDEBAR + CONTENT) =======
        HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull(); root.setPadding(false); root.setSpacing(false);

        // ======= SIDEBAR =======
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth(280, Unit.PIXELS); sidebar.setPadding(false); sidebar.setSpacing(false);
        sidebar.getStyle().set("background", SLATE).set("color", WHITE).set("min-height", "calc(100vh - 64px)");

        VerticalLayout avatarWrap = new VerticalLayout();
        avatarWrap.setAlignItems(FlexComponent.Alignment.CENTER);
        avatarWrap.getStyle().set("padding", "30px 16px 22px 16px");
        Div avatar = circle(100, LIGHT);
        Icon userIco = VaadinIcon.USER.create(); userIco.setSize("44px"); userIco.setColor("#7A8AA0");
        avatar.add(userIco);
        Paragraph hi = new Paragraph("Hi, JASTIPERs!"); hi.getStyle().set("color", WHITE).set("margin", "14px 0 0 0").set("font-weight", "600");
        avatarWrap.add(avatar, hi);

        // Build pages first
        pages.put("Home", buildHome());
        pages.put("Pesanan Masuk", buildPesanan());
        pages.put("Laporan Penghasilan", buildPenghasilan());
        pages.put("Riwayat dan Ulasan", buildRiwayat());
        pages.put("Data", buildEditData());

        sidebar.add(avatarWrap);
        pages.keySet().forEach(name -> sidebar.add(sideItem(name, () -> switchContent(name))));

        // ======= CONTENT WRAPPER =======
        content.setPadding(true); content.setSpacing(false); content.setSizeFull(); content.getStyle().set("padding", "24px");
        switchContent("Home");

        root.add(sidebar, content);
        root.setFlexGrow(0, sidebar); root.setFlexGrow(1, content);

        add(topBar, root);
    }

    // ------------------- PAGES -------------------

    private Component buildHome() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false); wrap.setSpacing(false); wrap.setWidthFull();

        H1 title = new H1("RANK OF JASTIPERs");
        title.getStyle().set("margin", "0 0 16px 0").set("font-size", "28px").set("letter-spacing", "1px").set("color", TEXT_DARK);
        wrap.add(title);

        // Tabel ranking semua jastiper
        Grid<RankingRow> allGrid = new Grid<>(RankingRow.class, false);
        allGrid.addColumn(r -> r.rank).setHeader("Ranking").setAutoWidth(true);
        allGrid.addColumn(r -> r.name).setHeader("Name (Jastiper)").setAutoWidth(true).setFlexGrow(1);
        allGrid.addColumn(r -> String.format("%.1f", r.avgKetepatan)).setHeader("Overall Ketepatan").setAutoWidth(true);
        allGrid.addColumn(r -> String.format("%.1f", r.avgPelayanan)).setHeader("Overall Pelayanan").setAutoWidth(true);
        allGrid.addColumn(r -> String.format("%.1f", r.overall)).setHeader("Overall").setAutoWidth(true);
        allGrid.setAllRowsVisible(true);
        allGrid.getStyle().set("background", WHITE).set("border-radius", "8px").set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

        List<RankingRow> rankings = computeJastiperRankings();
        // Exclude ADMINs from the ranking table
        rankings.removeIf(r -> {
            try {
                String role = userDAO.getUserRoleById(r.jastiperId);
                return role != null && role.equalsIgnoreCase("ADMIN");
            } catch (Exception e) { return false; }
        });
        allGrid.setItems(rankings);

        // Tabel ranking kamu sendiri
        H3 yourTitle = new H3("RANKING KAMU");
        yourTitle.getStyle().set("margin", "16px 0 8px 0").set("color", TEXT_DARK);
        Grid<RankingRow> youGrid = new Grid<>(RankingRow.class, false);
        youGrid.addColumn(r -> r.rank).setHeader("Ranking").setAutoWidth(true);
        youGrid.addColumn(r -> r.name).setHeader("Name (Jastiper)").setAutoWidth(true).setFlexGrow(1);
        youGrid.addColumn(r -> String.format("%.1f", r.avgKetepatan)).setHeader("Overall Ketepatan").setAutoWidth(true);
        youGrid.addColumn(r -> String.format("%.1f", r.avgPelayanan)).setHeader("Overall Pelayanan").setAutoWidth(true);
        youGrid.addColumn(r -> String.format("%.1f", r.overall)).setHeader("Overall").setAutoWidth(true);
        youGrid.setAllRowsVisible(true);
        youGrid.getStyle().set("background", WHITE).set("border-radius", "8px").set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

        Integer me = SessionUtils.getUserId();
        RankingRow mine = rankings.stream().filter(r -> Objects.equals(r.jastiperId, me)).findFirst().orElse(null);
        if (mine != null) {
            youGrid.setItems(Collections.singletonList(mine));
        } else {
            youGrid.setItems(Collections.emptyList());
        }

        // Keep existing buttons/sections under ranking
        HorizontalLayout bottom = new HorizontalLayout(); bottom.setWidthFull(); bottom.getStyle().set("gap", "24px").set("margin-top", "10px");

        VerticalLayout btns = new VerticalLayout(); btns.setWidthFull(); btns.setSpacing(true); btns.setPadding(false); btns.setMaxWidth("520px");
        btns.add(
                navBtn("Edit Data", VaadinIcon.USER_CARD.create(), "Data"),
                navBtn("Pesanan Masuk", VaadinIcon.CLIPBOARD_TEXT.create(), "Pesanan Masuk"),
                navBtn("Laporan Penghasilan", VaadinIcon.MONEY.create(), "Laporan Penghasilan"),
                navBtn("Riwayat dan Ulasan", VaadinIcon.COMMENTS.create(), "Riwayat dan Ulasan")
        );

        bottom.add(btns);

        wrap.add(allGrid, yourTitle, youGrid, bottom);
        return wrap;
    }

    private Component buildPesanan() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(true);
        wrap.setSpacing(true);
        wrap.setWidthFull();
        // Toolbar filter status
        HorizontalLayout tools = new HorizontalLayout();
        tools.setWidthFull();
        tools.setPadding(false);
        tools.setSpacing(true);
        tools.setAlignItems(FlexComponent.Alignment.END);

        // Keyword autocomplete (nama barang / lokasi / #id)
        keywordBox = new ComboBox<>();
        keywordBox.setLabel("Cari");
        keywordBox.setPlaceholder("Nama barang / lokasi / #id");
        keywordBox.setAllowCustomValue(true);
        keywordBox.setClearButtonVisible(true);
        keywordBox.addValueChangeListener(e -> {
            currentKeywordFilter = e.getValue() != null ? e.getValue() : "";
            if (pesananContent != null) refreshPesanan(pesananContent);
        });
        keywordBox.addCustomValueSetListener(e -> {
            keywordBox.setValue(e.getDetail());
        });

        Select<String> statusFilter = new Select<>();
        statusFilter.setLabel("Filter Status");
        statusFilter.setItems("Belum Selesai", "Menunggu", "Diproses", "Selesai", "Batal", "Semua");
        statusFilter.setValue(currentPesananStatusFilter);
        statusFilter.addValueChangeListener(e -> {
            currentPesananStatusFilter = e.getValue();
            if (pesananContent != null) refreshPesanan(pesananContent);
        });
        tools.add(keywordBox, statusFilter);
        // Content container for cards
        pesananContent = new VerticalLayout();
        pesananContent.setPadding(false);
        pesananContent.setSpacing(true);
        wrap.add(tools, pesananContent);

        refreshPesanan(pesananContent);

        return wrap;
    }

    private void refreshPesanan(VerticalLayout container) {
        container.removeAll(); // hapus isi lama

        // Ambil data titipan dan terapkan filter + sorting
        List<Titipan> list = titipanDAO.getAllTitipan("");
        if (list == null) list = new ArrayList<>();

        // Update suggestion items for keywordBox
        try {
            if (keywordBox != null) {
                LinkedHashSet<String> suggestions = new LinkedHashSet<>();
                for (Titipan t : list) {
                    if (t.getNama_barang() != null && !t.getNama_barang().isBlank()) suggestions.add(t.getNama_barang());
                    if (t.getLokasi_jemput() != null && !t.getLokasi_jemput().isBlank()) suggestions.add(t.getLokasi_jemput());
                    if (t.getLokasi_antar() != null && !t.getLokasi_antar().isBlank()) suggestions.add(t.getLokasi_antar());
                    if (t.getId() != null) suggestions.add("#" + t.getId());
                }
                keywordBox.setItems(suggestions);
            }
        } catch (Exception ignore) {}

        String kw = currentKeywordFilter == null ? "" : currentKeywordFilter.trim().toLowerCase();
        List<Titipan> filtered = list.stream()
                .filter(t -> {
                    // Status filter
                    if ("Semua".equalsIgnoreCase(currentPesananStatusFilter)) return true;
                    if ("Belum Selesai".equalsIgnoreCase(currentPesananStatusFilter)) {
                        return "MENUNGGU".equalsIgnoreCase(t.getStatus()) || "DIPROSES".equalsIgnoreCase(t.getStatus());
                    }
                    return currentPesananStatusFilter.equalsIgnoreCase(t.getStatus());
                })
                .filter(t -> {
                    // Keyword filter
                    if (kw.isEmpty()) return true;
                    boolean byName = t.getNama_barang() != null && t.getNama_barang().toLowerCase().contains(kw);
                    boolean byJemput = t.getLokasi_jemput() != null && t.getLokasi_jemput().toLowerCase().contains(kw);
                    boolean byAntar = t.getLokasi_antar() != null && t.getLokasi_antar().toLowerCase().contains(kw);
                    boolean byId = t.getId() != null && ("#" + t.getId()).toLowerCase().contains(kw);
                    return byName || byJemput || byAntar || byId;
                })
                .sorted(Comparator.comparing(Titipan::getCreated_at, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        for (Titipan t : filtered) {
            VerticalLayout card = new VerticalLayout();
            card.addClassName("pesanan-card");
            card.setSpacing(false);
            card.setPadding(true);
            card.getStyle()
                    .set("background", "#102E50")
                    .set("color", "white")
                    .set("border-radius", "12px")
                    .set("padding", "16px")
                    .set("box-shadow", "0 2px 6px rgba(0,0,0,0.2)");

            // Detail fields
            Component namaBarang = infoField("Nama Barang", t.getNama_barang());
            Component lokasi = infoField("Lokasi Jemput - Antar", t.getLokasi_jemput() + " → " + t.getLokasi_antar());
            Component harga = infoField("Biaya Barang", formatRupiah(t.getHarga_estimasi()));

            // Status badge
            Span statusBadge = new Span(t.getStatus());
            statusBadge.getStyle()
                    .set("padding", "4px 8px")
                    .set("border-radius", "8px")
                    .set("font-size", "12px")
                    .set("font-weight", "600");

            HorizontalLayout actions = new HorizontalLayout();

            if ("MENUNGGU".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "#F5C45E").set("color", "black");

                Button terima = new Button("Terima");
                terima.getStyle().set("background", "green").set("color", "white");
                terima.addClickListener(e -> {
                    Titipan titipan = new Titipan();
                    titipan.setId(t.getId());
                    titipan.setStatus("DIPROSES");
                    titipan.setDiambil_oleh(SessionUtils.getUserId());
                    titipanDAO.updateTitipan(titipan);
                    Notification.show("Pesanan #" + t.getId() + " diterima");
                    refreshPesanan(container);
                });

                actions.add(terima);
            }
            else if ("DIPROSES".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "#2196F3").set("color", "white");

                Button selesai = new Button("Selesai");
                selesai.getStyle().set("background", "#E78B48").set("color", "white");
                selesai.addClickListener(e -> {
                    Titipan titipan = new Titipan();
                    titipan.setId(t.getId());
                    titipan.setStatus("SELESAI");
                    titipanDAO.updateTitipan(titipan);
                    Notification.show("Pesanan #" + t.getId() + " selesai");
                    refreshPesanan(container);
                });
                actions.add(selesai);
            }
            else if ("SELESAI".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "gray").set("color", "white");
            }
            else if ("BATAL".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "#BE3D2A").set("color", "white");
            }

            card.add(namaBarang, lokasi, harga, statusBadge, actions);
            container.add(card);
        }
    }

    /**
     * Helper untuk bikin field info
     */
    private Component infoField(String label, String value) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getStyle().set("margin-bottom", "8px");

        Span lbl = new Span(label);
        lbl.getStyle()
                .set("font-size", "12px")
                .set("color", "#F5C45E") // warna label biar kontras
                .set("font-weight", "600");

        Span val = new Span(value != null ? value : "-");
        val.getStyle()
                .set("font-size", "14px")
                .set("color", "white");

        layout.add(lbl, val);
        return layout;
    }




    private Component buildPenghasilan() {
        VerticalLayout wrap = section("Laporan Penghasilan");

        Grid<Pendapatan> grid = new Grid<>(Pendapatan.class, false);
        grid.addColumn(p -> p.orderId).setHeader("Order ID").setAutoWidth(true);
        grid.addColumn(p -> p.deskripsi).setHeader("Deskripsi").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(p -> "Rp " + fnum(p.pendapatan)).setHeader("Pendapatan").setAutoWidth(true);
        
        List<Pendapatan> data = getRealPendapatan();
        
        grid.setItems(data);
        grid.setAllRowsVisible(true);

        long totalPendapatan = getCurrentMonthEarnings();
        int totalOrder = countSuccessfulOrdersByMonth(SessionUtils.getUserId(), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));

        Div card = new Div();
        card.getStyle().set("background", WHITE).set("border", "1px solid " + LIGHT).set("border-radius", "8px")
                .set("padding", "16px").set("margin-top", "12px");
        card.add(new H4("Ringkasan Bulan Ini"),
                new Paragraph("Total Pendapatan: Rp " + fnum(totalPendapatan)),
                new Paragraph("Total Order: " + totalOrder));

        wrap.add(grid, card);
        return wrap;
    }

    private Component buildRiwayat() {
        VerticalLayout wrap = section("Riwayat dan Ulasan");

        // Get real review data for this jastiper
        List<Ulasan> realReviews = getRealReviews();
        
        // Reviews Grid - TABLE DI ATAS
        if (realReviews.isEmpty()) {
            Div noReviews = new Div();
            noReviews.setText("Belum ada ulasan untuk Anda");
            noReviews.getStyle()
                    .set("text-align", "center")
                    .set("padding", "40px")
                    .set("color", SLATE)
                    .set("font-style", "italic");
            wrap.add(noReviews);
        } else {
            H3 reviewsTitle = new H3("Detail Ulasan per Order");
            reviewsTitle.getStyle().set("margin", "0 0 16px 0").set("color", TEXT_DARK);
            wrap.add(reviewsTitle);
            
            Grid<Ulasan> grid = new Grid<>(Ulasan.class, false);
            grid.addColumn(u -> u.orderId).setHeader("Order").setAutoWidth(true);
            grid.addColumn(u -> u.nama).setHeader("Pengulas").setAutoWidth(true);
            grid.addColumn(u -> String.valueOf(u.ratingKetepatan) + "/10").setHeader("Rating Ketepatan").setAutoWidth(true);
            grid.addColumn(u -> String.valueOf(u.ratingPelayanan) + "/10").setHeader("Rating Pelayanan").setAutoWidth(true);
            grid.addColumn(u -> String.format("%.1f/10", (u.ratingKetepatan + u.ratingPelayanan) / 2.0)).setHeader("Rating per Order").setAutoWidth(true);
            grid.addColumn(u -> truncateComment(u.komentar, 50)).setHeader("Komentar").setFlexGrow(1);
            grid.addComponentColumn(u -> createViewCommentButton(u.komentar)).setHeader("Aksi").setAutoWidth(true);
            grid.addColumn(u -> u.tanggal.toString()).setHeader("Tanggal").setAutoWidth(true);
            grid.setItems(realReviews);
            grid.setAllRowsVisible(true);
            grid.getStyle().set("background", WHITE).set("border-radius", "8px").set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");

            wrap.add(grid);
        }
        
        // Calculate overall ratings - OVERALL RATING DI BAWAH
        double overallKetepatan = 0.0;
        double overallPelayanan = 0.0;
        double overallRating = 0.0;
        
        if (!realReviews.isEmpty()) {
            overallKetepatan = realReviews.stream()
                    .mapToDouble(u -> u.ratingKetepatan)
                    .average()
                    .orElse(0.0);
            
            overallPelayanan = realReviews.stream()
                    .mapToDouble(u -> u.ratingPelayanan)
                    .average()
                    .orElse(0.0);
            
            overallRating = (overallKetepatan + overallPelayanan) / 2.0;
        }
        
        // Overall Rating Summary Card
        Div summaryCard = new Div();
        summaryCard.getStyle()
                .set("background", "linear-gradient(135deg, " + NAVY + ", " + SLATE + ")")
                .set("color", WHITE)
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("margin-top", "24px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.15)");
        
        H3 summaryTitle = new H3("RATING OVERALL");
        summaryTitle.getStyle().set("margin", "0 0 16px 0").set("color", WHITE).set("text-align", "center");
        
        HorizontalLayout ratingStats = new HorizontalLayout();
        ratingStats.setWidthFull();
        ratingStats.setSpacing(true);
        ratingStats.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        
        // Ketepatan Rating
        VerticalLayout ketepatanStat = new VerticalLayout();
        ketepatanStat.setAlignItems(FlexComponent.Alignment.CENTER);
        ketepatanStat.setPadding(false);
        ketepatanStat.setSpacing(false);
        
        Span ketepatanLabel = new Span("Ketepatan Waktu");
        ketepatanLabel.getStyle().set("font-size", "14px").set("color", "rgba(255,255,255,0.8)");
        
        H2 ketepatanValue = new H2(String.format("%.1f", overallKetepatan));
        ketepatanValue.getStyle().set("margin", "8px 0").set("color", WHITE).set("font-size", "32px");
        
        Span ketepatanMax = new Span("/10");
        ketepatanMax.getStyle().set("font-size", "16px").set("color", "rgba(255,255,255,0.6)");
        
        ketepatanStat.add(ketepatanLabel, ketepatanValue, ketepatanMax);
        
        // Pelayanan Rating
        VerticalLayout pelayananStat = new VerticalLayout();
        pelayananStat.setAlignItems(FlexComponent.Alignment.CENTER);
        pelayananStat.setPadding(false);
        pelayananStat.setSpacing(false);
        
        Span pelayananLabel = new Span("Pelayanan");
        pelayananLabel.getStyle().set("font-size", "14px").set("color", "rgba(255,255,255,0.8)");
        
        H2 pelayananValue = new H2(String.format("%.1f", overallPelayanan));
        pelayananValue.getStyle().set("margin", "8px 0").set("color", WHITE).set("font-size", "32px");
        
        Span pelayananMax = new Span("/10");
        pelayananMax.getStyle().set("font-size", "16px").set("color", "rgba(255,255,255,0.6)");
        
        pelayananStat.add(pelayananLabel, pelayananValue, pelayananMax);
        
        // Overall Rating
        VerticalLayout overallStat = new VerticalLayout();
        overallStat.setAlignItems(FlexComponent.Alignment.CENTER);
        overallStat.setPadding(false);
        overallStat.setSpacing(false);
        
        Span overallLabel = new Span("Rating Overall");
        overallLabel.getStyle().set("font-size", "14px").set("color", "rgba(255,255,255,0.8)");
        
        H2 overallValue = new H2(String.format("%.1f", overallRating));
        overallValue.getStyle().set("margin", "8px 0").set("color", WHITE).set("font-size", "32px");
        
        Span overallMax = new Span("/10");
        overallMax.getStyle().set("font-size", "16px").set("color", "rgba(255,255,255,0.6)");
        
        overallStat.add(overallLabel, overallValue, overallMax);
        
        ratingStats.add(ketepatanStat, pelayananStat, overallStat);
        summaryCard.add(summaryTitle, ratingStats);
        
        wrap.add(summaryCard);
        
        return wrap;
    }

    private Component buildEditData() {
        VerticalLayout wrap = section("Edit Data");
        User user = userDAO.getUserById(SessionUtils.getUserId());

        if (user == null) {
            Div errorDiv = new Div("Gagal memuat data user");
            errorDiv.getStyle().set("color", "red").set("text-align", "center").set("padding", "20px");
            wrap.add(errorDiv);
            return wrap;
        }

        FormLayout form = new FormLayout();
        
        // NISN Field
        TextField nisn = new TextField("NISN");
        nisn.setValue(user.getNisn() != null ? user.getNisn() : "");
        nisn.setPlaceholder("Masukkan NISN");
        nisn.setMaxLength(11);
        
        // Name Field
        TextField nama = new TextField("Nama Lengkap");
        nama.setValue(user.getName() != null ? user.getName() : "");
        nama.setPlaceholder("Masukkan nama lengkap");
        nama.setRequired(true);
        nama.setMaxLength(100);
        
        // Email Field
        EmailField email = new EmailField("Email");
        email.setValue(user.getEmail() != null ? user.getEmail() : "");
        email.setPlaceholder("contoh@email.com");
        email.setRequired(true);
        email.setMaxLength(100);
        
        // Password Fields
        PasswordField password = new PasswordField("Password Baru");
        password.setPlaceholder("Kosongkan jika tidak ingin mengubah password");
        password.setRevealButtonVisible(true);
        
        PasswordField confirmPassword = new PasswordField("Konfirmasi Password");
        confirmPassword.setPlaceholder("Ulangi password baru");
        confirmPassword.setRevealButtonVisible(true);

        // Add fields to form
        form.add(nisn, nama, email, password, confirmPassword);
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1), 
            new FormLayout.ResponsiveStep("640px", 2)
        );
        form.setColspan(email, 2); // Email spans full width

        // Save Button
        Button simpan = new Button("Simpan Perubahan", VaadinIcon.CHECK.create());
        stylePrimary(simpan); 
        simpan.getStyle().set("margin-top", "16px");
        
        simpan.addClickListener(e -> {
            // Validation
            if (nama.getValue().trim().isEmpty()) {
                Notification.show("Nama tidak boleh kosong!", 3000, Notification.Position.MIDDLE);
                nama.focus();
                return;
            }
            
            if (email.getValue().trim().isEmpty()) {
                Notification.show("Email tidak boleh kosong!", 3000, Notification.Position.MIDDLE);
                email.focus();
                return;
            }
            
            // Email format validation (basic)
            if (!email.getValue().contains("@") || !email.getValue().contains(".")) {
                Notification.show("Format email tidak valid!", 3000, Notification.Position.MIDDLE);
                email.focus();
                return;
            }
            
            // Password validation if provided
            String newPassword = password.getValue().trim();
            String confirmPass = confirmPassword.getValue().trim();
            
            if (!newPassword.isEmpty() || !confirmPass.isEmpty()) {
                if (!newPassword.equals(confirmPass)) {
                    Notification.show("Password dan konfirmasi password tidak sama!", 3000, Notification.Position.MIDDLE);
                    confirmPassword.focus();
                    return;
                }
                
                if (newPassword.length() < 6) {
                    Notification.show("Password minimal 6 karakter!", 3000, Notification.Position.MIDDLE);
                    password.focus();
                    return;
                }
            }
            
            // Save data
            saveUserData(user, nisn.getValue().trim(), nama.getValue().trim(), 
                        email.getValue().trim(), newPassword);
        });

        wrap.add(form, simpan);
        return wrap;
    }
    
    private void saveUserData(User currentUser, String nisn, String nama, String email, String newPassword) {
        try {
            // Use existing password if new password is empty
            String passwordToSave = newPassword.isEmpty() ? currentUser.getPassword() : newPassword;
            
            // Call UserDAO update method
            boolean success = userDAO.update(
                SessionUtils.getUserId(),
                nisn.isEmpty() ? null : nisn, 
                nama, 
                email, 
                passwordToSave, 
                currentUser.getRole() // Keep existing role
            );
            
            if (success) {
                Notification.show("Data berhasil disimpan!", 3000, Notification.Position.MIDDLE);
                
                // If email changed, might need to update session or reload
                if (!email.equals(currentUser.getEmail())) {
                    Notification.show("Email telah diubah. Silakan login ulang jika diperlukan.", 
                                    5000, Notification.Position.MIDDLE);
                }
                
                // Refresh the page to show updated data
                UI.getCurrent().getPage().reload();
                
            } else {
                Notification.show("Gagal menyimpan data. Silakan coba lagi.", 
                                3000, Notification.Position.MIDDLE);
            }
            
        } catch (Exception ex) {
            System.err.println("Error saving user data: " + ex.getMessage());
            ex.printStackTrace();
            Notification.show("Terjadi kesalahan saat menyimpan data: " + ex.getMessage(), 
                            5000, Notification.Position.MIDDLE);
        }
    }

    // ------------------- HELPERS -------------------

    private void switchContent(String name) {
        content.removeAll();
        Component page = pages.getOrDefault(name, buildHome());
        content.add(page);
    }

    private VerticalLayout section(String title) {
        VerticalLayout v = new VerticalLayout(); v.setPadding(false); v.setSpacing(false); v.setWidthFull();
        H1 h = new H1(title); h.getStyle().set("margin", "0 0 16px 0").set("font-size", "28px").set("letter-spacing", "1px").set("color", TEXT_DARK);
        v.add(h);
        return v;
    }

    private static Div bar(int width, int maxHeight, int height) {
        Div d = new Div();
        d.getStyle().set("width", width + "px").set("height", height + "px").set("background", NAVY)
                .set("border-radius", "6px 6px 0 0").set("align-self", "end");
        return d;
    }

    private static Div box(String width, String height) {
        Div d = new Div();
        d.getStyle().set("background", WHITE).set("border", "1px solid " + LIGHT).set("border-radius", "2px").set("width", width).set("height", height);
        return d;
    }

    private static Div circle(int sizePx, String bg) {
        Div d = new Div();
        d.getStyle().set("width", sizePx + "px").set("height", sizePx + "px").set("border-radius", "999px")
                .set("background", bg).set("border", "6px solid " + WHITE).set("box-shadow", "0 2px 6px rgba(0,0,0,.08)");
        return d;
    }

    private Button navBtn(String text, Icon icon, String to) {
        Button b = new Button(text, icon, e -> switchContent(to));
        stylePrimary(b);
        b.getStyle().set("width", "100%").set("justify-content", "flex-start").set("padding", "16px 18px").set("border-radius", "10px").set("font-weight", "700");
        icon.setColor(WHITE);
        return b;
    }

    private static void stylePrimary(Button b) {
        b.getStyle().set("background", NAVY).set("color", WHITE).set("border", "0").set("box-shadow", "0 6px 16px rgba(0,0,0,.15)");
    }

    private Div sideItem(String label, Runnable onClick) {
        Div item = new Div(); item.setText(label);
        item.getStyle().set("padding", "16px 20px").set("border-top", "1px solid rgba(255,255,255,.08)").set("cursor", "pointer").set("font-weight", "600");
        item.addClickListener(e -> { onClick.run(); highlight(item); });
        return item;
    }

    private void highlight(Div current) {
        // reset all siblings
        current.getParent().ifPresent(parent -> parent.getChildren().forEach(c -> c.getElement().getStyle().remove("background")));
        current.getStyle().set("background", "rgba(255,255,255,.10)");
    }

    private static String fnum(long n) {
        return String.format(new Locale("id","ID"), "%,d", n); // Format Indonesia dengan titik sebagai pemisah ribuan
    }


    // ------------------- RANKING HELPER -------------------
    private List<RankingRow> computeJastiperRankings() {
        // Ambil seluruh rating, group by idDriver (jastiper), hitung rata2
        List<RatingDAO.RatingData> all = ratingDAO.getAllRatings();
        Map<Integer, List<RatingDAO.RatingData>> byJastiper = new HashMap<>();
        for (RatingDAO.RatingData r : all) {
            if (r.jastiperId == null) continue;
            byJastiper.computeIfAbsent(r.jastiperId, k -> new ArrayList<>()).add(r);
        }

        List<RankingRow> rows = new ArrayList<>();
        for (Map.Entry<Integer, List<RatingDAO.RatingData>> e : byJastiper.entrySet()) {
            Integer jastiperId = e.getKey();
            // Hanya masukkan user dengan role Jastiper
            String role = userDAO.getUserRoleById(jastiperId);
            if (role == null || !role.equalsIgnoreCase("Jastiper")) continue;

            List<RatingDAO.RatingData> ratings = e.getValue();
            if (ratings.isEmpty()) continue;
            double avgKet = ratings.stream().mapToInt(r -> r.ratingKetepatan).average().orElse(0);
            double avgPel = ratings.stream().mapToInt(r -> r.ratingPelayanan).average().orElse(0);
            double overall = (avgKet + avgPel) / 2.0;
            String name = userDAO.getUserNameById(jastiperId);
            rows.add(new RankingRow(0, jastiperId, name != null ? name : ("Jastiper #" + jastiperId), avgKet, avgPel, overall));
        }

        // Sort overall desc
        rows.sort(Comparator.comparingDouble((RankingRow r) -> r.overall).reversed());
        // Assign rank 1..n
        int i = 1;
        for (RankingRow r : rows) r.rank = i++;
        return rows;
    }

    public static class RankingRow {
        public int rank;
        public final Integer jastiperId;
        public final String name;
        public final double avgKetepatan;
        public final double avgPelayanan;
        public final double overall;

        public RankingRow(int rank, Integer jastiperId, String name, double avgKetepatan, double avgPelayanan, double overall) {
            this.rank = rank;
            this.jastiperId = jastiperId;
            this.name = name;
            this.avgKetepatan = avgKetepatan;
            this.avgPelayanan = avgPelayanan;
            this.overall = overall;
        }
    }

    private List<Pendapatan> getRealPendapatan() {
        Integer jastiperId = SessionUtils.getUserId();
        List<Pendapatan> pendapatanList = new ArrayList<>();
        try {
            List<Titipan> completedOrders = titipanDAO.getOrdersByJastiper(jastiperId);
            if (completedOrders != null) {
                for (Titipan order : completedOrders) {
                    if ("SELESAI".equalsIgnoreCase(order.getStatus())) {
                        long pendapatan = 2000;
                        LocalDate tanggal = order.getCreated_at().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        Pendapatan p = new Pendapatan(
                            tanggal,
                            "#" + order.getId(),
                            "Upah " + (order.getNama_barang() != null ? order.getNama_barang() : "Barang"),
                            pendapatan
                        );
                        pendapatanList.add(p);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting real pendapatan: " + e.getMessage());
        }
        return pendapatanList;
    }
    
    // ------------------- CHART DATA -------------------
    
    private List<ChartData> getChartData() {
        Integer userId = SessionUtils.getUserId();
        List<ChartData> data = new ArrayList<>();
        
        // Get orders for the last 6 months
        Calendar cal = Calendar.getInstance();
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", 
                              "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        
        for (int i = 5; i >= 0; i--) {
            cal.add(Calendar.MONTH, -i);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            
            // Count successful orders for this month
            int count = countSuccessfulOrdersByMonth(userId, month + 1, year);
            
            data.add(new ChartData(monthNames[month], count));
            
            // Reset calendar for next iteration
            cal = Calendar.getInstance();
        }
        
        return data;
    }
    
    private int countSuccessfulOrdersByMonth(Integer userId, int month, int year) {
        try {
            // Get all orders taken by this jastiper
            List<Titipan> allOrders = titipanDAO.getOrdersByJastiper(userId);
            if (allOrders == null) return 0;
            
            // Filter by month, year, and status SELESAI
            int count = (int) allOrders.stream()
                    .filter(order -> {
                        Calendar orderCal = Calendar.getInstance();
                        orderCal.setTime(order.getCreated_at());
                        boolean matchesMonth = orderCal.get(Calendar.MONTH) + 1 == month;
                        boolean matchesYear = orderCal.get(Calendar.YEAR) == year;
                        boolean matchesStatus = "SELESAI".equalsIgnoreCase(order.getStatus());
                        
                        return matchesMonth && matchesYear && matchesStatus;
                    })
                    .count();
            
            return count;
        } catch (Exception e) {
            System.err.println("Error counting successful orders by month: " + e.getMessage());
            return 0;
        }
    }
    
    private List<Ulasan> getRealReviews() {
        Integer jastiperId = SessionUtils.getUserId();
        List<Ulasan> reviews = new ArrayList<>();
        
        System.out.println("DEBUG: getRealReviews called for jastiper ID: " + jastiperId);
        
        // DEBUG: Get all ratings from database first
        System.out.println("DEBUG: Getting ALL ratings from database...");
        List<RatingDAO.RatingData> allRatings = ratingDAO.getAllRatings();
        System.out.println("DEBUG: Total ratings in database: " + (allRatings != null ? allRatings.size() : "null"));
        
        try {
            // Get all ratings for this jastiper
            List<RatingDAO.RatingData> ratings = ratingDAO.getRatingsByJastiper(jastiperId);
            System.out.println("DEBUG: getRatingsByJastiper returned " + (ratings != null ? ratings.size() : "null") + " ratings");
            
            if (ratings != null) {
                for (RatingDAO.RatingData rating : ratings) {
                    // Get the actual user name who gave the rating
                    String userName = userDAO.getUserNameById(rating.userId);
                    if (userName == null || userName.trim().isEmpty()) {
                        userName = "User #" + rating.userId;
                    }
                    
                    String comment = rating.deskripsi != null && !rating.deskripsi.trim().isEmpty() ? 
                        rating.deskripsi : "Tidak ada komentar";
                    
                    Ulasan ulasan = new Ulasan(
                        "#" + rating.ratingId, 
                        userName, 
                        rating.ratingKetepatan, 
                        rating.ratingPelayanan, 
                        comment, 
                        rating.date
                    );
                    reviews.add(ulasan);
                    
                    System.out.println("DEBUG: Created Ulasan: " + ulasan.orderId + 
                                     ", User: " + userName + 
                                     ", Ketepatan: " + ulasan.ratingKetepatan + 
                                     ", Pelayanan: " + ulasan.ratingPelayanan + 
                                     ", Komentar: " + ulasan.komentar);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting real reviews: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("DEBUG: Returning " + reviews.size() + " reviews");
        return reviews;
    }
    
    // ------------------- COMMENT HELPERS -------------------
    
    private String truncateComment(String comment, int maxLength) {
        if (comment == null || comment.length() <= maxLength) {
            return comment != null ? comment : "Tidak ada komentar";
        }
        return comment.substring(0, maxLength) + "...";
    }
    
    private Button createViewCommentButton(String fullComment) {
        Button viewButton = new Button("Lihat Full", VaadinIcon.EYE.create());
        viewButton.getStyle()
            .set("background", NAVY)
            .set("color", WHITE)
            .set("border", "none")
            .set("border-radius", "6px")
            .set("padding", "8px 12px")
            .set("font-size", "12px")
            .set("cursor", "pointer");
        
        viewButton.addClickListener(e -> showFullCommentDialog(fullComment));
        
        // Disable button if comment is null or empty
        if (fullComment == null || fullComment.trim().isEmpty() || "Tidak ada komentar".equals(fullComment)) {
            viewButton.setEnabled(false);
            viewButton.setText("Tidak ada");
            viewButton.getStyle().set("background", "#cccccc").set("color", "#666666");
        }
        
        return viewButton;
    }
    
    private void showFullCommentDialog(String fullComment) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setMaxWidth("90vw");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        
        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);
        
        H3 title = new H3("Komentar Lengkap");
        title.getStyle()
            .set("margin", "0 0 16px 0")
            .set("color", TEXT_DARK)
            .set("font-size", "20px");
        
        Div commentText = new Div();
        commentText.setText(fullComment != null && !fullComment.trim().isEmpty() ? fullComment : "Tidak ada komentar");
        commentText.getStyle()
            .set("background", "#f8f9fa")
            .set("padding", "16px")
            .set("border-radius", "8px")
            .set("border", "1px solid #e9ecef")
            .set("line-height", "1.6")
            .set("color", TEXT_DARK)
            .set("white-space", "pre-wrap")
            .set("word-wrap", "break-word");
        
        Button closeButton = new Button("Tutup", VaadinIcon.CLOSE.create());
        stylePrimary(closeButton);
        closeButton.addClickListener(e -> dialog.close());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "16px");
        
        dialogContent.add(title, commentText, buttonLayout);
        dialog.add(dialogContent);
        dialog.open();
    }

    // ------------------- MODELS -------------------
    private String formatRupiah(Long amount) {
        if (amount == null) return "-";
        return rupiah.format(amount).replace(",00", "");
    }
    public record Pendapatan(LocalDate tanggal, String orderId, String deskripsi, long pendapatan) {}
    public record Ulasan(String orderId, String nama, int ratingKetepatan, int ratingPelayanan, String komentar, java.util.Date tanggal) {}
    public record ChartData(String month, int count) {}

    private long getCurrentMonthEarnings() {
        Integer userId = SessionUtils.getUserId();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = cal.get(Calendar.YEAR);
        int successfulOrders = countSuccessfulOrdersByMonth(userId, month, year);
        long totalEarnings = successfulOrders * 2000L;
        return totalEarnings;
    }
}

