package com.example.application.session;

import com.vaadin.flow.server.VaadinSession;

public class SessionUtils {

    private static final String KEY_USER_ID = "idUser";
    private static final String KEY_USER_ROLE = "userRole";

    // simpan id user
    public static void setUserId(int id) {
        VaadinSession.getCurrent().setAttribute(KEY_USER_ID, id);
    }

    // ambil id user
    public static Integer getUserId() {
        return (Integer) VaadinSession.getCurrent().getAttribute(KEY_USER_ID);
    }

    // simpan role user
    public static void setUserRole(String role) {
        VaadinSession.getCurrent().setAttribute(KEY_USER_ROLE, role);
    }

    // ambil role user
    public static String getUserRole() {
        return (String) VaadinSession.getCurrent().getAttribute(KEY_USER_ROLE);
    }

    // hapus session (logout)
    public static void clearSession() {
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }
}
