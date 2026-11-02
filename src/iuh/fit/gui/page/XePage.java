package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.XeFormData;
import iuh.fit.entity.Xe;
import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.XeFormPanel;
import iuh.fit.gui.form.XeSuaFormPanel;
import iuh.fit.gui.form.XeTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DanhMucXeService;
import iuh.fit.service.XeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XePage extends AbstractPage {

  private static final long serialVersionUID = 1L;
  private static final int SO_DONG_MOI_TRANG = 20;

  private static final int CHIEU_CAO_TAO = 530;

  private static final int CHIEU_CAO_SUA = 530;

  private static final int COT_BIEN_SO = 1;
  private static final int COT_HANG_XE = 2;
  private static final int COT_DONG_XE = 3;
  private static final int COT_DOI_XE = 4;
  private static final int COT_ODO = 5;

  private final XeService xeService = new XeService();
  private final DanhMucXeService danhMucXeService = new DanhMucXeService();

  private ManagementPanel panel;

  private JTextField boLocBienSo;
  private ManagementPanel.BoLocChon boLocHangXe;
  private ManagementPanel.BoLocChon boLocDongXe;
  private ManagementPanel.BoLocKhoang boLocDoiXe;
  private ManagementPanel.BoLocKhoang boLocOdo;

  private int trang = 1;

  public XePage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.panel = new ManagementPanel();
    this.panel.setListTitle("app.xe.danhSachTitle");
    this.panel.enableMultiSelection(true);

    this.panel.setHeaders(
            I18nReaderUtil.get("app.xe.bienSo"),
            I18nReaderUtil.get("app.xe.hangXe"),
            I18nReaderUtil.get("app.xe.dongXe"),
            I18nReaderUtil.get("app.xe.doiXe"),
            I18nReaderUtil.get("app.xe.odo"));

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
    this.boLocBienSo = this.panel.addTextFilter("app.xe.bienSo");
    this.boLocHangXe = this.panel.addComboBoxFilter("app.xe.hangXe", this.tenHangXeArraySafe());
    this.boLocDongXe = this.panel.addComboBoxFilter("app.xe.dongXe", this.tenDongXeArraySafe(null));
    this.boLocDoiXe = this.panel.addRangeFilter("app.xe.doiXeTuDen");
    this.boLocOdo = this.panel.addRangeFilter("app.xe.odoTuDen");
    this.panel.setFilterChangeListener(() -> this.taiLai(1));
  }

  // ==================== TẢI DANH SÁCH ====================

  private void taiLai(int page) {
    try {
      DanhSachXeCriteria tieuChi = new DanhSachXeCriteria(
              this.boLocBienSo.getText(),
              this.maHangTuBoLoc(),
              this.maDongTuBoLoc(),
              NumberParseUtil.toIntegerOrNull(this.boLocDoiXe.tuText()),
              NumberParseUtil.toIntegerOrNull(this.boLocDoiXe.denText()),
              NumberParseUtil.toIntegerOrNull(this.boLocOdo.tuText()),
              NumberParseUtil.toIntegerOrNull(this.boLocOdo.denText()),
              this.panel.getQuickFilter(),
              Math.max(page, 1),
              SO_DONG_MOI_TRANG);

      PaginatedResult<Xe> ketQua = this.xeService.search(tieuChi);
      List<Object[]> rows = new ArrayList<>();

      for (Xe xe : ketQua.items()) {
        rows.add(new Object[] {
            xe.getBienSo(),
            xe.getTenHangXe(),
            xe.getTenDongXe(),
            xe.getNamSX(),
            xe.getOdo()
        });
      }

      this.panel.setRows(rows);
      this.panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();

    } catch (NumberFormatException e) {
      ThongBao.loi(this, "app.xe.loiBoLocSo");
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private XeFormData docDuLieuDong(int row) {
    return new XeFormData(
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_BIEN_SO)),
            null,
            null,
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_DOI_XE)),
            NumberParseUtil.toIntegerOrNull(
                    NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_ODO))));
  }

  private XeFormData taoFormData(Xe xe) {
    return new XeFormData(xe.getBienSo(),
            xe.getMaHangXe(),
            xe.getMaDongXe(),
            xe.getMaDoiXe(),
            xe.getOdo());
  }

  private String layBienSoDong(int row) {
    return NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_BIEN_SO));
  }

  // ==================== GỢI Ý HÃNG / DÒNG XE ====================

  private void napHangXe(XeFormPanel form) {
    try {
      form.getHangXeCombo().removeAllItems();
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        form.getHangXeCombo().addItem(hangXe);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void napDongXe(XeFormPanel form) {
    try {
      Object sel = form.getHangXeCombo().getSelectedItem();
      String maHang = sel instanceof HangXe ? ((HangXe) sel).getMaHangXe() : null;
      form.getDongXeCombo().removeAllItems();
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(maHang)) {
        form.getDongXeCombo().addItem(dongXe);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void napDoiXe(XeFormPanel form) {
    try {
      Object sel = form.getDongXeCombo().getSelectedItem();
      String maDong = sel instanceof DongXe ? ((DongXe) sel).getMaDongXe() : null;
      form.getDoiXeCombo().removeAllItems();
      for (DoiXe doiXe : this.danhMucXeService.getDanhSachDoiXe(maDong)) {
        form.getDoiXeCombo().addItem(doiXe);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void ganGoiYHangXe(XeFormPanel form) {
    form.getHangXeCombo().addActionListener(e -> {
      form.getDongXeCombo().removeAllItems();
      form.getDoiXeCombo().removeAllItems();
      this.napDongXe(form);
    });
    form.getDongXeCombo().addActionListener(e -> {
      form.getDoiXeCombo().removeAllItems();
      this.napDoiXe(form);
    });
  }

  private String maHangTuBoLoc() {
    try {
      Object val = this.boLocHangXe.giaTri();
      if (val == null || String.valueOf(val).isBlank()) {
        return null;
      }
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        if (String.valueOf(val).equalsIgnoreCase(hangXe.getTen())) {
          return hangXe.getMaHangXe();
        }
      }
    } catch (SQLException e) {
      return null;
    }
    return null;
  }

  private String maDongTuBoLoc() {
    try {
      Object val = this.boLocDongXe.giaTri();
      if (val == null || String.valueOf(val).isBlank()) {
        return null;
      }
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(null)) {
        if (String.valueOf(val).equalsIgnoreCase(dongXe.getTen())) {
          return dongXe.getMaDongXe();
        }
      }
    } catch (SQLException e) {
      return null;
    }
    return null;
  }

  private Object[] tenHangXeArraySafe() {
    try {
      List<String> ten = new ArrayList<>();
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        ten.add(hangXe.getTen());
      }
      return ten.toArray();
    } catch (SQLException e) {
      return new Object[0];
    }
  }

  private Object[] tenDongXeArraySafe(String maHangXe) {
    try {
      List<String> ten = new ArrayList<>();
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(maHangXe)) {
        ten.add(dongXe.getTen());
      }
      return ten.toArray();
    } catch (SQLException e) {
      return new Object[0];
    }
  }

  // ==================== DIALOG ====================

  private void moDialogThem() {
    XeTaoFormPanel form = new XeTaoFormPanel();
    this.napHangXe(form);
    this.napDongXe(form);
    this.napDoiXe(form);
    this.ganGoiYHangXe(form);

    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.xe.themTitle",
            form,
            () -> this.taoXe(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    String bienSo = this.layBienSoDong(row);

    try {
      Xe xe = this.xeService.getByBienSo(bienSo)
              .orElseThrow(() -> new IllegalArgumentException("app.xe.loiKhongTimThay"));

      XeSuaFormPanel form = new XeSuaFormPanel();
      this.napHangXe(form);
      this.napDongXe(form);
      this.napDoiXe(form);
      form.napDuLieu(this.taoFormData(xe));
      this.ganGoiYHangXe(form);

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.xe.suaTitle",
              form,
              () -> this.capNhatXe(form, xe),
              CHIEU_CAO_SUA));

      if (dialog.showAndGet()) {
        this.taiLai(this.trang);
      }
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private boolean taoXe(XeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    XeFormData data = form.layDuLieu();

    try {
      Xe xe = new Xe();
      xe.setBienSo(data.bienSo());
      xe.setMaDoiXe(data.maDoiXe());
      xe.setOdo(data.odo() == null ? 0 : data.odo());

      this.xeService.create(xe);
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatXe(XeFormPanel form, Xe xe) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    XeFormData data = form.layDuLieu();

    try {
      xe.setMaDoiXe(data.maDoiXe());
      xe.setOdo(data.odo() == null ? 0 : data.odo());

      this.xeService.update(xe);
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(XeFormPanel form) {
    if (form.validateInput()) {
      return true;
    }

    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  // ==================== XÓA ====================

  private void xoa(int row) {
    String bienSo = this.layBienSoDong(row);

    if (bienSo.isBlank()) {
      ThongBao.canhBao(this, "app.xe.chuaChonXoa");
      return;
    }

    if (!ThongBao.xacNhan(this, "app.xe.xacNhanXoa", "app.xe.tieuDeXoa")) {
      return;
    }

    try {
      this.xeService.delete(bienSo);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLai(this.trang);
    } catch (IllegalArgumentException | IllegalStateException e) {
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
        this.xeService.delete(this.layBienSoDong(row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Xe đã lập phiếu tiếp nhận / chẩn đoán: ghi nhận vào báo cáo tổng hợp.
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
    return "app.menu.vehicles";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
