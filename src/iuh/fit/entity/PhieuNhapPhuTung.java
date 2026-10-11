package iuh.fit.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapPhuTung {
    private String maPhieuNhap;
    private LocalDateTime ngayTao;
    private String maNhanVien;
    private String moTa;
    private List<ChiTietPhieuNhapPhuTung> danhSachChiTietPhieuNhap = new ArrayList<>();

    public PhieuNhapPhuTung() {
    }

    public PhieuNhapPhuTung(String maPhieuNhap, LocalDateTime ngayTao, String maNhanVien, String moTa) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayTao = ngayTao;
        this.maNhanVien = maNhanVien;
        this.moTa = moTa;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public List<ChiTietPhieuNhapPhuTung> getDanhSachChiTietPhieuNhap() {
        return danhSachChiTietPhieuNhap;
    }

    public void setDanhSachChiTietPhieuNhap(List<ChiTietPhieuNhapPhuTung> danhSachChiTietPhieuNhap) {
        this.danhSachChiTietPhieuNhap = danhSachChiTietPhieuNhap;
    }
}
