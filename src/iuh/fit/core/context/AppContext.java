package iuh.fit.core.context;

import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.AppLocale;

public final class AppContext {
    private static final AppContext instance = new AppContext();
    private AppLocale locale = AppLocale.VN;
    private NhanVien currentUser = null;

    private AppContext() {
    }

    public static AppContext getInstance() {
        return instance;
    }

    public AppLocale getLocale() {
        return this.locale;
    }

    public void setLocale(AppLocale locale) {
        this.locale = locale;
    }

    public NhanVien getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(NhanVien currentUser) {
        this.currentUser = currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }
}
