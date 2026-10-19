package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiKhieuNai;

import java.time.LocalDateTime;

public class KhieuNai {
    private String maKhieuNai;
    private String maKhachHang;
    private String maPhieuTiepNhan;
    private String noiDung;
    private TrangThaiKhieuNai trangThai;
    private LocalDateTime ngayTao;

    public KhieuNai() {
    }

    public KhieuNai(String maKhieuNai, String maKhachHang, String maPhieuTiepNhan, String noiDung,
            TrangThaiKhieuNai trangThai, LocalDateTime ngayTao) {
        this.maKhieuNai = maKhieuNai;
        this.maKhachHang = maKhachHang;
        this.maPhieuTiepNhan = maPhieuTiepNhan;
        this.noiDung = noiDung;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public String getMaKhieuNai() {
        return maKhieuNai;
    }

    public void setMaKhieuNai(String maKhieuNai) {
        this.maKhieuNai = maKhieuNai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaPhieuTiepNhan() {
        return maPhieuTiepNhan;
    }

    public void setMaPhieuTiepNhan(String maPhieuTiepNhan) {
        this.maPhieuTiepNhan = maPhieuTiepNhan;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public TrangThaiKhieuNai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhieuNai trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}
