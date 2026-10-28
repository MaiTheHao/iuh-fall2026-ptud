package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachNhanVienCriteria;
import iuh.fit.dto.NhanVienFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.NhanVienFormPanel;
import iuh.fit.gui.form.NhanVienSuaFormPanel;
import iuh.fit.gui.form.NhanVienTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.NhanVienService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NhanVienPage extends AbstractPage {

  private static final long serialVersionUID = 1L;

  private static final int PAGE_SIZE = 20;

  /** Dialog tạo mới: 6 trường nhập + 1 ảnh. */
  private static final int CHIEU_CAO_TAO = 800;

  /** Dialog cập nhật: 8 trường nhập + 1 ảnh. */
  private static final int CHIEU_CAO_SUA = 800;

  private static final int COT_ANH = 1;
  private static final int COT_MA = 2;

  private final NhanVienService nhanVienService = new NhanVienService();

  private ManagementPanel managementPanel;

  private JTextField boLocMa;
  private JTextField boLocHoTen;
  private JTextField boLocSoDienThoai;
  private ManagementPanel.BoLocChon boLocVaiTro;
  private ManagementPanel.BoLocChon boLocTrangThai;
  private ManagementPanel.BoLocNgay boLocNgayTao;

  private int trangHienTai = 1;

  public NhanVienPage() {
    setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    removeAll();

    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.managementPanel = new ManagementPanel();
    this.managementPanel.setListTitle("app.nhanVien.danhSachTitle");
    this.managementPanel.enableMultiSelection(true);

    this.managementPanel.setHeaders(
            I18nReaderUtil.get("app.nhanVien.hinhAnh"),
            I18nReaderUtil.get("app.nhanVien.maNhanVien"),
            I18nReaderUtil.get("app.nhanVien.hoTen"),
            I18nReaderUtil.get("app.nhanVien.soDienThoai"),
            I18nReaderUtil.get("app.nhanVien.vaiTro"),
            I18nReaderUtil.get("app.nhanVien.trangThai"));

    this.managementPanel.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSua),
            new ThaoTac("trash-2",
                    "app.common.delete",
                    ThaoTac.KieuThaoTac.NGUY_HIEM,
                    this::xoa));

    this.managementPanel.setAddAction("app.common.add_new", this::moDialogThem);
    this.managementPanel.setBatchAction("app.common.batch_delete",
            "trash-2",
            ThaoTac.KieuThaoTac.NGUY_HIEM,
            this::xoaNhieu);
    this.managementPanel.setRowDoubleClickListener(this::moDialogSua);

    this.khoiTaoBoLoc();
    this.ganSuKien();

    this.trangHienTai = 1;
    this.taiLaiDanhSach();

    this.add(this.managementPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
  }

  @Override
  public String getTitleKey() {
    return "app.menu.employees";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }

  @Override
  public boolean hasSeparatorBefore() {
    return true;
  }

  // ==================== BỘ LỌC ====================

  private void khoiTaoBoLoc() {
    this.boLocMa = this.managementPanel.addTextFilter("app.nhanVien.maNhanVien");
    this.boLocHoTen = this.managementPanel.addTextFilter("app.nhanVien.hoTen");
    this.boLocSoDienThoai = this.managementPanel.addTextFilter("app.nhanVien.soDienThoai");
    this.boLocVaiTro = this.managementPanel.addComboBoxFilter("app.nhanVien.vaiTro", VaiTro.values());
    this.boLocTrangThai = this.managementPanel.addComboBoxFilter("app.nhanVien.trangThai",
            TrangThaiNhanVien.values());
    this.boLocNgayTao = this.managementPanel.addDateRangeFilter("app.nhanVien.ngayTao");

    this.managementPanel.setFilterChangeListener(() -> {
      this.trangHienTai = 1;
      this.taiLaiDanhSach();
    });
  }

  private String layGiaTriBoLoc(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }

  private String layTimNhanh() {
    String value = this.managementPanel.getQuickFilter();

    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }

  private void ganSuKien() {
    this.managementPanel.getClearFilterButton().addActionListener(e -> {
      this.trangHienTai = 1;
      this.managementPanel.clearFilters();
    });

    this.managementPanel.getPrevButton().addActionListener(e -> {
      if (this.trangHienTai > 1) {
        this.trangHienTai--;
        this.taiLaiDanhSach();
      }
    });

    this.managementPanel.getNextButton().addActionListener(e -> {
      this.trangHienTai++;
      this.taiLaiDanhSach();
    });
  }

  // ==================== TẢI DANH SÁCH ====================

  private void taiLaiDanhSach() {
    try {
      DanhSachNhanVienCriteria criteria = new DanhSachNhanVienCriteria(
              this.layGiaTriBoLoc(this.boLocMa.getText()),
              this.layGiaTriBoLoc(this.boLocHoTen.getText()),
              this.layGiaTriBoLoc(this.boLocSoDienThoai.getText()),
              this.boLocVaiTro.ma(),
              this.boLocTrangThai.ma(),
              this.layTimNhanh(),
              this.boLocNgayTao.tuNgay(),
              this.boLocNgayTao.denNgay(),
              Math.max(this.trangHienTai, 1),
              PAGE_SIZE);

      PaginatedResult<NhanVien> ketQua = this.nhanVienService.search(criteria);

      if (ketQua == null) {
        return;
      }

      if (ketQua.totalPages() > 0 && this.trangHienTai > ketQua.totalPages()) {
        this.trangHienTai = ketQua.totalPages();
        this.taiLaiDanhSach();
        return;
      }

      this.hienThiDanhSach(ketQua);

    } catch (SQLException ex) {
      ThongBao.loi(this, "app.nhanVien.loiTaiDanhSach");
    }
  }

  private void hienThiDanhSach(PaginatedResult<NhanVien> ketQua) {
    List<Object[]> rows = new ArrayList<>();

    for (NhanVien nv : ketQua.items()) {
      rows.add(new Object[] {
          nv.getAnhDaiDien(),
          nv.getMaNhanVien(),
          nv.getHoTen(),
          nv.getSoDienThoai() == null ? "" : nv.getSoDienThoai(),
          nv.getVaiTro() == null ? "" : nv.getVaiTro().getI18NBusinessName(),
          nv.getTrangThai() == null ? "" : nv.getTrangThai().getI18NBusinessName()
      });
    }

    this.managementPanel.setRows(rows);
    this.cauHinhCotAnh();

    this.managementPanel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
    this.managementPanel.setPage(ketQua.page(), Math.max(1, ketQua.totalPages()));

    this.trangHienTai = ketQua.page();
  }

  private void cauHinhCotAnh() {
    AnhTableCellRenderer.ganVao(this.managementPanel,
            COT_ANH,
            "app.nhanVien.chuaCoAnh",
            "app.nhanVien.khongTaiDuocAnh");
  }

  private String layMaDong(int row) {
    return NumberParseUtil.textOf(this.managementPanel.getModel().getValueAt(row, COT_MA));
  }

  // ==================== DIALOG ====================

  private void moDialogThem() {
    NhanVienTaoFormPanel form = new NhanVienTaoFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.nhanVien.themTitle",
            form,
            () -> this.taoNhanVien(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.trangHienTai = 1;
      this.taiLaiDanhSach();
    }
  }

  private void moDialogSua(int row) {
    String ma = this.layMaDong(row);

    try {
      NhanVien nhanVien = this.nhanVienService.getById(ma);
      NhanVienSuaFormPanel form = new NhanVienSuaFormPanel();
      form.napDuLieu(nhanVien);

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.nhanVien.suaTitle",
              form,
              () -> this.capNhatNhanVien(form, nhanVien),
              CHIEU_CAO_SUA));

      if (dialog.showAndGet()) {
        this.taiLaiDanhSach();
      }
    } catch (SQLException | IllegalArgumentException ex) {
      ThongBao.loi(this, "app.nhanVien.loiLayThongTin");
    }
  }

  private boolean taoNhanVien(NhanVienFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    NhanVienFormData data = form.layDuLieu();

    try {
      NhanVien nv = new NhanVien(data.maNhanVien(),
              data.tenDangNhap(),
              data.matKhau(),
              data.hoTen(),
              this.chuanHoaRong(data.soDienThoai()),
              this.chuanHoaRong(data.anhDaiDien()),
              data.vaiTro(),
              data.trangThai(),
              LocalDateTime.now());

      this.nhanVienService.create(nv);
      ThongBao.thongTin(this, "app.nhanVien.themThanhCong");
      return true;
    } catch (SQLException | IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
    }

    return false;
  }

  private boolean capNhatNhanVien(NhanVienFormPanel form, NhanVien nhanVien) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    NhanVienFormData data = form.layDuLieu();

    try {
      nhanVien.setHoTen(data.hoTen());
      nhanVien.setSoDienThoai(this.chuanHoaRong(data.soDienThoai()));
      nhanVien.setAnhDaiDien(this.chuanHoaRong(data.anhDaiDien()));
      nhanVien.setVaiTro(data.vaiTro());
      nhanVien.setTrangThai(data.trangThai());
      nhanVien.setMatKhau(data.matKhau().isBlank() ? null : data.matKhau());

      this.nhanVienService.update(nhanVien);
      ThongBao.thongTin(this, "app.nhanVien.capNhatThanhCong");
      return true;
    } catch (SQLException | IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
    }

    return false;
  }

  private String chuanHoaRong(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }

  private boolean kiemTraForm(NhanVienFormPanel form) {
    if (form.validateInput()) {
      return true;
    }

    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }

  // ==================== XÓA ====================

  private void xoa(int row) {
    String ma = this.layMaDong(row);

    if (ma.isBlank()) {
      ThongBao.loi(this, "app.nhanVien.chonNhanVienCanXoa");
      return;
    }

    if (!ThongBao.xacNhan(this, "app.nhanVien.xacNhanXoa", "app.common.confirm")) {
      return;
    }

    try {
      this.nhanVienService.delete(ma);
      ThongBao.thongTin(this, "app.nhanVien.xoaThanhCong");
      this.taiLaiDanhSach();
    } catch (IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
      ThongBao.canhBao(this, "app.nhanVien.goiYNghiViec");
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
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
        this.nhanVienService.delete(this.layMaDong(row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Nhân viên đã có giao dịch: ghi nhận vào báo cáo tổng hợp.
      } catch (SQLException ex) {
        loiHeThong = true;
        break;
      }
    }

    if (loiHeThong) {
      ThongBao.loi(this, "app.login.errorSystem");
    } else if (thanhCong == rows.size()) {
      ThongBao.thongTin(this, "app.nhanVien.xoaThanhCong");
    } else {
      ThongBao.canhBaoDinhDang(this, "app.common.batch_delete_result", thanhCong, rows.size());
      ThongBao.canhBao(this, "app.nhanVien.goiYNghiViec");
    }

    this.taiLaiDanhSach();
  }
}
