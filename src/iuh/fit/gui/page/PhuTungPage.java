package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhuTungFormData;
import iuh.fit.dto.DanhSachNhaCungCapCriteria;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiNhaCungCap;
import iuh.fit.gui.component.ChonNhaCungCapDialog;
import iuh.fit.service.NhaCungCapService;
import iuh.fit.gui.component.AnhTableCellRenderer;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhuTungFormPanel;
import iuh.fit.gui.form.PhuTungSuaFormPanel;
import iuh.fit.gui.form.PhuTungTaoFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhuTungPage extends AbstractPage {

  private static final long serialVersionUID = 1L;
  private static final int SO_DONG_MOI_TRANG = 20;

  /** Dialog tạo mới: 5 trường nhập + 1 ảnh. */
  private static final int CHIEU_CAO_TAO = 680;

  /** Dialog cập nhật: 6 trường nhập + 1 ảnh. */
  private static final int CHIEU_CAO_SUA = 800;

  private static final int COT_ANH = 1;
  private static final int COT_MA = 2;
  private static final int COT_TEN = 3;
  private static final int COT_MO_TA = 4;
  private static final int COT_DON_GIA = 5;
  private static final int COT_SO_LUONG = 6;
  private static final int COT_NCC = 7;

  private final PhuTungService phuTungService = new PhuTungService();
  private final NhaCungCapService nhaCungCapService = new NhaCungCapService();

  private ManagementPanel panel;

  private JTextField boLocMa;
  private JTextField boLocTen;
  private ManagementPanel.BoLocKhoang boLocDonGia;
  private ManagementPanel.BoLocKhoang boLocSoLuong;
  private ManagementPanel.BoLocChon boLocNhaCungCap;

  private int trang = 1;

  /** Chan taiLai de quy khi rebuild combo bo loc. */
  private boolean dangLamMoiBoLoc = false;

  public PhuTungPage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);

    this.panel = new ManagementPanel();
    this.panel.setListTitle("app.phuTung.danhSachTitle");
    this.panel.enableMultiSelection(true);

    this.panel.setHeaders(
            I18nReaderUtil.get("app.phuTung.hinhAnh"),
            I18nReaderUtil.get("app.phuTung.ma"),
            I18nReaderUtil.get("app.phuTung.ten"),
            I18nReaderUtil.get("app.phuTung.moTa"),
            I18nReaderUtil.get("app.phuTung.donGia"),
            I18nReaderUtil.get("app.phuTung.soLuongTon"),
            I18nReaderUtil.get("app.phuTung.nhaCungCap"));

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
    this.boLocMa = this.panel.addTextFilter("app.phuTung.ma");
    this.boLocTen = this.panel.addTextFilter("app.phuTung.ten");
    this.boLocDonGia = this.panel.addRangeFilter("app.phuTung.donGiaTuDen");
    this.boLocSoLuong = this.panel.addRangeFilter("app.phuTung.soLuongTuDen");
    this.boLocNhaCungCap = this.panel.addComboBoxFilter("app.phuTung.nhaCungCap", this.mangMaNhaCungCap());
    this.boLocNhaCungCap.combo().addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        lamMoiBoLocNhaCungCap();
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
    this.panel.setFilterChangeListener(() -> {
      if (!this.dangLamMoiBoLoc) {
        this.taiLai(1);
      }
    });
  }

  private String[] mangMaNhaCungCap() {
    try {
      return this.nhaCungCapService.getAllForSelection().stream()
          .filter(ncc -> ncc.getTrangThai() == TrangThaiNhaCungCap.HOAT_DONG)
          .map(NhaCungCap::getMaNhaCungCap)
          .toArray(String[]::new);
    } catch (SQLException e) {
      return new String[0];
    }
  }

  /** Tieu chi chon NCC cho phu tung: chi hien NCC dang hop tac. */
  private DanhSachNhaCungCapCriteria tieuChiChonNhaCungCap() {
    return new DanhSachNhaCungCapCriteria(
        null, null, null, null,
        TrangThaiNhaCungCap.HOAT_DONG.name(),
        null, null, 1, PaginatedResult.PAGE_SIZE_TAI_TOAN_BO);
  }

  /**
   * Giai phap stale combo: tai lai danh sach NCC moi khi mo dropdown,
   * giu lua chon hien tai neu van hop le. Co chan de quy filter-change.
   */
  private void lamMoiBoLocNhaCungCap() {
    String dangChon = this.boLocNhaCungCap.ma();
    String[] moi = this.mangMaNhaCungCap();

    JComboBox<Object> combo = this.boLocNhaCungCap.combo();
    if (combo.getItemCount() == moi.length + 1) {
      boolean giong = true;
      for (int i = 0; i < moi.length; i++) {
        if (!moi[i].equals(String.valueOf(combo.getItemAt(i + 1)))) {
          giong = false;
          break;
        }
      }
      if (giong) {
        return;
      }
    }

    this.dangLamMoiBoLoc = true;
    try {
      combo.removeAllItems();
      combo.addItem(null);
      for (String ma : moi) {
        combo.addItem(ma);
      }
      if (dangChon != null) {
        for (int i = 1; i < combo.getItemCount(); i++) {
          if (dangChon.equals(String.valueOf(combo.getItemAt(i)))) {
            combo.setSelectedIndex(i);
            break;
          }
        }
      }
    } finally {
      this.dangLamMoiBoLoc = false;
    }
  }

  private void taiLai(int page) {
    try {
      DanhSachPhuTungCriteria tieuChi = new DanhSachPhuTungCriteria(
              this.boLocMa.getText(),
              this.boLocTen.getText(),
              this.panel.getQuickFilter(),
              NumberParseUtil.toDoubleOrNull(this.boLocDonGia.tuText()),
              NumberParseUtil.toDoubleOrNull(this.boLocDonGia.denText()),
              NumberParseUtil.toIntegerOrNull(this.boLocSoLuong.tuText()),
              NumberParseUtil.toIntegerOrNull(this.boLocSoLuong.denText()),
              this.boLocNhaCungCap.ma(),
              Math.max(page, 1),
              SO_DONG_MOI_TRANG);

      PaginatedResult<PhuTung> ketQua = this.phuTungService.search(tieuChi);
      List<Object[]> rows = new ArrayList<>();

      for (PhuTung phuTung : ketQua.items()) {
        rows.add(new Object[] {
            phuTung.getAnhUrl(),
            phuTung.getMaPhuTung(),
            phuTung.getTenPhuTung(),
            phuTung.getMoTa(),
            phuTung.getDonGiaNhap(),
            phuTung.getSoLuongTon(),
            phuTung.getMaNhaCungCap()
        });
      }

      this.panel.setRows(rows);
      this.cauHinhCotAnh();
      this.panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();

    } catch (NumberFormatException e) {
      ThongBao.loi(this, "app.phuTung.loiBoLocSo");
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void cauHinhCotAnh() {
    AnhTableCellRenderer.ganVao(this.panel,
            COT_ANH,
            "app.phuTung.chuaCoAnh",
            "app.phuTung.khongTaiDuocAnh");
  }

  private PhuTungFormData docDuLieuDong(int row) {
    return new PhuTungFormData(
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MA)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_TEN)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MO_TA)),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_ANH)),
            NumberParseUtil.toDoubleOrNull(
                    NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_DON_GIA))),
            NumberParseUtil.toIntegerOrNull(
                    NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_SO_LUONG))),
            NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_NCC)));
  }

  private String layMaDong(int row) {
    return NumberParseUtil.textOf(this.panel.getModel().getValueAt(row, COT_MA));
  }

  // ==================== DIALOG ====================

  private void moDialogThem() {
    PhuTungTaoFormPanel form = new PhuTungTaoFormPanel();
    form.getChonNhaCungCapButton().addActionListener(e -> {
      new ChonNhaCungCapDialog(SwingUtilities.getWindowAncestor(this), this.nhaCungCapService, this.tieuChiChonNhaCungCap()).showDialog()
          .ifPresent(kq -> form.setNhaCungCap(kq.sanPham().getMaNhaCungCap()));
    });
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.phuTung.themTitle",
            form,
            () -> this.taoPhuTung(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    PhuTungFormData duLieu = this.docDuLieuDong(row);
    PhuTungSuaFormPanel form = new PhuTungSuaFormPanel();
    form.getChonNhaCungCapButton().addActionListener(e -> {
      new ChonNhaCungCapDialog(SwingUtilities.getWindowAncestor(this), this.nhaCungCapService, this.tieuChiChonNhaCungCap()).showDialog()
          .ifPresent(kq -> form.setNhaCungCap(kq.sanPham().getMaNhaCungCap()));
    });

    try {
      PhuTung phuTung = this.phuTungService.getById(duLieu.maPhuTung())
              .orElseThrow(() -> new IllegalArgumentException("app.phuTung.khongTimThay"));

      form.napDuLieu(this.taoFormData(phuTung));

      BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
              SwingUtilities.getWindowAncestor(this),
              "app.phuTung.suaTitle",
              form,
              () -> this.capNhatPhuTung(form, phuTung),
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

  private PhuTungFormData taoFormData(PhuTung phuTung) {
    return new PhuTungFormData(
            phuTung.getMaPhuTung(),
            phuTung.getTenPhuTung(),
            phuTung.getMoTa(),
            phuTung.getAnhUrl(),
            phuTung.getDonGiaNhap(),
            phuTung.getSoLuongTon(),
            phuTung.getMaNhaCungCap());
  }

  private boolean taoPhuTung(PhuTungFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    PhuTungFormData data = form.layDuLieu();

    try {
      this.phuTungService.create(new PhuTung(
              null,
              data.tenPhuTung(),
              data.moTa(),
              data.anhUrl(),
              data.donGiaNhap(),
              0,
              data.maNhaCungCap()));
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatPhuTung(PhuTungFormPanel form, PhuTung phuTung) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    PhuTungFormData data = form.layDuLieu();

    try {
      this.phuTungService.update(new PhuTung(
              phuTung.getMaPhuTung(),
              data.tenPhuTung(),
              data.moTa(),
              data.anhUrl(),
              data.donGiaNhap(),
              phuTung.getSoLuongTon(),
              data.maNhaCungCap()));
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(PhuTungFormPanel form) {
    if (!form.validateInput()) {
      ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
      return false;
    }

    return true;
  }

  // ==================== XÓA ====================

  private void xoa(int row) {
    String maPhuTung = this.layMaDong(row);

    if (maPhuTung.isBlank()) {
      ThongBao.loi(this, "app.phuTung.chuaChonXoa");
      return;
    }

    if (!ThongBao.xacNhan(this, "app.phuTung.xacNhanXoa", "app.phuTung.tieuDeXoa")) {
      return;
    }

    try {
      this.phuTungService.delete(maPhuTung);
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
        this.phuTungService.delete(this.layMaDong(row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
        // Phụ tùng đã phát sinh phiếu nhập / hóa đơn: ghi nhận vào báo cáo tổng hợp.
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
    return "app.menu.inventory";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
