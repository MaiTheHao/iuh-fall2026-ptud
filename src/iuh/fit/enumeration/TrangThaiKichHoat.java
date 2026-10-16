package iuh.fit.enumeration;

public enum TrangThaiKichHoat implements I18nEnum {
    DANG_KICH_HOAT(true),
    NGUNG_KICH_HOAT(false);

    private final boolean giaTri;

    TrangThaiKichHoat(boolean giaTri) {
        this.giaTri = giaTri;
    }

    public boolean isGiaTri() {
        return this.giaTri;
    }

    public static TrangThaiKichHoat tu(boolean giaTri) {
        return giaTri ? DANG_KICH_HOAT : NGUNG_KICH_HOAT;
    }
}
