package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.KhachHangFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.KhachHangFormPanel;
import iuh.fit.gui.form.KhachHangSuaFormPanel;
import iuh.fit.gui.form.KhachHangTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KhachHangPage extends AbstractPage {

  private static final long serialVersionUID = 1L;

  private static final int SO_DONG_MOI_TRANG = 20;

  private static final int CHIEU_CAO_TAO = 280;

  private static final int CHIEU_CAO_SUA = 450;

  private static final int COT_MA = 1;
  private static final int COT_SDT = 2;
  private static final int COT_HO_TEN = 3;
  private static final int COT_NGAY_TAO = 4;

  private final KhachHangService khachHangService = new KhachHangService();

  private ManagementPanel panel;

  private JTextField boLocMa;
  private JTextField boLocSdt;
  private JTextField boLocHoTen;
  private ManagementPanel.BoLocNgay boLocNgayTao;

  private int trang = 1;

  public KhachHangPage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.panel = new ManagementPanel();
    this.panel.setListTitle("app.khachHang.danhSachTitle");
    this.panel.enableMultiSelection(true);
    this.panel.setHeaders(
            I18nReaderUtil.get("app.khachHang.ma"),
            I18nReaderUtil.get("app.khachHang.sdt"),
            I18nReaderUtil.get("app.khachHang.hoTen"),
            I18nReaderUtil.get("app.khachHang.ngayTao"));

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

    this.boLocMa = this.panel.addTextFilter("app.khachHang.ma");
    this.boLocSdt = this.panel.addTextFilter("app.khachHang.sdt");
    this.boLocHoTen = this.panel.addTextFilter("app.khachHang.hoTen");
    this.boLocNgayTao = this.panel.addDateRangeFilter("app.khachHang.ngayTao");
    this.panel.setFilterChangeListener(() -> this.taiLai(1));

    this.panel.getClearFilterButton().addActionListener(e -> this.panel.clearFilters());
    this.panel.getPrevButton().addActionListener(e -> this.taiLai(this.trang - 1));
    this.panel.getNextButton().addActionListener(e -> this.taiLai(this.trang + 1));

    this.taiLai(1);

    this.add(this.panel, BorderLayout.CENTER);
    this.revalidate();
    this.repaint();
  }

  // ==================== TẢI DANH SÁCH ====================

  private void taiLai(int page) {
    DanhSachKhachHangCriteria tieuChi = new DanhSachKhachHangCriteria(
            this.boLocMa.getText(),
            this.boLocSdt.getText(),
            this.boLocHoTen.getText(),
            this.boLocNgayTao.tuNgay(),
            this.boLocNgayTao.denNgay(),
            this.panel.getQuickFilter(),
            Math.max(page, 1),
            SO_DONG_MOI_TRANG);

    try {
      PaginatedResult<KhachHang> ketQua = this.khachHangService.search(tieuChi);
      List<Object[]> rows = new ArrayList<>();

      for (KhachHang khachHang : ketQua.items()) {
        rows.add(new Object[] {
            khachHang.getMaKhachHang(),
            khachHang.getSdt(),
            khachHang.getHoTen(),
            this.dinhDangNgay(khachHang.getNgayTao())
        });
      }

      this.panel.setRows(rows);
      this.panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private String dinhDangNgay(LocalDateTime thoiGian) {
    if (thoiGian == null) {
      return "";
    }
    return thoiGian.format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format")));
  }

  private KhachHangFormData docDuLieuDong(int row) {
    return new KhachHangFormData(
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MA)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_SDT)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_HO_TEN)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_NGAY_TAO)));
  }

  // ==================== DIALOG ====================

  private void moDialogThem() {
    KhachHangTaoFormPanel form = new KhachHangTaoFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.khachHang.themTitle",
            form,
            () -> this.taoKhachHang(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    KhachHangFormData duLieu = this.docDuLieuDong(row);
    KhachHangSuaFormPanel form = new KhachHangSuaFormPanel();
    form.napDuLieu(duLieu);

    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.khachHang.suaTitle",
            form,
            () -> this.capNhatKhachHang(form, duLieu),
            CHIEU_CAO_SUA));

    if (dialog.showAndGet()) {
      this.taiLai(this.trang);
    }
  }

  private boolean taoKhachHang(KhachHangFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    KhachHangFormData data = form.layDuLieu();

    try {
      KhachHang khachHang = new KhachHang();
      khachHang.setSdt(data.sdt());
      khachHang.setHoTen(data.hoTen());

      this.khachHangService.create(khachHang);
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatKhachHang(KhachHangFormPanel form, KhachHangFormData duLieuCu) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    KhachHangFormData data = form.layDuLieu();

    try {
      KhachHang khachHang = new KhachHang();
      khachHang.setMaKhachHang(duLieuCu.maKhachHang());
      khachHang.setSdt(data.sdt());
      khachHang.setHoTen(data.hoTen());

      this.khachHangService.update(khachHang);
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(KhachHangFormPanel form) {
    if (form.validateInput()) {
      return true;
    }

    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  // ==================== XÓA ====================

  private void xoa(int row) {
    String maKhachHang = NumberParseUtil.textOf(
            this.panel.getModel().getValueAt(row, COT_MA));

    if (maKhachHang.isBlank()) {
      ThongBao.canhBao(this, "app.khachHang.chuaChonXoa");
      return;
    }

    if (!ThongBao.xacNhan(this, "app.khachHang.xacNhanXoa", "app.khachHang.tieuDeXoa")) {
      return;
    }

    try {
      this.khachHangService.delete(maKhachHang);
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
      String maKhachHang = NumberParseUtil.textOf(
              this.panel.getModel().getValueAt(row, COT_MA));

      try {
        this.khachHangService.delete(maKhachHang);
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Bản ghi đang được tham chiếu: ghi nhận vào báo cáo tổng hợp.
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
    return "app.menu.customers";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
