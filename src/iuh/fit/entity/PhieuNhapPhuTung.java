package iuh.fit.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapPhuTung {
    private String maPhieuNhap;
    private LocalDateTime ngayTao;
    private NhanVien nhanVien;
    private String moTa;
    private List<ChiTietPhieuNhapPhuTung> danhSachChiTietPhieuNhap = new ArrayList<>();

    public PhieuNhapPhuTung() {
    }

    public PhieuNhapPhuTung(String maPhieuNhap, LocalDateTime ngayTao, NhanVien nhanVien, String moTa) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayTao = ngayTao;
        this.nhanVien = nhanVien;
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

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
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
