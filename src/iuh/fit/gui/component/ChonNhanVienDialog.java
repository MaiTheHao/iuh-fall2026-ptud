package iuh.fit.gui.component;

import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.service.PhanCongService;
import iuh.fit.util.I18nReaderUtil;

import java.awt.Window;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Dialog chọn một nhân viên đủ điều kiện phân ca (không kèm số lượng).
 */
public final class ChonNhanVienDialog extends ChonSanPhamDialog<NhanVien> {

  private static final long serialVersionUID = 1L;

  private final PhanCongService phanCongService;
  private final LocalDate ngay;
  private final CaLamViec ca;

  public ChonNhanVienDialog(Window owner, PhanCongService phanCongService, LocalDate ngay, CaLamViec ca) {
    super(owner, "app.schedule.chonNhanVien", false);

    this.phanCongService = Objects.requireNonNull(phanCongService);
    this.ngay = ngay;
    this.ca = ca;
  }

  @Override
  protected KetQuaTai<NhanVien> taiDuLieu(String tuKhoa) throws SQLException {
    List<NhanVien> danhSach = this.phanCongService.timNhanVienCoThePhanCong(tuKhoa, this.ngay, this.ca);

    return new KetQuaTai<>(danhSach, -1);
  }

  @Override
  protected String[] tenCot() {
    return new String[]{
        I18nReaderUtil.get("app.nhanVien.maNhanVien"),
        I18nReaderUtil.get("app.nhanVien.hoTen"),
        I18nReaderUtil.get("app.nhanVien.soDienThoai"),
        I18nReaderUtil.get("app.nhanVien.vaiTro")
    };
  }

  @Override
  protected Object[] giaTriDong(NhanVien nhanVien) {
    return new Object[]{
        nhanVien.getMaNhanVien(),
        nhanVien.getHoTen(),
        nhanVien.getSoDienThoai(),
        nhanVien.getVaiTro() == null ? "" : nhanVien.getVaiTro().getI18NBusinessName()
    };
  }

  @Override
  protected String placeholderTimKiem() {
    return "app.schedule.timNhanVien";
  }

  @Override
  protected String keyKhongTimThay() {
    return "app.schedule.khongCoNhanVienPhuHop";
  }

  @Override
  protected String keyChuaChon() {
    return "app.schedule.loiChuaChonNhanVien";
  }
}
