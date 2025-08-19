package com.example.application.session;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class ValidationUtils implements BeforeEnterObserver {
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
