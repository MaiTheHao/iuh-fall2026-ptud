package iuh.fit.core.context;

import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.gui.page.AbstractPage;

public final class AppContext {
    private static final AppContext instance = new AppContext();
    private AppLocale locale = AppLocale.VN;
    private boolean darkMode = false;
    private NhanVien currentUser = null;
    private Class<? extends AbstractPage> currentPage;

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

    public boolean isDarkMode() {
        return this.darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public NhanVien getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(NhanVien currentUser) {
        this.currentUser = currentUser;
    }

    public Class<? extends AbstractPage> getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Class<? extends AbstractPage> currentPage) {
        this.currentPage = currentPage;
    }

    public void logout() {
        this.currentUser = null;
    }
}