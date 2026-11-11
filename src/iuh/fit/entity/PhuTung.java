package iuh.fit.entity;

import iuh.fit.enumeration.LoaiSanPham;

public class PhuTung extends SanPham {
    private Double donGiaNhap;
    private int soLuongTon;
    private String maNhaCungCap;

    public PhuTung() {
    }

    public PhuTung(String maSanPham) {
        super(maSanPham, null, null, null);
    }

    public PhuTung(String maSanPham, String tenSanPham, String moTa, String anhUrl, Double donGiaNhap, int soLuongTon, String maNhaCungCap) {
        super(maSanPham, tenSanPham, moTa, anhUrl);
        this.donGiaNhap = donGiaNhap;
        this.soLuongTon = soLuongTon;
        this.maNhaCungCap = maNhaCungCap;
    }

    @Override
    public LoaiSanPham getLoaiSanPham() {
        return LoaiSanPham.PHU_TUNG;
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

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }
}
