package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachNhaCungCapCriteria;
import iuh.fit.dto.NhaCungCapFormData;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhaCungCap;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.NhaCungCapFormPanel;
import iuh.fit.gui.form.NhaCungCapSuaFormPanel;
import iuh.fit.gui.form.NhaCungCapTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.NhaCungCapService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class NhaCungCapPage extends AbstractPage {

  private static final long serialVersionUID = 1L;

  private static final int COT_MA = 1;

  private static final int PAGE_SIZE = PaginatedResult.DEFAULT_PAGE_SIZE;
  private static final int CHIEU_CAO_TAO = 640;
  private static final int CHIEU_CAO_SUA = 720;

  private final NhaCungCapService service = new NhaCungCapService();

  private ManagementPanel pnlDanhSach;

  private JTextField txtBoLocMa;
  private JTextField txtBoLocTen;
  private JTextField txtBoLocSdt;
  private ManagementPanel.BoLocChon boLocTrangThai;
  private ManagementPanel.BoLocNgay boLocNgayTao;

  private int trangHienTai = 1;

  public NhaCungCapPage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.pnlDanhSach = new ManagementPanel();
    this.pnlDanhSach.setListTitle("app.nhaCungCap.danhSachTitle");
    this.pnlDanhSach.enableMultiSelection(true);

    this.pnlDanhSach.setHeaders(
            I18nReaderUtil.get("app.nhaCungCap.ma"),
            I18nReaderUtil.get("app.nhaCungCap.ten"),
            I18nReaderUtil.get("app.nhaCungCap.soDienThoai"),
            I18nReaderUtil.get("app.nhaCungCap.email"),
            I18nReaderUtil.get("app.nhaCungCap.diaChi"),
            I18nReaderUtil.get("app.nhaCungCap.trangThai"),
            I18nReaderUtil.get("app.nhaCungCap.ghiChu"));

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
    this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai(1));

    this.pnlDanhSach.getClearFilterButton().addActionListener(e -> this.pnlDanhSach.clearFilters());
    this.pnlDanhSach.getPrevButton().addActionListener(e -> this.taiLai(this.trangHienTai - 1));
    this.pnlDanhSach.getNextButton().addActionListener(e -> this.taiLai(this.trangHienTai + 1));

    this.taiLai(1);

    this.add(this.pnlDanhSach, BorderLayout.CENTER);
    this.revalidate();
    this.repaint();
  }

  @Override
  public String getTitleKey() {
    return "app.menu.suppliers";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }

  private void khoiTaoBoLoc() {
    this.txtBoLocMa = this.pnlDanhSach.addTextFilter("app.nhaCungCap.ma");
    this.txtBoLocTen = this.pnlDanhSach.addTextFilter("app.nhaCungCap.ten");
    this.txtBoLocSdt = this.pnlDanhSach.addTextFilter("app.nhaCungCap.soDienThoai");
    this.boLocTrangThai = this.pnlDanhSach.addComboBoxFilter("app.nhaCungCap.trangThai", TrangThaiNhaCungCap.values());
    this.boLocNgayTao = this.pnlDanhSach.addDateRangeFilter("app.nhaCungCap.ngayTao");
  }

  private void taiLai(int page) {
    try {
      DanhSachNhaCungCapCriteria tieuChi = new DanhSachNhaCungCapCriteria(
              this.txtBoLocMa.getText(),
              this.txtBoLocTen.getText(),
              this.txtBoLocSdt.getText(),
              this.pnlDanhSach.getQuickFilter(),
              this.boLocTrangThai.ma(),
              this.boLocNgayTao.tuNgay(),
              this.boLocNgayTao.denNgay(),
              Math.max(page, 1),
              PAGE_SIZE);

      PaginatedResult<NhaCungCap> kq = this.service.search(tieuChi);

      if (kq.totalPages() > 0 && this.trangHienTai > kq.totalPages()) {
        this.trangHienTai = kq.totalPages();
        this.taiLai(this.trangHienTai);
        return;
      }

      this.hienThiDanhSach(kq);
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
    }
  }

  private void hienThiDanhSach(PaginatedResult<NhaCungCap> kq) {
    List<Object[]> rows = new ArrayList<>();
    for (NhaCungCap ncc : kq.items()) {
      rows.add(new Object[] {
          ncc.getMaNhaCungCap(),
          ncc.getTenNhaCungCap(),
          ncc.getSdt() == null ? "" : ncc.getSdt(),
          ncc.getEmail() == null ? "" : ncc.getEmail(),
          ncc.getDiaChi() == null ? "" : ncc.getDiaChi(),
          ncc.getTrangThai() == null ? "" : ncc.getTrangThai().getI18NBusinessName(),
          ncc.getGhiChu() == null ? "" : ncc.getGhiChu()
      });
    }
    this.pnlDanhSach.setRows(rows);
    this.pnlDanhSach.setCount(kq.fromIndex(), kq.toIndex(), kq.totalItems());
    this.pnlDanhSach.setPage(kq.page(), Math.max(1, kq.totalPages()));
    this.trangHienTai = kq.page();
  }

  private NhaCungCapFormData taoFormData(NhaCungCap ncc) {
    return new NhaCungCapFormData(
            ncc.getMaNhaCungCap(),
            ncc.getTenNhaCungCap(),
            ncc.getSdt(),
            ncc.getEmail(),
            ncc.getDiaChi(),
            ncc.getTrangThai(),
            ncc.getGhiChu());
  }

  private void moDialogThem() {
    NhaCungCapTaoFormPanel form = new NhaCungCapTaoFormPanel();
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.nhaCungCap.themTitle",
            form,
            () -> this.taoNhaCungCap(form),
            CHIEU_CAO_TAO));
    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    String ma = NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MA));
    try {
      NhaCungCap ncc = this.service.getById(ma)
              .orElseThrow(() -> new IllegalArgumentException("app.nhaCungCap.khongTimThay"));
      NhaCungCapSuaFormPanel form = new NhaCungCapSuaFormPanel();
      form.napDuLieu(this.taoFormData(ncc));
      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.nhaCungCap.suaTitle",
              form,
              () -> this.capNhatNhaCungCap(form, ncc),
              CHIEU_CAO_SUA));
      if (dialog.showAndGet()) {
        this.taiLai(this.trangHienTai);
      }
    } catch (IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
    }
  }

  private boolean taoNhaCungCap(NhaCungCapFormPanel form) {
    if (!form.validateInput()) {
      return false;
    }
    try {
      NhaCungCapFormData d = form.layDuLieu();
      NhaCungCap ncc = new NhaCungCap(null, d.tenNhaCungCap(), d.sdt(), d.email(), d.diaChi(), d.trangThai(), d.ghiChu(), null);
      this.service.create(ncc);
      return true;
    } catch (IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
      return false;
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
      return false;
    }
  }

  private boolean capNhatNhaCungCap(NhaCungCapFormPanel form, NhaCungCap ncc) {
    if (!form.validateInput()) {
      return false;
    }
    try {
      NhaCungCapFormData d = form.layDuLieu();
      ncc.setTenNhaCungCap(d.tenNhaCungCap());
      ncc.setSdt(d.sdt());
      ncc.setEmail(d.email());
      ncc.setDiaChi(d.diaChi());
      ncc.setTrangThai(d.trangThai());
      ncc.setGhiChu(d.ghiChu());
      this.service.update(ncc);
      return true;
    } catch (IllegalArgumentException ex) {
      ThongBao.loi(this, ex);
      return false;
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
      return false;
    }
  }

  private void xoa(int row) {
    String ma = NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MA));
    if (ma.isBlank()) {
      ThongBao.canhBao(this, "app.nhaCungCap.chuaChonXoa");
      return;
    }
    if (!ThongBao.xacNhan(this, "app.nhaCungCap.xacNhanXoa", "app.nhaCungCap.tieuDeXoa")) {
      return;
    }
    try {
      this.service.delete(ma);
      this.taiLai(this.trangHienTai);
    } catch (IllegalArgumentException | IllegalStateException ex) {
      ThongBao.loi(this, ex.getMessage());
    } catch (SQLException ex) {
      ThongBao.loi(this, ex);
    }
  }

  private void xoaNhieu(List<Integer> dong) {
    if (dong.isEmpty()) {
      ThongBao.canhBao(this, "app.nhaCungCap.chuaChonXoa");
      return;
    }
    if (!ThongBao.xacNhan(this, "app.nhaCungCap.xacNhanXoa", "app.nhaCungCap.tieuDeXoa")) {
      return;
    }
    for (int row : dong) {
      String ma = NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MA));
      try {
        this.service.delete(ma);
      } catch (IllegalArgumentException | IllegalStateException ex) {
        ThongBao.loi(this, ex.getMessage());
      } catch (SQLException ex) {
        ThongBao.loi(this, ex);
        break;
      }
    }
    this.taiLai(this.trangHienTai);
  }
}
