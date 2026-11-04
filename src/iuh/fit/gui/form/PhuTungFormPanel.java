package iuh.fit.gui.form;

import iuh.fit.dto.PhuTungFormData;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * Phần dùng chung của form phụ tùng: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class PhuTungFormPanel extends FormFieldPanel {

  

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtTen = new JTextField(14);
  protected final JTextArea txaMoTa = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.txaMoTa);
  protected final JTextField txtDonGia = new JTextField(14);
  protected final JTextField txtSoLuongTon = new JTextField(14);
  private final JTextField txtNhaCungCap = new JTextField(14);
  private final JButton btnChonNhaCungCap = new JButton("...");
  private final JPanel pnlHangNhaCungCap = new JPanel(new BorderLayout(4, 0));

  protected final AnhField pnlAnh = new AnhField(new AnhField.KhoaAnh(
          "app.phuTung.anhUrl",
          "app.phuTung.chonAnh",
          "app.phuTung.goAnh",
          "app.phuTung.chuaCoAnh",
          "app.phuTung.khongTaiDuocAnh",
          "app.phuTung.loaiTepAnh",
          "app.phuTung.duLieuKhongPhaiAnh"));

  protected PhuTungFormPanel() {
    this.txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
    this.txtSoLuongTon.setHorizontalAlignment(SwingConstants.RIGHT);

    Theme.applyReadOnly(this.txtMa);
    // Tồn kho chỉ thay đổi qua phiếu nhập / nghiệp vụ kho nên luôn chỉ đọc.
    Theme.applyReadOnly(this.txtSoLuongTon);
    this.txtSoLuongTon.setText("0");
    Theme.applyReadOnly(this.txtNhaCungCap);
    this.pnlHangNhaCungCap.add(this.txtNhaCungCap, BorderLayout.CENTER);
    this.pnlHangNhaCungCap.add(this.btnChonNhaCungCap, BorderLayout.EAST);

    FormValidationHelper.autoClearOnInput(this.txtTen);
    FormValidationHelper.autoClearOnInput(this.txtDonGia);
  }

  public JButton getChonNhaCungCapButton() {
    return this.btnChonNhaCungCap;
  }

  public void setNhaCungCap(String ma) {
    this.txtNhaCungCap.setText(ma == null ? "" : ma);
    FormValidationHelper.clearError(this.txtNhaCungCap);
  }

  public String getMaNhaCungCap() {
    return this.txtNhaCungCap.getText().trim();
  }

  public JComponent thanhNhaCungCap() {
    return this.pnlHangNhaCungCap;
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(PhuTungFormData data) {
    this.txtMa.setText(data.maPhuTung() == null ? "" : data.maPhuTung());
    this.txtTen.setText(data.tenPhuTung() == null ? "" : data.tenPhuTung());
    this.txaMoTa.setText(data.moTa() == null ? "" : data.moTa());
    this.pnlAnh.setDuongDan(data.anhUrl());
    this.txtDonGia.setText(data.donGiaNhap() == null ? "" : String.valueOf(data.donGiaNhap()));
    this.txtSoLuongTon.setText(data.soLuongTon() == null ? "0" : String.valueOf(data.soLuongTon()));
    this.setNhaCungCap(data.maNhaCungCap());
  }

  public PhuTungFormData layDuLieu() {
    String ma = this.txtMa.getText().trim();
    String moTa = this.txaMoTa.getText().trim();
    String anhUrl = this.pnlAnh.getDuongDan();
    Integer soLuongTon = NumberParseUtil.toIntegerOrNull(this.txtSoLuongTon.getText());

    return new PhuTungFormData(
            ma.isBlank() ? null : ma,
            this.txtTen.getText().trim(),
            moTa.isBlank() ? null : moTa,
            anhUrl.isBlank() ? null : anhUrl,
            NumberParseUtil.toDoubleOrNull(this.txtDonGia.getText()),
            soLuongTon == null ? 0 : soLuongTon,
            this.getMaNhaCungCap());
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

    if (this.txtTen.getText().trim().isEmpty()) {
      FormValidationHelper.markError(this.txtTen,
              I18nReaderUtil.get("app.phuTung.loiTen"));
      loiDauTien = this.txtTen;
    } else {
      FormValidationHelper.clearError(this.txtTen);
    }

    Double donGia = NumberParseUtil.toDoubleOrNull(this.txtDonGia.getText());

    if (donGia == null || !Double.isFinite(donGia) || donGia < 0) {
      FormValidationHelper.markError(this.txtDonGia,
              I18nReaderUtil.get("app.phuTung.loiDonGia"));
      if (loiDauTien == null) {
        loiDauTien = this.txtDonGia;
      }
    } else {
      FormValidationHelper.clearError(this.txtDonGia);
    }

    if (this.getMaNhaCungCap().isEmpty()) {
      FormValidationHelper.markError(this.txtNhaCungCap,
              I18nReaderUtil.get("app.phuTung.loiNhaCungCap"));
      if (loiDauTien == null) {
        loiDauTien = this.txtNhaCungCap;
      }
    } else {
      FormValidationHelper.clearError(this.txtNhaCungCap);
    }

    return loiDauTien;
  }
}
