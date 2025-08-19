package com.example.application.view.admin;

import com.example.application.dao.TitipanDAO;
import com.example.application.dao.TitipanDetailDAO;
import com.example.application.dao.UserDAO;
import com.example.application.model.Titipan;
import com.example.application.model.TitipanDetail;
import com.example.application.model.User;
import com.example.application.session.SessionUtils;
import com.example.application.view.main.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Route(value = "admin")
@PageTitle("Admin Dashboard | JastipKuy")
public class AdminView extends VerticalLayout {
    private TitipanDetailDAO titipanDetailDAO = new TitipanDetailDAO();
    private TitipanDAO titipanDAO = new TitipanDAO();
    private final UserDAO userDAO = new UserDAO();
    private ArrayList<User> userList = new ArrayList<>();
    private Grid<User> userGrid= new Grid<>();
    private Grid<Titipan> titipanGrid = new Grid<>();

    public AdminView() {
        Integer userId = SessionUtils.getUserId();
        String userRole = SessionUtils.getUserRole();
        if (userId != null){
            if (!Objects.equals(userRole, "Admin")){
                if (userRole.equals("Jastiper")){
                    UI.getCurrent().navigate("jastiper");
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
        try {

            initializeView();
        } catch (Exception e) {
            showErrorView(e);
        }
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassNames(LumoUtility.Background.BASE, "admin-view");

        add(createHeader());

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.setPadding(false);
        mainContent.setSpacing(false);
        mainContent.addClassNames(LumoUtility.Padding.MEDIUM, "main-content");

        Tabs navTabs = createNavigationTabs();

        Div contentArea = new Div();
        contentArea.setSizeFull();
        contentArea.addClassNames(LumoUtility.Padding.Top.MEDIUM, "content-area");

        Div dashboardPage = createDashboardPage();
        Div usersPage = createUsersPage();
        Div ordersPage = createOrdersPage();
        Div reportsPage = createReportsPage();

        setPageVisibility(dashboardPage, usersPage, ordersPage, reportsPage, 0);

        contentArea.add(dashboardPage, usersPage, ordersPage, reportsPage);

        navTabs.addSelectedChangeListener(event -> {
            int selectedIndex = navTabs.indexOf(event.getSelectedTab());
            setPageVisibility(dashboardPage, usersPage, ordersPage, reportsPage, selectedIndex);
        });

        mainContent.add(navTabs, contentArea);
        mainContent.expand(contentArea);

        add(mainContent);
        expand(mainContent);

        addCustomStyles();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BoxShadow.SMALL,
                "app-header"
        );
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.addClassNames(LumoUtility.Gap.MEDIUM);

        Icon logo = VaadinIcon.DASHBOARD.create();
        logo.addClassNames(
                LumoUtility.IconSize.LARGE,
                LumoUtility.TextColor.PRIMARY,
                "app-logo"
        );

        H1 title = new H1("JastipKuy Admin");
        title.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                LumoUtility.Margin.NONE,
                "app-title"
        );

        logoSection.add(logo, title);

        HorizontalLayout userSection = new HorizontalLayout();
        userSection.setAlignItems(FlexComponent.Alignment.CENTER);
        userSection.addClassNames(LumoUtility.Gap.SMALL);

        Button notificationBtn = new Button(VaadinIcon.BELL.create());
        notificationBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        notificationBtn.addClassNames("notification-btn");
        notificationBtn.setTooltipText("Notifikasi");

        // Logout Button
        Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        logoutBtn.addClassNames("logout-btn");
        logoutBtn.addClickListener(e -> handleLogout());

        userSection.add(notificationBtn, logoutBtn);

        header.add(logoSection, userSection);
        return header;
    }

    private Tabs createNavigationTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderColor.CONTRAST_10,
                "nav-tabs"
        );

        Tab dashboardTab = createTab("Dashboard", VaadinIcon.DASHBOARD);
        Tab usersTab = createTab("Manajemen User", VaadinIcon.USERS);
        Tab ordersTab = createTab("Manajemen Titipan", VaadinIcon.CART);
        Tab reportsTab = createTab("Laporan", VaadinIcon.CHART);

