// MainLayout.java
package com.example.application.view.user;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("user")
@PageTitle("JASTIPKUY")
public class UserDashboardView extends AppLayout {

    private H2 viewTitle;

    public UserDashboardView() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        // Logout button positioned on the right
        var logoutButton = new Span("Logout");
        logoutButton.addClassNames("logout-btn");
        logoutButton.getStyle().set("cursor", "pointer");
        logoutButton.getStyle().set("color", "white");
        logoutButton.getStyle().set("margin-right", "20px");

        var header = new HorizontalLayout(toggle, viewTitle);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        // Add logout to the end
        header.add(logoutButton);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);

        // Custom CSS for header styling
        getElement().getStyle().set("--vaadin-app-layout-navbar-background", "#2c5282");
    }

    private void addDrawerContent() {
        // Logo and title
        var logoLayout = new HorizontalLayout();
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.setPadding(true);
        logoLayout.getStyle().set("background-color", "#2c5282");
        logoLayout.getStyle().set("color", "white");

        var logo = new Span("✈");
        logo.getStyle().set("font-size", "24px");
        logo.getStyle().set("margin-right", "8px");

        var title = new Span("JASTIPKUY");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("font-size", "18px");

        logoLayout.add(logo, title);

        // User info section
        var userSection = new VerticalLayout();
        userSection.setPadding(true);
        userSection.setSpacing(false);
        userSection.getStyle().set("background-color", "#4a5568");
        userSection.getStyle().set("color", "white");

        var avatar = new Avatar();
        avatar.setName("Customer JASTIPKUY");
        avatar.getStyle().set("width", "40px");
        avatar.getStyle().set("height", "40px");

        var greeting = new Span("Hi, Customer JASTIPKUY!");
        greeting.getStyle().set("font-size", "14px");

        var userLayout = new HorizontalLayout(avatar, greeting);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.setSpacing(true);

        userSection.add(userLayout);

        // Navigation menu
        SideNav navigation = createNavigation();
        navigation.getStyle().set("background-color", "#4a5568");

        var drawerLayout = new VerticalLayout(logoLayout, userSection, new Scroller(navigation));
        drawerLayout.setPadding(false);
        drawerLayout.setSpacing(false);
        drawerLayout.setSizeFull();

        addToDrawer(drawerLayout);

        // Drawer styling
        getElement().getStyle().set("--vaadin-app-layout-drawer-background", "#4a5568");
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Data", DataView.class, VaadinIcon.DATABASE.create()));
        nav.addItem(new SideNavItem("Order JASTIPKUY", OrderView.class, VaadinIcon.CART.create()));
        nav.addItem(new SideNavItem("Riwayat", RiwayatView.class, VaadinIcon.CLOCK.create()));
        nav.addItem(new SideNavItem("Logout", LogoutView.class, VaadinIcon.SIGN_OUT.create()));

        // Custom styling for nav items
        nav.getElement().getStyle().set("--vaadin-side-nav-item-background", "transparent");
        nav.getElement().getStyle().set("--vaadin-side-nav-item-color", "white");
        nav.getElement().getStyle().set("--vaadin-side-nav-item-selected-background", "#2c5282");

        return nav;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

@PageTitle("Home")
@Route(value = "", layout = UserDashboardView.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Status section
        var statusSection = createStatusSection();

        // Action buttons
        var actionButtons = createActionButtons();

        // Order history table
        var historySection = createHistorySection();

        add(statusSection, actionButtons, historySection);

        // Custom styling
        getStyle().set("background-color", "#f7fafc");
        getStyle().set("padding", "20px");
    }

    private Div createStatusSection() {
        var statusDiv = new Div();
        statusDiv.getStyle().set("background-color", "white");
        statusDiv.getStyle().set("border-radius", "8px");
        statusDiv.getStyle().set("padding", "20px");
        statusDiv.getStyle().set("margin-bottom", "20px");
        statusDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        statusDiv.setWidth("100%");

        var title = new Span("STATUS PESANAN SAAT INI");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("color", "#2d3748");

        var timeInfo = new HorizontalLayout();
        timeInfo.setAlignItems(Alignment.CENTER);
        timeInfo.setJustifyContentMode(JustifyContentMode.BETWEEN);
        timeInfo.setWidthFull();

        var time = new Span("09:30");
        time.getStyle().set("font-size", "24px");
        time.getStyle().set("font-weight", "bold");
        time.getStyle().set("color", "#2b6cb0");

        var date = new Span("Jasa Titipan | 09 JAN 2024");
        date.getStyle().set("color", "#718096");

        var viewDetail = new Button("View Detail");
        viewDetail.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var statusLayout = new VerticalLayout(title, time, date);
        statusLayout.setSpacing(false);
        statusLayout.setPadding(false);

        var contentLayout = new HorizontalLayout(statusLayout, viewDetail);
        contentLayout.setWidthFull();
        contentLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        contentLayout.setAlignItems(Alignment.CENTER);

        statusDiv.add(contentLayout);
        return statusDiv;
    }

    private HorizontalLayout createActionButtons() {
        var editDataBtn = new Button("Edit Data");
        editDataBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editDataBtn.getStyle().set("background-color", "#2c5282");
        editDataBtn.setWidth("200px");
        editDataBtn.setHeight("50px");

        var buatPesananBtn = new Button("Buat Pesanan");
        buatPesananBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buatPesananBtn.getStyle().set("background-color", "#2c5282");
        buatPesananBtn.setWidth("200px");
        buatPesananBtn.setHeight("50px");

        var buttonLayout = new HorizontalLayout(editDataBtn, buatPesananBtn);
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return buttonLayout;
    }

    private Div createHistorySection() {
        var historyDiv = new Div();
        historyDiv.getStyle().set("background-color", "white");
        historyDiv.getStyle().set("border-radius", "8px");
        historyDiv.getStyle().set("padding", "20px");
        historyDiv.getStyle().set("margin-top", "20px");
        historyDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        historyDiv.setWidth("100%");

        var title = new Span("RIWAYAT PESANAN");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("color", "#2d3748");
        title.getStyle().set("margin-bottom", "15px");
        title.getStyle().set("display", "block");

        // Table headers
        var headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.getStyle().set("border-bottom", "1px solid #e2e8f0");
        headerLayout.getStyle().set("padding-bottom", "10px");

        var headers = new String[]{"ID", "Tanggal", "Jasa", "Status", "Aksi"};
        for (String header : headers) {
            var headerSpan = new Span(header);
            headerSpan.getStyle().set("font-weight", "bold");
            headerSpan.getStyle().set("color", "#4a5568");
            headerLayout.add(headerSpan);
        }

        historyDiv.add(title, headerLayout);
        return historyDiv;
    }
}


