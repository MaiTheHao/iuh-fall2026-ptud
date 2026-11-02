package iuh.fit.entity;

public class DoiXe {
    private String maDoiXe;
    private String maDongXe;
    private int namSX;
    // chi de hien thi
    private String maHangXe;
    // chi de hien thi
    private String tenHangXe;
    // chi de hien thi
    private String tenDongXe;

    public DoiXe() {
    }

    public DoiXe(String maDoiXe, String maDongXe, int namSX, String maHangXe, String tenHangXe, String tenDongXe) {
        this.maDoiXe = maDoiXe;
        this.maDongXe = maDongXe;
        this.namSX = namSX;
        this.maHangXe = maHangXe;
        this.tenHangXe = tenHangXe;
        this.tenDongXe = tenDongXe;
    }

    public String getMaDoiXe() {
        return maDoiXe;
    }

    public void setMaDoiXe(String maDoiXe) {
        this.maDoiXe = maDoiXe;
    }

    public String getMaDongXe() {
        return maDongXe;
    }

    public void setMaDongXe(String maDongXe) {
        this.maDongXe = maDongXe;
    }

    public int getNamSX() {
        return namSX;
    }

    public void setNamSX(int namSX) {
        this.namSX = namSX;
    }

    @Override
    public String toString() {
        String ten = this.tenDongXe == null ? "" : this.tenDongXe;
        return ten.isEmpty() ? this.maDoiXe : ten + " - " + this.namSX;
    }

    public String getMaHangXe() {
        return maHangXe;
    }

    public void setMaHangXe(String maHangXe) {
        this.maHangXe = maHangXe;
    }

    public String getTenHangXe() {
        return tenHangXe;
    }

    public void setTenHangXe(String tenHangXe) {
        this.tenHangXe = tenHangXe;
    }

    public String getTenDongXe() {
        return tenDongXe;
    }

    public void setTenDongXe(String tenDongXe) {
        this.tenDongXe = tenDongXe;
    }
}
