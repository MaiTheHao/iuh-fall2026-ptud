package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.VoucherFormData;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKichHoat;
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.VoucherFormPanel;
import iuh.fit.service.VoucherService;
import iuh.fit.util.DateFormats;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;
import iuh.fit.util.TextNormalizerUtil;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoucherPage extends AbstractPage {

  


  private static final int CHIEU_CAO_TAO = 590;

  private static final int CHIEU_CAO_SUA = 670;

  private static final int COT_MA = 1;
  private static final int COT_TEN = 2;
  private static final int COT_MO_TA = 3;
  private static final int COT_MUC_GIAM = 4;
  private static final int COT_THOI_HAN = 5;
  private static final int COT_TRANG_THAI = 6;

  private final VoucherService voucherService = new VoucherService();

  private ManagementPanel pnlDanhSach;

  private JTextField txtBoLocMa;
  private ManagementPanel.BoLocKhoang boLocMucGiam;
  private ManagementPanel.BoLocNgay boLocThoiHan;
  private ManagementPanel.BoLocChon boLocTrangThai;

  private int trang = 1;

  public VoucherPage() {
    this.setLayout(new BorderLayout());
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(Theme.leDeu(8));
    this.setBackground(Theme.BG);

    this.pnlDanhSach = new ManagementPanel();
    this.pnlDanhSach.setListTitle("app.voucher.danhSachTitle");
    this.pnlDanhSach.enableMultiSelection(true);

    this.pnlDanhSach.setHeaders(
            I18nReaderUtil.get("app.voucher.ma"),
            I18nReaderUtil.get("app.voucher.tenChuongTrinh"),
            I18nReaderUtil.get("app.voucher.moTa"),
            I18nReaderUtil.get("app.voucher.mucGiam"),
            I18nReaderUtil.get("app.voucher.thoiHanApDung"),
            I18nReaderUtil.get("app.voucher.trangThaiKichHoat"));

    this.pnlDanhSach.addActionColumn("app.common.actions",
            new ThaoTac("pencil",
                    "app.common.edit",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::moDialogSua),
            new ThaoTac("check",
                    "app.voucher.batch_toggle_active",
                    ThaoTac.KieuThaoTac.THUONG,
                    this::doiTrangThai));

    this.pnlDanhSach.setAddAction("app.common.add_new", this::moDialogThem);
    this.pnlDanhSach.setBatchAction("app.voucher.batch_toggle_active",
            "check",
            ThaoTac.KieuThaoTac.THUONG,
            this::doiTrangThaiNhieu);
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
    this.txtBoLocMa = this.pnlDanhSach.addTextFilter("app.voucher.ma");
    this.boLocMucGiam = this.pnlDanhSach.addRangeFilter("app.voucher.mucGiam");
    this.boLocThoiHan = this.pnlDanhSach.addDateRangeFilter("app.voucher.thoiHanApDung");
    this.boLocTrangThai = this.pnlDanhSach.addComboBoxFilter("app.voucher.trangThaiKichHoat",
            TrangThaiKichHoat.values());
    this.pnlDanhSach.setFilterChangeListener(() -> this.taiLai(1));
  }


  private void taiLai(int page) {
    try {
      Object chonTrangThai = this.boLocTrangThai.giaTri();
      Boolean trangThai = chonTrangThai == null
              ? null
              : ((TrangThaiKichHoat) chonTrangThai).isGiaTri();

      DanhSachVoucherCriteria tieuChi = new DanhSachVoucherCriteria(
              this.pnlDanhSach.getQuickFilter(),
              this.txtBoLocMa.getText(),
              this.docMucGiam(this.boLocMucGiam.tuText()),
              this.docMucGiam(this.boLocMucGiam.denText()),
              this.boLocThoiHan.tuNgay(),
              this.boLocThoiHan.denNgay(),
              trangThai,
              Math.max(page, 1),
              UiConstants.SO_DONG_MOI_TRANG);

      PaginatedResult<Voucher> ketQua = this.voucherService.search(tieuChi);
      List<Object[]> rows = new ArrayList<>();

      for (Voucher voucher : ketQua.items()) {
        rows.add(new Object[] {
            voucher.getMaVoucher(),
            voucher.getTenChuongTrinh(),
            voucher.getMoTa() == null ? "" : voucher.getMoTa(),
            voucher.getMucGiam(),
            this.dinhDangNgayGio(voucher.getThoiHanApDung()),
            TrangThaiKichHoat.tu(voucher.isTrangThaiKichHoat()).getI18NBusinessName()
        });
      }

      this.pnlDanhSach.setRows(rows);
      this.pnlDanhSach.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
      this.pnlDanhSach.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
      this.trang = ketQua.page();

    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
      this.pnlDanhSach.setRows(Collections.emptyList());
      this.pnlDanhSach.setCount(0, 0, 0);
      this.pnlDanhSach.setPage(1, 1);
      this.trang = 1;
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private Double docMucGiam(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      double mucGiam = Double.parseDouble(value.trim());

      if (!Double.isFinite(mucGiam) || mucGiam < 0) {
        throw new NumberFormatException();
      }

      return mucGiam;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("app.voucher.loiMucGiam");
    }
  }

  private String dinhDangNgayGio(LocalDateTime thoiGian) {
    if (thoiGian == null) {
      return "";
    }

    return thoiGian.format(DateFormats.NGAY_GIO);
  }

  private VoucherFormData docDuLieuDong(int row) {
    String thoiHan = NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_THOI_HAN));
    LocalDateTime thoiHanApDung = thoiHan.isBlank()
            ? null
            : LocalDateTime.parse(thoiHan, DateFormats.NGAY_GIO);

    String trangThai = TextNormalizerUtil.normalize(
            NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_TRANG_THAI)));

    return new VoucherFormData(
            NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MA)),
            NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_TEN)),
            NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MO_TA)),
            NumberParseUtil.toDoubleOrNull(
                    NumberParseUtil.textOf(this.pnlDanhSach.getModel().getValueAt(row, COT_MUC_GIAM))),
            thoiHanApDung,
            trangThai.equals(TextNormalizerUtil.normalize(
                    TrangThaiKichHoat.DANG_KICH_HOAT.getI18NBusinessName())));
  }

  private Voucher taoVoucher(VoucherFormData data) {
    return new Voucher(
            data.maVoucher(),
            data.tenChuongTrinh(),
            data.moTa(),
            data.mucGiam(),
            data.thoiHanApDung(),
            Boolean.TRUE.equals(data.trangThaiKichHoat()));
  }


  private void moDialogThem() {
    VoucherFormPanel form = new VoucherFormPanel(true);
    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.voucher.themTitle",
            form,
            () -> this.taoVoucherMoi(form),
            CHIEU_CAO_TAO));

    if (dialog.showAndGet()) {
      this.taiLai(1);
    }
  }

  private void moDialogSua(int row) {
    VoucherFormData duLieu = this.docDuLieuDong(row);
    VoucherFormPanel form = new VoucherFormPanel(false);
    form.napDuLieu(duLieu);

    BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
            SwingUtilities.getWindowAncestor(this),
            "app.voucher.suaTitle",
            form,
            () -> this.capNhatVoucher(form, duLieu),
            CHIEU_CAO_SUA));

    if (dialog.showAndGet()) {
      this.taiLai(this.trang);
    }
  }

  private boolean taoVoucherMoi(VoucherFormPanel form) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    VoucherFormData data = form.layDuLieu();

    try {
      this.voucherService.create(this.taoVoucher(data));
      ThongBao.thongTin(this, "app.common.create_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean capNhatVoucher(VoucherFormPanel form, VoucherFormData duLieuCu) {
    if (!this.kiemTraForm(form)) {
      return false;
    }

    VoucherFormData data = form.layDuLieu();

    try {
      Voucher voucher = this.taoVoucher(data);
      voucher.setMaVoucher(duLieuCu.maVoucher());

      this.voucherService.update(voucher);
      ThongBao.thongTin(this, "app.common.update_succeed");
      return true;
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }

    return false;
  }

  private boolean kiemTraForm(VoucherFormPanel form) {
    if (form.validateInput()) {
      return true;
    }

    ThongBao.canhBao(this, "app.common.kiemTraLaiThongTin");
    return false;
  }


  private boolean doiTrangThaiVoucher(VoucherFormData duLieu) throws SQLException {
    Voucher voucher = this.taoVoucher(duLieu);
    voucher.setTrangThaiKichHoat(!voucher.isTrangThaiKichHoat());
    this.voucherService.update(voucher);
    return voucher.isTrangThaiKichHoat();
  }

  private void doiTrangThai(int row) {
    try {
      this.doiTrangThaiVoucher(this.docDuLieuDong(row));
      ThongBao.thongTin(this, "app.common.update_succeed");
      this.taiLai(this.trang);
    } catch (IllegalArgumentException e) {
      ThongBao.loi(this, e);
    } catch (SQLException e) {
      ThongBao.loi(this, "app.login.errorSystem");
    }
  }

  private void doiTrangThaiNhieu(List<Integer> rows) {
    if (rows.isEmpty()) {
      return;
    }

    if (!ThongBao.xacNhanDinhDang(this,
            "app.common.batch_toggle_confirm",
            "app.common.confirm",
            rows.size())) {
      return;
    }

    int thanhCong = 0;
    boolean loiHeThong = false;

    for (int row : rows) {
      try {
        this.doiTrangThaiVoucher(this.docDuLieuDong(row));
        thanhCong++;
      } catch (IllegalArgumentException | IllegalStateException ignored) {
      } catch (SQLException e) {
        loiHeThong = true;
        break;
      }
    }

    if (loiHeThong) {
      ThongBao.loi(this, "app.login.errorSystem");
    } else if (thanhCong == rows.size()) {
      ThongBao.thongTinDinhDang(this, "app.voucher.batch_toggle_result_all", thanhCong);
    } else {
      ThongBao.canhBaoDinhDang(this, "app.voucher.batch_toggle_result", thanhCong, rows.size());
    }

    this.taiLai(this.trang);
  }

  @Override
  public String getTitleKey() {
    return "app.menu.vouchers";
  }

  @Override
  public MenuGroup getGroup() {
    return MenuGroup.TO_CHUC;
  }
}
