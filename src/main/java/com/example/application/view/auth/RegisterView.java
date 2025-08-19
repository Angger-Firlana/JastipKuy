package com.example.application.view.auth;

import com.example.application.dao.UserDAO;
import com.example.application.model.User;
import com.example.application.session.SessionUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("register")
@PageTitle("Register | Jastip Kuy")
public class RegisterView extends VerticalLayout implements BeforeEnterObserver {

    UserDAO userDAO = new UserDAO();

    TextField nisnField;
    TextField nameField;
    TextField emailField;
    PasswordField passwordField;
    RadioButtonGroup<String> roleGroup = new RadioButtonGroup<>();


    public RegisterView() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "#14274E");

        // Kontainer Utama
        HorizontalLayout mainContainer = new HorizontalLayout();
        mainContainer.setWidth("800px");
        mainContainer.setHeight("500px");
        mainContainer.getStyle()
                .set("background-color", "#FFFFFF")
                .set("border-radius", "5px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.2)");

        // Kiri (Gambar)
        Image img = new Image("images/photo1.png", "register");
        img.setWidth("100%");
        img.setHeight("100%");
        img.getStyle().set("object-fit", "cover");

        VerticalLayout leftPane = new VerticalLayout(img);
        leftPane.setWidth("50%");
        leftPane.setHeightFull();
        leftPane.setPadding(false);
        leftPane.setMargin(false);

        // Kanan (Form Daftar)
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        formLayout.setWidth("50%");
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        H1 title = new H1("REGISTER");
        title.getStyle()
                .set("font-family", "Poppins, sans-serif")
                .set("font-size", "24px")
                .set("color", "#000000")
                .set("margin", "0");

        nisnField = new TextField();
        nisnField.setPlaceholder("NISN");
        nisnField.setWidth("250px");

        nameField = new TextField();
        nameField.setPlaceholder("Nama Lengkap");
        nameField.setWidth("250px");

        emailField = new TextField();
        emailField.setPlaceholder("Email");
        emailField.setWidth("250px");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");
        passwordField.setWidth("250px");

        // RadioButtonGroup untuk Role
        roleGroup.setItems("Jastiper", "Penitip");
        roleGroup.setLabel("Pilih Role");
        roleGroup.setValue("Penitip"); // Default value
        roleGroup.getStyle()
                .set("font-family", "Poppins, sans-serif")
                .set("color", "#000000")
                .set("width", "250px")
                .set("margin", "0.5rem 0");

        Button registerButton = new Button("DAFTAR", this::register);
        registerButton.setWidth("250px");
        registerButton.getStyle()
                .set("background-color", "#394867")
                .set("color", "#FFFFFF")
                .set("border-radius", "5px")
                .set("font-weight", "bold");

        Anchor loginLink = new Anchor("login", "Sudah punya akun? klik untuk login");
        loginLink.getStyle()
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

        formLayout.add(title, nisnField, nameField, emailField, passwordField, roleGroup, registerButton, loginLink, homeLink);

        mainContainer.add(leftPane, formLayout);
        add(mainContainer);
    }

    private void register(ClickEvent<Button> event) {
        try {
            String nisn = nisnField.getValue();
            String name = nameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();
             String role = roleGroup.getValue();

            if (nisn.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Notification.show("Semua field harus diisi!", 3000, Notification.Position.MIDDLE);
                return;
            }

            User user = new User();
            user.setNisn(nisn);
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
             user.setRole(role);

            userDAO.register(user);

            Notification.show("Registrasi berhasil!", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login");

        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Gagal registrasi. Coba lagi!", 3000, Notification.Position.MIDDLE);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Integer idUser = SessionUtils.getUserId();
        String role = SessionUtils.getUserRole();

        if (idUser != null && role != null) {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    event.forwardTo("admin");
                    break;
                case "PENITIP":
                    event.forwardTo("user");
                    break;
                case "JASTIPER":
                    event.forwardTo("jastiper");
                    break;
                default:
                    event.forwardTo(""); // fallback ke home
                    break;
            }
        }
    }
}