package iuh.fit.entity;

public class DongXe {
    private String maDongXe;
    private String maHangXe;
    private String ten;
    private String tenHangXe;

    public DongXe() {
    }

    public DongXe(String maDongXe, String maHangXe, String ten, String tenHangXe) {
        this.maDongXe = maDongXe;
        this.maHangXe = maHangXe;
        this.ten = ten;
        this.tenHangXe = tenHangXe;
    }

    public String getMaDongXe() {
        return maDongXe;
    }

    public void setMaDongXe(String maDongXe) {
        this.maDongXe = maDongXe;
    }

    public String getMaHangXe() {
        return maHangXe;
    }

    public void setMaHangXe(String maHangXe) {
        this.maHangXe = maHangXe;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTenHangXe() {
        return tenHangXe;
    }

    public void setTenHangXe(String tenHangXe) {
        this.tenHangXe = tenHangXe;
    }

    @Override
    public String toString() {
        return this.ten;
    }
}
