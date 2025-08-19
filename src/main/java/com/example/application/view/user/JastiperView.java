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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

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
           SessionUtils.clearSession();
           UI.getCurrent().navigate("login");
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

        HorizontalLayout rankRow = new HorizontalLayout(); rankRow.setWidthFull();
        rankRow.getStyle().set("gap", "12px");
        Div small = box("64px", "180px");
        Div b1 = box("100%", "180px");
        Div b2 = box("100%", "180px");
        Div b3 = box("100%", "180px");
        rankRow.add(small, b1, b2, b3);
        rankRow.setFlexGrow(0, small); rankRow.setFlexGrow(1, b1, b2, b3);

        Paragraph rkLabel = new Paragraph("RANK KAMU");
        rkLabel.getStyle().set("margin", "18px 0 8px 4px").set("font-weight", "700");
        HorizontalLayout rkRow = new HorizontalLayout(); rkRow.setWidthFull(); rkRow.getStyle().set("gap", "12px");
        rkRow.add(box("64px", "56px"), box("100%", "56px"), box("100%", "56px"), box("100%", "56px"));
        rkRow.setFlexGrow(0, rkRow.getComponentAt(0)); rkRow.setFlexGrow(1, rkRow.getComponentAt(1), rkRow.getComponentAt(2), rkRow.getComponentAt(3));

        HorizontalLayout bottom = new HorizontalLayout(); bottom.setWidthFull(); bottom.getStyle().set("gap", "24px").set("margin-top", "10px");

        VerticalLayout btns = new VerticalLayout(); btns.setWidthFull(); btns.setSpacing(true); btns.setPadding(false); btns.setMaxWidth("520px");
        btns.add(
                navBtn("Edit Data", VaadinIcon.USER_CARD.create(), "Data"),
                navBtn("Pesanan Masuk", VaadinIcon.CLIPBOARD_TEXT.create(), "Pesanan Masuk"),
                navBtn("Laporan Penghasilan", VaadinIcon.MONEY.create(), "Laporan Penghasilan"),
                navBtn("Riwayat dan Ulasan", VaadinIcon.COMMENTS.create(), "Riwayat dan Ulasan")
        );

        VerticalLayout chartWrap = new VerticalLayout(); chartWrap.setPadding(false); chartWrap.setSpacing(false); chartWrap.setSizeFull();
        Paragraph chartTitle = new Paragraph("GRAFIK PESANAN SUKSES"); chartTitle.getStyle().set("margin", "4px 0 8px 0").set("font-weight", "700");

        Div chartCard = new Div();
        chartCard.getStyle().set("background", WHITE).set("border", "1px solid " + LIGHT).set("border-radius", "8px")
                .set("height", "340px").set("display", "flex");

        // Left accent bar
        Div accent = new Div(); accent.getStyle().set("width", "48px").set("background", SLATE).set("border-radius", "8px 0 0 8px");
        // Simple column bars with dummy data
        Div bars = new Div(); bars.getStyle().set("display", "flex").set("align-items", "end").set("gap", "14px")
                .set("padding", "24px").set("width", "100%");
        Random r = new Random(7);
        IntStream.range(0, 8).forEach(i -> bars.add(bar(24, 220, 60 + r.nextInt(160))));

        chartCard.add(accent, bars);
        chartWrap.add(chartTitle, chartCard);

        bottom.add(btns, chartWrap); bottom.setFlexGrow(0, btns); bottom.setFlexGrow(1, chartWrap);

        wrap.add(title, rankRow, rkLabel, rkRow, bottom);
        return wrap;
    }

    private Component buildPesanan() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(true);
        wrap.setSpacing(true);
        wrap.setWidthFull();

        refreshPesanan(wrap);

        return wrap;
    }

    private void refreshPesanan(VerticalLayout wrap) {
        wrap.removeAll(); // hapus isi lama

        // Ambil data titipan
        List<Titipan> list = titipanDAO.getAllTitipan("");

        for (Titipan t : list) {
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
                    refreshPesanan(wrap);
                });

                Button batal = new Button("Batal");
                batal.getStyle().set("background", "red").set("color", "white");
                batal.addClickListener(e -> {
                    Titipan titipan = new Titipan();
                    titipan.setId(t.getId());
                    titipan.setStatus("BATAL");
                    titipanDAO.updateTitipan(titipan);
                    Notification.show("Pesanan #" + t.getId() + " dibatalkan");
                    refreshPesanan(wrap);
                });

                actions.add(terima, batal);
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
                    refreshPesanan(wrap);
                });

                Button batal = new Button("Batal");
                batal.getStyle().set("background", "red").set("color", "white");
                batal.addClickListener(e -> {
                    Titipan titipan = new Titipan();
                    titipan.setId(t.getId());
                    titipan.setStatus("BATAL");
                    titipanDAO.updateTitipan(titipan);
                    Notification.show("Pesanan #" + t.getId() + " dibatalkan");
                    refreshPesanan(wrap);
                });

                actions.add(selesai, batal);
            }
            else if ("SELESAI".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "gray").set("color", "white");
            }
            else if ("BATAL".equals(t.getStatus())) {
                statusBadge.getStyle().set("background", "#BE3D2A").set("color", "white");
            }

            card.add(namaBarang, lokasi, harga, statusBadge, actions);
            wrap.add(card);
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
        grid.addColumn(p -> p.tanggal.toString()).setHeader("Tanggal").setAutoWidth(true);
        grid.addColumn(p -> p.orderId).setHeader("Order").setAutoWidth(true);
        grid.addColumn(p -> p.deskripsi).setHeader("Deskripsi").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(p -> "Rp " + fnum(p.pendapatan)).setHeader("Pendapatan").setAutoWidth(true);
        grid.addColumn(p -> "Rp " + fnum(p.biaya)).setHeader("Biaya").setAutoWidth(true);
        grid.addColumn(p -> "Rp " + fnum(p.pendapatan - p.biaya)).setHeader("Net").setAutoWidth(true);
        List<Pendapatan> data = dummyPendapatan();
        grid.setItems(data);
        grid.setAllRowsVisible(true);

        long total = data.stream().mapToLong(p -> p.pendapatan - p.biaya).sum();

        Div card = new Div();
        card.getStyle().set("background", WHITE).set("border", "1px solid " + LIGHT).set("border-radius", "8px")
                .set("padding", "16px").set("margin-top", "12px");
        card.add(new H4("Ringkasan Bulan Ini"),
                new Paragraph("Total Net: Rp " + fnum(total)),
                new Paragraph("Total Order: " + data.size()));

        wrap.add(grid, card);
        return wrap;
    }

    private Component buildRiwayat() {
        VerticalLayout wrap = section("Riwayat dan Ulasan");

        Grid<Ulasan> grid = new Grid<>(Ulasan.class, false);
        grid.addColumn(u -> u.orderId).setHeader("Order").setAutoWidth(true);
        grid.addColumn(u -> u.nama).setHeader("Pengulas").setAutoWidth(true);
        grid.addColumn(u -> "★".repeat(u.rating)).setHeader("Rating").setAutoWidth(true);
        grid.addColumn(u -> u.komentar).setHeader("Komentar").setFlexGrow(1);
        grid.addColumn(u -> u.tanggal.toString()).setHeader("Tanggal").setAutoWidth(true);
        grid.setItems(dummyUlasan());
        grid.setAllRowsVisible(true);

        wrap.add(grid);
        return wrap;
    }

    private Component buildEditData() {
        VerticalLayout wrap = section("Edit Data");
        User user = userDAO.getUserById(SessionUtils.getUserId());

        FormLayout form = new FormLayout();
        TextField nama = new TextField("Nama Lengkap"); nama.setValue(user.getName());
        EmailField email = new EmailField("Email"); email.setValue("jastiper@example.com");
        TextField phone = new TextField("No. HP"); phone.setValue("0812-3456-7890");
        TextField kota = new TextField("Kota"); kota.setValue("Jakarta");
        NumberField fee = new NumberField("Fee (%)"); fee.setValue(10d);
        TextArea bio = new TextArea("Deskripsi"); bio.setValue("Siap titip belanja dan kirim cepat.");

        form.add(nama, email, phone, kota, fee, bio);
        form.setColspan(bio, 2);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("640px", 2));

        Button simpan = new Button("Simpan Perubahan", VaadinIcon.CHECK.create(), e -> Notification.show("Data disimpan (dummy)."));
        stylePrimary(simpan); simpan.getStyle().set("margin-top", "8px");

        wrap.add(form, simpan);
        return wrap;
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
        String s = String.format(Locale.US, "%,d", n);
        return s.replace(",", ".");
    }


    private List<Pendapatan> dummyPendapatan() {
        List<Pendapatan> l = new ArrayList<>();
        l.add(new Pendapatan(LocalDate.now().minusDays(6), "#1010", "Fee pesanan Andi", 60000, 15000));
        l.add(new Pendapatan(LocalDate.now().minusDays(5), "#1011", "Fee pesanan Rina", 80000, 20000));
        l.add(new Pendapatan(LocalDate.now().minusDays(4), "#1012", "Fee pesanan Andi", 45000, 12000));
        l.add(new Pendapatan(LocalDate.now().minusDays(3), "#1013", "Fee pesanan Sinta", 70000, 18000));
        l.add(new Pendapatan(LocalDate.now().minusDays(2), "#1014", "Fee pesanan Budi", 90000, 25000));
        l.add(new Pendapatan(LocalDate.now().minusDays(1), "#1015", "Fee pesanan Maya", 65000, 14000));
        return l;
    }

    private List<Ulasan> dummyUlasan() {
        return Arrays.asList(
                new Ulasan("#1010", "Rina", 5, "Cepat & ramah, recommended!", LocalDate.now().minusDays(4)),
                new Ulasan("#1012", "Andi", 4, "Barang aman, packing rapi.", LocalDate.now().minusDays(3)),
                new Ulasan("#1014", "Budi", 5, "Mantap sesuai request.", LocalDate.now().minusDays(2)),
                new Ulasan("#1015", "Maya", 3, "Sedikit telat tapi oke.", LocalDate.now().minusDays(1))
        );
    }

    // ------------------- MODELS -------------------
    private String formatRupiah(Long amount) {
        if (amount == null) return "-";
        return rupiah.format(amount).replace(",00", "");
    }
    public record Pendapatan(LocalDate tanggal, String orderId, String deskripsi, long pendapatan, long biaya) {}
    public record Ulasan(String orderId, String nama, int rating, String komentar, LocalDate tanggal) {}
}