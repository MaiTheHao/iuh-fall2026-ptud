package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuTiepNhan {
    private String maPhieuTiepNhan;
    private LocalDateTime ngayTao;
    private String moTa;
    private TrangThaiPhieuTiepNhan trangThai;
    private String maNhanVien;
    private String maKhachHang;
    private String bienSo;
    private List<VanDeKyThuat> danhSachVanDeKyThuat = new ArrayList<>();

    public PhieuTiepNhan() {
    }

    public PhieuTiepNhan(String maPhieuTiepNhan, LocalDateTime ngayTao, String moTa, TrangThaiPhieuTiepNhan trangThai,
            String maNhanVien, String maKhachHang, String bienSo) {
        this.maPhieuTiepNhan = maPhieuTiepNhan;
        this.ngayTao = ngayTao;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.bienSo = bienSo;
    }

    public String getMaPhieuTiepNhan() {
        return maPhieuTiepNhan;
    }

    public void setMaPhieuTiepNhan(String maPhieuTiepNhan) {
        this.maPhieuTiepNhan = maPhieuTiepNhan;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public TrangThaiPhieuTiepNhan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhieuTiepNhan trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public List<VanDeKyThuat> getDanhSachVanDeKyThuat() {
        return danhSachVanDeKyThuat;
    }

    public void setDanhSachVanDeKyThuat(List<VanDeKyThuat> danhSachVanDeKyThuat) {
        this.danhSachVanDeKyThuat = danhSachVanDeKyThuat;
    }
}
