package iuh.fit.entity;

import iuh.fit.enumeration.LoaiSanPham;

public class ChiTietHoaDon {
    private String maHoaDon;
    private LoaiSanPham loaiSanPham;
    private String maSanPham;
    private int soLuong;
    private Double donGia;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHoaDon, LoaiSanPham loaiSanPham, String maSanPham, int soLuong, Double donGia) {
        this.maHoaDon = maHoaDon;
        this.loaiSanPham = loaiSanPham;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
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
