package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form cập nhật xe: biển số là khóa nên chỉ đọc.
 */
public final class XeSuaFormPanel extends XeFormPanel {

  private static final long serialVersionUID = 1L;

  public XeSuaFormPanel() {
    Theme.applyReadOnly(this.txtBienSo);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.xe.bienSo", this.txtBienSo);
    this.addField("app.xe.hangXe", this.cmbHangXe);
    this.addField("app.xe.dongXe", this.cmbDongXe);
    this.addField("app.xe.doiXe", this.cmbDoiXe);
    this.addField("app.xe.odo", this.txtOdo);
  }
}
