package iuh.fit.gui.component;

import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.service.DichVuService;
import iuh.fit.util.I18nReaderUtil;

import java.awt.Window;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Dialog chọn dịch vụ có tìm kiếm theo mã/tên/mô tả.
 */
public final class ChonDichVuDialog extends ChonSanPhamDialog<DichVu> {

  private static final long serialVersionUID = 1L;

  private final DichVuService dichVuService;
  private final DanhSachDichVuCriteria boLocGoc;

  public ChonDichVuDialog(Window owner, DichVuService dichVuService,
          DanhSachDichVuCriteria boLocGoc) {
    super(owner, "app.hoaDon.chonDichVu");

    this.dichVuService = Objects.requireNonNull(dichVuService);
    this.boLocGoc = boLocGoc == null ? DanhSachDichVuCriteria.macDinh() : boLocGoc;
  }

  @Override
  protected KetQuaTai<DichVu> taiDuLieu(String tuKhoa) throws SQLException {
    PaginatedResult<DichVu> ketQua = this.dichVuService.search(this.taoCriteria(tuKhoa));
    return new KetQuaTai<>(ketQua.items(), tongSoKetQuaCanBao(ketQua));
  }

  private DanhSachDichVuCriteria taoCriteria(String tuKhoa) {
    return new DanhSachDichVuCriteria(
            this.boLocGoc.maDichVu(),
            this.boLocGoc.tenDichVu(),
            tuKhoa,
            this.boLocGoc.donGiaTu(),
            this.boLocGoc.donGiaDen(),
            1,
            PAGE_SIZE_TAI_TOAN_BO);
  }

  @Override
  protected String[] tenCot() {
    return new String[]{
        I18nReaderUtil.get("app.dichVu.ma"),
        I18nReaderUtil.get("app.dichVu.ten"),
        I18nReaderUtil.get("app.dichVu.moTa"),
        I18nReaderUtil.get("app.dichVu.donGia")
    };
  }

  @Override
  protected Object[] giaTriDong(DichVu dichVu) {
    return new Object[]{
        dichVu.getMaDichVu(),
        dichVu.getTenDichVu(),
        dichVu.getMoTa(),
        dichVu.getDonGia()
    };
  }

  @Override
  protected String placeholderTimKiem() {
    return "app.hoaDon.timDichVu";
  }

  @Override
  protected String keyKhongTimThay() {
    return "app.dichVu.khongCoDichVuPhuHop";
  }

  @Override
  protected String keyChuaChon() {
    return "app.hoaDon.loiDichVu";
  }
}