@PageTitle("Data")
@Route(value = "data", layout = UserDashboardView.class)
public class DataView extends VerticalLayout {

    public DataView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        var dataSection = createDataSection();
        add(dataSection);

        getStyle().set("background-color", "#f7fafc");
        getStyle().set("padding", "20px");
    }

    private Div createDataSection() {
        var dataDiv = new Div();
        dataDiv.getStyle().set("background-color", "#a0aec0");
        dataDiv.getStyle().set("border-radius", "8px");
        dataDiv.getStyle().set("padding", "40px");
        dataDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        dataDiv.setWidth("600px");

        var title = new Span("DATA KAMU");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("color", "white");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("display", "block");
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin-bottom", "30px");

        // Create form fields
        var formLayout = new VerticalLayout();
        formLayout.setSpacing(true);
        formLayout.setPadding(false);

        // Create text fields with edit buttons
        var fields = new String[]{"Nama", "Email", "Nomor Telepon", "Alamat"};

        for (String fieldName : fields) {
            var fieldLayout = new HorizontalLayout();
            fieldLayout.setWidthFull();
            fieldLayout.setAlignItems(Alignment.CENTER);

            var textField = new TextField();
            textField.setPlaceholder(fieldName);
            textField.setWidth("400px");
            textField.getStyle().set("background-color", "white");

            var editBtn = new Button("Edit");
            editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editBtn.getStyle().set("background-color", "#4a5568");
            editBtn.setWidth("80px");

            fieldLayout.add(textField, editBtn);
            formLayout.add(fieldLayout);
        }

        dataDiv.add(title, formLayout);
        return dataDiv;
    }
}


@PageTitle("Order JASTIPKUY")
@Route(value = "order", layout = UserDashboardView.class)
public class OrderView extends VerticalLayout {

    public OrderView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        var orderForm = createOrderForm();
        add(orderForm);

