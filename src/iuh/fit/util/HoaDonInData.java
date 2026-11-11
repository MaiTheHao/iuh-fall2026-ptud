package iuh.fit.util;

import iuh.fit.entity.ChiTietHoaDon;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.entity.SanPham;
import iuh.fit.entity.ThanhToan;
import iuh.fit.service.HoaDonService;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.NhanVienService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.ThanhToanService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Dữ liệu đã chuẩn hoá để dựng ảnh hoá đơn, gồm thông tin chung và các dòng in.
 */
public record HoaDonInData(
        HoaDon hoaDon,
        PhieuTiepNhan phieuTiepNhan,
        List<DongIn> danhSachDong,
        double tongGoc,
        double mucGiam,
        BigDecimal soTienGiam,
        BigDecimal thucThu,
        Optional<ThanhToan> thanhToan) {

  private static final String ERROR_PHIEU_NOT_FOUND = "app.phieuTiepNhan.khongTonTai";

  private static final HoaDonService hoaDonService = new HoaDonService();

  private static final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();

  private static final KhachHangService khachHangService = new KhachHangService();

  private static final NhanVienService nhanVienService = new NhanVienService();

  private static final ThanhToanService thanhToanService = new ThanhToanService();

  public HoaDonInData {
    danhSachDong = danhSachDong == null
            ? List.of()
            : List.copyOf(danhSachDong);

    thanhToan = thanhToan == null
            ? Optional.empty()
            : thanhToan;
  }

  public record DongIn(
          String ten,
          String loai,
          int soLuong,
          double donGia,
          double thanhTien) {}

  public static HoaDonInData tao(String maHoaDon) throws SQLException {
    HoaDon hoaDon = hoaDonService.getById(maHoaDon);
    PhieuTiepNhan phieu = hoaDon.getPhieuTiepNhan();

    if (phieu == null || phieu.getMaPhieuTiepNhan() == null) {
      throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
    }

    phieu = phieuTiepNhanService.getById(phieu.getMaPhieuTiepNhan());

    if (phieu == null) {
      throw new IllegalArgumentException(ERROR_PHIEU_NOT_FOUND);
    }

    // Entity gắn trong hoá đơn chỉ có khoá ngoại nên phải nạp lại để lấy tên hiển thị.
    if (phieu.getKhachHang() != null
            && phieu.getKhachHang().getMaKhachHang() != null) {

      KhachHang khachHang = khachHangService.getById(
              phieu.getKhachHang().getMaKhachHang());

      phieu.setKhachHang(khachHang);
    }

    if (phieu.getNhanVien() != null
            && phieu.getNhanVien().getMaNhanVien() != null) {

      NhanVien nhanVien = nhanVienService.getById(
              phieu.getNhanVien().getMaNhanVien());

      phieu.setNhanVien(nhanVien);
    }

    List<DongIn> danhSachDong = hoaDon.getDanhSachChiTietHoaDon()
            .stream()
            .map(HoaDonInData::taoDongIn)
            .toList();

    double tongGoc = TinhTienUtil.tongGoc(
            hoaDon.getDanhSachChiTietHoaDon());

    double mucGiam = TinhTienUtil.mucGiam(
            hoaDonService.tinhMucGiam(hoaDon));

    BigDecimal thucThu = TinhTienUtil.thucThu(
            tongGoc,
            mucGiam);

    BigDecimal soTienGiam = BigDecimal.valueOf(tongGoc)
            .setScale(2, RoundingMode.HALF_UP)
            .subtract(thucThu)
            .max(BigDecimal.ZERO);

    Optional<ThanhToan> thanhToan = thanhToanService.timTheoHoaDon(maHoaDon);

    return new HoaDonInData(
            hoaDon,
            phieu,
            danhSachDong,
            tongGoc,
            mucGiam,
            soTienGiam,
            thucThu,
            thanhToan);
  }

  private static DongIn taoDongIn(ChiTietHoaDon chiTiet) {
    SanPham sanPham = chiTiet.getSanPham();

    String maSanPham = sanPham == null ? null : sanPham.getMaSanPham();

    String tenSanPham = sanPham == null ? null : sanPham.getTenSanPham();

    if (tenSanPham == null || tenSanPham.isBlank()) {
      // Hiển thị mã thay cho tên để dòng in không bị trống.
      tenSanPham = maSanPham;
    }

    String loaiSanPham = sanPham == null
            ? ""
            : sanPham.getLoaiSanPham().getI18NBusinessName();

    double donGia = chiTiet.getDonGia() == null
            ? 0.0
            : chiTiet.getDonGia();

    double thanhTien = chiTiet.getSoLuong() * donGia;

    return new DongIn(
            tenSanPham,
            loaiSanPham,
            chiTiet.getSoLuong(),
            donGia,
            thanhTien);
  }
}
