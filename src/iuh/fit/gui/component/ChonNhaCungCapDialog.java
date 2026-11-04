package iuh.fit.gui.component;

import iuh.fit.dto.DanhSachNhaCungCapCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.service.NhaCungCapService;
import iuh.fit.util.I18nReaderUtil;

import java.awt.Window;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Dialog chọn nhà cung cấp cho phụ tùng.
 */
public final class ChonNhaCungCapDialog extends ChonSanPhamDialog<NhaCungCap> {

  

  private final NhaCungCapService service;
  private final DanhSachNhaCungCapCriteria boLocGoc;

  public ChonNhaCungCapDialog(Window owner, NhaCungCapService service, DanhSachNhaCungCapCriteria boLocGoc) {
    super(owner, "app.nhaCungCap.chonNhaCungCap", false);
    this.service = Objects.requireNonNull(service);
    this.boLocGoc = boLocGoc == null ? DanhSachNhaCungCapCriteria.macDinh() : boLocGoc;
  }

  @Override
  protected KetQuaTai<NhaCungCap> taiDuLieu(String tuKhoa) throws SQLException {
    PaginatedResult<NhaCungCap> kq = this.service.search(this.taoCriteria(tuKhoa));
    return new KetQuaTai<>(kq.items(), tongSoKetQuaCanBao(kq));
  }

  private DanhSachNhaCungCapCriteria taoCriteria(String tuKhoa) {
    return new DanhSachNhaCungCapCriteria(
            this.boLocGoc.maNhaCungCap(),
            this.boLocGoc.tenNhaCungCap(),
            this.boLocGoc.sdt(),
            tuKhoa,
            this.boLocGoc.trangThai(),
            this.boLocGoc.ngayTaoTu(),
            this.boLocGoc.ngayTaoDen(),
            1,
            PAGE_SIZE_TAI_TOAN_BO);
  }

  @Override
  protected String[] tenCot() {
    return new String[] {
        I18nReaderUtil.get("app.nhaCungCap.ma"),
        I18nReaderUtil.get("app.nhaCungCap.ten"),
        I18nReaderUtil.get("app.nhaCungCap.soDienThoai"),
        I18nReaderUtil.get("app.nhaCungCap.trangThai")
    };
  }

  @Override
  protected Object[] giaTriDong(NhaCungCap n) {
    return new Object[] {
        n.getMaNhaCungCap(),
        n.getTenNhaCungCap(),
        n.getSdt() == null ? "" : n.getSdt(),
        n.getTrangThai() == null ? "" : n.getTrangThai().getI18NBusinessName()
    };
  }

  @Override
  protected String placeholderTimKiem() {
    return "app.nhaCungCap.timNhaCungCap";
  }

  @Override
  protected String keyKhongTimThay() {
    return "app.nhaCungCap.khongCoNhaCungCapPhuHop";
  }

  @Override
  protected String keyChuaChon() {
    return "app.nhaCungCap.chonNhaCungCap";
  }
}
