package iuh.fit.gui.form;

import iuh.fit.dto.NhanVienFormData;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.time.format.DateTimeFormatter;

/**
 * Form nhập nhân viên, dùng chung cho tạo mới và sửa.
 */
public class NhanVienFormPanel extends FormFieldPanel {

  protected final boolean tao;

  private static final String SDT_REGEX = "\\d{9,10}";
  private static final int DO_DAI_HO_TEN_TOI_DA = 50;
  private static final int DO_DAI_TEN_DANG_NHAP_TOI_DA = 30;
  private static final int DO_DAI_MAT_KHAU_TOI_DA = 255;

  protected final AnhField pnlAnh = new AnhField(new AnhField.KhoaAnh(
          "app.nhanVien.anhUrl",
          "app.nhanVien.chonAnh",
          "app.nhanVien.goAnh",
          "app.nhanVien.chuaCoAnh",
          "app.nhanVien.khongTaiDuocAnh",
          "app.nhanVien.loaiTepAnh",
          "app.nhanVien.duLieuKhongPhaiAnh"));

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtTenDangNhap = new JTextField(14);
  protected final JPasswordField txtMatKhau = new JPasswordField(14);
  protected final JTextField txtHoTen = new JTextField(14);
  protected final JTextField txtSoDienThoai = new JTextField(14);
  protected final JTextField txtNgayTao = new JTextField(14);
  protected final JComboBox<VaiTro> cmbVaiTro = new JComboBox<>(VaiTro.values());
  protected final JComboBox<TrangThaiNhanVien> cmbTrangThai =
          new JComboBox<>(TrangThaiNhanVien.values());

  public NhanVienFormPanel(boolean tao) {
    this.tao = tao;
    Theme.applyReadOnly(this.txtMa);
    this.txtMa.setFocusable(false);
    Theme.applyReadOnly(this.txtNgayTao);
    Theme.applyEditable(this.txtMatKhau);

    if (this.tao) {
      Theme.applyEditable(this.txtTenDangNhap);
    } else {
      // Tên đăng nhập là khoá đăng nhập nên không cho sửa.
      Theme.applyReadOnly(this.txtTenDangNhap);
    }

    this.cmbVaiTro.setRenderer(BoHienThiI18n.tao());
    this.cmbTrangThai.setRenderer(BoHienThiI18n.tao());

    this.cmbVaiTro.setSelectedItem(VaiTro.NHAN_VIEN_TIEP_NHAN);
    this.cmbTrangThai.setSelectedItem(TrangThaiNhanVien.HOAT_DONG);

    FormValidationHelper.autoClearOnInput(this.txtTenDangNhap);
    FormValidationHelper.autoClearOnInput(this.txtMatKhau);
    FormValidationHelper.autoClearOnInput(this.txtHoTen);
    FormValidationHelper.autoClearOnInput(this.txtSoDienThoai);

    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    if (!this.tao) {
      this.addField("app.nhanVien.maNhanVien", this.txtMa);
    }
    this.addField("app.nhanVien.tenDangNhap", this.txtTenDangNhap);
    this.addField("app.nhanVien.matKhau", this.txtMatKhau);
    if (!this.tao) {
      this.addBlock(taoGoiY("app.nhanVien.matKhauGoiY"));
    }
    this.addField("app.nhanVien.hoTen", this.txtHoTen);
    this.addField("app.nhanVien.soDienThoai", this.txtSoDienThoai);
    this.addField("app.nhanVien.vaiTro", this.cmbVaiTro);
    this.addField("app.nhanVien.trangThai", this.cmbTrangThai);
    if (!this.tao) {
      this.addField("app.nhanVien.ngayTao", this.txtNgayTao);
    }
    this.addBlock(this.pnlAnh);
  }

