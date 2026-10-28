package iuh.fit.gui.form;

import iuh.fit.dto.XeFormData;
import iuh.fit.gui.component.ComboGoiY;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.time.Year;

/**
 * Phần dùng chung của form xe: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class XeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final String BIEN_SO_REGEX = "^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$";
  private static final int NAM_SAN_XUAT_NHO_NHAT = 1886;
  private static final int DO_DAI_TEN_XE_TOI_DA = 50;

  protected final JTextField bienSoField = new JTextField(14);
  protected final JComboBox<String> hangXeCombo = new JComboBox<>();
  protected final JComboBox<String> dongXeCombo = new JComboBox<>();
  protected final JTextField doiXeField = new JTextField(14);
  protected final JTextField odoField = new JTextField(14);

  protected XeFormPanel() {
    this.odoField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.doiXeField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.hangXeCombo.setEditable(true);
    this.dongXeCombo.setEditable(true);

    FormValidationHelper.autoClearOnInput(this.bienSoField);
    FormValidationHelper.autoClearOnInput(this.doiXeField);
    FormValidationHelper.autoClearOnInput(this.odoField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(XeFormData data) {
    this.bienSoField.setText(data.bienSo() == null ? "" : data.bienSo());
    this.hangXeCombo.setSelectedItem(data.hangXe() == null ? "" : data.hangXe());
    this.dongXeCombo.setSelectedItem(data.dongXe() == null ? "" : data.dongXe());
    this.doiXeField.setText(data.doiXe() == null ? "" : String.valueOf(data.doiXe()));
    this.odoField.setText(data.odo() == null ? "" : String.valueOf(data.odo()));
  }

  public XeFormData layDuLieu() {
    return new XeFormData(
            this.bienSoField.getText().trim().toUpperCase(),
            ComboGoiY.giaTri(this.hangXeCombo),
            ComboGoiY.giaTri(this.dongXeCombo),
            NumberParseUtil.toIntegerOrNull(this.doiXeField.getText()),
            NumberParseUtil.toIntegerOrNull(this.odoField.getText()));
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

    String bienSo = this.bienSoField.getText().trim().toUpperCase();
    String hangXe = ComboGoiY.giaTri(this.hangXeCombo);
    String dongXe = ComboGoiY.giaTri(this.dongXeCombo);

    if (!FormValidationHelper.kiemTra(this.bienSoField,
            bienSo.matches(BIEN_SO_REGEX),
            "app.xe.loiBienSo")) {
      loiDauTien = this.bienSoField;
    }

    if (hangXe.isBlank()) {
      FormValidationHelper.markError(this.hangXeCombo,
              I18nReaderUtil.get("app.xe.loiHangXe"));
      if (loiDauTien == null) {
        loiDauTien = this.hangXeCombo;
      }
    } else if (hangXe.trim().length() > DO_DAI_TEN_XE_TOI_DA) {
      FormValidationHelper.markError(this.hangXeCombo,
              I18nReaderUtil.get("app.xe.loiHangXeQuaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.hangXeCombo;
      }
    } else {
      FormValidationHelper.clearError(this.hangXeCombo);
    }

    if (dongXe.isBlank()) {
      FormValidationHelper.markError(this.dongXeCombo,
              I18nReaderUtil.get("app.xe.loiDongXe"));
      if (loiDauTien == null) {
        loiDauTien = this.dongXeCombo;
      }
    } else if (dongXe.trim().length() > DO_DAI_TEN_XE_TOI_DA) {
      FormValidationHelper.markError(this.dongXeCombo,
              I18nReaderUtil.get("app.xe.loiDongXeQuaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.dongXeCombo;
      }
    } else {
      FormValidationHelper.clearError(this.dongXeCombo);
    }

    Integer doiXe = NumberParseUtil.toIntegerOrNull(this.doiXeField.getText());
    int namToiDa = Year.now().getValue() + 1;

    if (doiXe == null || doiXe < NAM_SAN_XUAT_NHO_NHAT || doiXe > namToiDa) {
      FormValidationHelper.markError(this.doiXeField,
              I18nReaderUtil.get("app.xe.loiDoiXe"));
      if (loiDauTien == null) {
        loiDauTien = this.doiXeField;
      }
    } else {
      FormValidationHelper.clearError(this.doiXeField);
    }

    Integer odo = NumberParseUtil.toIntegerOrNull(this.odoField.getText());

    if (odo != null && odo < 0) {
      FormValidationHelper.markError(this.odoField,
              I18nReaderUtil.get("app.xe.loiOdo"));
      if (loiDauTien == null) {
        loiDauTien = this.odoField;
      }
    } else {
      FormValidationHelper.clearError(this.odoField);
    }

    return loiDauTien;
  }

  public JComboBox<String> getHangXeCombo() {
    return this.hangXeCombo;
  }

  public JComboBox<String> getDongXeCombo() {
    return this.dongXeCombo;
  }
}
