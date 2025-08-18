package com.example.application.view.auth;

import com.example.application.dao.UserDAO;
import com.example.application.model.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("login")
@PageTitle("Login | Jastip Kuy")
public class LoginView extends VerticalLayout {

    UserDAO userDAO = new UserDAO();
    TextField emailField;
    PasswordField passwordField;

    public LoginView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "#14274E");

        // Kontainer Utama
        HorizontalLayout mainContainer = new HorizontalLayout();
        mainContainer.setWidth("800px");
        mainContainer.setHeight("400px");
        mainContainer.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "5px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.2)");

        // Kiri (Gambar)
        Image img = new Image("images/photo1.png", "login");
        img.setWidth("100%");
        img.setHeight("100%");
        img.getStyle().set("object-fit", "cover");

        Div leftPane = new Div(img);
        leftPane.setWidth("50%");
        leftPane.setHeightFull();

        // Kanan (Form Login)
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout.setWidth("50%");
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        H1 title = new H1("LOGIN");
        title.getStyle()
                .set("font-family", "Poppins, sans-serif")
                .set("font-size", "24px")
                .set("color", "#000000")
                .set("margin", "0");

        emailField = new TextField();
        emailField.setPlaceholder("Email");
        emailField.setWidth("250px");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");
        passwordField.setWidth("250px");

        Button loginButton = new Button("SUBMIT", this::login);
        loginButton.setWidth("250px");
        loginButton.getStyle()
                .set("background-color", "#394867")
                .set("color", "#FFFFFF")
                .set("border-radius", "5px")
                .set("font-weight", "bold");

        Anchor registerLink = new Anchor("register", "Belum punya akun? klik disini");
        registerLink.getStyle()
                .set("font-size", "12px")
                .set("color", "#000000")
                .set("text-decoration", "underline")
                .set("cursor", "pointer");

        Anchor homeLink = new Anchor("", "Back to home");
        homeLink.getStyle()
                .set("font-size", "12px")
                .set("color", "#000000")
                .set("text-decoration", "underline")
                .set("cursor", "pointer");


        formLayout.add(title, emailField, passwordField, loginButton, registerLink, homeLink);


        mainContainer.add(leftPane, formLayout);
        add(mainContainer);
    }

    private void login(ClickEvent<Button> event) {
        try {
            int idUser = userDAO.login(emailField.getValue(), passwordField.getValue());
            if (idUser != 0) {
                // simpan id di session (nama key sama seperti MainLayout)
                VaadinSession.getCurrent().setAttribute("idUser", idUser);

                // ambil role untuk routing
                User u = userDAO.getUserById(idUser);
                if (u != null && "ADMIN".equalsIgnoreCase(u.getRole())) {
                    UI.getCurrent().navigate("admin");
                } else {
                    UI.getCurrent().navigate("user"); // <-- view user (kita buat di langkah 4)
                }
            } else {
                Notification.show("Email atau password salah!", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Terjadi kesalahan saat login!", 3000, Notification.Position.MIDDLE);
        }
    }
}


