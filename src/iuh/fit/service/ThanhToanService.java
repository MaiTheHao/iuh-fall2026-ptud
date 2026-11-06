package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.HoaDonDAO;
import iuh.fit.dao.ThanhToanDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.ThanhToan;
import iuh.fit.enumeration.LoaiMa;
import iuh.fit.enumeration.PhuongThucThanhToan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ThanhToanService {

  private static final String ERROR_TRANG_THAI = "app.hoaDon.loiTrangThai";

  private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
  private final ThanhToanDAO thanhToanDAO = new ThanhToanDAO();
  private final HoaDonService hoaDonService = new HoaDonService();
  private final MaService maService = new MaService();

  public ThanhToan thanhToan(String maHoaDon, PhuongThucThanhToan phuongThuc, String ghiChu)
          throws SQLException {
    if (maHoaDon == null || maHoaDon.isBlank()) {
      throw new IllegalArgumentException(ERROR_TRANG_THAI);
    }
    if (phuongThuc == null) {
      throw new IllegalArgumentException(ERROR_TRANG_THAI);
    }
    HoaDon hoaDon = this.hoaDonService.getById(maHoaDon);
    if (hoaDon.getTrangThaiHoaDon() != iuh.fit.enumeration.TrangThaiHoaDon.CHO_THANH_TOAN) {
      throw new IllegalStateException(ERROR_TRANG_THAI);
    }

    LocalDateTime now = LocalDateTime.now();
    Double mucGiamChot = hoaDon.getMucGiamApDung();
    if (mucGiamChot == null && hoaDon.getVoucher() != null) {
      mucGiamChot = this.hoaDonService.tinhMucGiam(hoaDon);
    }
    BigDecimal soTien = BigDecimal.valueOf(this.hoaDonService.tinhTongTien(hoaDon));
    NhanVien nhanVien = AppContext.getInstance().getCurrentUser();
    String ghiChuChuan = ghiChu == null || ghiChu.isBlank() ? null : ghiChu.trim();

    ThanhToan thanhToan = new ThanhToan();
    thanhToan.setHoaDon(hoaDon);
    thanhToan.setPhuongThuc(phuongThuc);
    thanhToan.setSoTien(soTien);
    thanhToan.setThoiGian(now);
    thanhToan.setNhanVien(nhanVien);
    thanhToan.setGhiChu(ghiChuChuan);

    try (TransactionScope scope = TransactionScope.begin()) {
      if (!this.hoaDonDAO.capNhatThanhToan(scope, maHoaDon, now, mucGiamChot)) {
        throw new IllegalStateException(ERROR_TRANG_THAI);
      }
      thanhToan.setMaThanhToan(this.maService.genMa(scope, LoaiMa.TT));
      this.thanhToanDAO.insert(scope, thanhToan);
      scope.commit();
    }

    return thanhToan;
  }
}
