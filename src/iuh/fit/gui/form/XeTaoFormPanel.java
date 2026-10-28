package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form tạo mới xe: biển số là dữ liệu nhập.
 */
public final class XeTaoFormPanel extends XeFormPanel {

  private static final long serialVersionUID = 1L;

  public XeTaoFormPanel() {
    Theme.applyEditable(this.bienSoField);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.xe.bienSo", this.bienSoField);
    this.addField("app.xe.hangXe", this.hangXeCombo);
    this.addField("app.xe.dongXe", this.dongXeCombo);
    this.addField("app.xe.doiXe", this.doiXeField);
    this.addField("app.xe.odo", this.odoField);
  }
}
