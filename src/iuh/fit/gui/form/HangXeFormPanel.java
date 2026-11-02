package iuh.fit.gui.form;

import iuh.fit.entity.HangXe;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JTextField;

public final class HangXeFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;
  private static final int DO_DAI_TEN_TOI_DA = 100;

  private final JTextField tenField = new JTextField(20);

  public HangXeFormPanel() {
    this.addField("app.danhMucXe.ten", this.tenField);
  }

  public void napDuLieu(HangXe hangXe) {
    this.tenField.setText(hangXe == null || hangXe.getTen() == null ? "" : hangXe.getTen());
  }

  public String layTen() {
    return this.tenField.getText().trim();
  }

  public boolean validateInput() {
    String ten = this.layTen();
    if (ten.isEmpty()) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTen"));
      this.tenField.requestFocusInWindow();
      return false;
    }
    if (ten.length() > DO_DAI_TEN_TOI_DA) {
      FormValidationHelper.markError(this.tenField, I18nReaderUtil.get("app.danhMucXe.loiTenQuaDai"));
      this.tenField.requestFocusInWindow();
      return false;
    }
    FormValidationHelper.clearError(this.tenField);
    return true;
  }
}
