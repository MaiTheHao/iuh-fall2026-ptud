package iuh.fit.gui.component;

import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhuTung;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JCheckBox;

import java.awt.Window;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Dialog chọn phụ tùng có tìm kiếm theo mã/tên/mô tả và bộ lọc tồn kho.
 */
public final class ChonPhuTungDialog extends ChonSanPhamDialog<PhuTung> {

  private static final long serialVersionUID = 1L;

  private final PhuTungService phuTungService;
  private final DanhSachPhuTungCriteria boLocGoc;
  private final Function<PhuTung, PhuTung> hienThi;
  private final JCheckBox chiConTon;
  private boolean chiConTonDangChon;

  public ChonPhuTungDialog(Window owner, PhuTungService phuTungService,
          DanhSachPhuTungCriteria boLocGoc) {
    this(owner, phuTungService, boLocGoc, Function.identity(), false);
  }

  public ChonPhuTungDialog(Window owner, PhuTungService phuTungService,
          DanhSachPhuTungCriteria boLocGoc, Function<PhuTung, PhuTung> hienThi,
          boolean chiConTonMacDinh) {
    super(owner, "app.phieuNhap.chonPhuTung");

    this.phuTungService = Objects.requireNonNull(phuTungService);
    this.boLocGoc = boLocGoc == null ? DanhSachPhuTungCriteria.macDinh() : boLocGoc;
    this.hienThi = hienThi == null ? Function.identity() : hienThi;

    this.chiConTon = new JCheckBox(I18nReaderUtil.get("app.phieuNhap.chiConTon"));
    this.chiConTon.setOpaque(false);
    this.chiConTon.setSelected(chiConTonMacDinh);
    this.chiConTon.addActionListener(e -> this.locLai());
    this.datBoLoc(this.chiConTon);
  }

  @Override
  protected void chuanBiTaiLieu() {
    this.chiConTonDangChon = this.chiConTon.isSelected();
  }

  @Override
  protected KetQuaTai<PhuTung> taiDuLieu(String tuKhoa) throws SQLException {
    PaginatedResult<PhuTung> ketQua = this.phuTungService.search(this.taoCriteria(tuKhoa));

    List<PhuTung> danhSach = ketQua.items().stream()
            .map(this.hienThi)
            .toList();

    // Lọc trên số lượng đã ánh xạ: phụ tùng đã giữ chỗ trong hóa đơn vẫn còn khả dụng
    // dù tồn trong DB bằng 0, nên không thể lọc bằng criteria.
    if (this.chiConTonDangChon) {
      int nguong = this.nguongTon();
      danhSach = danhSach.stream()
              .filter(phuTung -> phuTung.getSoLuongTon() >= nguong)
              .toList();
    }

    return new KetQuaTai<>(danhSach, tongSoKetQuaCanBao(ketQua));
  }

  private DanhSachPhuTungCriteria taoCriteria(String tuKhoa) {
    return new DanhSachPhuTungCriteria(
            this.boLocGoc.maPhuTung(),
            this.boLocGoc.tenPhuTung(),
            tuKhoa,
            this.boLocGoc.donGiaTu(),
            this.boLocGoc.donGiaDen(),
            this.boLocGoc.soLuongTu(),
            this.boLocGoc.soLuongDen(),
            1,
            PAGE_SIZE_TAI_TOAN_BO);
  }

  /** Ngưỡng tồn tối thiểu khi bật "chỉ hiện còn tồn". */
  private int nguongTon() {
    Integer goc = this.boLocGoc.soLuongTu();
    return goc == null ? 1 : Math.max(1, goc);
  }

  @Override
  protected String[] tenCot() {
    return new String[]{
        I18nReaderUtil.get("app.phuTung.ma"),
        I18nReaderUtil.get("app.phuTung.ten"),
        I18nReaderUtil.get("app.phuTung.soLuongTon"),
        I18nReaderUtil.get("app.phieuNhap.donGia")
    };
  }

  @Override
  protected Object[] giaTriDong(PhuTung phuTung) {
    double giaNhap = phuTung.getDonGiaNhap() == null ? 0.0 : phuTung.getDonGiaNhap();

    return new Object[]{
        phuTung.getMaPhuTung(),
        phuTung.getTenPhuTung(),
        phuTung.getSoLuongTon(),
        giaNhap
    };
  }

  @Override
  protected String placeholderTimKiem() {
    return "app.phieuNhap.timPhuTung";
  }

  @Override
  protected String keyKhongTimThay() {
    return "app.phieuNhap.khongCoPhuTungPhuHop";
  }

  @Override
  protected String keyChuaChon() {
    return "app.phieuNhap.loiPhuTung";
  }
}
