package iuh.fit.entity;

import java.time.LocalDateTime;

public class Voucher {
    private String maVoucher;
    private String tenChuongTrinh;
    private String moTa;
    private Double mucGiam;
    private LocalDateTime thoiHanApDung;
    private boolean trangThaiKichHoat;

    public Voucher() {
    }

    public Voucher(String maVoucher, String tenChuongTrinh, String moTa, Double mucGiam, LocalDateTime thoiHanApDung,
            boolean trangThaiKichHoat) {
        this.maVoucher = maVoucher;
        this.tenChuongTrinh = tenChuongTrinh;
        this.moTa = moTa;
        this.mucGiam = mucGiam;
        this.thoiHanApDung = thoiHanApDung;
        this.trangThaiKichHoat = trangThaiKichHoat;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }

    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Double getMucGiam() {
        return mucGiam;
    }

    public void setMucGiam(Double mucGiam) {
        this.mucGiam = mucGiam;
    }

    public LocalDateTime getThoiHanApDung() {
        return thoiHanApDung;
    }

    public void setThoiHanApDung(LocalDateTime thoiHanApDung) {
        this.thoiHanApDung = thoiHanApDung;
    }

    public boolean isTrangThaiKichHoat() {
        return trangThaiKichHoat;
    }

    public void setTrangThaiKichHoat(boolean trangThaiKichHoat) {
        this.trangThaiKichHoat = trangThaiKichHoat;
    }
}
