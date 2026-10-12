package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiHoaDon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHoaDon;
    private String maPhieuTiepNhan;
    private String maVoucher;
    private TrangThaiHoaDon trangThaiHoaDon;
    private String lyDoHuy;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime thoiGianThanhToan;
    private List<ChiTietHoaDon> danhSachChiTietHoaDon = new ArrayList<>();

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, String maPhieuTiepNhan, String maVoucher, TrangThaiHoaDon trangThaiHoaDon,
            String lyDoHuy, String ghiChu, LocalDateTime ngayTao, LocalDateTime thoiGianThanhToan) {
        this.maHoaDon = maHoaDon;
        this.maPhieuTiepNhan = maPhieuTiepNhan;
        this.maVoucher = maVoucher;
        this.trangThaiHoaDon = trangThaiHoaDon;
        this.lyDoHuy = lyDoHuy;
        this.ghiChu = ghiChu;
        this.ngayTao = ngayTao;
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhieuTiepNhan() {
        return maPhieuTiepNhan;
    }

    public void setMaPhieuTiepNhan(String maPhieuTiepNhan) {
        this.maPhieuTiepNhan = maPhieuTiepNhan;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public TrangThaiHoaDon getTrangThaiHoaDon() {
        return trangThaiHoaDon;
    }

    public void setTrangThaiHoaDon(TrangThaiHoaDon trangThaiHoaDon) {
        this.trangThaiHoaDon = trangThaiHoaDon;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public List<ChiTietHoaDon> getDanhSachChiTietHoaDon() {
        return danhSachChiTietHoaDon;
    }

    public void setDanhSachChiTietHoaDon(List<ChiTietHoaDon> danhSachChiTietHoaDon) {
        this.danhSachChiTietHoaDon = danhSachChiTietHoaDon;
    }
}
