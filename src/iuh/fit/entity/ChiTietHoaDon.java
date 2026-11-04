package iuh.fit.entity;

import iuh.fit.enumeration.LoaiSanPham;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private LoaiSanPham loaiSanPham;
    private String maSanPham;
    private int soLuong;
    private Double donGia;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, LoaiSanPham loaiSanPham, String maSanPham, int soLuong, Double donGia) {
        this.hoaDon = hoaDon;
        this.loaiSanPham = loaiSanPham;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }
}
