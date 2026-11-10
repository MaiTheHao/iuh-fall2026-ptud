package iuh.fit.gui.form;

import iuh.fit.dto.DichVuFormData;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.AnhField;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Form nhập thông tin dịch vụ, dùng chung cho cả tạo mới và sửa.
 */
public class DichVuFormPanel extends FormFieldPanel {

  protected final boolean tao;

  private static final int DO_DAI_TEN_TOI_DA = 100;
  private static final int DO_DAI_MO_TA_TOI_DA = 500;

  protected final JTextField txtMa = new JTextField(14);
  protected final JTextField txtTen = new JTextField(14);
  protected final JTextArea txaMoTa = new JTextArea(4, 20);
  protected final JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.txaMoTa);
  protected final JTextField txtDonGia = new JTextField(14);

  protected final AnhField pnlAnh = new AnhField(new AnhField.KhoaAnh(
          "app.dichVu.anhUrl",
          "app.dichVu.chonAnh",
          "app.dichVu.goAnh",
          "app.dichVu.chuaCoAnh",
          "app.dichVu.khongTaiDuocAnh",
          "app.dichVu.loaiTepAnh",
          "app.dichVu.duLieuKhongPhaiAnh"));

  public DichVuFormPanel(boolean tao) {
    this.tao = tao;
    this.txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
    Theme.applyReadOnly(this.txtMa);

    FormValidationHelper.autoClearOnInput(this.txtTen);
    FormValidationHelper.autoClearOnInput(this.txtDonGia);

    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    // Mã dịch vụ do hệ thống sinh nên chỉ hiển thị khi sửa.
    if (!this.tao) {
      this.addField("app.dichVu.ma", this.txtMa);
    }
    this.addField("app.dichVu.ten", this.txtTen);
    this.addField("app.dichVu.moTa", this.moTaScroll);
    this.addBlock(this.pnlAnh);
    this.addField("app.dichVu.donGia", this.txtDonGia);
  }

  public void napDuLieu(DichVuFormData data) {
    this.txtMa.setText(data.maDichVu() == null ? "" : data.maDichVu());
    this.txtTen.setText(data.tenDichVu() == null ? "" : data.tenDichVu());
    this.txaMoTa.setText(data.moTa() == null ? "" : data.moTa());
    this.pnlAnh.setDuongDan(data.anhUrl());
    this.txtDonGia.setText(data.donGia() == null ? "" : String.valueOf(data.donGia()));
  }

  public DichVuFormData layDuLieu() {
    String ma = this.txtMa.getText().trim();
    String moTa = this.txaMoTa.getText().trim();
    String anhUrl = this.pnlAnh.getDuongDan();

    return new DichVuFormData(
            ma.isBlank() ? null : ma,
            this.txtTen.getText().trim(),
            moTa.isBlank() ? null : moTa,
            anhUrl.isBlank() ? null : anhUrl,
            NumberParseUtil.toDoubleOrNull(this.txtDonGia.getText()));
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
    return null;
  }

  protected JComponent kiemTraChung() {
    JComponent loiDauTien = null;

    String ten = this.txtTen.getText().trim();

    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.txtTen,
              I18nReaderUtil.get("app.dichVu.loiTen"));
      loiDauTien = this.txtTen;
    } else if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.txtTen,
              I18nReaderUtil.get("app.dichVu.loiTenQuaDai"));
      loiDauTien = this.txtTen;
    } else {
      FormValidationHelper.clearError(this.txtTen);
    }

    if (this.txaMoTa.getText().trim().length() > DO_DAI_MO_TA_TOI_DA) {
      FormValidationHelper.markError(this.txaMoTa,
              I18nReaderUtil.get("app.dichVu.loiMoTa"));
      if (loiDauTien == null) {
        loiDauTien = this.txaMoTa;
      }
    } else {
      FormValidationHelper.clearError(this.txaMoTa);
    }

    Double donGia = NumberParseUtil.toDoubleOrNull(this.txtDonGia.getText());

    if (donGia == null || !Double.isFinite(donGia) || donGia < 0) {
      // Chặn cả NaN/Infinity vì người dùng có thể nhập ký tự đặc biệt.
      FormValidationHelper.markError(this.txtDonGia,
              I18nReaderUtil.get("app.dichVu.loiDonGia"));
      if (loiDauTien == null) {
        loiDauTien = this.txtDonGia;
      }
    } else {
      FormValidationHelper.clearError(this.txtDonGia);
    }

    return loiDauTien;
  }
}
