package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form cập nhật xe: biển số là khóa nên chỉ đọc.
 */
public final class XeSuaFormPanel extends XeFormPanel {

  private static final long serialVersionUID = 1L;

  public XeSuaFormPanel() {
    Theme.applyReadOnly(this.bienSoField);
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
