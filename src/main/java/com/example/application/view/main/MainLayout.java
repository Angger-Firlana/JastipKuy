package com.example.application.view.main;

import com.example.application.dao.UserDAO;
import com.example.application.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.awt.*;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.example.application.session.SessionUtils;

@Route("dashboard")
public class MainLayout extends AppLayout {

    private User userLog = null;
    private UserDAO userDAO = new UserDAO();
    private int idUser = 0;

    public MainLayout() {
        getDataUser();

        if (idUser == 0) {
            UI.getCurrent().navigate("login");
            return;
        }

        createHeader();
        createDrawer();

        setContent(new VerticalLayout());
    }

    private void getDataUser() {
        try {
            if (VaadinSession.getCurrent() != null &&
                    VaadinSession.getCurrent().getAttribute("idUser") != null) {
                idUser = (Integer) VaadinSession.getCurrent().getAttribute("idUser");
                User user = userDAO.getUserById(idUser);
                if (user != null) {
                    userLog = user;
                }
            } else {
                UI.getCurrent().navigate("login");
            }
        } catch (Exception e) {
            UI.getCurrent().navigate("login");
        }
    }

    private void createHeader() {
        H1 appName = new H1("JastipKuy");
        appName.addClassName("app-title");
        appName.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0")
                .set("color", "var(--lumo-primary-text-color)");

        Avatar avatar = new Avatar(userLog != null ? userLog.getName() : "Guest");
        avatar.setColorIndex(1);

        MenuBar userMenu = new MenuBar();

        MenuItem userItem = userMenu.addItem(avatar);
        SubMenu subMenu = userItem.getSubMenu();

        subMenu.addItem("Profil", e -> UI.getCurrent().navigate("profile"));

        if (userLog != null && "ADMIN".equals(userLog.getRole())) {
            subMenu.addItem("Admin Dashboard", e -> UI.getCurrent().navigate("admin"));
        }

        subMenu.addItem("Logout", e -> logout());

        HorizontalLayout header = new HorizontalLayout(appName, userMenu);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();
        header.setPadding(true);
        header.addClassName("app-header");
        header.getStyle()
                .set("background-color", "var(--lumo-base-color)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        addToNavbar(header);
    }

    private void createDrawer() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setWidthFull();

        Tab dashboardTab = createTab("Dashboard", VaadinIcon.HOME);
        Tab ordersTab = createTab("Order Saya", VaadinIcon.CART);
        Tab productsTab = createTab("Produk", VaadinIcon.PACKAGE);
        Tab historyTab = createTab("Riwayat", VaadinIcon.TIME_BACKWARD);
        Tab cameraTab = createTab("Kamera", VaadinIcon.CAMERA);

        if (userLog != null && "ADMIN".equals(userLog.getRole())) {
            Tab adminTab = createTab("Admin", VaadinIcon.COG);
            tabs.add(dashboardTab, ordersTab, productsTab, historyTab, adminTab);
        } else {
            tabs.add(dashboardTab, ordersTab, productsTab, historyTab);
        }

        tabs.addSelectedChangeListener(event -> {
            Tab selected = event.getSelectedTab();
            if (selected != null) {
                String label = getTabLabel(selected);
                navigateTo(label);
            }
        });

        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        drawerContent.setSizeFull();

        VerticalLayout navSection = new VerticalLayout(tabs);
        navSection.setPadding(true);
        navSection.setSpacing(false);
        navSection.setFlexGrow(1, tabs);

        VerticalLayout drawerFooter = new VerticalLayout();
        drawerFooter.setPadding(true);
        drawerFooter.setSpacing(false);
        drawerFooter.addClassName("drawer-footer");
        drawerFooter.getStyle()
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("background-color", "var(--lumo-contrast-5pct)");

        if (userLog != null) {
            Span userName = new Span(userLog.getName());
            userName.addClassName("user-name");
            userName.getStyle()
                    .set("font-weight", "600")
                    .set("font-size", "var(--lumo-font-size-s)")
                    .set("color", "var(--lumo-primary-text-color)");

            Span userRole = new Span(userLog.getRole());
            userRole.addClassName("user-role");
            userRole.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("text-transform", "capitalize");

            drawerFooter.add(userName, userRole);
        }

        drawerContent.add(navSection, drawerFooter);
        drawerContent.setFlexGrow(1, navSection);

        addToDrawer(drawerContent);
    }

    private Tab createTab(String label, VaadinIcon vaadinIcon) {
        Span iconSpan = new Span(vaadinIcon.create());
        iconSpan.addClassName("tab-icon");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("tab-label");
        labelSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");

        HorizontalLayout tabContent = new HorizontalLayout(iconSpan, labelSpan);
        tabContent.setAlignItems(FlexComponent.Alignment.CENTER);
        tabContent.setSpacing(true);
        tabContent.setPadding(false);
        tabContent.addClassName("tab-content");

        Tab tab = new Tab(tabContent);
        tab.addClassName("navigation-tab");

        tab.getElement().setAttribute("data-label", label);

        return tab;
    }

    private String getTabLabel(Tab tab) {
        return tab.getElement().getAttribute("data-label");
    }

    private void navigateTo(String label) {
        switch (label) {
            case "Dashboard":
                UI.getCurrent().navigate("");
                break;
            case "Order Saya":
                UI.getCurrent().navigate("orders");
                break;
            case "Produk":
                UI.getCurrent().navigate("products");
                break;
            case "Riwayat":
                UI.getCurrent().navigate("history");
                break;
            case "Admin":
                UI.getCurrent().navigate("admin");
                break;
            case "Camera":
                UI.getCurrent().navigate("kamera");
                break;
            default:
                UI.getCurrent().navigate("");
                break;
        }
    }

    private void logout() {
        try {
            SessionUtils.clearSession();
            UI.getCurrent().navigate("login");
        } catch (Exception e) {
            // Fallback logout - clear session manually and navigate
            try {
                VaadinSession currentSession = VaadinSession.getCurrent();
                if (currentSession != null) {
                    currentSession.setAttribute("idUser", null);
                    currentSession.setAttribute("userRole", null);
                    if (currentSession.getSession() != null) {
                        currentSession.getSession().invalidate();
                    }
                }
            } catch (Exception ignored) {}
            UI.getCurrent().navigate("login");
        }
    }

    public User getCurrentUser() {
        return userLog;
    }

    public boolean isAdmin() {
        return userLog != null && "ADMIN".equals(userLog.getRole());
    }
}