  public void napDuLieu(NhanVien nhanVien) {
    this.txtMa.setText(nhanVien.getMaNhanVien() == null ? "" : nhanVien.getMaNhanVien());
    this.txtTenDangNhap.setText(nhanVien.getTenDangNhap() == null ? "" : nhanVien.getTenDangNhap());
    this.txtMatKhau.setText("");
    this.txtHoTen.setText(nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen());
    this.txtSoDienThoai.setText(nhanVien.getSoDienThoai() == null ? "" : nhanVien.getSoDienThoai());
    this.txtNgayTao.setText(nhanVien.getNgayTao() == null ? ""
            : nhanVien.getNgayTao().format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"))));
    this.pnlAnh.setDuongDan(nhanVien.getAnhDaiDien());
    this.cmbVaiTro.setSelectedItem(nhanVien.getVaiTro());
    this.cmbTrangThai.setSelectedItem(nhanVien.getTrangThai());
  }

  public NhanVienFormData layDuLieu() {
    String soDienThoai = this.txtSoDienThoai.getText().trim();

    return new NhanVienFormData(
            this.txtMa.getText().trim(),
            this.txtTenDangNhap.getText().trim(),
            new String(this.txtMatKhau.getPassword()),
            this.txtHoTen.getText().trim(),
            soDienThoai,
            this.pnlAnh.getDuongDan(),
            (VaiTro) this.cmbVaiTro.getSelectedItem(),
            (TrangThaiNhanVien) this.cmbTrangThai.getSelectedItem(),
            this.txtNgayTao.getText().trim());
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

  protected JComponent kiemTraRieng() {
    // Chỉ bắt buộc tên đăng nhập và mật khẩu khi tạo mới;
    // lúc sửa, mật khẩu để trống nghĩa là giữ nguyên mật khẩu cũ.
    if (!this.tao) {
      return null;
    }

    if (this.txtTenDangNhap.getText().trim().isEmpty()) {
      FormValidationHelper.markError(this.txtTenDangNhap,
              I18nReaderUtil.get("app.nhanVien.loiTenDangNhap"));
      return this.txtTenDangNhap;
    }

    if (new String(this.txtMatKhau.getPassword()).isBlank()) {
      FormValidationHelper.markError(this.txtMatKhau,
              I18nReaderUtil.get("app.nhanVien.loiMatKhau"));
      return this.txtMatKhau;
    }

    return null;
  }

  protected JComponent kiemTraChung() {
    JComponent loiDauTien = null;

    String tenDangNhap = this.txtTenDangNhap.getText().trim();
    String matKhau = new String(this.txtMatKhau.getPassword());
    String hoTen = this.txtHoTen.getText().trim();

    if (tenDangNhap.length() > DO_DAI_TEN_DANG_NHAP_TOI_DA) {
      FormValidationHelper.markError(this.txtTenDangNhap,
              I18nReaderUtil.get("app.nhanVien.loiTenDangNhap"));
      loiDauTien = this.txtTenDangNhap;
    } else {
      FormValidationHelper.clearError(this.txtTenDangNhap);
    }

    if (matKhau.length() > DO_DAI_MAT_KHAU_TOI_DA) {
      FormValidationHelper.markError(this.txtMatKhau,
              I18nReaderUtil.get("app.nhanVien.loiMatKhau"));
      if (loiDauTien == null) {
        loiDauTien = this.txtMatKhau;
      }
    } else {
      FormValidationHelper.clearError(this.txtMatKhau);
    }

    if (hoTen.isEmpty() || hoTen.length() > DO_DAI_HO_TEN_TOI_DA) {
      FormValidationHelper.markError(this.txtHoTen,
              I18nReaderUtil.get("app.nhanVien.loiHoTen"));
      if (loiDauTien == null) {
        loiDauTien = this.txtHoTen;
      }
    } else {
      FormValidationHelper.clearError(this.txtHoTen);
    }

    if (!FormValidationHelper.kiemTra(this.txtSoDienThoai,
            this.txtSoDienThoai.getText().trim().matches(SDT_REGEX),
            "app.nhanVien.loiSoDienThoai") && loiDauTien == null) {
      loiDauTien = this.txtSoDienThoai;
    }

    if (this.cmbVaiTro.getSelectedItem() == null) {
      FormValidationHelper.markError(this.cmbVaiTro,
              I18nReaderUtil.get("app.nhanVien.loiVaiTro"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbVaiTro;
      }
    } else {
      FormValidationHelper.clearError(this.cmbVaiTro);
    }

    if (this.cmbTrangThai.getSelectedItem() == null) {
      FormValidationHelper.markError(this.cmbTrangThai,
              I18nReaderUtil.get("app.nhanVien.loiTrangThai"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbTrangThai;
      }
    } else {
      FormValidationHelper.clearError(this.cmbTrangThai);
    }

    return loiDauTien;
  }
}
