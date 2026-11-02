package iuh.fit.entity;

public class HangXe {
    private String maHangXe;
    private String ten;

    public HangXe() {
    }

    public HangXe(String maHangXe, String ten) {
        this.maHangXe = maHangXe;
        this.ten = ten;
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

    @Override
    public String toString() {
        return this.ten;
    }
}