        getStyle().set("background-color", "#f7fafc");
        getStyle().set("padding", "20px");
    }

    private Div createOrderForm() {
        var formDiv = new Div();
        formDiv.getStyle().set("background-color", "white");
        formDiv.getStyle().set("border-radius", "8px");
        formDiv.getStyle().set("padding", "30px");
        formDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        formDiv.setWidth("700px");

        var title = new Span("FORM ORDER JASTIPKUY!");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("color", "#2d3748");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("display", "block");
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin-bottom", "30px");

        // Create form layout with two columns
        var formLayout = new HorizontalLayout();
        formLayout.setWidthFull();
        formLayout.setSpacing(true);

        // Left column
        var leftColumn = new VerticalLayout();
        leftColumn.setWidth("50%");
        leftColumn.setSpacing(true);
        leftColumn.setPadding(false);

        var namaBarang = new TextField("Nama Barang");
        namaBarang.setWidthFull();

        var lokasiJemput = new TextField("Lokasi Jemput");
        lokasiJemput.setWidthFull();

        var lokasiAntar = new TextField("Lokasi Antar");
        lokasiAntar.setWidthFull();

        var hargaBarang = new TextField("Harga Barang");
        hargaBarang.setPlaceholder("*berdasarkan uang");
        hargaBarang.setWidthFull();

        leftColumn.add(namaBarang, lokasiJemput, lokasiAntar, hargaBarang);

        // Right column
        var rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");
        rightColumn.setSpacing(true);
        rightColumn.setPadding(false);

        var tanggal = new TextField("Tanggal");
        tanggal.setWidthFull();

        var hargaJastiper = new TextField("Harga Jastiper");
        hargaJastiper.setPlaceholder("Rp. 2.000,00");
        hargaJastiper.setWidthFull();

        var totalBiaya = new TextField("Total Biaya");
        totalBiaya.setWidthFull();

        rightColumn.add(tanggal, hargaJastiper, totalBiaya);

        formLayout.add(leftColumn, rightColumn);

        // Buttons
        var buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);

        var cancelBtn = new Button("Cancel");
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelBtn.getStyle().set("color", "#4a5568");

        var submitBtn = new Button("Submit");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.getStyle().set("background-color", "#2c5282");

        buttonLayout.add(cancelBtn, submitBtn);

        formDiv.add(title, formLayout, buttonLayout);
        return formDiv;
    }
}

@PageTitle("Riwayat")
@Route(value = "riwayat", layout = UserDashboardView.class)
public class RiwayatView extends VerticalLayout {

    public RiwayatView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        var riwayatSection = createRiwayatSection();
        add(riwayatSection);

        getStyle().set("background-color", "#f7fafc");
        getStyle().set("padding", "20px");
    }

    private HorizontalLayout createRiwayatSection() {
        var mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setMaxWidth("1200px");
        mainLayout.setSpacing(true);

        // Left side - Form
        var formDiv = createFormSection();

        // Right side - Rating dialog placeholder
        var rightDiv = new Div();
        rightDiv.getStyle().set("background-color", "white");
        rightDiv.getStyle().set("border-radius", "8px");
        rightDiv.getStyle().set("padding", "20px");
        rightDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        rightDiv.setWidth("400px");
        rightDiv.setHeight("300px");

        mainLayout.add(formDiv, rightDiv);
        return mainLayout;
    }

    private Div createFormSection() {
        var formDiv = new Div();
        formDiv.getStyle().set("background-color", "white");
        formDiv.getStyle().set("border-radius", "8px");
        formDiv.getStyle().set("padding", "30px");
        formDiv.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        formDiv.setWidth("600px");

        var title = new Span("RIWAYAT PESANAN");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("color", "#2d3748");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("display", "block");
        title.getStyle().set("margin-bottom", "30px");

        var formLayout = new VerticalLayout();
        formLayout.setSpacing(true);
        formLayout.setPadding(false);

        // Form fields
        var fields = new String[]{
                "ID", "Tanggal", "Nama Barang", "Nama Customer",
                "Nama JASTIPER", "Lokasi Jemput", "Lokasi Antar",
                "Harga Barang", "Harga Jastiper", "Total Biaya"
        };

        for (String fieldName : fields) {
            var textField = new TextField(fieldName);
            textField.setWidthFull();
            textField.setReadOnly(true);
            formLayout.add(textField);
        }

        // Rating button
        var ratingBtn = new Button("Rating");
        ratingBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ratingBtn.getStyle().set("background-color", "#2c5282");
        ratingBtn.addClickListener(e -> openRatingDialog());

        formLayout.add(ratingBtn);
        formDiv.add(title, formLayout);
        return formDiv;
    }

    private void openRatingDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("300px");

        var dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        var title = new Span("RATING JASTIPER");
        title.getStyle().set("font-weight", "bold");
        title.getStyle().set("text-align", "center");

        var ratingField = new TextField("Rating (1-10)");
        ratingField.setPlaceholder("Masukkan rating");

        var commentField = new TextField("Komentar");
        commentField.setPlaceholder("Masukkan komentar");

        var buttonLayout = new HorizontalLayout();
        var cancelBtn = new Button("Cancel", e -> dialog.close());
        var submitBtn = new Button("Submit", e -> {
            // Handle rating submission
            dialog.close();
        });
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(cancelBtn, submitBtn);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        dialogLayout.add(title, ratingField, commentField, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
}

@PageTitle("Logout")
@Route(value = "logout", layout = UserDashboardView.class)
public class LogoutView extends VerticalLayout {

    public LogoutView() {
        setSpacing(false);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(new H1("Logout functionality would be implemented here"));
    }
}

