package iuh.fit.enumeration;

import java.util.Locale;

public enum AppLocale implements I18nEnum {
    EN("i18n_en.json", Locale.forLanguageTag("en")),
    VN("i18n_vn.json", Locale.forLanguageTag("vi"));

    private final String fileName;
    private final Locale locale;

    AppLocale(String fileName, Locale locale) {
        this.fileName = fileName;
        this.locale = locale;
    }

    public String getFileName() {
        return fileName;
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public String getI18nKey() {
        return this == EN ? "app.localeName.en" : "app.localeName.vn";
    }
}
