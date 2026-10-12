package iuh.fit.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KhachHang {
    private String maKhachHang;
    private String sdt;
    private String hoTen;
    private LocalDateTime ngayTao;
    private List<Xe> danhSachXe = new ArrayList<>();

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String sdt, String hoTen, LocalDateTime ngayTao) {
        this.maKhachHang = maKhachHang;
        this.sdt = sdt;
        this.hoTen = hoTen;
        this.ngayTao = ngayTao;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public List<Xe> getDanhSachXe() {
        return danhSachXe;
    }

    public void setDanhSachXe(List<Xe> danhSachXe) {
        this.danhSachXe = danhSachXe;
    }
}
