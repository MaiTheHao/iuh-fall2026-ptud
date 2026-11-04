package iuh.fit.gui.form;

import iuh.fit.gui.Theme;

public final class XeSuaFormPanel extends XeFormPanel {

  

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
