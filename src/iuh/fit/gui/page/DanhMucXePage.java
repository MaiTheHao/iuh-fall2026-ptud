package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
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
import iuh.fit.gui.form.DoiXeFormPanel;
import iuh.fit.gui.form.DoiXeSuaFormPanel;
import iuh.fit.gui.form.DoiXeTaoFormPanel;
import iuh.fit.gui.form.DongXeFormPanel;
import iuh.fit.gui.form.DongXeSuaFormPanel;
import iuh.fit.gui.form.DongXeTaoFormPanel;
import iuh.fit.gui.form.HangXeFormPanel;
import iuh.fit.gui.form.HangXeSuaFormPanel;
import iuh.fit.gui.form.HangXeTaoFormPanel;
import iuh.fit.service.DanhMucXeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DanhMucXePage extends AbstractPage {

  


  /** Dialog tạo mới: 1 trường nhập. */
  private static final int CHIEU_CAO_TAO_HANG = 240;

  /** Dialog cập nhật: 2 trường (mã chỉ đọc + tên). */
  private static final int CHIEU_CAO_SUA_HANG = 280;

  /** Dialog tạo mới: 2 trường nhập. */
  private static final int CHIEU_CAO_TAO_DONG = 300;

  /** Dialog cập nhật: 3 trường (mã chỉ đọc + hãng + tên). */
  private static final int CHIEU_CAO_SUA_DONG = 360;

  /** Dialog tạo mới: 3 trường nhập. */
  private static final int CHIEU_CAO_TAO_DOI = 380;

  /** Dialog cập nhật: 4 trường (mã chỉ đọc + hãng + dòng + năm). */
  private static final int CHIEU_CAO_SUA_DOI = 440;

  private static final int COT_MA = 1;
  private static final int COT_HANG_TEN = 2;
  private static final int COT_DONG_HANG = 2;
  private static final int COT_DONG_TEN = 3;
  private static final int COT_DOI_HANG = 2;
  private static final int COT_DOI_DONG = 3;
  private static final int COT_DOI_NAM = 4;

  private final DanhMucXeService danhMucXeService = new DanhMucXeService();

  private ManagementPanel pnlHang;
  private ManagementPanel pnlDong;
  private ManagementPanel pnlDoi;

  private ManagementPanel.BoLocChon boLocHangDong;
  private ManagementPanel.BoLocChon boLocHangDoi;
  private ManagementPanel.BoLocChon boLocDongDoi;

  private List<Object[]> duLieuHang = new ArrayList<>();
  private List<Object[]> duLieuDong = new ArrayList<>();
  private List<Object[]> duLieuDoi = new ArrayList<>();

  private int trangHang = 1;
  private int trangDong = 1;
  private int trangDoi = 1;

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

    this.trangHang = 1;
    this.trangDong = 1;
    this.trangDoi = 1;
    this.taiLaiTatCa();

    this.revalidate();
    this.repaint();
  }

  @Override
  public String getTitleKey() {
    return "app.menu.vehicleCatalog";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }

  // ==================== DỰNG TAB ====================

  private JPanel taoTabHangXe() {
    this.pnlHang = new ManagementPanel();
    this.pnlHang.setListTitle("app.danhMucXe.hangTitle");
    this.pnlHang.enableMultiSelection(true);
    this.pnlHang.setHeaders(
            I18nReaderUtil.get("app.danhMucXe.ma"),
            I18nReaderUtil.get("app.danhMucXe.ten"));

    this.pnlHang.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSuaHangXe),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoaHangXe));
    this.pnlHang.setAddAction("app.common.add_new", this::moDialogThemHangXe);
    this.pnlHang.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieuHangXe);
    this.pnlHang.setRowDoubleClickListener(this::moDialogSuaHangXe);
    this.pnlHang.setFilterChangeListener(() -> {
      this.trangHang = 1;
      this.taiLaiHangXe();
    });
    this.pnlHang.getPrevButton().addActionListener(e -> this.doiTrangHang(-1));
    this.pnlHang.getNextButton().addActionListener(e -> this.doiTrangHang(1));
    this.pnlHang.getClearFilterButton().addActionListener(e -> {
      this.pnlHang.clearFilters();
      this.trangHang = 1;
      this.taiLaiHangXe();
    });

    return this.pnlHang;
  }

  private JPanel taoTabDongXe() {
    this.pnlDong = new ManagementPanel();
    this.pnlDong.setListTitle("app.danhMucXe.dongTitle");
    this.pnlDong.enableMultiSelection(true);
    this.pnlDong.setHeaders(
            I18nReaderUtil.get("app.danhMucXe.ma"),
            I18nReaderUtil.get("app.danhMucXe.hang"),
            I18nReaderUtil.get("app.danhMucXe.ten"));

    this.pnlDong.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSuaDongXe),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoaDongXe));
    this.pnlDong.setAddAction("app.common.add_new", this::moDialogThemDongXe);
    this.pnlDong.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieuDongXe);
    this.pnlDong.setRowDoubleClickListener(this::moDialogSuaDongXe);
    this.boLocHangDong = this.pnlDong.addComboBoxFilter("app.danhMucXe.hang", new Object[0]);
    this.pnlDong.setFilterChangeListener(() -> {
      this.trangDong = 1;
      this.taiLaiDongXe();
    });
    this.pnlDong.getPrevButton().addActionListener(e -> this.doiTrangDong(-1));
    this.pnlDong.getNextButton().addActionListener(e -> this.doiTrangDong(1));
    this.pnlDong.getClearFilterButton().addActionListener(e -> {
      this.pnlDong.clearFilters();
      this.trangDong = 1;
      this.taiLaiDongXe();
    });

    return this.pnlDong;
  }

  private JPanel taoTabDoiXe() {
    this.pnlDoi = new ManagementPanel();
    this.pnlDoi.setListTitle("app.danhMucXe.doiTitle");
    this.pnlDoi.enableMultiSelection(true);
    this.pnlDoi.setHeaders(
            I18nReaderUtil.get("app.danhMucXe.ma"),
            I18nReaderUtil.get("app.danhMucXe.hang"),
            I18nReaderUtil.get("app.danhMucXe.dong"),
            I18nReaderUtil.get("app.danhMucXe.nam"));

    this.pnlDoi.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSuaDoiXe),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoaDoiXe));
    this.pnlDoi.setAddAction("app.common.add_new", this::moDialogThemDoiXe);
    this.pnlDoi.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieuDoiXe);
    this.pnlDoi.setRowDoubleClickListener(this::moDialogSuaDoiXe);
    this.boLocHangDoi = this.pnlDoi.addComboBoxFilter("app.danhMucXe.hang", new Object[0]);
    this.boLocDongDoi = this.pnlDoi.addComboBoxFilter("app.danhMucXe.dong", new Object[0]);
    this.boLocHangDoi.combo().addActionListener(e -> this.napBoLocDong());
    this.pnlDoi.setFilterChangeListener(() -> {
      this.trangDoi = 1;
      this.taiLaiDoiXe();
    });
    this.pnlDoi.getPrevButton().addActionListener(e -> this.doiTrangDoi(-1));
    this.pnlDoi.getNextButton().addActionListener(e -> this.doiTrangDoi(1));
    this.pnlDoi.getClearFilterButton().addActionListener(e -> {
      this.pnlDoi.clearFilters();
      this.trangDoi = 1;
      this.taiLaiDoiXe();
    });

    return this.pnlDoi;
  }

  // ==================== NẠP DỮ LIỆU ====================

  private void taiLaiTatCa() {
    try {
      List<HangXe> danhSachHang = this.danhMucXeService.getDanhSachHangXe();
      this.napBoLocHang(this.boLocHangDong, danhSachHang);
      this.napBoLocHang(this.boLocHangDoi, danhSachHang);
      this.napBoLocDong();
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
      return;
    }

    this.taiLaiHangXe();
    this.taiLaiDongXe();
    this.taiLaiDoiXe();
  }

  private void taiLaiHangXe() {
    try {
      List<Object[]> rows = new ArrayList<>();
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        rows.add(new Object[] {hangXe.getMaHangXe(), hangXe.getTen()});
      }
      this.duLieuHang = rows;
      this.trangHang = this.hienThi(this.pnlHang, rows, this.trangHang);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void taiLaiDongXe() {
    try {
      HangXe hangXe = this.chonHang(this.boLocHangDong);
      List<Object[]> rows = new ArrayList<>();
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(
              hangXe == null ? null : hangXe.getMaHangXe())) {
        rows.add(new Object[] {dongXe.getMaDongXe(), dongXe.getTenHangXe(), dongXe.getTen()});
      }
      this.duLieuDong = rows;
      this.trangDong = this.hienThi(this.pnlDong, rows, this.trangDong);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void taiLaiDoiXe() {
    try {
      HangXe hangXe = this.chonHang(this.boLocHangDoi);
      DongXe dongXe = this.chonDong(this.boLocDongDoi);
      List<Object[]> rows = new ArrayList<>();
      for (DoiXe doiXe : this.danhMucXeService.getDanhSachDoiXe(
              dongXe == null ? null : dongXe.getMaDongXe())) {
        if (hangXe != null && !hangXe.getMaHangXe().equals(doiXe.getMaHangXe())) {
          continue;
        }
        rows.add(new Object[] {
            doiXe.getMaDoiXe(),
            doiXe.getTenHangXe(),
            doiXe.getTenDongXe(),
            doiXe.getNamSX()});
      }
      this.duLieuDoi = rows;
      this.trangDoi = this.hienThi(this.pnlDoi, rows, this.trangDoi);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void napBoLocHang(ManagementPanel.BoLocChon boLoc, List<HangXe> danhSach) {
    String maDangChon = this.maHangDangChon(boLoc);
    JComboBox<Object> combo = boLoc.combo();
    combo.removeAllItems();
    combo.addItem(null);
    for (HangXe hangXe : danhSach) {
      combo.addItem(hangXe);
    }
    this.chonLaiHang(combo, maDangChon);
  }

  private void napBoLocDong() {
    try {
      HangXe hangXe = this.chonHang(this.boLocHangDoi);
      List<DongXe> danhSach = this.danhMucXeService.getDanhSachDongXe(
              hangXe == null ? null : hangXe.getMaHangXe());

      String maDangChon = null;
      Object giaTri = this.boLocDongDoi.giaTri();
      if (giaTri instanceof DongXe dongXe) {
        maDangChon = dongXe.getMaDongXe();
      }

      JComboBox<Object> combo = this.boLocDongDoi.combo();
      combo.removeAllItems();
      combo.addItem(null);
      for (DongXe dongXe : danhSach) {
        combo.addItem(dongXe);
      }
      this.chonLaiDong(combo, maDangChon);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void chonLaiHang(JComboBox<Object> combo, String maHangXe) {
    combo.setSelectedIndex(0);
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < combo.getItemCount(); i++) {
      if (combo.getItemAt(i) instanceof HangXe hangXe && maHangXe.equals(hangXe.getMaHangXe())) {
        combo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonLaiDong(JComboBox<Object> combo, String maDongXe) {
    combo.setSelectedIndex(0);
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < combo.getItemCount(); i++) {
      if (combo.getItemAt(i) instanceof DongXe dongXe && maDongXe.equals(dongXe.getMaDongXe())) {
        combo.setSelectedIndex(i);
        return;
      }
    }
  }

  private HangXe chonHang(ManagementPanel.BoLocChon boLoc) {
    Object giaTri = boLoc.giaTri();
    return giaTri instanceof HangXe hangXe ? hangXe : null;
  }

  private DongXe chonDong(ManagementPanel.BoLocChon boLoc) {
    Object giaTri = boLoc.giaTri();
    return giaTri instanceof DongXe dongXe ? dongXe : null;
  }

  private String maHangDangChon(ManagementPanel.BoLocChon boLoc) {
    HangXe hangXe = this.chonHang(boLoc);
    return hangXe == null ? null : hangXe.getMaHangXe();
  }

  /**
   * Áp tìm nhanh và phân trang phía client trên danh sách đã tải.
   *
   * @return trang thực tế đã hiển thị (đã kẹp trong khoảng hợp lệ)
   */
  private int hienThi(ManagementPanel panel, List<Object[]> duLieu, int trang) {
    String tuKhoa = panel.getQuickFilter();
    List<Object[]> ketQua = new ArrayList<>();

    if (tuKhoa == null || tuKhoa.isBlank()) {
      ketQua.addAll(duLieu);
    } else {
      String chuanHoa = tuKhoa.trim().toLowerCase(Locale.ROOT);
      for (Object[] row : duLieu) {
        for (Object giaTri : row) {
          if (NumberParseUtil.textOf(giaTri).toLowerCase(Locale.ROOT).contains(chuanHoa)) {
            ketQua.add(row);
            break;
          }
        }
      }
    }

    int tong = ketQua.size();
    int tongTrang = Math.max(1, (tong + UiConstants.SO_DONG_MOI_TRANG - 1) / UiConstants.SO_DONG_MOI_TRANG);
    int trangHienTai = Math.min(Math.max(trang, 1), tongTrang);
    int tu = (trangHienTai - 1) * UiConstants.SO_DONG_MOI_TRANG;
    int den = Math.min(tu + UiConstants.SO_DONG_MOI_TRANG, tong);

    panel.setRows(new ArrayList<>(ketQua.subList(tu, den)));
    panel.setCount(tong == 0 ? 0 : tu + 1, den, tong);
    panel.setPage(trangHienTai, tongTrang);
    return trangHienTai;
  }

  private void doiTrangHang(int buoc) {
    this.trangHang = this.hienThi(this.pnlHang, this.duLieuHang, this.trangHang + buoc);
  }

  private void doiTrangDong(int buoc) {
    this.trangDong = this.hienThi(this.pnlDong, this.duLieuDong, this.trangDong + buoc);
  }

  private void doiTrangDoi(int buoc) {
    this.trangDoi = this.hienThi(this.pnlDoi, this.duLieuDoi, this.trangDoi + buoc);
  }

  // ==================== DIALOG ====================

  private void moDialogThemHangXe() {
    HangXeTaoFormPanel form = new HangXeTaoFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.danhMucXe.themHangTitle",
            form,
            () -> this.taoHangXe(form),
            CHIEU_CAO_TAO_HANG));

    if (dialog.showAndGet()) {
      this.taiLaiTatCa();
    }
  }

  private void moDialogSuaHangXe(int row) {
    String ma = this.layMa(this.pnlHang, row);

    try {
      HangXe hangXe = this.danhMucXeService.getHangXe(ma);
      HangXeSuaFormPanel form = new HangXeSuaFormPanel();
      form.napDuLieu(hangXe);

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.suaHangTitle",
              form,
              () -> this.capNhatHangXe(form),
              CHIEU_CAO_SUA_HANG));

      if (dialog.showAndGet()) {
        this.taiLaiTatCa();
      }
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogThemDongXe() {
    try {
      List<HangXe> danhSachHang = this.danhMucXeService.getDanhSachHangXe();
      if (danhSachHang.isEmpty()) {
        ThongBao.canhBao(this, "app.danhMucXe.chuaCoHangXe");
        return;
      }

      DongXeTaoFormPanel form = new DongXeTaoFormPanel();
      form.napDanhSachHang(danhSachHang);

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.themDongTitle",
              form,
              () -> this.taoDongXe(form),
              CHIEU_CAO_TAO_DONG));

      if (dialog.showAndGet()) {
        this.taiLaiTatCa();
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogSuaDongXe(int row) {
    String ma = this.layMa(this.pnlDong, row);

    try {
      DongXe dongXe = this.danhMucXeService.getDongXe(ma);
      DongXeSuaFormPanel form = new DongXeSuaFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      form.napDuLieu(dongXe);

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.suaDongTitle",
              form,
              () -> this.capNhatDongXe(form),
              CHIEU_CAO_SUA_DONG));

      if (dialog.showAndGet()) {
        this.taiLaiTatCa();
      }
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogThemDoiXe() {
    try {
      List<HangXe> danhSachHang = this.danhMucXeService.getDanhSachHangXe();
      if (danhSachHang.isEmpty()) {
        ThongBao.canhBao(this, "app.danhMucXe.chuaCoHangXe");
        return;
      }

      DoiXeTaoFormPanel form = new DoiXeTaoFormPanel();
      form.napDanhSachHang(danhSachHang);

      List<DongXe> danhSachDong = this.danhMucXeService.getDanhSachDongXe(form.layMaHangXe());
      if (danhSachDong.isEmpty()) {
        ThongBao.canhBao(this, "app.danhMucXe.chuaCoDongXe");
        return;
      }
      form.napDanhSachDong(danhSachDong);
      form.getHangXeCombo().addActionListener(e -> this.napDongChoForm(form));

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.themDoiTitle",
              form,
              () -> this.taoDoiXe(form),
              CHIEU_CAO_TAO_DOI));

      if (dialog.showAndGet()) {
        this.taiLaiTatCa();
      }
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void moDialogSuaDoiXe(int row) {
    String ma = this.layMa(this.pnlDoi, row);

    try {
      DoiXe doiXe = this.danhMucXeService.getDoiXe(ma);
      DoiXeSuaFormPanel form = new DoiXeSuaFormPanel();
      form.napDanhSachHang(this.danhMucXeService.getDanhSachHangXe());
      form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(doiXe.getMaHangXe()));
      form.napDuLieu(doiXe);
      form.getHangXeCombo().addActionListener(e -> this.napDongChoForm(form));

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.danhMucXe.suaDoiTitle",
              form,
              () -> this.capNhatDoiXe(form),
              CHIEU_CAO_SUA_DOI));

      if (dialog.showAndGet()) {
        this.taiLaiTatCa();
      }
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void napDongChoForm(DoiXeFormPanel form) {
    try {
      form.napDanhSachDong(this.danhMucXeService.getDanhSachDongXe(form.layMaHangXe()));
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  // ==================== LƯU ====================

  private boolean taoHangXe(HangXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    try {
      this.danhMucXeService.taoHangXe(form.layDuLieu().getTen());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiTen(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatHangXe(HangXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    try {
      this.danhMucXeService.capNhatHangXe(form.layDuLieu());
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiTen(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean taoDongXe(DongXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    DongXe data = form.layDuLieu();

    try {
      this.danhMucXeService.taoDongXe(data.getMaHangXe(), data.getTen());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiTen(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatDongXe(DongXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    try {
      this.danhMucXeService.capNhatDongXe(form.layDuLieu());
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiTen(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean taoDoiXe(DoiXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    DoiXe data = form.layDuLieu();

    try {
      this.danhMucXeService.taoDoiXe(data.getMaDongXe(), data.getNamSX());
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiNam(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatDoiXe(DoiXeFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    try {
      this.danhMucXeService.capNhatDoiXe(form.layDuLieu());
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      return this.xuLyLoiNam(form, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(HangXeFormPanel form) {
    if (form.validateInput()) {
      return true;
    }
    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  private boolean kiemTraForm(DongXeFormPanel form) {
    if (form.validateInput()) {
      return true;
    }
    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  private boolean kiemTraForm(DoiXeFormPanel form) {
    if (form.validateInput()) {
      return true;
    }
    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  private boolean xuLyLoiTen(HangXeFormPanel form, IllegalArgumentException e) {
    if (this.laLoiTen(e)) {
      form.hienThiLoiTen(I18nReaderUtil.get(e.getMessage()));
      return false;
    }
    ThongBao.loi(this, e);
    return false;
  }

  private boolean xuLyLoiTen(DongXeFormPanel form, IllegalArgumentException e) {
    if (this.laLoiTen(e)) {
      form.hienThiLoiTen(I18nReaderUtil.get(e.getMessage()));
      return false;
    }
    ThongBao.loi(this, e);
    return false;
  }

  private boolean xuLyLoiNam(DoiXeFormPanel form, IllegalArgumentException e) {
    if ("app.danhMucXe.loiNamSX".equals(e.getMessage())) {
      form.hienThiLoiNam(I18nReaderUtil.get(e.getMessage()));
      return false;
    }
    ThongBao.loi(this, e);
    return false;
  }

  private boolean laLoiTen(IllegalArgumentException e) {
    String key = e.getMessage();
    return "app.danhMucXe.loiTen".equals(key)
            || "app.danhMucXe.loiTenQuaDai".equals(key)
            || "app.danhMucXe.loiTrungTen".equals(key);
  }

  // ==================== XÓA ====================

  private void xoaHangXe(int row) {
    String ma = this.layMa(this.pnlHang, row);
    if (ma.isBlank()) {
      ThongBao.canhBao(this, "app.danhMucXe.chuaChonXoa");
      return;
    }

    String ten = this.layChuoi(this.pnlHang, row, COT_HANG_TEN);
    if (!ThongBao.xacNhanDinhDang(this,
            "app.danhMucXe.xacNhanXoaTen",
            "app.danhMucXe.tieuDeXoa",
            ten)) {
      return;
    }

    try {
      this.danhMucXeService.xoaHangXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiTatCa();
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaDongXe(int row) {
    String ma = this.layMa(this.pnlDong, row);
    if (ma.isBlank()) {
      ThongBao.canhBao(this, "app.danhMucXe.chuaChonXoa");
      return;
    }

    String ten = this.layChuoi(this.pnlDong, row, COT_DONG_HANG)
            + " - " + this.layChuoi(this.pnlDong, row, COT_DONG_TEN);
    if (!ThongBao.xacNhanDinhDang(this,
            "app.danhMucXe.xacNhanXoaTen",
            "app.danhMucXe.tieuDeXoa",
            ten)) {
      return;
    }

    try {
      this.danhMucXeService.xoaDongXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiTatCa();
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaDoiXe(int row) {
    String ma = this.layMa(this.pnlDoi, row);
    if (ma.isBlank()) {
      ThongBao.canhBao(this, "app.danhMucXe.chuaChonXoa");
      return;
    }

    String ten = this.layChuoi(this.pnlDoi, row, COT_DOI_HANG)
            + " - " + this.layChuoi(this.pnlDoi, row, COT_DOI_DONG)
            + " - " + this.layChuoi(this.pnlDoi, row, COT_DOI_NAM);
    if (!ThongBao.xacNhanDinhDang(this,
            "app.danhMucXe.xacNhanXoaTen",
            "app.danhMucXe.tieuDeXoa",
            ten)) {
      return;
    }

    try {
      this.danhMucXeService.xoaDoiXe(ma);
      ThongBao.thongTin(this, "app.common.delete_succeed");
      this.taiLaiTatCa();
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void xoaNhieuHangXe(List<Integer> rows) {
    if (!this.xacNhanXoaNhieu(rows)) {
      return;
    }

    int thanhCong = 0;
    boolean loiHeThong = false;

    for (int row : rows) {
      try {
        this.danhMucXeService.xoaHangXe(this.layMa(this.pnlHang, row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Bản ghi đang được tham chiếu: ghi nhận vào báo cáo tổng hợp.
      } catch (SQLException e) {
        loiHeThong = true;
        break;
      }
    }

    this.baoKetQuaHangLoat(thanhCong, rows.size(), loiHeThong);
    this.taiLaiTatCa();
  }

  private void xoaNhieuDongXe(List<Integer> rows) {
    if (!this.xacNhanXoaNhieu(rows)) {
      return;
    }

    int thanhCong = 0;
    boolean loiHeThong = false;

    for (int row : rows) {
      try {
        this.danhMucXeService.xoaDongXe(this.layMa(this.pnlDong, row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Bản ghi đang được tham chiếu: ghi nhận vào báo cáo tổng hợp.
      } catch (SQLException e) {
        loiHeThong = true;
        break;
      }
    }

    this.baoKetQuaHangLoat(thanhCong, rows.size(), loiHeThong);
    this.taiLaiTatCa();
  }

  private void xoaNhieuDoiXe(List<Integer> rows) {
    if (!this.xacNhanXoaNhieu(rows)) {
      return;
    }

    int thanhCong = 0;
    boolean loiHeThong = false;

    for (int row : rows) {
      try {
        this.danhMucXeService.xoaDoiXe(this.layMa(this.pnlDoi, row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Bản ghi đang được tham chiếu: ghi nhận vào báo cáo tổng hợp.
      } catch (SQLException e) {
        loiHeThong = true;
        break;
      }
    }

    this.baoKetQuaHangLoat(thanhCong, rows.size(), loiHeThong);
    this.taiLaiTatCa();
  }

  private boolean xacNhanXoaNhieu(List<Integer> rows) {
    if (rows.isEmpty()) {
      return false;
    }
    return ThongBao.xacNhanDinhDang(this,
            "app.common.batch_delete_confirm",
            "app.common.confirm",
            rows.size());
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

  // ==================== TIỆN ÍCH ====================

  private String layMa(ManagementPanel panel, int row) {
    return NumberParseUtil.textOf(panel.getModel().getValueAt(row, COT_MA));
  }

  private String layChuoi(ManagementPanel panel, int row, int cot) {
    return NumberParseUtil.textOf(panel.getModel().getValueAt(row, cot));
  }
}