        tabs.add(dashboardTab, usersTab, ordersTab, reportsTab);
        return tabs;
    }

    private Tab createTab(String title, VaadinIcon icon) {
        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.addClassNames(LumoUtility.Gap.SMALL);

        Icon tabIcon = icon.create();
        tabIcon.addClassNames(LumoUtility.IconSize.SMALL, "tab-icon");

        Span tabText = new Span(title);
        tabText.addClassNames(LumoUtility.FontWeight.MEDIUM, "tab-text");

        content.add(tabIcon, tabText);

        Tab tab = new Tab(content);
        tab.addClassNames(LumoUtility.Padding.MEDIUM, "nav-tab");
        return tab;
    }

    private Div createDashboardPage() {
        Div page = new Div();
        page.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.LARGE, "page-content");

        // Page Header
        H2 pageTitle = new H2("Dashboard Overview");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                LumoUtility.Margin.Bottom.MEDIUM,
                "page-title"
        );

        HorizontalLayout statsGrid = new HorizontalLayout();
        statsGrid.setWidthFull();
        statsGrid.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.FlexWrap.WRAP,
                "stats-grid"
        );

        int totalUser = userDAO.getTotalUsers();
        int totalOrder = titipanDAO.getOrderCountThisMonth();
        long pendapatan = titipanDAO.getPendapatanThisMonth();

        statsGrid.add(
                createStatCard("Total User", String.valueOf(totalUser), VaadinIcon.USERS, "primary"),
                createStatCard("Order Bulan Ini", String.valueOf(totalOrder), VaadinIcon.CART, "success"),
                createStatCard("Pendapatan", formatRupiah(pendapatan), VaadinIcon.MONEY, "error")
        );


        HorizontalLayout quickActions = new HorizontalLayout();
        quickActions.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.FlexWrap.WRAP,
                "quick-actions"
        );

        Button addUserBtn = new Button("Tambah User", VaadinIcon.PLUS.create());
        addUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button newOrderBtn = new Button("Order Baru", VaadinIcon.CART.create());
        newOrderBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        quickActions.add(addUserBtn, newOrderBtn);

        VerticalLayout activitySection = new VerticalLayout();
        activitySection.setPadding(false);
        activitySection.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.LARGE,
                LumoUtility.BoxShadow.SMALL,
                "activity-section"
        );

        H3 activityTitle = new H3("Aktivitas Terakhir");
        activityTitle.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.TextColor.HEADER,
                LumoUtility.Margin.NONE,
                "section-title"
        );

        VerticalLayout activityList = new VerticalLayout();
        activityList.setPadding(false);
        activityList.addClassNames(LumoUtility.Gap.SMALL, "activity-list");

        List<Titipan> lastTitipan = titipanDAO.getLastTitipan(5);

        for (Titipan t : lastTitipan) {
            String userName = userDAO.getUserNameById(t.getUser_id());
            String timeAgo = getTimeAgo(t.getCreated_at());
            activityList.add(
                    createActivityItem("Order dibuat", "oleh " + userName, timeAgo, VaadinIcon.CART)
            );
        }


        activitySection.add(activityTitle, activityList);

        page.add(pageTitle, statsGrid, quickActions, activitySection);
        return page;
    }

    //Users Page

    private Div createUsersPage() {
        Div page = new Div();
        page.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.LARGE, "page-content");

        H2 pageTitle = new H2("Manajemen User");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                LumoUtility.Margin.Bottom.MEDIUM,
                "page-title"
        );

        // Load user data
        loadUserList();

        // Toolbar
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.AlignItems.END,
                LumoUtility.FlexWrap.WRAP,
                "page-toolbar"
        );

        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari user...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.addClassNames(LumoUtility.Flex.GROW, "search-field");

        ComboBox<String> roleFilter = new ComboBox<>("Filter Role");
        roleFilter.setItems("Semua", "Admin", "User", "Jastipper");
        roleFilter.setValue("Semua");
        roleFilter.setWidth("200px");

        Button addUserBtn = new Button("Tambah User", VaadinIcon.PLUS.create());
        addUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addUserBtn.addClickListener(e -> openUserForm(null));

        toolbar.add(searchField, roleFilter, addUserBtn);

        createUserGrid();

        page.add(pageTitle, toolbar, userGrid);
        return page;
    }

    private void reloadGridUser(String text) {
        List<User> userList = userDAO.getAllUser(text);
        userGrid.setItems(userList);
    }

    private void createUserGrid() {
        userGrid.setWidthFull();
        userGrid.setHeight("600px");
        userGrid.addClassNames("users-grid");
        userGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        userGrid.setItems(userList);

        userGrid.addColumn(User::getId)
                .setHeader("ID")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true);

        userGrid.addColumn(User::getNisn)
                .setHeader("NISN")
                .setAutoWidth(true)
                .setSortable(true);

        userGrid.addColumn(User::getName)
                .setHeader("Nama")
                .setAutoWidth(true)
                .setSortable(true);

        userGrid.addColumn(User::getEmail)
                .setHeader("Email")
                .setAutoWidth(true)
                .setSortable(true);

        userGrid.addColumn(User::getRole)
                .setHeader("Role")
                .setAutoWidth(true)
                .setSortable(true);

        userGrid.addComponentColumn(this::createUserActions)
                .setHeader("Aksi")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    private HorizontalLayout createUserActions(User user) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassNames(LumoUtility.Gap.XSMALL);

        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        editBtn.setTooltipText("Edit");
        editBtn.addClickListener(e -> openUserForm(user));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        deleteBtn.setTooltipText("Hapus");
        deleteBtn.addClickListener(e -> openDeleteUserDialog(user));

        actions.add(editBtn, deleteBtn);
        return actions;
    }

    private void openDeleteUserDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Hapus User");

        Text confirmLabel = new Text("Apakah kamu yakin ingin menghapus user: " + user.getName() + "?");

        Button cancelButton = new Button("Batal", e -> dialog.close());
        Button deleteButton = new Button("Ya, Hapus", e -> {
            boolean success = userDAO.delete(user.getId());
            if (success) {
                Notification.show("User berhasil dihapus");
                reloadGridUser("");
            } else {
                Notification.show("Gagal menghapus user", 3000, Notification.Position.MIDDLE);
            }
            dialog.close();
        });

        deleteButton.getStyle().set("background-color", "red");
        deleteButton.getStyle().set("color", "white");

        HorizontalLayout actions = new HorizontalLayout(cancelButton, deleteButton);
        dialog.add(confirmLabel, actions);
        dialog.open();
    }

    private void openUserForm(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(user == null ? "Tambah User" : "Edit User");

        TextField nisnField = new TextField("NISN");
        TextField nameField = new TextField("Nama");
        TextField emailField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        ComboBox<String> roleCombo = new ComboBox<>("Role");
        roleCombo.setItems("admin", "user");

        if (user != null) {
            nisnField.setValue(user.getNisn() != null ? user.getNisn() : "");
            nameField.setValue(user.getName() != null ? user.getName() : "");
            emailField.setValue(user.getEmail() != null ? user.getEmail() : "");
            passwordField.setValue(user.getPassword() != null ? user.getPassword() : "");
            roleCombo.setValue(user.getRole());
        }

        // Layout form
        VerticalLayout formLayout = new VerticalLayout(
                nisnField,
                nameField,
                emailField,
                passwordField,
                roleCombo
        );
        dialog.add(formLayout);

        // Tombol
        Button saveButton = new Button("Simpan", event -> {
            String nisn = nisnField.getValue();
            String name = nameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String role = roleCombo.getValue();

            boolean result = false;
            if (user == null) {
                // Mode Tambah
                result = userDAO.insert(nisn, name, email, password, role);
            } else {
                // Mode Edit
                result = userDAO.update(user.getId(), nisn, name, email, password, role);
            }

            if (result) {
                Notification.show("Data berhasil disimpan");
                reloadGridUser("");
                dialog.close();
            } else {
                Notification.show("Gagal menyimpan data", 3000, Notification.Position.MIDDLE);
            }
        });

        Button cancelButton = new Button("Batal", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    //Titipan

    private Div createOrdersPage() {
        Div page = new Div();
        page.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.LARGE, "page-content");

        H2 pageTitle = new H2("Manajemen Titipan");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                "page-title"
        );

        // Search field
        TextField searchField = new TextField();
        searchField.setPlaceholder("Cari nama barang atau nama user...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidthFull();

        // Grid
        Grid<Titipan> grid = new Grid<>(Titipan.class, false);
        grid.addColumn(Titipan::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(t -> userDAO.getUserNameById(t.getUser_id()))
                .setHeader("Nama User").setAutoWidth(true);
        grid.addColumn(t -> formatRupiah(t.getHarga_estimasi())).setHeader("Estimasi Harga");
        grid.addColumn(Titipan::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(t -> formatTanggal(t.getCreated_at())).setHeader("Tanggal Titip");
        grid.addColumn(t -> t.getDiambil_oleh() != null ? t.getDiambil_oleh() : "-")
                .setHeader("Diambil Oleh").setAutoWidth(true);

        grid.addComponentColumn(titipan -> {
            HorizontalLayout actions = new HorizontalLayout();

            // Tombol Edit
            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> openTitipanForm(titipan, grid));

            // Tombol Delete
            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
            deleteBtn.addClickListener(e -> {
                grid.setItems(titipanDAO.getAllTitipan(searchField.getValue()));
            });

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Aksi").setAutoWidth(true);

        List<Titipan> allData = titipanDAO.getAllTitipan(""); // kosong = ambil semua
        grid.setItems(allData);

        Button addButton = new Button("Tambah Titipan", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openTitipanForm(null, grid)); // null artinya tambah baru

        HorizontalLayout topBar = new HorizontalLayout(searchField, addButton);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topBar.setAlignItems(Alignment.CENTER);

        page.add(pageTitle, topBar, grid);

        searchField.addValueChangeListener(e -> {
            String keyword = e.getValue();
            List<Titipan> filtered = titipanDAO.getAllTitipan(keyword);
            grid.setItems(filtered);
        });


        page.add(pageTitle, topBar, grid);
        return page;
    }

    private String formatRupiah(Long amount) {
        if (amount == null) return "-";
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private void openTitipanForm(Titipan titipan, Grid<Titipan> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(titipan == null ? "Tambah Titipan" : "Edit Titipan");

        TextField userIdField = new TextField("User ID");
        userIdField.setPlaceholder("Masukkan ID User");

        TextField idPengirimField = new TextField("ID Pengirim");
        idPengirimField.setPlaceholder("Masukkan ID Driver");

        TextField hargaField = new TextField("Harga Estimasi");
        hargaField.setValue("0");
        hargaField.setReadOnly(true);

        TextField tanggalField = new TextField("Tanggal Titipan");
        tanggalField.setReadOnly(true);

        ComboBox<String> statusBox = new ComboBox<>("Status");
        statusBox.setItems("MENUNGGU", "DIPROSES", "SELESAI");

        TextArea deskripsiField = new TextArea("Deskripsi Barang");
        TextArea catatanField = new TextArea("Catatan Opsional");

        List<TitipanDetail> detailList = titipan != null
                ? titipanDetailDAO.getDetailsByTransaksiId(titipan.getId())
                : new ArrayList<>();

        Grid<TitipanDetail> detailGrid = new Grid<>(TitipanDetail.class, false);
        detailGrid.addColumn(TitipanDetail::getDeskripsi).setHeader("Deskripsi");
        detailGrid.addColumn(TitipanDetail::getCatatan_opsional).setHeader("Catatan");
        detailGrid.setItems(detailList);

        Button tambahBarangBtn = new Button("Tambah Barang", e -> {
            if (!deskripsiField.isEmpty()) {
                TitipanDetail detail = new TitipanDetail();
                detail.setDeskripsi(deskripsiField.getValue());
                detail.setCatatan_opsional(catatanField.getValue());
                detailList.add(detail);

                detailGrid.setItems(detailList);
                final int HARGA_PER_BARANG = 3000;
                hargaField.setValue(String.valueOf(detailList.size() * HARGA_PER_BARANG));


                deskripsiField.clear();
                catatanField.clear();
            }
        });

        if (titipan != null) {
            userIdField.setValue(String.valueOf(titipan.getUser_id()));
            hargaField.setValue(String.valueOf(titipan.getHarga_estimasi()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tanggalField.setValue(sdf.format(titipan.getCreated_at()));

            statusBox.setValue(titipan.getStatus());
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tanggalField.setValue(sdf.format(new Date(System.currentTimeMillis())));
        }

        Button saveBtn = new Button("Simpan", event -> {
            Titipan data = titipan != null ? titipan : new Titipan();
            data.setUser_id(Integer.parseInt(userIdField.getValue()));
            data.setStatus(statusBox.getValue());
            data.setDiambil_oleh(Integer.parseInt(idPengirimField.getValue()));

            Date now = new Date(System.currentTimeMillis());
            data.setCreated_at(now);

            long estimasiHarga = detailList.size() * 3000L;
            data.setHarga_estimasi(estimasiHarga);

            if (titipan == null) {
                int idBaru = titipanDAO.insertTitipanReturnId(data);
                for (TitipanDetail d : detailList) {
                    d.setIdTransaksi(idBaru);
                    titipanDetailDAO.insertDetail(d);
                }
            } else {
                titipanDAO.updateTitipan(data);
                titipanDetailDAO.deleteByTitipanId(data.getId()); // hapus dulu detail lama
                for (TitipanDetail d : detailList) {
                    d.setIdTransaksi(data.getId());
                    titipanDetailDAO.insertDetail(d);
                }
            }

            reloadTitipanGrid();
            dialog.close();
        });

        Button cancelBtn = new Button("Batal", e -> dialog.close());

        VerticalLayout detailBarangSection = new VerticalLayout(
                new HorizontalLayout(deskripsiField, catatanField, tambahBarangBtn),
                detailGrid
        );

        VerticalLayout formLayout = new VerticalLayout(
                userIdField,
                idPengirimField,
                hargaField,
                tanggalField,
                statusBox,
                detailBarangSection,
                new HorizontalLayout(cancelBtn, saveBtn)
        );

        dialog.add(formLayout);
        dialog.open();
    }

    private void reloadTitipanGrid(){
        titipanGrid.setItems(titipanDAO.getAllTitipan(""));
    }

    private Div createReportsPage() {
        Div page = new Div();
        page.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.LARGE, "page-content");

        H2 pageTitle = new H2("Laporan");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                "page-title"
        );

        // Report Controls
        HorizontalLayout controls = new HorizontalLayout();
        controls.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.AlignItems.END,
                LumoUtility.FlexWrap.WRAP,
                "report-controls"
        );

        DatePicker fromDate = new DatePicker("Dari Tanggal");
        DatePicker toDate = new DatePicker("Sampai Tanggal");

        ComboBox<String> reportType = new ComboBox<>("Jenis Laporan");
        reportType.setItems("Penjualan", "User", "Produk", "Pendapatan");
        reportType.setWidth("200px");

        Button generateBtn = new Button("Generate", VaadinIcon.PLAY.create());
        generateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generateBtn.addClickListener(e -> generateReport());

        controls.add(fromDate, toDate, reportType, generateBtn);

        // Report Placeholder
        Div reportArea = new Div();
        reportArea.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.TextColor.SECONDARY,
                "report-area"
        );
        reportArea.setText("Laporan akan muncul di sini setelah di-generate");

        page.add(pageTitle, controls, reportArea);
        return page;
    }

    private Component createStatCard(String title, String value, VaadinIcon icon, String variant) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Flex.GROW,
                "stat-card"
        );

        HorizontalLayout header = new HorizontalLayout();
        header.addClassNames(
                LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM,
                "stat-card__header"
        );

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.FontWeight.MEDIUM,
                LumoUtility.TextColor.SECONDARY,
                "stat-card__title"
        );

        Icon cardIcon = icon.create();
        cardIcon.addClassNames(
                LumoUtility.IconSize.MEDIUM,
                getVariantColorClass(variant),
                "stat-card__icon"
        );

        header.add(titleSpan, cardIcon);

        Span valueSpan = new Span(value);
        valueSpan.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.HEADER,
                "stat-card__value"
        );

        card.add(header, valueSpan);
        return card;
    }

    private String getVariantColorClass(String variant) {
        return switch (variant) {
            case "primary" -> LumoUtility.TextColor.PRIMARY;
            case "success" -> LumoUtility.TextColor.SUCCESS;
            case "warning" -> LumoUtility.TextColor.WARNING;
            case "error" -> LumoUtility.TextColor.ERROR;
            default -> LumoUtility.TextColor.PRIMARY;
        };
    }

    private Div createActivityItem(String action, String detail, String time, VaadinIcon icon) {
        Div item = new Div();
        item.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Gap.MEDIUM,
                LumoUtility.Padding.SMALL,
                LumoUtility.BorderRadius.SMALL,
                "activity-item"
        );

        Icon itemIcon = icon.create();
        itemIcon.addClassNames(
                LumoUtility.IconSize.SMALL,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Flex.NONE,
                "activity-item__icon"
        );

        Div content = new Div();
        content.addClassNames(LumoUtility.Flex.GROW, "activity-item__content");

        Span actionSpan = new Span(action);
        actionSpan.addClassNames(
                LumoUtility.FontWeight.MEDIUM,
                LumoUtility.TextColor.HEADER,
                "activity-item__action"
        );

        Span detailSpan = new Span(detail);
        detailSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                "activity-item__detail"
        );

        Span timeSpan = new Span(time);
        timeSpan.addClassNames(
                LumoUtility.FontSize.XSMALL,
                LumoUtility.TextColor.TERTIARY,
                LumoUtility.Flex.NONE,
                "activity-item__time"
        );

        content.add(actionSpan, detailSpan);
        item.add(itemIcon, content, timeSpan);

        return item;
    }

    // Event Handlers
    private void handleLogout() {
        try {
            VaadinSession.getCurrent().getSession().invalidate();
            UI.getCurrent().navigate("login");
        } catch (Exception e) {
            showNotification("Error saat logout: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void loadUserList() {
        try {
            userList = userDAO.getAllUser("");
        } catch (Exception e) {
            showNotification("Error loading users: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void editUser(User user) {
        showNotification("Edit user: " + user.getName(), NotificationVariant.LUMO_CONTRAST);
        // TODO: Implement edit user dialog
    }

    private void deleteUser(User user) {
        showNotification("Delete user: " + user.getName(), NotificationVariant.LUMO_CONTRAST);
        // TODO: Implement delete confirmation and action
        loadUserList();
        if (userGrid != null) {
            userGrid.setItems(userList);
        }
    }

    private void showAddUserDialog() {
        showNotification("Add user dialog", NotificationVariant.LUMO_CONTRAST);
        // TODO: Implement add user dialog
    }

    private void generateReport() {
        showNotification("Generating report...", NotificationVariant.LUMO_SUCCESS);
        // TODO: Implement report generation
    }

    // Utility Methods
    private void setPageVisibility(Div dashboard, Div users, Div orders, Div reports, int selectedIndex) {
        dashboard.setVisible(selectedIndex == 0);
        users.setVisible(selectedIndex == 1);
        orders.setVisible(selectedIndex == 2);
        reports.setVisible(selectedIndex == 3);
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }

    private void showErrorView(Exception e) {
        removeAll();
        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setSizeFull();
        errorLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        errorLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        errorLayout.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.TextAlignment.CENTER
        );

        H1 errorTitle = new H1("Error Loading Admin View");
        errorTitle.addClassNames(
                LumoUtility.TextColor.ERROR,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        Div errorMessage = new Div("Error: " + e.getMessage());
        errorMessage.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.LARGE
        );

        errorLayout.add(errorTitle, errorMessage);
        add(errorLayout);
        e.printStackTrace();
    }

    private void addCustomStyles() {
        // Using CSS custom properties for theming
        getElement().getStyle()
                .set("--lumo-primary-color", "#3b82f6")
                .set("--lumo-primary-color-50pct", "rgba(59, 130, 246, 0.5)")
                .set("--lumo-primary-color-10pct", "rgba(59, 130, 246, 0.1)");
    }

    private String formatRupiah(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return format.format(amount).replace(",00", "");
    }

    private String formatTanggal(java.util.Date date)
    {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private String getTimeAgo(java.util.Date date) {
        long millis = new Date().getTime() - date.getTime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) return seconds + " detik lalu";
        else if (minutes < 60) return minutes + " menit lalu";
        else if (hours < 24) return hours + " jam lalu";
        else return days + " hari lalu";
    }



}