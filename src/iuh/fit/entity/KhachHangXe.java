package iuh.fit.entity;

import java.time.LocalDateTime;

public class KhachHangXe {
    private String maKhachHang;
    private String bienSo;
    private LocalDateTime ngayTao;

    public KhachHangXe() {
    }

    public KhachHangXe(String maKhachHang, String bienSo, LocalDateTime ngayTao) {
        this.maKhachHang = maKhachHang;
        this.bienSo = bienSo;
        this.ngayTao = ngayTao;
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

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}
