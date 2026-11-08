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
import iuh.fit.enumeration.TrangThaiHoaDon;
import iuh.fit.util.TinhTienUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

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
    double mucGiamChot;
    if (hoaDon.getMucGiamApDung() != null) {
      mucGiamChot = TinhTienUtil.mucGiam(hoaDon.getMucGiamApDung());
    } else if (hoaDon.getVoucher() != null) {
      mucGiamChot = this.hoaDonService.tinhMucGiam(hoaDon);
    } else {
      mucGiamChot = 0.0;
    }
    double tongGoc = this.hoaDonService.tinhTongTienGoc(hoaDon);
    BigDecimal soTien = TinhTienUtil.thucThu(tongGoc, mucGiamChot);
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
    hoaDon.setTrangThaiHoaDon(
            TrangThaiHoaDon.DA_THANH_TOAN
    );
    hoaDon.setThoiGianThanhToan(now);
    hoaDon.setMucGiamApDung(mucGiamChot);
    return thanhToan;
  }
  
  public Optional<ThanhToan> timTheoHoaDon(String maHoaDon)
	        throws SQLException {
	    if (maHoaDon == null || maHoaDon.isBlank()) {
	        return Optional.empty();
	    }

	    return this.thanhToanDAO.findByMaHoaDon(maHoaDon);
	}
}
