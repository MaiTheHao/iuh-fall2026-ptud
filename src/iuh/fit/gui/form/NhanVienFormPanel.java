package iuh.fit.gui.form;

import iuh.fit.dto.NhanVienFormData;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.time.format.DateTimeFormatter;

/**
 * Phần dùng chung của form nhân viên: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class NhanVienFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final String SDT_REGEX = "\\d{9,10}";
  private static final int DO_DAI_HO_TEN_TOI_DA = 50;
  private static final int DO_DAI_TEN_DANG_NHAP_TOI_DA = 30;
  private static final int DO_DAI_MAT_KHAU_TOI_DA = 255;

  protected final AnhField anhField = new AnhField(new AnhField.KhoaAnh(
          "app.nhanVien.anhUrl",
          "app.nhanVien.chonAnh",
          "app.nhanVien.goAnh",
          "app.nhanVien.chuaCoAnh",
          "app.nhanVien.khongTaiDuocAnh",
          "app.nhanVien.loaiTepAnh",
          "app.nhanVien.duLieuKhongPhaiAnh"));

  protected final JTextField maField = new JTextField(14);
  protected final JTextField tenDangNhapField = new JTextField(14);
  protected final JPasswordField matKhauField = new JPasswordField(14);
  protected final JTextField hoTenField = new JTextField(14);
  protected final JTextField soDienThoaiField = new JTextField(14);
  protected final JTextField ngayTaoField = new JTextField(14);
  protected final JComboBox<VaiTro> vaiTroCombo = new JComboBox<>(VaiTro.values());
  protected final JComboBox<TrangThaiNhanVien> trangThaiCombo =
          new JComboBox<>(TrangThaiNhanVien.values());

  protected NhanVienFormPanel() {
    Theme.applyReadOnly(this.maField);
    this.maField.setFocusable(false);
    Theme.applyReadOnly(this.ngayTaoField);
    Theme.applyEditable(this.matKhauField);

    this.vaiTroCombo.setRenderer(BoHienThiI18n.tao());
    this.trangThaiCombo.setRenderer(BoHienThiI18n.tao());

    this.vaiTroCombo.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);
    this.trangThaiCombo.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);

    FormValidationHelper.autoClearOnInput(this.tenDangNhapField);
    FormValidationHelper.autoClearOnInput(this.matKhauField);
    FormValidationHelper.autoClearOnInput(this.hoTenField);
    FormValidationHelper.autoClearOnInput(this.soDienThoaiField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(NhanVien nhanVien) {
    this.maField.setText(nhanVien.getMaNhanVien() == null ? "" : nhanVien.getMaNhanVien());
    this.tenDangNhapField.setText(nhanVien.getTenDangNhap() == null ? "" : nhanVien.getTenDangNhap());
    this.matKhauField.setText("");
    this.hoTenField.setText(nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen());
    this.soDienThoaiField.setText(nhanVien.getSoDienThoai() == null ? "" : nhanVien.getSoDienThoai());
    this.ngayTaoField.setText(nhanVien.getNgayTao() == null ? ""
            : nhanVien.getNgayTao().format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"))));
    this.anhField.setDuongDan(nhanVien.getAnhDaiDien());
    this.vaiTroCombo.setSelectedItem(nhanVien.getVaiTro());
    this.trangThaiCombo.setSelectedItem(nhanVien.getTrangThai());
  }

  public NhanVienFormData layDuLieu() {
    String soDienThoai = this.soDienThoaiField.getText().trim();

    return new NhanVienFormData(
            this.maField.getText().trim(),
            this.tenDangNhapField.getText().trim(),
            new String(this.matKhauField.getPassword()),
            this.hoTenField.getText().trim(),
            soDienThoai,
            this.anhField.getDuongDan(),
            (VaiTro) this.vaiTroCombo.getSelectedItem(),
            (TrangThaiNhanVien) this.trangThaiCombo.getSelectedItem(),
            this.ngayTaoField.getText().trim());
  }

  public final boolean validateInput() {
    JComponent loiChung = this.kiemTraChung();
    JComponent loiRieng = this.kiemTraRieng();
    JComponent loiDauTien = loiChung != null ? loiChung : loiRieng;

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  /** Chỗ mở rộng cho rule chỉ áp dụng ở một mode. */
  protected JComponent kiemTraRieng() {
    return null;
  }

  protected JComponent kiemTraChung() {
    JComponent loiDauTien = null;

    String tenDangNhap = this.tenDangNhapField.getText().trim();
    String matKhau = new String(this.matKhauField.getPassword());
    String hoTen = this.hoTenField.getText().trim();

    if (tenDangNhap.length() > DO_DAI_TEN_DANG_NHAP_TOI_DA) {
      FormValidationHelper.markError(this.tenDangNhapField,
              I18nReaderUtil.get("app.nhanVien.loiTenDangNhap"));
      loiDauTien = this.tenDangNhapField;
    } else {
      FormValidationHelper.clearError(this.tenDangNhapField);
    }

    if (matKhau.length() > DO_DAI_MAT_KHAU_TOI_DA) {
      FormValidationHelper.markError(this.matKhauField,
              I18nReaderUtil.get("app.nhanVien.loiMatKhau"));
      if (loiDauTien == null) {
        loiDauTien = this.matKhauField;
      }
    } else {
      FormValidationHelper.clearError(this.matKhauField);
    }

    if (hoTen.isEmpty() || hoTen.length() > DO_DAI_HO_TEN_TOI_DA) {
      FormValidationHelper.markError(this.hoTenField,
              I18nReaderUtil.get("app.nhanVien.loiHoTen"));
      if (loiDauTien == null) {
        loiDauTien = this.hoTenField;
      }
    } else {
      FormValidationHelper.clearError(this.hoTenField);
    }

    if (!FormValidationHelper.kiemTra(this.soDienThoaiField,
            this.soDienThoaiField.getText().trim().matches(SDT_REGEX),
            "app.nhanVien.loiSoDienThoai") && loiDauTien == null) {
      loiDauTien = this.soDienThoaiField;
    }

    if (this.vaiTroCombo.getSelectedItem() == null) {
      FormValidationHelper.markError(this.vaiTroCombo,
              I18nReaderUtil.get("app.nhanVien.loiVaiTro"));
      if (loiDauTien == null) {
        loiDauTien = this.vaiTroCombo;
      }
    } else {
      FormValidationHelper.clearError(this.vaiTroCombo);
    }

    if (this.trangThaiCombo.getSelectedItem() == null) {
      FormValidationHelper.markError(this.trangThaiCombo,
              I18nReaderUtil.get("app.nhanVien.loiTrangThai"));
      if (loiDauTien == null) {
        loiDauTien = this.trangThaiCombo;
      }
    } else {
      FormValidationHelper.clearError(this.trangThaiCombo);
    }

    return loiDauTien;
  }
}
