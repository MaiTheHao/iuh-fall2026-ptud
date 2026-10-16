package iuh.fit.enumeration;

public enum MenuGroup implements I18nEnum {
    TRANG_CHU("app.menuGroup.trangChu"),
    TO_CHUC("app.menuGroup.toChuc"),
    KE_HOACH("app.menuGroup.keHoach"),
    TIEP_NHAN("app.menuGroup.tiepNhan"),
    NHAP_PHU_TUNG("app.menuGroup.nhapPhuTung"),
    THANH_TOAN("app.menuGroup.thanhToan"),
    TONG_KET("app.menuGroup.tongKet"),
    CAI_DAT("app.menuGroup.caiDat");

    private final String titleKey;

    MenuGroup(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    @Override
    public String getI18nKey() {
        return this.titleKey;
    }
}
