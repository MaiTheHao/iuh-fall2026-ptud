package iuh.fit.entity;

public class PhuTung {
    private String maPhuTung;
    private String tenPhuTung;
    private String moTa;
    private String anhUrl;
    private Double donGiaNhap;
    private int soLuongTon;

    public PhuTung() {
    }

    public PhuTung(String maPhuTung, String tenPhuTung, String moTa, String anhUrl, Double donGiaNhap, int soLuongTon) {
        this.maPhuTung = maPhuTung;
        this.tenPhuTung = tenPhuTung;
        this.moTa = moTa;
        this.anhUrl = anhUrl;
        this.donGiaNhap = donGiaNhap;
        this.soLuongTon = soLuongTon;
    }

    public String getMaPhuTung() {
        return maPhuTung;
    }

    public void setMaPhuTung(String maPhuTung) {
        this.maPhuTung = maPhuTung;
    }

    public String getTenPhuTung() {
        return tenPhuTung;
    }

    public void setTenPhuTung(String tenPhuTung) {
        this.tenPhuTung = tenPhuTung;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnhUrl() {
        return anhUrl;
    }

    public void setAnhUrl(String anhUrl) {
        this.anhUrl = anhUrl;
    }

    public Double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(Double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }
}
