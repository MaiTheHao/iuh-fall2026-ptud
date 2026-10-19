package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

import java.time.LocalDateTime;

public class VanDeKyThuat {
    private String maVanDe;
    private String maPhieuTiepNhan;
    private String tieuDe;
    private String moTa;
    private int mucDoUuTien;
    private String giaiPhap;
    private TrangThaiVanDeKyThuat khachHangDongY;
    private LocalDateTime ngayTao;

    public VanDeKyThuat() {
    }

    public VanDeKyThuat(String maVanDe, String maPhieuTiepNhan, String tieuDe, String moTa, int mucDoUuTien,
            String giaiPhap, TrangThaiVanDeKyThuat khachHangDongY, LocalDateTime ngayTao) {
        this.maVanDe = maVanDe;
        this.maPhieuTiepNhan = maPhieuTiepNhan;
        this.tieuDe = tieuDe;
        this.moTa = moTa;
        this.mucDoUuTien = mucDoUuTien;
        this.giaiPhap = giaiPhap;
        this.khachHangDongY = khachHangDongY;
        this.ngayTao = ngayTao;
    }

    public String getMaVanDe() {
        return maVanDe;
    }

    public void setMaVanDe(String maVanDe) {
        this.maVanDe = maVanDe;
    }

    public String getMaPhieuTiepNhan() {
        return maPhieuTiepNhan;
    }

    public void setMaPhieuTiepNhan(String maPhieuTiepNhan) {
        this.maPhieuTiepNhan = maPhieuTiepNhan;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getMucDoUuTien() {
        return mucDoUuTien;
    }

    public void setMucDoUuTien(int mucDoUuTien) {
        this.mucDoUuTien = mucDoUuTien;
    }

    public String getGiaiPhap() {
        return giaiPhap;
    }

    public void setGiaiPhap(String giaiPhap) {
        this.giaiPhap = giaiPhap;
    }

    public TrangThaiVanDeKyThuat getKhachHangDongY() {
        return khachHangDongY;
    }

    public void setKhachHangDongY(TrangThaiVanDeKyThuat khachHangDongY) {
        this.khachHangDongY = khachHangDongY;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}
