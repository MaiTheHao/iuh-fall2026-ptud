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
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.XeFormPanel;
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

  

  private static final int CHIEU_CAO_TAO = 530;

  private static final int CHIEU_CAO_SUA = 530;

  private static final int COT_BIEN_SO = 1;
  private final XeService xeService = new XeService();
  private final DanhMucXeService danhMucXeService = new DanhMucXeService();

  private ManagementPanel pnlDanhSach;

  private JTextField txtBoLocBienSo;
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

    this.pnlDanhSach = new ManagementPanel();
    this.pnlDanhSach.setListTitle("app.xe.danhSachTitle");
    this.pnlDanhSach.enableMultiSelection(true);

    this.pnlDanhSach.setHeaders(
            I18nReaderUtil.get("app.xe.bienSo"),
            I18nReaderUtil.get("app.xe.hangXe"),
            I18nReaderUtil.get("app.xe.dongXe"),
            I18nReaderUtil.get("app.xe.doiXe"),
            I18nReaderUtil.get("app.xe.odo"));

    this.pnlDanhSach.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSua),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoa));

    this.pnlDanhSach.setAddAction("app.common.add_new", this::moDialogThem);
    this.pnlDanhSach.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieu);
    this.pnlDanhSach.setRowDoubleClickListener(this::moDialogSua);

    this.khoiTaoBoLoc();

    this.pnlDanhSach.getClearFilterButton().addActionListener(e -> this.pnlDanhSach.clearFilters());
    this.pnlDanhSach.getPrevButton().addActionListener(e -> this.taiLai(this.trang - 1));
    this.pnlDanhSach.getNextButton().addActionListener(e -> this.taiLai(this.trang + 1));

    this.taiLai(1);

    this.add(this.pnlDanhSach, BorderLayout.CENTER);
    this.revalidate();
    this.repaint();
  }

  private void khoiTaoBoLoc() {
    this.txtBoLocBienSo = this.pnlDanhSach.addTextFilter("app.xe.bienSo");
    this.boLocHangXe = this.pnlDanhSach.addComboBoxFilter("app.xe.hangXe", this.tenHangXeArraySafe());
    this.boLocDongXe = this.pnlDanhSach.addComboBoxFilter("app.xe.dongXe", this.tenDongXeArraySafe(null));
    this.boLocDoiXe = this.pnlDanhSach.addRangeFilter("app.xe.doiXeTuDen");
    this.boLocOdo = this.pnlDanhSach.addRangeFilter("app.xe.odoTuDen");
    this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai(1));
  }


  private void taiLai(int page) {
    try {
      DanhSachXeCriteria tieuChi = new DanhSachXeCriteria(
              this.txtBoLocBienSo.getText(),
              this.maHangTuBoLoc(),
              this.maDongTuBoLoc(),
              NumberParseUtil.toIntegerOrNull(this.boLocDoiXe.tuText()),
              NumberParseUtil.toIntegerOrNull(this.boLocDoiXe.denText()),
              NumberParseUtil.toIntegerOrNull(this.boLocOdo.tuText()),
              NumberParseUtil.toIntegerOrNull(this.boLocOdo.denText()),
              this.pnlDanhSach.getQuickFilter(),
              Math.max(page, 1),
              UiConstants.SO_DONG_MOI_TRANG);

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

      this.pnlDanhSach.setRows(rows);
      this.pnlDanhSach.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.pnlDanhSach.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();

    } catch (NumberFormatException e) {
      ThongBao.loi(this, "app.xe.loiBoLocSo");
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private XeFormData taoFormData(Xe xe) {
    return new XeFormData(xe.getBienSo(),
            xe.getMaHangXe(),
            xe.getMaDongXe(),
            xe.getMaDoiXe(),
            xe.getOdo());
  }

  private String layBienSoDong(int row) {
    return NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_BIEN_SO));
  }


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


  private void moDialogThem() {
    XeFormPanel form = new XeFormPanel(true);
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

      XeFormPanel form = new XeFormPanel(false);
      this.napHangXe(form);
      form.chonHangXe(xe.getMaHangXe());
      this.napDongXe(form);
      form.chonDongXe(xe.getMaDongXe());
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
