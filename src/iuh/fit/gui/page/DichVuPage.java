package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachDichVuCriteria;
import iuh.fit.dto.DichVuFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.DichVu;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.DichVuFormPanel;
import iuh.fit.gui.form.DichVuSuaFormPanel;
import iuh.fit.gui.form.DichVuTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DichVuService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DichVuPage extends AbstractPage {

  private static final long serialVersionUID = 1L;
  private static final int SO_DONG_MOI_TRANG = 20;

  private static final int CHIEU_CAO_TAO = 670;

  private static final int CHIEU_CAO_SUA = 750;

  private static final int COT_ANH = 1;
  private static final int COT_MA = 2;
  private static final int COT_TEN = 3;
  private static final int COT_MO_TA = 4;
  private static final int COT_DON_GIA = 5;

  private final DichVuService dichVuService = new DichVuService();

  private ManagementPanel panel;

  private JTextField boLocMa;
  private JTextField boLocTen;
  private ManagementPanel.BoLocKhoang boLocDonGia;

  private int trang = 1;

  public DichVuPage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.panel = new ManagementPanel();
    this.panel.setListTitle("app.dichVu.danhSachTitle");
    this.panel.enableMultiSelection(true);

    this.panel.setHeaders(
            I18nReaderUtil.get("app.dichVu.hinhAnh"),
            I18nReaderUtil.get("app.dichVu.ma"),
            I18nReaderUtil.get("app.dichVu.ten"),
            I18nReaderUtil.get("app.dichVu.moTa"),
            I18nReaderUtil.get("app.dichVu.donGia"));

    this.panel.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSua),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoa));

    this.panel.setAddAction("app.common.add_new", this::moDialogThem);
    this.panel.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieu);
    this.panel.setRowDoubleClickListener(this::moDialogSua);

    this.khoiTaoBoLoc();

    this.panel.getClearFilterButton().addActionListener(e -> this.panel.clearFilters());
    this.panel.getPrevButton().addActionListener(e -> this.taiLai(this.trang - 1));
    this.panel.getNextButton().addActionListener(e -> this.taiLai(this.trang + 1));

    this.taiLai(1);

    this.add(this.panel, BorderLayout.CENTER);
    this.revalidate();
    this.repaint();
  }

  private void khoiTaoBoLoc() {
    this.boLocMa = this.panel.addTextFilter("app.dichVu.ma");
    this.boLocTen = this.panel.addTextFilter("app.dichVu.ten");
    this.boLocDonGia = this.panel.addRangeFilter("app.dichVu.donGiaTuDen");
    this.panel.setFilterChangeListener(() -> this.taiLai(1));
  }

  // ==================== TẢI DANH SÁCH ====================

  private void taiLai(int page) {
    try {
      DanhSachDichVuCriteria tieuChi = new DanhSachDichVuCriteria(
              this.boLocMa.getText(),
              this.boLocTen.getText(),
              this.panel.getQuickFilter(),
              NumberParseUtil.toDoubleOrNull(this.boLocDonGia.tuText()),
              NumberParseUtil.toDoubleOrNull(this.boLocDonGia.denText()),
              Math.max(page, 1),
              SO_DONG_MOI_TRANG);

      PaginatedResult<DichVu> ketQua = this.dichVuService.search(tieuChi);
      List<Object[]> rows = new ArrayList<>();

      for (DichVu dichVu : ketQua.items()) {
        rows.add(new Object[] {
            dichVu.getAnhUrl(),
            dichVu.getMaDichVu(),
            dichVu.getTenDichVu(),
            dichVu.getMoTa(),
            dichVu.getDonGia()
        });
      }

      this.panel.setRows(rows);
      this.cauHinhCotAnh();
      this.panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();

    } catch (NumberFormatException e) {
      ThongBao.loi(this, "app.dichVu.loiBoLocSo");
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void cauHinhCotAnh() {
    AnhTableCellRenderer.ganVao(this.panel,
            COT_ANH,
            "app.dichVu.chuaCoAnh",
            "app.dichVu.khongTaiDuocAnh");
  }

  private DichVuFormData docDuLieuDong(int row) {
    return new DichVuFormData(
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MA)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_TEN)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MO_TA)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_ANH)),
            NumberParseUtil.toDoubleOrNull(
                    NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_DON_GIA))));
  }

  private String layMaDong(int row) {
    return NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MA));
  }

  // ==================== DIALOG ====================

  private void moDialogThem() {
    DichVuTaoFormPanel form = new DichVuTaoFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.dichVu.themTitle",
            form,
            () -> this.taoDichVu(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    DichVuFormData duLieu = this.docDuLieuDong(row);
    DichVuSuaFormPanel form = new DichVuSuaFormPanel();
    form.napDuLieu(duLieu);

    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.dichVu.suaTitle",
            form,
            () -> this.capNhatDichVu(form, duLieu),
            CHIEU_CAO_SUA));

    if (dialog.showAndGet()) {
      this.taiLai(this.trang);
    }
  }

  private boolean taoDichVu(DichVuFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    DichVuFormData data = form.layDuLieu();

    try {
      this.dichVuService.create(new DichVu(
              null,
              data.tenDichVu(),
              data.moTa(),
              data.anhUrl(),
              data.donGia()));
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatDichVu(DichVuFormPanel form, DichVuFormData duLieuCu) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    DichVuFormData data = form.layDuLieu();

    try {
      this.dichVuService.update(new DichVu(
              duLieuCu.maDichVu(),
              data.tenDichVu(),
              data.moTa(),
              data.anhUrl(),
              data.donGia()));
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(DichVuFormPanel form) {
    if (form.validateInput()) {
      return true;
    }

    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  // ==================== XÓA ====================

  private void xoa(int row) {
    String maDichVu = this.layMaDong(row);

    if (maDichVu.isBlank()) {
      ThongBao.loi(this, "app.dichVu.chuaChonXoa");
      return;
    }

    if (!ThongBao.xacNhan(this, "app.dichVu.xacNhanXoa", "app.dichVu.tieuDeXoa")) {
      return;
    }

    try {
      this.dichVuService.delete(maDichVu);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLai(this.trang);
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaNhieu(List<Integer> rows) {
    if (rows.isEmpty()) {
      return;
    }

    if (!ThongBao.xacNhanDinhDang(this,
            "app.common.batch_delete_confirm",
            "app.common.confirm",
            rows.size())) {
      return;
    }

    int thanhCong = 0;
    boolean loiHeThong = false;

    for (int row : rows) {
      try {
        this.dichVuService.delete(this.layMaDong(row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Dịch vụ đã dùng trong hóa đơn: ghi nhận vào báo cáo tổng hợp.
      } catch (SQLException e) {
        loiHeThong = true;
        break;
      }
    }

    this.baoKetQuaHangLoat(thanhCong, rows.size(), loiHeThong);
    this.taiLai(this.trang);
  }

  private void baoKetQuaHangLoat(int thanhCong, int tong, boolean loiHeThong) {
    if (loiHeThong) {
      ThongBao.loi(this, "app.login.errorSystem");
      return;
    }

    if (thanhCong == tong) {
      ThongBao.thongTin(this, "app.common.delete_succeed");
      return;
    }

    ThongBao.canhBaoDinhDang(this, "app.common.batch_delete_result", thanhCong, tong);
  }

  @Override
  public String getTitleKey() {
    return "app.menu.servicePriceList";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
