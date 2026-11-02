package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.DoiXeFormPanel;
import iuh.fit.gui.form.DongXeFormPanel;
import iuh.fit.gui.form.HangXeFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DanhMucXeService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DanhMucXePage extends AbstractPage {

  private static final long serialVersionUID = 1L;
  private static final int CHIEU_CAO = 420;

  private final DanhMucXeService danhMucXeService = new DanhMucXeService();
  private final Map<ManagementPanel, List<ManagementPanel.BoLocChon>> boLocTheoPanel = new HashMap<>();

  public DanhMucXePage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab(I18nReaderUtil.get("app.danhMucXe.tabHang"), this.taoTabHangXe());
    tabs.addTab(I18nReaderUtil.get("app.danhMucXe.tabDong"), this.taoTabDongXe());
    tabs.addTab(I18nReaderUtil.get("app.danhMucXe.tabDoi"), this.taoTabDoiXe());
    this.add(tabs, BorderLayout.CENTER);
    this.revalidate();
    this.repaint();
  }

  private JPanel taoTabHangXe() {
    ManagementPanel panel = new ManagementPanel();
    panel.setListTitle("app.danhMucXe.hangTitle");
    panel.setHeaders(I18nReaderUtil.get("app.danhMucXe.ma"), I18nReaderUtil.get("app.danhMucXe.ten"));
    panel.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    row -> this.moDialogSuaHangXe(panel, row)),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    row -> this.xoaHangXe(row, panel)));
    panel.setAddAction("app.common.add_new", () -> this.moDialogThemHangXe(panel));
    panel.setRowDoubleClickListener(row -> this.moDialogSuaHangXe(panel, row));
    this.taiLaiHangXe(panel);
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(panel, BorderLayout.CENTER);
    return wrapper;
  }

  private JPanel taoTabDongXe() {
    ManagementPanel panel = new ManagementPanel();
    panel.setListTitle("app.danhMucXe.dongTitle");
    panel.setHeaders(I18nReaderUtil.get("app.danhMucXe.ma"), I18nReaderUtil.get("app.danhMucXe.hang"), I18nReaderUtil.get("app.danhMucXe.ten"));
    panel.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    row -> this.moDialogSuaDongXe(panel, row)),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    row -> this.xoaDongXe(row, panel)));
    panel.setAddAction("app.common.add_new", () -> this.moDialogThemDongXe(panel));
    panel.setRowDoubleClickListener(row -> this.moDialogSuaDongXe(panel, row));
    var loc = panel.addComboBoxFilter("app.danhMucXe.hang", this.tenHangXeArraySafe());
    this.boLocTheoPanel.put(panel, List.of(loc));
    panel.setFilterChangeListener(() -> this.taiLaiDongXe(panel));
    this.taiLaiDongXe(panel);
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(panel, BorderLayout.CENTER);
    return wrapper;
  }

  private JPanel taoTabDoiXe() {
    ManagementPanel panel = new ManagementPanel();
    panel.setListTitle("app.danhMucXe.doiTitle");
    panel.setHeaders(I18nReaderUtil.get("app.danhMucXe.ma"), I18nReaderUtil.get("app.danhMucXe.hang"), I18nReaderUtil.get("app.danhMucXe.dong"), I18nReaderUtil.get("app.danhMucXe.nam"));
    panel.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    row -> this.moDialogSuaDoiXe(panel, row)),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    row -> this.xoaDoiXe(row, panel)));
    panel.setAddAction("app.common.add_new", () -> this.moDialogThemDoiXe(panel));
    panel.setRowDoubleClickListener(row -> this.moDialogSuaDoiXe(panel, row));
    var locHang = panel.addComboBoxFilter("app.danhMucXe.hang", this.tenHangXeArraySafe());
    var locDong = panel.addComboBoxFilter("app.danhMucXe.dong", this.tenDongXeArraySafe(null));
    this.boLocTheoPanel.put(panel, List.of(locHang, locDong));
    panel.setFilterChangeListener(() -> this.taiLaiDoiXe(panel));
    this.taiLaiDoiXe(panel);
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(panel, BorderLayout.CENTER);
    return wrapper;
  }

  private void taiLaiHangXe(ManagementPanel panel) {
    try {
      List<Object[]> rows = new ArrayList<>();
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        rows.add(new Object[] {hangXe.getMaHangXe(), hangXe.getTen()});
      }
      panel.setRows(rows);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void taiLaiDongXe(ManagementPanel panel) {
    try {
      String maHang = this.maHangTuBoLoc(panel, 0);
      List<Object[]> rows = new ArrayList<>();
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(maHang)) {
        rows.add(new Object[] {dongXe.getMaDongXe(), dongXe.getTenHangXe(), dongXe.getTen()});
      }
      panel.setRows(rows);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void taiLaiDoiXe(ManagementPanel panel) {
    try {
      String maDong = this.maDongTuBoLoc(panel);
      List<Object[]> rows = new ArrayList<>();
      for (DoiXe doiXe : this.danhMucXeService.getDanhSachDoiXe(maDong)) {
        rows.add(new Object[] {doiXe.getMaDoiXe(), doiXe.getTenHangXe(), doiXe.getTenDongXe(), doiXe.getNamSX()});
      }
      panel.setRows(rows);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogThemHangXe(ManagementPanel panel) {
    HangXeFormPanel form = new HangXeFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.danhMucXe.themHangTitle",
            form,
            () -> this.taoHangXe(form, panel),
            CHIEU_CAO));
    if (dialog.showAndGet()) {
      this.taiLaiHangXe(panel);
    }
  }

  private void moDialogSuaHangXe(ManagementPanel panel, int row) {
    String ma = (String) panel.getModel().getValueAt(row, 0);
    String ten = (String) panel.getModel().getValueAt(row, 1);
    HangXeFormPanel form = new HangXeFormPanel();
    form.napDuLieu(new HangXe(ma, ten));
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.danhMucXe.suaHangTitle",
            form,
            () -> this.capNhatHangXe(form, panel, ma),
            CHIEU_CAO));
    if (dialog.showAndGet()) {
      this.taiLaiHangXe(panel);
    }
  }

  private void moDialogThemDongXe(ManagementPanel panel) {
    try {
      DongXeFormPanel form = new DongXeFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.themDongTitle",
              form,
              () -> this.taoDongXe(form, panel),
              CHIEU_CAO));
      if (dialog.showAndGet()) {
        this.taiLaiDongXe(panel);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogSuaDongXe(ManagementPanel panel, int row) {
    try {
      String ma = (String) panel.getModel().getValueAt(row, 0);
      String ten = (String) panel.getModel().getValueAt(row, 2);
      DongXeFormPanel form = new DongXeFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      form.napDuLieu(new DongXe(ma, null, ten, null));
      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.suaDongTitle",
              form,
              () -> this.capNhatDongXe(form, panel, ma),
              CHIEU_CAO));
      if (dialog.showAndGet()) {
        this.taiLaiDongXe(panel);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogThemDoiXe(ManagementPanel panel) {
    try {
      DoiXeFormPanel form = new DoiXeFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(form.layMaHangXe()));
      form.getHangXeCombo().addActionListener(e -> {
        try {
          form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(form.layMaHangXe()));
        } catch (SQLException ex) {
          ThongBao.loi(this, "app.login.errorSystem");
        }
      });
      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.themDoiTitle",
              form,
              () -> this.taoDoiXe(form, panel),
              CHIEU_CAO));
      if (dialog.showAndGet()) {
        this.taiLaiDoiXe(panel);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogSuaDoiXe(ManagementPanel panel, int row) {
    try {
      String ma = (String) panel.getModel().getValueAt(row, 0);
      Object namObj = panel.getModel().getValueAt(row, 3);
      int namSX = namObj instanceof Number ? ((Number) namObj).intValue() : Integer.parseInt(String.valueOf(namObj));
      String maHangXe = this.maHangTheoTen(String.valueOf(panel.getModel().getValueAt(row, 1)));
      String maDongXe = this.maDongTheoTen(String.valueOf(panel.getModel().getValueAt(row, 2)), maHangXe);
      DoiXeFormPanel form = new DoiXeFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(maHangXe));
      form.napDuLieu(new DoiXe(ma, maDongXe, namSX, maHangXe, null, null));
      form.getHangXeCombo().addActionListener(e -> {
        try {
          form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(form.layMaHangXe()));
        } catch (SQLException ex) {
          ThongBao.loi(this, "app.login.errorSystem");
        }
      });
      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.suaDoiTitle",
              form,
              () -> this.capNhatDoiXe(form, panel, ma),
              CHIEU_CAO));
      if (dialog.showAndGet()) {
        this.taiLaiDoiXe(panel);
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private boolean taoHangXe(HangXeFormPanel form, ManagementPanel panel) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.taoHangXe(form.layTen());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private boolean capNhatHangXe(HangXeFormPanel form, ManagementPanel panel, String maCu) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.capNhatHangXe(new HangXe(maCu, form.layTen()));
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private boolean taoDongXe(DongXeFormPanel form, ManagementPanel panel) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.taoDongXe(form.layMaHangXe(), form.layTen());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private boolean capNhatDongXe(DongXeFormPanel form, ManagementPanel panel, String maCu) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.capNhatDongXe(new DongXe(maCu, form.layMaHangXe(), form.layTen(), null));
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private boolean taoDoiXe(DoiXeFormPanel form, ManagementPanel panel) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.taoDoiXe(form.layMaDongXe(), form.layNamSX());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private boolean capNhatDoiXe(DoiXeFormPanel form, ManagementPanel panel, String maCu) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }
    try {
      this.danhMucXeService.capNhatDoiXe(new DoiXe(maCu, form.layMaDongXe(), form.layNamSX(), null, null, null));
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
    return false;
  }

  private void xoaHangXe(int row, ManagementPanel panel) {
    String ma = (String) panel.getModel().getValueAt(row, 0);
    if (ma == null || ma.isBlank()) {
      return;
    }
    if (!ThongBao.xacNhan(this, "app.danhMucXe.xacNhanXoa", "app.danhMucXe.tieuDeXoa")) {
      return;
    }
    try {
      this.danhMucXeService.xoaHangXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiHangXe(panel);
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaDongXe(int row, ManagementPanel panel) {
    String ma = (String) panel.getModel().getValueAt(row, 0);
    if (ma == null || ma.isBlank()) {
      return;
    }
    if (!ThongBao.xacNhan(this, "app.danhMucXe.xacNhanXoa", "app.danhMucXe.tieuDeXoa")) {
      return;
    }
    try {
      this.danhMucXeService.xoaDongXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiDongXe(panel);
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaDoiXe(int row, ManagementPanel panel) {
    String ma = (String) panel.getModel().getValueAt(row, 0);
    if (ma == null || ma.isBlank()) {
      return;
    }
    if (!ThongBao.xacNhan(this, "app.danhMucXe.xacNhanXoa", "app.danhMucXe.tieuDeXoa")) {
      return;
    }
    try {
      this.danhMucXeService.xoaDoiXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiDoiXe(panel);
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private Object[] tenHangXeArray() throws SQLException {
    List<String> ten = new ArrayList<>();
    for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
      ten.add(hangXe.getTen());
    }
    return ten.toArray();
  }

  private Object[] tenHangXeArraySafe() {
    try {
      return this.tenHangXeArray();
    } catch (SQLException e) {
      return new Object[0];
    }
  }

  private Object[] tenDongXeArray(String maHangXe) throws SQLException {
    List<String> ten = new ArrayList<>();
    for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(maHangXe)) {
      ten.add(dongXe.getTen());
    }
    return ten.toArray();
  }

  private Object[] tenDongXeArraySafe(String maHangXe) {
    try {
      return this.tenDongXeArray(maHangXe);
    } catch (SQLException e) {
      return new Object[0];
    }
  }

  private String maHangTuBoLoc(ManagementPanel panel, int index) {
    try {
      List<ManagementPanel.BoLocChon> locs = this.boLocTheoPanel.get(panel);
      if (locs == null || locs.isEmpty()) {
        return null;
      }
      Object val = locs.get(0).giaTri();
      if (val == null || String.valueOf(val).isBlank()) {
        return null;
      }
      return this.maHangTheoTen(String.valueOf(val));
    } catch (Exception e) {
      return null;
    }
  }

  private String maDongTuBoLoc(ManagementPanel panel) {
    try {
      List<ManagementPanel.BoLocChon> locs = this.boLocTheoPanel.get(panel);
      if (locs == null || locs.size() < 2) {
        return null;
      }
      Object val = locs.get(1).giaTri();
      if (val == null || String.valueOf(val).isBlank()) {
        return null;
      }
      return this.maDongTheoTen(String.valueOf(val), null);
    } catch (Exception e) {
      return null;
    }
  }

  private String maHangTheoTen(String ten) {
    try {
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        if (ten.equalsIgnoreCase(hangXe.getTen())) {
          return hangXe.getMaHangXe();
        }
      }
    } catch (SQLException e) {
      return null;
    }
    return null;
  }

  private String maDongTheoTen(String ten, String maHangXe) {
    try {
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(maHangXe)) {
        if (ten.equalsIgnoreCase(dongXe.getTen())) {
          return dongXe.getMaDongXe();
        }
      }
    } catch (SQLException e) {
      return null;
    }
    return null;
  }

  @Override
  public String getTitleKey() {
    return "app.menu.vehicleCatalog";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
