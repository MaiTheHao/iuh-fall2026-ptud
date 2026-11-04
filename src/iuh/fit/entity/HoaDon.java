package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiHoaDon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHoaDon;
    private PhieuTiepNhan phieuTiepNhan;
    private Voucher voucher;
    private TrangThaiHoaDon trangThaiHoaDon;
    private String lyDoHuy;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime thoiGianThanhToan;
    private Double mucGiamApDung;
    private List<ChiTietHoaDon> danhSachChiTietHoaDon = new ArrayList<>();

    public HoaDon() {
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, PhieuTiepNhan phieuTiepNhan, Voucher voucher,
            TrangThaiHoaDon trangThaiHoaDon, String lyDoHuy, String ghiChu, LocalDateTime ngayTao,
            LocalDateTime thoiGianThanhToan) {
        this.maHoaDon = maHoaDon;
        this.phieuTiepNhan = phieuTiepNhan;
        this.voucher = voucher;
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

    public PhieuTiepNhan getPhieuTiepNhan() {
        return phieuTiepNhan;
    }

    public void setPhieuTiepNhan(PhieuTiepNhan phieuTiepNhan) {
        this.phieuTiepNhan = phieuTiepNhan;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
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

    public Double getMucGiamApDung() {
        return mucGiamApDung;
    }

    public void setMucGiamApDung(Double mucGiamApDung) {
        this.mucGiamApDung = mucGiamApDung;
    }

    public List<ChiTietHoaDon> getDanhSachChiTietHoaDon() {
        return danhSachChiTietHoaDon;
    }

    public void setDanhSachChiTietHoaDon(List<ChiTietHoaDon> danhSachChiTietHoaDon) {
        this.danhSachChiTietHoaDon = danhSachChiTietHoaDon;
    }
}
