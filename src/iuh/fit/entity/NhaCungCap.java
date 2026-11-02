package iuh.fit.entity;

import iuh.fit.enumeration.TrangThaiNhaCungCap;

import java.time.LocalDateTime;

public class NhaCungCap {
  private String maNhaCungCap;
  private String tenNhaCungCap;
  private String sdt;
  private String email;
  private String diaChi;
  private TrangThaiNhaCungCap trangThai;
  private String ghiChu;
  private LocalDateTime ngayTao;

  public NhaCungCap() {}

  public NhaCungCap(String maNhaCungCap,
          String tenNhaCungCap,
          String sdt,
          String email,
          String diaChi,
          TrangThaiNhaCungCap trangThai,
          String ghiChu,
          LocalDateTime ngayTao) {
    this.maNhaCungCap = maNhaCungCap;
    this.tenNhaCungCap = tenNhaCungCap;
    this.sdt = sdt;
    this.email = email;
    this.diaChi = diaChi;
    this.trangThai = trangThai;
    this.ghiChu = ghiChu;
    this.ngayTao = ngayTao;
  }

  public String getMaNhaCungCap() {
    return maNhaCungCap;
  }

  public void setMaNhaCungCap(String maNhaCungCap) {
    this.maNhaCungCap = maNhaCungCap;
  }

  public String getTenNhaCungCap() {
    return tenNhaCungCap;
  }

  public void setTenNhaCungCap(String tenNhaCungCap) {
    this.tenNhaCungCap = tenNhaCungCap;
  }

  public String getSdt() {
    return sdt;
  }

  public void setSdt(String sdt) {
    this.sdt = sdt;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDiaChi() {
    return diaChi;
  }

  public void setDiaChi(String diaChi) {
    this.diaChi = diaChi;
  }

  public TrangThaiNhaCungCap getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(TrangThaiNhaCungCap trangThai) {
    this.trangThai = trangThai;
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
}
