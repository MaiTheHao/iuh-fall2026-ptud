package iuh.fit.entity;

import iuh.fit.enumeration.PhuongThucThanhToan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ThanhToan {
  private String maThanhToan;
  private HoaDon hoaDon;
  private PhuongThucThanhToan phuongThuc;
  private BigDecimal soTien;
  private LocalDateTime thoiGian;
  private NhanVien nhanVien;
  private String ghiChu;

  public ThanhToan() {}

  public ThanhToan(String maThanhToan) {
    this.maThanhToan = maThanhToan;
  }

  public ThanhToan(String maThanhToan,
          HoaDon hoaDon,
          PhuongThucThanhToan phuongThuc,
          BigDecimal soTien,
          LocalDateTime thoiGian,
          NhanVien nhanVien,
          String ghiChu) {
    this.maThanhToan = maThanhToan;
    this.hoaDon = hoaDon;
    this.phuongThuc = phuongThuc;
    this.soTien = soTien;
    this.thoiGian = thoiGian;
    this.nhanVien = nhanVien;
    this.ghiChu = ghiChu;
  }

  public String getMaThanhToan() {
    return maThanhToan;
  }

  public void setMaThanhToan(String maThanhToan) {
    this.maThanhToan = maThanhToan;
  }

  public HoaDon getHoaDon() {
    return hoaDon;
  }

  public void setHoaDon(HoaDon hoaDon) {
    this.hoaDon = hoaDon;
  }

  public PhuongThucThanhToan getPhuongThuc() {
    return phuongThuc;
  }

  public void setPhuongThuc(PhuongThucThanhToan phuongThuc) {
    this.phuongThuc = phuongThuc;
  }

  public BigDecimal getSoTien() {
    return soTien;
  }

  public void setSoTien(BigDecimal soTien) {
    this.soTien = soTien;
  }

  public LocalDateTime getThoiGian() {
    return thoiGian;
  }

  public void setThoiGian(LocalDateTime thoiGian) {
    this.thoiGian = thoiGian;
  }

  public NhanVien getNhanVien() {
    return nhanVien;
  }

  public void setNhanVien(NhanVien nhanVien) {
    this.nhanVien = nhanVien;
  }

  public String getGhiChu() {
    return ghiChu;
  }

  public void setGhiChu(String ghiChu) {
    this.ghiChu = ghiChu;
  }
}
