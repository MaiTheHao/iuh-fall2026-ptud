package iuh.fit.gui.form;

import iuh.fit.gui.theme.Theme;

/**
 * Form tạo mới xe: biển số là dữ liệu nhập.
 */
public final class XeTaoFormPanel extends XeFormPanel {

  private static final long serialVersionUID = 1L;

  public XeTaoFormPanel() {
    Theme.applyEditable(this.txtBienSo);
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
