package iuh.fit.enumeration;

public enum AppLocale {
    EN("i18n_en.json"),
    VN("i18n_vn.json");

    private final String fileName;

    AppLocale(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
