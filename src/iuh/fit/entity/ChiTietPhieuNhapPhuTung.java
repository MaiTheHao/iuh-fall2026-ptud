package iuh.fit.entity;

public class ChiTietPhieuNhapPhuTung {
    private String maPhieuNhap;
    private String maPhuTung;
    private int soLuong;
    private Double donGiaNhap;

    public ChiTietPhieuNhapPhuTung() {
    }

    public ChiTietPhieuNhapPhuTung(String maPhieuNhap, String maPhuTung, int soLuong, Double donGiaNhap) {
        this.maPhieuNhap = maPhieuNhap;
        this.maPhuTung = maPhuTung;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public String getMaPhuTung() {
        return maPhuTung;
    }

    public void setMaPhuTung(String maPhuTung) {
        this.maPhuTung = maPhuTung;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(Double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }
}
